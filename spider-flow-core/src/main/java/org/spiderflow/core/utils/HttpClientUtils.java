package org.spiderflow.core.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yida
 * @package com.wdph.core.utils
 * @date 2023-12-07 08:47
 * @description HttpClient工具类
 */
public class HttpClientUtils {
	private static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

	public static void main(String[] args) {
		int bufferSize = 1024 * 1024 * 10;
		String remoteRequestUrl = "https://www.apache.org/dyn/closer.lua/solr/solr/9.7.0/solr-9.7.0.tgz?action=download";
		String targetSavePath = "F:/tmp/20240920/";
		String targetFileName = "solr-9.7.0.tar.gz";
		downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, bufferSize);
	}

	private static final int MAX_TOTAL_CONN = 600;
	private static final int MAX_CONN_PER_HOST = 300;
	private static final int SOCKET_TIMEOUT = 18000000;
	private static final int CONNECTION_TIMEOUT = 200;
	private static final int CONNECTION_MANAGER_TIMEOUT = 100;

	/**
	 * 每个分片50M(单位:bytes)
	 */
	private final static long per_page_size = 1024L * 1024L * 50L;


	private static CloseableHttpClient httpclient;
	private static PoolingHttpClientConnectionManager connMrg;

	static {
		init();
		destroyByJvmExit();
	}

	private static void destroyByJvmExit() {
		Thread hook = new Thread(new Runnable() {
			public void run() {
				try {
					httpclient.close();
				} catch (IOException e) {
					log.error("Close the HttpClient instance as JVM exit.but occur exception.");
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(hook);
	}

	private static void init() {
		Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
				.register("http", PlainConnectionSocketFactory.INSTANCE)
				.register("https", trustAllHttpsCertificates()).build();
		connMrg = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		connMrg.setMaxTotal(MAX_TOTAL_CONN);
		connMrg.setDefaultMaxPerRoute(MAX_CONN_PER_HOST);//每个路由基础的连接
		RequestConfig defaultRequestConfig = RequestConfig.custom()
				.setConnectTimeout(CONNECTION_TIMEOUT)//设置连接超时时间，单位毫秒
				.setSocketTimeout(SOCKET_TIMEOUT)//请求获取数据的超时时间，单位毫秒
				.setConnectionRequestTimeout(CONNECTION_MANAGER_TIMEOUT)//设置从连接池获取连接超时时间，单位毫秒
				.build();
		try {
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, (x509Certificates, authType) -> true).build();
			HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
			httpclient = HttpClients.custom()
					.setConnectionManager(connMrg)
					.setDefaultRequestConfig(defaultRequestConfig)
					.setSSLContext(sslContext)
					.setSSLSocketFactory(sslConnectionSocketFactory)
					.build();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static SSLConnectionSocketFactory trustAllHttpsCertificates() {
		SSLConnectionSocketFactory socketFactory = null;
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager customTrustManager = new CustomTrustManager();
		trustAllCerts[0] = customTrustManager;
		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, null);
			socketFactory = new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		return socketFactory;
	}

	/**
	 * @param url       请求地址
	 * @param headers   请求头
	 * @param jsonParam JSON格式的请求参数
	 * @param encoding 字符集
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, Map<String, String> headers, String jsonParam, String encoding) {
		// 创建Client
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse response = null;
		// 创建HttpPost对象
		HttpPost httpPost = null;
		try {
			if (StringUtils.isEmpty(encoding)) {
				encoding = Constants.DEFAULT_CHARSET;
			}
			httpPost = new HttpPost();
			httpPost.setURI(new URI(url));
			if (null == headers) {
				headers = new HashMap<>();
				headers.put("content-type", "application/json;charset=utf8");
			}
			putUpRequestHeader(httpPost, headers);
			if (null != jsonParam && jsonParam.length() > 0) {
				// 设置实体
				httpPost.setEntity(new StringEntity(jsonParam, encoding));
			}
			// 发送请求,返回响应对象
			response = client.execute(httpPost);
			return parseResponse(response);
		} catch (Exception e) {
			log.error("发送post请求失败", e);
		} finally {
			if (null != response) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (null != httpPost) {
				httpPost.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * @param url       请求地址
	 * @param headers   请求头
	 * @param jsonParam JSON格式的请求参数
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, Map<String, String> headers, String jsonParam) {
		return sendPostJSON(url, headers, jsonParam, null);
	}


	/**
	 * @param url  请求地址
	 * @param jsonParam JSON格式的请求参数
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, String jsonParam, String encoding) {
		// 设置默认请求头
		Map<String, String> headers = new HashMap<>();
		headers.put("content-type", "application/json;charset=utf8");
		return sendPostJSON(url, headers, jsonParam, encoding);
	}

	/**
	 * @param url      请求地址
	 * @param headers  请求头
	 * @param encoding 字符集
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, String encoding, Map<String, String> headers) {
		if (null == headers) {
			headers = new HashMap<>();
			headers.put("content-type", "application/json;charset=utf8");
		}
		headers.put("content-type", "application/json;charset=utf8");
		return sendPostJSON(url, headers, null, encoding);
	}

	/**
	 * @param url     请求地址
	 * @param headers 请求头
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, Map<String, String> headers) {
		return sendPostJSON(url, headers, null);
	}

	/**
	 * @param url    请求地址
	 * @param jsonParam JSON格式的请求参数
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url, String jsonParam) {
		return sendPostJSON(url, jsonParam, (String) null);
	}

	/**
	 * @param url 请求地址
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPostJSON(String url) {
		return sendPostJSON(url, (String) null);
	}

	/**
	 * @param url     请求地址
	 * @param headers 请求头
	 * @param params  请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description:(发送post请求，请求数据默认使用UTF-8编码)
	 */
	public static String sendPost(String url, Map<String, String> headers, Map<String, String> params, String encoding) {
		// 创建Client
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse response = null;
		// 创建HttpPost对象
		HttpPost httpPost = null;
		try {
			if (StringUtils.isEmpty(encoding)) {
				encoding = Constants.DEFAULT_CHARSET;
			}
			httpPost = new HttpPost();
			httpPost.setURI(new URI(url));
			putUpRequestHeader(httpPost, headers);
			if (null != params && params.size() > 0) {
				putUpRequestParameter(httpPost, params);
			}
			// 发送请求,返回响应对象
			response = client.execute(httpPost);
			return parseResponse(response, encoding);
		} catch (Exception e) {
			log.error("发送post请求失败", e);
		} finally {
			if (null != response) {
				try {
					response.close();
				} catch (Exception e) {}
			}
			if (null != httpPost) {
				httpPost.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * @param url     请求地址
	 * @param headers 请求头
	 * @param params  请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description:(发送post请求，请求数据默认使用UTF-8编码)
	 */
	public static String sendPost(String url, Map<String, String> headers, Map<String, String> params) {
		return sendPost(url, headers, params, null);
	}

	/**
	 * @param url     请求地址
	 * @param headers 请求头
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description:(发送post请求，请求数据默认使用UTF-8编码)
	 */
	public static String sendPost(String url, String encoding, Map<String, String> headers) {
		return sendPost(url, headers, null, encoding);
	}

	/**
	 * @param url    请求地址
	 * @param params 请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description:(发送post请求，请求数据默认使用UTF-8编码)
	 */
	public static String sendPost(String url, Map<String, String> params, String encoding) {
		return sendPost(url, null, params, encoding);
	}

	/**
	 * @param url 请求地址
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description:(发送post请求，请求数据默认使用UTF-8编码)
	 */
	public static String sendPost(String url, String encoding) {
		return sendPost(url, null, encoding);
	}

	/**
	 * @param url 请求地址
	 * @return String
	 * @throws
	 * @Title: sendPost
	 */
	public static String sendPost(String url) {
		return sendPost(url, null);
	}

	/**
	 * @param url      请求地址
	 * @param params   请求参数
	 * @param headerMap 请求头信息
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(String url, Map<String, Object> params, Map<String, Object> headerMap, String encoding) {
		CloseableHttpResponse response = null;
		CloseableHttpClient client = httpclient;
		// 创建HttpGet
		HttpGet httpGet = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(url);
			// 封装参数
			if (params != null) {
				for (String key : params.keySet()) {
					builder.addParameter(key, params.get(key).toString());
				}
			}
			URI uri = builder.build();
			httpGet = new HttpGet();
			httpGet.setURI(uri);
			if (headerMap != null) {
				for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
					String key = entry.getKey();
					Object val = entry.getValue();
					httpGet.addHeader(key, (null == val) ? "" : val.toString());
				}
			}
			// 发送请求，返回响应对象
			response = client.execute(httpGet);
			return parseResponse(response, encoding);
		} catch (Exception e) {
			log.error("发送get请求失败", e);
		} finally {
			if (null != response) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (null != httpGet) {
				httpGet.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * @param url       请求地址
	 * @param params    请求参数
	 * @param headerMap 请求头信息
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(String url, Map<String, Object> params, Map<String, Object> headerMap) {
		return sendGet(url, params, headerMap, null);
	}

	/**
	 * @param url    请求地址
	 * @param params 请求参数
	 * @return String
	 * @throws
	 * @Title: sendGet
	 * @Description:
	 */
	public static String sendGet(String url, Map<String, Object> params, String encoding) {
		return sendGet(url, params, null, encoding);
	}

	/**
	 * @param url    请求地址
	 * @param params 请求参数
	 * @return String
	 * @throws
	 * @Title: sendGet
	 * @Description:
	 */
	public static String sendGet(String url, Map<String, Object> params) {
		return sendGet(url, params, (String) null);
	}

	/**
	 * @param url       请求地址
	 * @param headerMap 请求头信息
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(String url, String encoding, Map<String, Object> headerMap) {
		return sendGet(url, null, headerMap, encoding);
	}

	/**
	 * @param url       请求地址
	 * @param headerMap 请求头信息
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(Map<String, Object> headerMap, String url) {
		return sendGet(url, (String) null, headerMap);
	}

	/**
	 * @param url 请求地址
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(String url, String encoding) {
		return sendGet(url, null, encoding);
	}

	/**
	 * @param url 请求地址
	 * @return String
	 * @throws
	 * @Title: sendGet
	 */
	public static String sendGet(String url) {
		return sendGet(url, (String)null);
	}

	/**
	 * 解析response
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String parseResponse(CloseableHttpResponse response) {
		return parseResponse(response, null);
	}

	/**
	 * 解析response
	 *
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public static String parseResponse(CloseableHttpResponse response, String encoding) {
		// 获取响应状态
		int status = response.getStatusLine().getStatusCode();
		if (status == HttpStatus.SC_OK) {
			// 获取响应数据
			try {
				if(StringUtils.isEmpty(encoding)) {
					encoding = Constants.DEFAULT_CHARSET;
				}
				return EntityUtils.toString(response.getEntity(), encoding);
			} catch (Exception e) {
			    return null;
			}
		} else {
			log.error("响应失败，状态码：" + status);
		}
		return null;
	}

	/**
	 * 发送Post请求
	 *
	 * @param url
	 * @param parameterMap
	 * @return
	 */
	public static String post(String url, Map<String, String> parameterMap, Map<String, String> headerMap, RequestConfig requestConfig, String charset) {
		// 创建Client
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse response = null;
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost();
			httpPost.setURI(new URI(url));
			setRequestConfig(httpPost, requestConfig);
			putUpRequestHeader(httpPost, headerMap);
			putUpRequestParameter(httpPost, parameterMap, charset);
			response = client.execute(httpPost);
			return parseResponse(response);
		} catch (Exception e) {
			log.error("Send post request with url:[{}] and params:[{}], but occur exception.", url, JacksonUtils.toJSONString(parameterMap));
		} finally {
			if (null != response) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (null != httpPost) {
				httpPost.releaseConnection();
			}
		}
		return null;
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @param headerMap
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void download(String remoteFileUrl, String localFilePath, Map<String, Object> params, Map<String, Object> headerMap, int bufferSize) {
		if (null == localFilePath || localFilePath.length() <= 0) {
			return;
		}
		if (bufferSize <= 0) {
			bufferSize = Constants.DEFAULT_BUFFER_SIZE;
		}
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse httpResponse = null;
		// 创建HttpGet
		HttpGet httpGet = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(remoteFileUrl);
			// 封装参数
			if (params != null) {
				for (String key : params.keySet()) {
					builder.addParameter(key, params.get(key).toString());
				}
			}
			URI uri = builder.build();
			httpGet = new HttpGet();
			httpGet.setURI(uri);
			if (headerMap != null) {
				for (Map.Entry<String, Object> entry : headerMap.entrySet()) {
					String key = entry.getKey();
					Object val = entry.getValue();
					httpGet.addHeader(key, (null == val) ? "" : val.toString());
				}
			}
			// 发送请求，返回响应对象
			httpResponse = client.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			parseHttpEntity(localFilePath, remoteFileUrl, httpEntity, bufferSize, true, false);
		} catch (Exception e) {
			log.error("下载文件出现异常:{}", e.getMessage());
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
		}
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @param headerMap
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void download(String remoteFileUrl, String localFilePath, Map<String, Object> params, Map<String, Object> headerMap) {
		download(remoteFileUrl, localFilePath, params, headerMap, Constants.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void download(String remoteFileUrl, String localFilePath, Map<String, Object> params) {
		download(remoteFileUrl, localFilePath, params, null);
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void download(String remoteFileUrl, String localFilePath) {
		download(remoteFileUrl, localFilePath, null);
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @param headerMap
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void downloadAsPost(String remoteFileUrl, String localFilePath, Map<String, String> params, Map<String, String> headerMap, int bufferSize) {
		if (null == localFilePath || localFilePath.length() <= 0) {
			return;
		}
		if (bufferSize <= 0) {
			bufferSize = Constants.DEFAULT_BUFFER_SIZE;
		}
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse httpResponse = null;
		// 创建HttpPost
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost();
			httpPost.setURI(new URI(remoteFileUrl));
			putUpRequestHeader(httpPost, headerMap);
			putUpRequestHeader(httpPost, params);
			// 发送请求,返回响应对象
			httpResponse = client.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			parseHttpEntity(localFilePath, remoteFileUrl, httpEntity, bufferSize, true, false);
		} catch (Exception e) {
			log.error("下载文件出现异常:{}", e.getMessage());
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
		}
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @param headerMap
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void downloadAsPost(String remoteFileUrl, String localFilePath, Map<String, String> params, Map<String, String> headerMap) {
		downloadAsPost(remoteFileUrl, localFilePath, params, headerMap, Constants.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @param params
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void downloadAsPost(String remoteFileUrl, String localFilePath, Map<String, String> params) {
		downloadAsPost(remoteFileUrl, localFilePath, params, null);
	}

	/**
	 * @param remoteFileUrl
	 * @param localFilePath
	 * @description 下载文件
	 * @author yida
	 * @date 2024-09-19 17:00:08
	 */
	public static void downloadAsPost(String remoteFileUrl, String localFilePath) {
		downloadAsPost(remoteFileUrl, localFilePath, null);
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @param headers       请求头
	 * @param params        请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName, Map<String, String> headers, Map<String, String> params, String encoding) {
		File localFile = new File(localFilePath);
		if (!localFile.exists()) {
			return false;
		}
		boolean uploadResult = false;
		// 创建Client
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse response = null;
		// 创建HttpPost对象
		HttpPost httpPost = null;
		try {
			if (StringUtils.isEmpty(encoding)) {
				encoding = Constants.DEFAULT_CHARSET;
			}
			httpPost = new HttpPost();
			httpPost.setURI(new URI(remoteServiceUrl));
			putUpRequestHeader(httpPost, headers);
			MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
			if (null != params && params.size() > 0) {
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					StringBody stringBody = new StringBody(val, ContentType.create("text/plain", encoding));
					multipartEntityBuilder.addPart(key, stringBody);
				}
			}
			FileBody fileBody = new FileBody(localFile);
			multipartEntityBuilder.addPart(formFileFieldName, fileBody);
			HttpEntity reqEntity = multipartEntityBuilder.build();
			httpPost.setEntity(reqEntity);
			response = client.execute(httpPost);
			HttpEntity resEntity = response.getEntity();
			EntityUtils.consume(resEntity);
			uploadResult = true;
		} catch (Exception e) {
			log.error("发送post请求失败", e);
		} finally {
			if (null != response) {
				try {
					response.close();
				} catch (Exception e) {
				}
			}
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			return uploadResult;
		}
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @param headers       请求头
	 * @param params        请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName, Map<String, String> headers, Map<String, String> params) {
		return upload(localFilePath, remoteServiceUrl, formFileFieldName, headers, params, null);
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @param params        请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName, Map<String, String> params, String encoding) {
		return upload(localFilePath, remoteServiceUrl, formFileFieldName, null, params, encoding);
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @param params        请求实体
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName, Map<String, String> params) {
		return upload(localFilePath, remoteServiceUrl, formFileFieldName, params, (String) null);
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName, String encoding) {
		return upload(localFilePath, remoteServiceUrl, formFileFieldName, null, encoding);
	}

	/**
	 * @param localFilePath 待上传文件的本地路径
	 * @return String
	 * @throws
	 * @Title: sendPost
	 * @Description: 上传文件
	 */
	public static boolean upload(String localFilePath, String remoteServiceUrl, String formFileFieldName) {
		return upload(localFilePath, remoteServiceUrl, formFileFieldName, (String) null);
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @param paramsMap
	 * @param headersMap
	 * @param bufferSize
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName,
												 Map<String, String> paramsMap, Map<String, String> headersMap, int bufferSize) {
		if (null == targetSavePath || targetSavePath.isEmpty()) {
			return false;
		}
		if (null == targetFileName || targetFileName.isEmpty()) {
			return false;
		}
		targetSavePath = StringUtils.replaceBackSlash(targetSavePath);
		if (!targetSavePath.endsWith("/")) {
			targetSavePath = targetSavePath + "/";
		}
		String targetSaveFilePath = targetSavePath + targetFileName;
		long alreadyDownloadSize = 0L;
		File targetFile = new File(targetSaveFilePath);
		if (targetFile.exists()) {
			alreadyDownloadSize = targetFile.length();
		} else {
			File saveDir = new File(targetSavePath);
			if (!saveDir.exists()) {
				saveDir.mkdirs();
			}
			try {
				targetFile.createNewFile();
			} catch (Exception e) {
				log.error("create file:[{}] occur exception:\n{}.", targetSaveFilePath, e.getMessage());
				return false;
			}
		}

		boolean downloadResult = false;
		CloseableHttpClient client = httpclient;
		CloseableHttpResponse httpResponse = null;
		// 创建HttpGet
		HttpGet httpGet = null;
		try {
			// 创建uri
			URIBuilder builder = new URIBuilder(remoteRequestUrl);
			// 封装参数
			if (paramsMap != null && paramsMap.size() > 0) {
				for (String key : paramsMap.keySet()) {
					builder.addParameter(key, paramsMap.get(key));
				}
			}
			URI uri = builder.build();
			httpGet = new HttpGet();
			httpGet.setURI(uri);
			if (headersMap == null || headersMap.size() <= 0) {
				headersMap = new HashMap<>();
			}
			headersMap.put("range", "bytes=" + alreadyDownloadSize + "-");
			putUpRequestHeader(httpGet, headersMap);
			httpResponse = client.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			parseHttpEntity(targetSaveFilePath, remoteRequestUrl, httpEntity, alreadyDownloadSize, bufferSize, true, true);
			downloadResult = true;
		} catch (Exception e) {
			log.error("As downloading file:{} occur exception:\n{}", remoteRequestUrl, e.getMessage());
		} finally {
			try {
				if (httpResponse != null) {
					httpResponse.close();
				}
			} catch (IOException e) {
			}
			if (httpGet != null) {
				httpGet.releaseConnection();
			}
			return downloadResult;
		}
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @param paramsMap
	 * @param headersMap
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName,
												 Map<String, String> paramsMap, Map<String, String> headersMap) {
		return downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, paramsMap, headersMap, Constants.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @param paramsMap
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName,
												 Map<String, String> paramsMap, int bufferSize) {
		return downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, paramsMap, null, bufferSize);
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @param paramsMap
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName,
												 Map<String, String> paramsMap) {
		return downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, paramsMap, Constants.DEFAULT_BUFFER_SIZE);
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @param bufferSize
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName,
												 int bufferSize) {
		return downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, null, null, bufferSize);
	}

	/**
	 * @param remoteRequestUrl
	 * @param targetSavePath
	 * @param targetFileName
	 * @return boolean
	 * @description 断点下载文件
	 * @author yida
	 * @date 2024-09-20 09:52:07
	 */
	public static boolean downloadSupportRecover(String remoteRequestUrl, String targetSavePath, String targetFileName) {
		return downloadSupportRecover(remoteRequestUrl, targetSavePath, targetFileName, Constants.DEFAULT_BUFFER_SIZE);
	}


	/**
	 * @param targetFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String targetFilePath, String remoteRequestUrl, HttpEntity httpEntity, long alreadyDownloadSize, int bufferSize, boolean appendWrite, boolean chunkDownload) {
		if(alreadyDownloadSize < 0L) {
			alreadyDownloadSize = 0L;
		}
		if(bufferSize <= 0) {
			bufferSize = Constants.DEFAULT_BUFFER_SIZE;
		}
		OutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			inputStream = httpEntity.getContent();
			long unDownloadedLength = httpEntity.getContentLength();
			if (unDownloadedLength <= 0) {
				if (chunkDownload) {
					log.error("待下载文件:[{}]的体积大小为零", remoteRequestUrl);
					return;
				}
			}
			long totalFileSize = alreadyDownloadSize + unDownloadedLength;
			targetFilePath = StringUtils.replaceBackSlash(targetFilePath);
			String parentPath = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));
			File parentDir = new File(parentPath);
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			File file = new File(targetFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			outputStream = new BufferedOutputStream(new FileOutputStream(file, appendWrite));
			byte[] buffer = new byte[bufferSize];
			int readLength = 0;
			while ((readLength = inputStream.read(buffer)) > 0) {
				byte[] bytes = new byte[readLength];
				System.arraycopy(buffer, 0, bytes, 0, readLength);
				outputStream.write(bytes);
				alreadyDownloadSize += readLength;
				// 下载进度
				BigDecimal alreadyDownloadSizeBigDecimal = new BigDecimal(String.valueOf(alreadyDownloadSize));
				BigDecimal totalFileSizeBigDecimal = new BigDecimal(String.valueOf(totalFileSize));
				BigDecimal downloadPercentBigDecimal = alreadyDownloadSizeBigDecimal.divide(totalFileSizeBigDecimal, 4, RoundingMode.HALF_UP)
						.multiply(new BigDecimal("100"));
				String downloadPercentText = downloadPercentBigDecimal.toPlainString();
				long currentMills = System.currentTimeMillis();
				if(currentMills % 1000 == 0) {
					if (!chunkDownload) {
						totalFileSize = alreadyDownloadSize;
					}
					log.info("Download status info:{},alreadyDownloadSize:{},totalFileSize:{}", downloadPercentText, alreadyDownloadSize, totalFileSize);
				}
			}
			outputStream.flush();
		} catch (Exception e) {
			log.error("As parsing HttpEntity occur exception:{}.", e.getMessage());
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
			try {
				if (outputStream != null) {
					outputStream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	/**
	 * @param targetFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String targetFilePath, String remoteRequestUrl, HttpEntity httpEntity, long alreadyDownloadSize, int bufferSize, boolean chunkDownload) {
		parseHttpEntity(targetFilePath, remoteRequestUrl, httpEntity, alreadyDownloadSize, bufferSize, alreadyDownloadSize > 0L, chunkDownload);
	}

	/**
	 * @param targetFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String targetFilePath, String remoteRequestUrl, HttpEntity httpEntity, long alreadyDownloadSize, boolean chunkDownload) {
		parseHttpEntity(targetFilePath, remoteRequestUrl, httpEntity, alreadyDownloadSize, Constants.DEFAULT_BUFFER_SIZE, chunkDownload);
	}

	/**
	 * @param targetFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String targetFilePath, String remoteRequestUrl, HttpEntity httpEntity, int bufferSize, boolean appendWrite, boolean chunkDownload) {
		parseHttpEntity(targetFilePath, remoteRequestUrl, httpEntity, 0L, bufferSize, appendWrite, chunkDownload);
	}

	/**
	 * @param targetFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String targetFilePath, String remoteRequestUrl, HttpEntity httpEntity, boolean appendWrite, boolean chunkDownload) {
		parseHttpEntity(targetFilePath, remoteRequestUrl, httpEntity, Constants.DEFAULT_BUFFER_SIZE, appendWrite, chunkDownload);
	}

	/**
	 * @param localFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String localFilePath, String remoteRequestUrl, HttpEntity httpEntity, int bufferSize, boolean chunkDownload) {
		parseHttpEntity(localFilePath, remoteRequestUrl, httpEntity, bufferSize, false, chunkDownload);
	}

	/**
	 * @param localFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String localFilePath, String remoteRequestUrl, HttpEntity httpEntity, boolean chunkDownload) {
		parseHttpEntity(localFilePath, remoteRequestUrl, httpEntity, Constants.DEFAULT_BUFFER_SIZE, chunkDownload);
	}

	/**
	 * @param localFilePath
	 * @param httpEntity
	 * @description 解析HttpEntity
	 * @author yida
	 * @date 2024-09-19 17:15:31
	 */
	public static void parseHttpEntity(String localFilePath, String remoteRequestUrl, HttpEntity httpEntity) {
		parseHttpEntity(localFilePath, remoteRequestUrl, httpEntity, false);
	}

	public static String putUpRequestParameter(String getRequestURL, Map<String, String> parameterMap) {
		return putUpRequestParameter(getRequestURL, parameterMap, null);
	}

	public static String putUpRequestParameter(String getRequestURL, Map<String, String> parameterMap, String charset) {
		if (null == parameterMap || parameterMap.size() <= 0) {
			return getRequestURL;
		}
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		List<NameValuePair> nameValuePairList = map2NameValuePair(parameterMap, charset);
		try {
			String parameters = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, charset));
			if (getRequestURL.endsWith("?")) {
				return getRequestURL + parameters;
			}
			return getRequestURL + "?" + parameters;
		} catch (Exception e) {
			log.error("put up the request parameters,but occur exception. post request url:[{}],parameter:{},charset:[{}]",
					getRequestURL, JacksonUtils.toJSONString(parameterMap), charset);
			return null;
		}
	}

	public static void putUpRequestParameter(HttpGet httpGet, Map<String, String> parameterMap) {
		putUpRequestParameter(httpGet, parameterMap, null);
	}

	public static void putUpRequestParameter(HttpGet httpGet, Map<String, String> parameterMap, String charset) {
		if (null == parameterMap || parameterMap.size() <= 0) {
			return;
		}
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		List<NameValuePair> nameValuePairList = map2NameValuePair(parameterMap, charset);
		try {
			String parameters = EntityUtils.toString(new UrlEncodedFormEntity(nameValuePairList, charset));
			String requestUrl = httpGet.getURI().toString();
			httpGet.setURI(new URI(requestUrl + "?" + parameters));
		} catch (Exception e) {
			log.error("put up the request parameters,but occur exception. post request url:[{}],parameter:{},charset:[{}]",
					httpGet.getURI().toString(), JacksonUtils.toJSONString(parameterMap), charset);
		}
	}

	public static void putUpRequestParameter(HttpPost httpPost, Map<String, String> parameterMap) {
		putUpRequestParameter(httpPost, parameterMap, null);
	}

	public static void putUpRequestParameter(HttpPost httpPost, Map<String, String> parameterMap, String charset) {
		if (null == parameterMap || parameterMap.size() <= 0) {
			return;
		}
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		List<NameValuePair> nameValuePairList = map2NameValuePair(parameterMap, charset);
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList, charset));
		} catch (Exception e) {
			log.error("put up the request parameters,but occur exception. post request url:[{}],parameter:{},charset:[{}]",
					httpPost.getURI().toString(), JacksonUtils.toJSONString(parameterMap), charset);
		}
	}

	public static void putUpRequestHeader(HttpPost httpPost, Map<String, String> headerMap) {
		if (null == headerMap || headerMap.size() <= 0) {
			return;
		}
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			httpPost.setHeader(key, value);
		}
	}

	public static void putUpRequestHeader(HttpPost httpPost, Header[] headerArray) {
		if (null == headerArray || headerArray.length <= 0) {
			return;
		}
		httpPost.setHeaders(headerArray);
	}

	public static void putUpRequestHeader(HttpGet httpGet, Map<String, String> headerMap) {
		if (null == headerMap || headerMap.size() <= 0) {
			return;
		}
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			httpGet.setHeader(key, value);
		}
	}

	public static void putUpRequestHeader(HttpGet httpGet, Header[] headerArray) {
		if (null == headerArray || headerArray.length <= 0) {
			return;
		}
		httpGet.setHeaders(headerArray);
	}

	public static void setHeader(HttpPost httpPost, Header header) {
		if (null == header) {
			return;
		}
		httpPost.setHeader(header);
	}

	public static void setHeader(HttpPost httpPost, String headerKey, String headerValue) {
		if (StringUtils.isEmpty(headerKey)) {
			return;
		}
		httpPost.setHeader(headerKey, headerValue);
	}

	public static void setHeader(HttpGet httpGet, Header header) {
		if (null == header) {
			return;
		}
		httpGet.setHeader(header);
	}

	public static void setHeader(HttpGet httpGet, String headerKey, String headerValue) {
		if (StringUtils.isEmpty(headerKey)) {
			return;
		}
		httpGet.setHeader(headerKey, headerValue);
	}

	public static List<NameValuePair> map2NameValuePair(Map<String, String> parameterMap, String charset) {
		if (null == parameterMap || parameterMap.size() <= 0) {
			return null;
		}
		List<NameValuePair> nameValuePairList = new ArrayList<>();
		for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			//对中文参数进行编码，防止中文参数乱码
			val = URLUtils.encode(val, charset);
			NameValuePair nameValuePair = new BasicNameValuePair(key, val);
			nameValuePairList.add(nameValuePair);
		}
		return nameValuePairList;
	}

	public static Header[] map2HeaderArray(Map<String, String> headerMap, String charset) {
		if (null == headerMap || headerMap.size() <= 0) {
			return null;
		}
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		Header[] headerArray = new Header[headerMap.size()];
		int index = 0;
		for (Map.Entry<String, String> entry : headerMap.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			//application/x-www-form-urlencoded;charset=utf-8
			if (Constants.CONTENT_TYPE_HEADER_KEY.equalsIgnoreCase(key)) {
				if (StringUtils.isNotEmpty(val) && !val.contains(";charset=")) {
					val += ";charset=" + charset;
				}
			}
			Header header = new BasicHeader(key, val);
			headerArray[index++] = header;
		}
		return headerArray;
	}

	public static void setRequestConfig(HttpPost httpPost, RequestConfig requestConfig) {
		if (null != requestConfig) {
			httpPost.setConfig(requestConfig);
		}
	}
}

