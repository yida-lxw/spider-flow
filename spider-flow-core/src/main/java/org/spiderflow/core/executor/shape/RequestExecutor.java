package org.spiderflow.core.executor.shape;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.Funnels;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.context.CookieContext;
import org.spiderflow.core.context.SpiderContext;
import org.spiderflow.core.executor.ShapeExecutor;
import org.spiderflow.core.executor.function.MD5FunctionExecutor;
import org.spiderflow.core.expression.Grammerable;
import org.spiderflow.core.http.SpiderResponse;
import org.spiderflow.core.io.HttpRequest;
import org.spiderflow.core.io.HttpRequestBean;
import org.spiderflow.core.io.HttpResponse;
import org.spiderflow.core.listener.SpiderListener;
import org.spiderflow.core.model.Grammer;
import org.spiderflow.core.model.SpiderNode;
import org.spiderflow.core.utils.ExpressionUtils;
import org.spiderflow.core.utils.JacksonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 请求执行器
 *
 * @author Administrator
 */
@Component
public class RequestExecutor implements ShapeExecutor, Grammerable, SpiderListener {
	private static final Logger logger = LoggerFactory.getLogger(RequestExecutor.class);

	public static final String SLEEP = "sleep";

	public static final String URL = "url";

	public static final String PROXY = "proxy";

	public static final String REQUEST_METHOD = "method";

	public static final String PARAMETER_NAME = "parameter-name";

	public static final String PARAMETER_VALUE = "parameter-value";

	public static final String COOKIE_NAME = "cookie-name";

	public static final String COOKIE_VALUE = "cookie-value";

	public static final String PARAMETER_FORM_NAME = "parameter-form-name";

	public static final String PARAMETER_FORM_VALUE = "parameter-form-value";

	public static final String PARAMETER_FORM_FILENAME = "parameter-form-filename";

	public static final String PARAMETER_FORM_TYPE = "parameter-form-type";

	public static final String BODY_TYPE = "body-type";

	public static final String BODY_CONTENT_TYPE = "body-content-type";

	public static final String REQUEST_BODY = "request-body";

	public static final String HEADER_NAME = "header-name";

	public static final String HEADER_VALUE = "header-value";

	public static final String TIMEOUT = "timeout";

	public static final String RETRY_COUNT = "retryCount";

	public static final String RETRY_INTERVAL = "retryInterval";

	public static final String RESPONSE_CHARSET = "response-charset";

	public static final String FOLLOW_REDIRECT = "follow-redirect";

	public static final String TLS_VALIDATE = "tls-validate";

	public static final String LAST_EXECUTE_TIME = "__last_execute_time_";

	public static final String COOKIE_AUTO_SET = "cookie-auto-set";

	//自动去重
	public static final String AUTO_DEDUPLICATE = "auto-deduplicate";

	public static final String BLOOM_FILTER_KEY = "_bloomfilter";

	@Value("${spider.workspace}")
	private String workspcace;

	@Value("${spider.bloomfilter.capacity:5000000}")
	private Integer capacity;

	@Value("${spider.bloomfilter.error-rate:0.00001}")
	private Double errorRate;

	/**
	 * 是否为重试后仍然失败的请求记录日志
	 */
	@Value("${spider.record-failed-request-after-retry:false}")
	private boolean recordFailedRequestAfterRetry;

	@Override
	public String supportShape() {
		return "request";
	}

	@PostConstruct
	void init() {
		//允许设置被限制的请求头
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
	}

