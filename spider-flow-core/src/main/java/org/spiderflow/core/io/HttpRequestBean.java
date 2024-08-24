package org.spiderflow.core.io;

import org.spiderflow.core.utils.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author yida
 * @package org.spiderflow.core.io
 * @date 2024-08-24 13:21
 * @description HTTP请求Bean(用于判断是否重复请求)
 */
public class HttpRequestBean {
	private String method;
	private String contentType;
	private String url;
	//当请求参数为JSON字符串时使用此参数
	private String requestBody;
	private Map<String, Object> requestParamMap;
	private Map<String, String> requestHeaderMap;
	private Map<String, String> requestCookieMap;
	//是否包含文件域
	private boolean hasFile;
	//文件域参数(key:表单, entry-->参数名,key:文件名, value:文件大小[单位:byte])
	private Map<String, Map<String, Long>> requestFileParamMap;

	//代理主机ip或域名
	private String proxyHost;
	//代理主机端口
	private int proxyPort;

	public HttpRequestBean() {
		this.requestParamMap = new LinkedHashMap<>();
		this.requestHeaderMap = new LinkedHashMap<>();
		this.requestCookieMap = new LinkedHashMap<>();
		this.requestFileParamMap = new LinkedHashMap<>();
	}

	public HttpRequestBean(String method, String contentType, String url, String requestBody,
						   Map<String, Object> requestParamMap,
						   Map<String, String> requestHeaderMap, Map<String, String> requestCookieMap,
						   Map<String, Map<String, Long>> requestFileParamMap, boolean hasFile,
						   String proxyHost, int proxyPort) {
		if (null == requestParamMap) {
			requestParamMap = new LinkedHashMap<>();
		}
		if (null == requestHeaderMap) {
			requestHeaderMap = new LinkedHashMap<>();
		}
		if (null == requestCookieMap) {
			requestCookieMap = new LinkedHashMap<>();
		}
		if (null == requestFileParamMap) {
			requestFileParamMap = new LinkedHashMap<>();
		}
		this.method = method;
		this.contentType = contentType;
		this.url = url;
		this.requestBody = requestBody;
		this.requestParamMap = requestParamMap;
		this.requestHeaderMap = requestHeaderMap;
		this.requestCookieMap = requestCookieMap;
		this.requestFileParamMap = requestFileParamMap;
		this.hasFile = hasFile;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
	}

	public static HttpRequestBean create() {
		return new HttpRequestBean();
	}

	public HttpRequestBean setHttpProxy(String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		return this;
	}

	public HttpRequestBean addRequestHeader(String headerName, String headerValue) {
		this.requestHeaderMap.put(headerName, headerValue);
		return this;
	}

	public HttpRequestBean addRequestParam(String paramName, Object paramValue) {
		this.requestParamMap.put(paramName, paramValue);
		return this;
	}

	public HttpRequestBean addRequestCookie(String cookieName, String cookieValue) {
		this.requestCookieMap.put(cookieName, cookieValue);
		return this;
	}

	public HttpRequestBean addRequestFileParam(String paramName, String fileName, long fileSize) {
		Map<String, Long> fileInfoMap = this.requestFileParamMap.get(paramName);
		if (null == fileInfoMap) {
			fileInfoMap = new LinkedHashMap<>();
		}
		fileInfoMap.put(fileName, fileSize);
		this.requestFileParamMap.put(paramName, fileInfoMap);
		return this;
	}

	public HttpRequestBean addRequestFileParam(String paramName, Map<String, Long> fileInfoMap) {
		if (null == fileInfoMap) {
			Map<String, Long> curFileInfoMap = this.requestFileParamMap.get(paramName);
			if (null == curFileInfoMap) {
				curFileInfoMap = new LinkedHashMap<>();
				this.requestFileParamMap.put(paramName, curFileInfoMap);
			}
			return this;
		}
		Map<String, Long> tempFileInfoMap = null;
		if (fileInfoMap instanceof LinkedHashMap) {
			tempFileInfoMap = fileInfoMap;
		} else {
			tempFileInfoMap = MapUtils.sortByKey(fileInfoMap);
		}
		Map<String, Long> curFileInfoMap = this.requestFileParamMap.get(paramName);
		if (null == curFileInfoMap) {
			curFileInfoMap = new LinkedHashMap<>();
		}
		curFileInfoMap.putAll(tempFileInfoMap);
		this.requestFileParamMap.put(paramName, curFileInfoMap);
		return this;
	}

