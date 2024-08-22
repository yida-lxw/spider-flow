package org.spiderflow.core.io;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.spiderflow.core.http.SpiderResponse;
import org.spiderflow.core.utils.JacksonUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * 响应对象包装类
 *
 * @author Administrator
 */
public class HttpResponse implements SpiderResponse {

	private Response response;

	private int statusCode;

	private String urlLink;

	private String htmlValue;

	private String titleName;

	private Map<String, Object> jsonMap;

	private List<Map<String, Object>> jsonList;

	public HttpResponse(Response response) {
		super();
		this.response = response;
		this.statusCode = response.statusCode();
		this.urlLink = response.url().toExternalForm();
	}

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getTitle() {
		if (titleName == null) {
			synchronized (this) {
				titleName = Jsoup.parse(getHtml()).title();
			}
		}
		return titleName;
	}

	@Override
	public String getHtml() {
		if (htmlValue == null) {
			synchronized (this) {
				htmlValue = response.body();
			}
		}
		return htmlValue;
	}

	@Override
	public Map<String, Object> getJsonMap() {
		if (jsonMap == null || jsonMap.size() <= 0) {
			jsonMap = JacksonUtils.json2Map(getHtml());
		}
		return jsonMap;
	}

	@Override
	public List<Map<String, Object>> getJsonList() {
		if (jsonList == null || jsonList.size() <= 0) {
			jsonList = JacksonUtils.json2ListMap(getHtml());
		}
		return jsonList;
	}

	@Override
	public Map<String, String> getCookies() {
		return response.cookies();
	}

	@Override
	public Map<String, String> getHeaders() {
		return response.headers();
	}

	@Override
	public byte[] getBytes() {
		return response.bodyAsBytes();
	}

	@Override
	public String getContentType() {
		return response.contentType();
	}

	@Override
	public void setCharset(String charset) {
		this.response.charset(charset);
	}

	@Override
	public String getUrl() {
		return urlLink;
	}

	@Override
	public InputStream getStream() {
		return response.bodyStream();
	}
}
