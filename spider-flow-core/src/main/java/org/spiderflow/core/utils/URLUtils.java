package org.spiderflow.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spiderflow.core.constants.Constants;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author yida
 * @date 2023-08-02 11:15
 * @description URL编解码工具类
 */
public class URLUtils {
	private static final Logger log = LoggerFactory.getLogger(URLUtils.class);

	public static String decode(String sourceValue) {
		return decode(sourceValue, null);
	}

	public static String decode(String sourceValue, String charset) {
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		try {
			return URLDecoder.decode(sourceValue, charset);
		} catch (UnsupportedEncodingException e) {
			log.error("Exception occurred as decoding the value:[{}].", sourceValue);
			return sourceValue;
		}
	}

	public static String encode(String sourceValue) {
		return encode(sourceValue, null);
	}

	public static String encode(String sourceValue, String charset) {
		if (StringUtils.isEmpty(charset)) {
			charset = Constants.DEFAULT_CHARSET;
		}
		try {
			return URLEncoder.encode(sourceValue, charset);
		} catch (UnsupportedEncodingException e) {
			log.error("Exception occurred as encoding the value:[{}].", sourceValue);
			return sourceValue;
		}
	}
}