	public HttpRequestBean addRequestHeader(Map<String, String> requestHeaderMap) {
		if (null == requestHeaderMap) {
			if (null == this.requestHeaderMap) {
				this.requestHeaderMap = new LinkedHashMap<>();
			}
			return this;
		}
		Map<String, String> tempRequestHeaderMap = null;
		if (requestHeaderMap instanceof LinkedHashMap) {
			tempRequestHeaderMap = requestHeaderMap;
		} else {
			tempRequestHeaderMap = MapUtils.sortByKey(requestHeaderMap);
		}
		this.requestHeaderMap.putAll(tempRequestHeaderMap);
		return this;
	}

	public HttpRequestBean addRequestParam(Map<String, Object> requestParamMap) {
		if (null == requestParamMap) {
			if (null == this.requestParamMap) {
				this.requestParamMap = new LinkedHashMap<>();
			}
			return this;
		}
		Map<String, Object> tempRequestParamMap = null;
		if (requestParamMap instanceof LinkedHashMap) {
			tempRequestParamMap = requestParamMap;
		} else {
			tempRequestParamMap = MapUtils.sortByKey(requestParamMap);
		}
		this.requestParamMap.putAll(tempRequestParamMap);
		return this;
	}

	public HttpRequestBean addRequestCookie(Map<String, String> requestCookieMap) {
		if (null == requestCookieMap) {
			if (null == this.requestCookieMap) {
				this.requestCookieMap = new LinkedHashMap<>();
			}
			return this;
		}
		Map<String, String> tempRequestCookieMap = null;
		if (requestCookieMap instanceof LinkedHashMap) {
			tempRequestCookieMap = requestCookieMap;
		} else {
			tempRequestCookieMap = MapUtils.sortByKey(requestCookieMap);
		}
		this.requestCookieMap.putAll(tempRequestCookieMap);
		return this;
	}

	public HttpRequestBean addRequestFileParam(Map<String, Map<String, Long>> requestFileParamMap) {
		if (null == requestFileParamMap) {
			if (null == this.requestFileParamMap) {
				this.requestFileParamMap = new LinkedHashMap<>();
			}
			return this;
		}
		if (null == this.requestFileParamMap) {
			this.requestFileParamMap = new LinkedHashMap<>();
		}
		Map<String, Map<String, Long>> tempRequestFileParamMap = null;
		if (requestFileParamMap instanceof LinkedHashMap) {
			tempRequestFileParamMap = requestFileParamMap;
		} else {
			tempRequestFileParamMap = MapUtils.sortByKey(requestFileParamMap);
		}
		this.requestFileParamMap.putAll(tempRequestFileParamMap);
		return this;
	}

	public HttpRequestBean removeRequestHeader(String headerName, String headerValue) {
		this.requestHeaderMap.remove(headerName, headerValue);
		return this;
	}

	public HttpRequestBean removeRequestParam(String paramName, Object paramValue) {
		this.requestParamMap.remove(paramName, paramValue);
		return this;
	}

	public HttpRequestBean removeRequestCookie(String cookieName, String cookieValue) {
		this.requestCookieMap.remove(cookieName, cookieValue);
		return this;
	}

	public HttpRequestBean removeRequestFileParam(String paramName, String fileName, long fileSize) {
		this.requestFileParamMap.remove(fileName, fileSize);
		return this;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public Map<String, Object> getRequestParamMap() {
		return requestParamMap;
	}

	public void setRequestParamMap(Map<String, Object> requestParamMap) {
		this.requestParamMap = requestParamMap;
	}

	public Map<String, String> getRequestHeaderMap() {
		return requestHeaderMap;
	}

	public void setRequestHeaderMap(Map<String, String> requestHeaderMap) {
		this.requestHeaderMap = requestHeaderMap;
	}

	public Map<String, String> getRequestCookieMap() {
		return requestCookieMap;
	}

	public void setRequestCookieMap(Map<String, String> requestCookieMap) {
		this.requestCookieMap = requestCookieMap;
	}

	public boolean isHasFile() {
		return hasFile;
	}

	public void setHasFile(boolean hasFile) {
		this.hasFile = hasFile;
	}

	public Map<String, Map<String, Long>> getRequestFileParamMap() {
		return requestFileParamMap;
	}

	public void setRequestFileParamMap(Map<String, Map<String, Long>> requestFileParamMap) {
		this.requestFileParamMap = requestFileParamMap;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}
}