	@Override
	public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
		CookieContext cookieContext = context.getCookieContext();
		String sleepCondition = node.getStringJsonValue(SLEEP);
		if (StringUtils.isNotBlank(sleepCondition)) {
			try {
				Object value = ExpressionUtils.execute(sleepCondition, variables);
				if (value != null) {
					long sleepTime = NumberUtils.toLong(value.toString(), 0L);
					synchronized (node.getNodeId().intern()) {
						//实际等待时间 = 上次执行时间 + 睡眠时间 - 当前时间
						Long lastExecuteTime = context.get(LAST_EXECUTE_TIME + node.getNodeId(), 0L);
						if (lastExecuteTime != 0) {
							sleepTime = lastExecuteTime + sleepTime - System.currentTimeMillis();
						}
						if (sleepTime > 0) {
							context.pause(node.getNodeId(), "common", SLEEP, sleepTime);
							logger.debug("设置延迟时间:{}ms", sleepTime);
							Thread.sleep(sleepTime);
						}
						context.put(LAST_EXECUTE_TIME + node.getNodeId(), System.currentTimeMillis());
					}
				}
			} catch (Throwable t) {
				logger.error("设置延迟时间失败", t);
			}
		}
		BloomFilter<String> bloomFilter = null;
		//重试次数
		int retryCount = NumberUtils.toInt(node.getStringJsonValue(RETRY_COUNT), 0) + 1;
		//重试间隔时间，单位毫秒
		int retryInterval = NumberUtils.toInt(node.getStringJsonValue(RETRY_INTERVAL), 0);
		boolean successed = false;
		for (int i = 0; i < retryCount && !successed; i++) {
			HttpRequest request = HttpRequest.create();
			HttpRequestBean httpRequestBean = HttpRequestBean.create();
			//设置请求url
			String url = null;
			try {
				url = ExpressionUtils.execute(node.getStringJsonValue(URL), variables).toString();
			} catch (Exception e) {
				logger.error("设置请求url出错，异常信息", e);
				ExceptionUtils.wrapAndThrow(e);
			}

			context.pause(node.getNodeId(), "common", URL, url);
			logger.info("设置请求url:{}", url);
			request.url(url);
			httpRequestBean.setUrl(url);
			//设置请求超时时间
			int timeout = NumberUtils.toInt(node.getStringJsonValue(TIMEOUT), 60000);
			logger.debug("设置请求超时时间:{}", timeout);
			request.timeout(timeout);

			String method = Objects.toString(node.getStringJsonValue(REQUEST_METHOD), "GET");
			//设置请求方法
			request.method(method);
			httpRequestBean.setMethod(method.toLowerCase());
			logger.debug("设置请求方法:{}", method);

			//是否跟随重定向
			boolean followRedirects = !"0".equals(node.getStringJsonValue(FOLLOW_REDIRECT));
			request.followRedirect(followRedirects);
			logger.debug("设置跟随重定向：{}", followRedirects);

			//是否验证TLS证书,默认是验证
			if ("0".equals(node.getStringJsonValue(TLS_VALIDATE))) {
				request.validateTLSCertificates(false);
				logger.debug("设置TLS证书验证：{}", false);
			}
			SpiderNode root = context.getRootNode();
			//设置请求header
			setRequestHeader(root, request, httpRequestBean, root.getListJsonValue(HEADER_NAME, HEADER_VALUE), context, variables);
			setRequestHeader(node, request, httpRequestBean, node.getListJsonValue(HEADER_NAME, HEADER_VALUE), context, variables);

			//设置全局Cookie
			Map<String, String> cookies = getRequestCookie(root, httpRequestBean, root.getListJsonValue(COOKIE_NAME, COOKIE_VALUE), context, variables);
			if (!cookies.isEmpty()) {
				logger.info("设置全局Cookie：{}", cookies);
				request.cookies(cookies);
			}
			//设置自动管理的Cookie
			boolean cookieAutoSet = !"0".equals(node.getStringJsonValue(COOKIE_AUTO_SET));
			if (cookieAutoSet && !cookieContext.isEmpty()) {
				context.pause(node.getNodeId(), COOKIE_AUTO_SET, COOKIE_AUTO_SET, cookieContext);
				request.cookies(cookieContext);
				httpRequestBean.addRequestCookie(cookieContext);
				logger.info("自动设置Cookie：{}", cookieContext);
			}
			//设置本节点Cookie
			cookies = getRequestCookie(node, httpRequestBean, node.getListJsonValue(COOKIE_NAME, COOKIE_VALUE), context, variables);
			if (!cookies.isEmpty()) {
				request.cookies(cookies);
				logger.debug("设置Cookie：{}", cookies);
			}
			if (cookieAutoSet) {
				cookieContext.putAll(cookies);
			}

			String bodyType = node.getStringJsonValue(BODY_TYPE);
			List<InputStream> streams = null;
			if ("raw".equals(bodyType)) {
				String contentType = node.getStringJsonValue(BODY_CONTENT_TYPE);
				request.contentType(contentType);
				httpRequestBean.setContentType(contentType);
				try {
					Object requestBody = ExpressionUtils.execute(node.getStringJsonValue(REQUEST_BODY), variables);
					context.pause(node.getNodeId(), "request-body", REQUEST_BODY, requestBody);
					if (null != requestBody) {
						request.data(requestBody);
						httpRequestBean.setRequestBody(requestBody.toString());
						logger.info("设置请求Body:{}", requestBody);
					}
				} catch (Exception e) {
					logger.debug("设置请求Body出错", e);
				}
			} else if ("form-data".equals(bodyType)) {
				List<Map<String, String>> formParameters = node.getListJsonValue(PARAMETER_FORM_NAME, PARAMETER_FORM_VALUE, PARAMETER_FORM_TYPE, PARAMETER_FORM_FILENAME);
				streams = setRequestFormParameter(node, request, httpRequestBean, formParameters, context, variables);
			} else {
				//设置请求参数
				setRequestParameter(root, request, httpRequestBean, root.getListJsonValue(PARAMETER_NAME, PARAMETER_VALUE), context, variables);
				setRequestParameter(node, request, httpRequestBean, node.getListJsonValue(PARAMETER_NAME, PARAMETER_VALUE), context, variables);
			}
			//设置代理
			String proxy = node.getStringJsonValue(PROXY);
			if (StringUtils.isNotBlank(proxy)) {
				try {
					Object value = ExpressionUtils.execute(proxy, variables);
					context.pause(node.getNodeId(), "common", PROXY, value);
					if (value != null) {
						String[] proxyArr = value.toString().split(":");
						if (proxyArr.length == 2) {
							String proxyHost = proxyArr[0];
							int proxyPort = Integer.parseInt(proxyArr[1]);
							request.proxy(proxyHost, proxyPort);
							httpRequestBean.setHttpProxy(proxyHost, proxyPort);
							logger.info("设置代理：{}", proxy);
						}
					}
				} catch (Exception e) {
					logger.error("设置代理出错，异常信息:{}", e);
				}
			}

			//若开启了自动去重
			if ("1".equalsIgnoreCase(node.getStringJsonValue(AUTO_DEDUPLICATE, "0"))) {
				bloomFilter = createBloomFilter(context);
				synchronized (bloomFilter) {
					String httpRequestJSON = JacksonUtils.toJSONString(httpRequestBean);
					if (bloomFilter.mightContain(MD5FunctionExecutor.string(httpRequestJSON))) {
						logger.info("过滤重复HTTP请求:{}", httpRequestJSON);
						return;
					}
				}
			}

			Throwable exception = null;
			try {
				HttpResponse response = request.execute();
				successed = response.getStatusCode() == 200;
				if (successed) {
					if (bloomFilter != null) {
						synchronized (bloomFilter) {
							bloomFilter.put(MD5FunctionExecutor.string(url));
						}
					}
					String charset = node.getStringJsonValue(RESPONSE_CHARSET);
					if (StringUtils.isNotBlank(charset)) {
						response.setCharset(charset);
						logger.debug("设置response charset:{}", charset);
					}
					//cookie存入cookieContext
					cookieContext.putAll(response.getCookies());
					//结果存入变量
					variables.put("resp", response);
				}
			} catch (IOException e) {
				successed = false;
				exception = e;
			} finally {
				if (streams != null) {
					for (InputStream is : streams) {
						try {
							is.close();
						} catch (Exception e) {
						}
					}
				}
				if (!successed) {
					if (i + 1 < retryCount) {
						if (retryInterval > 0) {
							try {
								Thread.sleep(retryInterval);
							} catch (InterruptedException ignored) {
							}
						}
						logger.info("第{}次重试:{}", i + 1, url);
					} else {
						//记录重试后仍访问失败的请求日志
						if (recordFailedRequestAfterRetry && context.getFlowId() != null) {
							File file = new File(workspcace, context.getFlowId() + File.separator + "logs" + File.separator + "access_error.log");
							try {
								File directory = file.getParentFile();
								if (!directory.exists()) {
									directory.mkdirs();
								}
								String recordRequestJSON = JacksonUtils.toJSONString(httpRequestBean);
								FileUtils.write(file, recordRequestJSON + "\r\n", "UTF-8", true);
							} catch (IOException ignored) {
							}
						}
						logger.error("请求{}出错,异常信息:{}", url, exception);
					}
				}
			}
		}
	}

	private List<InputStream> setRequestFormParameter(SpiderNode node, HttpRequest request, HttpRequestBean httpRequestBean, List<Map<String, String>> parameters, SpiderContext context, Map<String, Object> variables) {
		List<InputStream> streams = new ArrayList<>();
		if (parameters != null) {
			for (Map<String, String> nameValue : parameters) {
				Object value;
				String parameterName = nameValue.get(PARAMETER_FORM_NAME);
				if (StringUtils.isNotBlank(parameterName)) {
					String parameterValue = nameValue.get(PARAMETER_FORM_VALUE);
					String parameterType = nameValue.get(PARAMETER_FORM_TYPE);
					String parameterFilename = nameValue.get(PARAMETER_FORM_FILENAME);
					boolean hasFile = "file".equals(parameterType);
					try {
						value = ExpressionUtils.execute(parameterValue, variables);
						if (hasFile) {
							InputStream stream = null;
							if (value instanceof byte[]) {
								stream = new ByteArrayInputStream((byte[]) value);
							} else if (value instanceof String) {
								stream = new ByteArrayInputStream(((String) value).getBytes());
							} else if (value instanceof InputStream) {
								stream = (InputStream) value;
							}
							if (stream != null) {
								streams.add(stream);
								request.data(parameterName, parameterFilename, stream);
								httpRequestBean.addRequestFileParam(parameterName, parameterFilename, Long.valueOf(stream.available()));
								context.pause(node.getNodeId(), "request-body", parameterName, parameterFilename);
								logger.info("设置请求参数：{}={}", parameterName, parameterFilename);
							} else {
								logger.warn("设置请求参数：{}失败，无二进制内容", parameterName);
							}
						} else {
							request.data(parameterName, value);
							httpRequestBean.addRequestParam(parameterName, value);
							context.pause(node.getNodeId(), "request-body", parameterName, value);
							logger.info("设置请求参数：{}={}", parameterName, value);
						}
					} catch (Exception e) {
						logger.error("设置请求参数：{}出错,异常信息:{}", parameterName, e);
					}
				}
			}
		}
		return streams;
	}

	private Map<String, String> getRequestCookie(SpiderNode node, HttpRequestBean httpRequestBean, List<Map<String, String>> cookies, SpiderContext context, Map<String, Object> variables) {
		Map<String, String> cookieMap = new HashMap<>();
		if (cookies != null) {
			for (Map<String, String> nameValue : cookies) {
				Object value;
				String cookieName = nameValue.get(COOKIE_NAME);
				if (StringUtils.isNotBlank(cookieName)) {
					String cookieValue = nameValue.get(COOKIE_VALUE);
					try {
						value = ExpressionUtils.execute(cookieValue, variables);
						if (value != null) {
							String cookieVal = value.toString();
							cookieMap.put(cookieName, cookieVal);
							httpRequestBean.addRequestCookie(cookieName, cookieVal);
							context.pause(node.getNodeId(), "request-cookie", cookieName, cookieVal);
							logger.info("设置请求Cookie：{}={}", cookieName, cookieVal);
						}
					} catch (Exception e) {
						logger.error("设置请求Cookie：{}出错,异常信息：{}", cookieName, e);
					}
				}
			}
		}
		return cookieMap;
	}

	private void setRequestParameter(SpiderNode node, HttpRequest request, HttpRequestBean httpRequestBean, List<Map<String, String>> parameters, SpiderContext context, Map<String, Object> variables) {
		if (parameters != null && !parameters.isEmpty()) {
			for (Map<String, String> nameValue : parameters) {
				Object value = null;
				String parameterName = nameValue.get(PARAMETER_NAME);
				if (StringUtils.isNotBlank(parameterName)) {
					String parameterValue = nameValue.get(PARAMETER_VALUE);
					try {
						value = ExpressionUtils.execute(parameterValue, variables);
						context.pause(node.getNodeId(), "request-parameter", parameterName, value);
						logger.info("设置请求参数：{}={}", parameterName, value);
					} catch (Exception e) {
						logger.error("设置请求参数：{}出错,异常信息：{}", parameterName, e);
					}
					request.data(parameterName, value);
					httpRequestBean.addRequestParam(parameterName, value);
				}
			}
		}
	}

	private void setRequestHeader(SpiderNode node, HttpRequest request, HttpRequestBean httpRequestBean, List<Map<String, String>> headers, SpiderContext context, Map<String, Object> variables) {
		if (headers != null) {
			for (Map<String, String> nameValue : headers) {
				Object value = null;
				String headerName = nameValue.get(HEADER_NAME);
				if (StringUtils.isNotBlank(headerName)) {
					String headerValue = nameValue.get(HEADER_VALUE);
					try {
						value = ExpressionUtils.execute(headerValue, variables);
						context.pause(node.getNodeId(), "request-header", headerName, value);
						logger.info("设置请求Header：{}={}", headerName, value);
					} catch (Exception e) {
						logger.error("设置请求Header：{}出错,异常信息：{}", headerName, e);
					}
					request.header(headerName, value);
					httpRequestBean.addRequestHeader(headerName, (null == value) ? "" : value.toString());
				}
			}
		}
	}

	@Override
	public List<Grammer> grammers() {
		List<Grammer> grammers = Grammer.findGrammers(SpiderResponse.class, "resp", "SpiderResponse", false);
		Grammer grammer = new Grammer();
		grammer.setFunction("resp");
		grammer.setComment("抓取结果");
		grammer.setOwner("SpiderResponse");
		grammers.add(grammer);
		return grammers;
	}

	@Override
	public void beforeStart(SpiderContext context) {

	}

	private BloomFilter<String> createBloomFilter(SpiderContext context) {
		BloomFilter<String> filter = context.get(BLOOM_FILTER_KEY);
		if (filter == null) {
			Funnel<CharSequence> funnel = Funnels.stringFunnel(Charset.forName("UTF-8"));
			String fileName = context.getFlowId() + File.separator + "url.bf";
			File file = new File(workspcace, fileName);
			if (file.exists()) {
				try (FileInputStream fis = new FileInputStream(file)) {
					filter = BloomFilter.readFrom(fis, funnel);
				} catch (IOException e) {
					logger.error("读取布隆过滤器出错", e);
				}

			} else {
				filter = BloomFilter.create(funnel, capacity, errorRate);
			}
			context.put(BLOOM_FILTER_KEY, filter);
		}
		return filter;
	}

	@Override
	public void afterEnd(SpiderContext context) {
		BloomFilter<String> filter = context.get(BLOOM_FILTER_KEY);
		if (filter != null) {
			File file = new File(workspcace, context.getFlowId() + File.separator + "url.bf");
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			try (FileOutputStream fos = new FileOutputStream(file)) {
				filter.writeTo(fos);
				fos.flush();
			} catch (IOException e) {
				logger.error("保存布隆过滤器出错", e);
			}
		}
	}
}
