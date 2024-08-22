package org.spiderflow.core.http;

import org.spiderflow.core.annotation.Comment;
import org.spiderflow.core.annotation.Example;
import org.spiderflow.core.utils.JacksonUtils;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SpiderResponse {

	@Comment("获取返回状态码")
	@Example("${resp.statusCode}")
	int getStatusCode();

	@Comment("获取网页标题")
	@Example("${resp.title}")
	String getTitle();

	@Comment("获取网页html")
	@Example("${resp.html}")
	String getHtml();

	@Comment("获取jsonMap")
	@Example("${resp.jsonMap}")
	default Map<String, Object> getJsonMap() {
		return JacksonUtils.json2Map(getHtml());
	}

	@Comment("获取jsonList")
	@Example("${resp.jsonList}")
	default List<Map<String, Object>> getJsonList() {
		return JacksonUtils.json2ListMap(getHtml());
	}

	@Comment("获取cookies")
	@Example("${resp.cookies}")
	Map<String, String> getCookies();

	@Comment("获取headers")
	@Example("${resp.headers}")
	Map<String, String> getHeaders();

	@Comment("获取byte[]")
	@Example("${resp.bytes}")
	byte[] getBytes();

	@Comment("获取ContentType")
	@Example("${resp.contentType}")
	String getContentType();

	@Comment("获取当前url")
	@Example("${resp.url}")
	String getUrl();

	@Example("${resp.setCharset('UTF-8')}")
	default void setCharset(String charset) {

	}

	@Example("${resp.stream}")
	default InputStream getStream() {
		return null;
	}
}
