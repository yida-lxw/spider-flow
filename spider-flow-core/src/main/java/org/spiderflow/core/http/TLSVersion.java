package org.spiderflow.core.http;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yida
 * @package org.spiderflow.core.http
 * @date 2024-08-20 16:30
 * @description TLS协议版本
 */
public enum TLSVersion {
	SSL(0, TLSVersion.TLS_VERSION_SSL),
	TLS_V1_0(1, TLSVersion.TLS_VERSION_V1_0),
	TLS_V1_1(2, TLSVersion.TLS_VERSION_V1_1),
	TLS_V1_2(3, TLSVersion.TLS_VERSION_V1_2),
	TLS_V1_3(4, TLSVersion.TLS_VERSION_V1_3);

	private int code;
	private String version;

	public static final TLSVersion TLS_VERSION_DEFAULT = TLSVersion.SSL;

	public static final String TLS_VERSION_SSL = "SSL";
	public static final String TLS_VERSION_V1_0 = "TLSv1.0";
	public static final String TLS_VERSION_V1_1 = "TLSv1.1";
	public static final String TLS_VERSION_V1_2 = "TLSv1.2";
	public static final String TLS_VERSION_V1_3 = "TLSv1.3";

	private static final Set<String> TLS_VERSION_SET = new HashSet<>();

	static {
		TLS_VERSION_SET.add(TLS_VERSION_SSL);
		TLS_VERSION_SET.add(TLS_VERSION_V1_0);
		TLS_VERSION_SET.add(TLS_VERSION_V1_1);
		TLS_VERSION_SET.add(TLS_VERSION_V1_2);
		TLS_VERSION_SET.add(TLS_VERSION_V1_3);
	}

	public static TLSVersion of(String tlsVersion) {
		if (StringUtils.isEmpty(tlsVersion)) {
			throw new IllegalArgumentException("The parameter tlsVersion MUST not be NULL or empty.");
		}
		if (!TLS_VERSION_SET.contains(tlsVersion)) {
			throw new IllegalArgumentException("The parameter tlsVersion MUST be in the TLS_VERSION_SET.");
		}
		if (TLSVersion.TLS_VERSION_SSL.equals(tlsVersion)) {
			return SSL;
		}
		if (TLSVersion.TLS_VERSION_V1_0.equals(tlsVersion)) {
			return TLS_V1_0;
		}
		if (TLSVersion.TLS_VERSION_V1_1.equals(tlsVersion)) {
			return TLS_V1_1;
		}
		if (TLSVersion.TLS_VERSION_V1_2.equals(tlsVersion)) {
			return TLS_V1_2;
		}
		if (TLSVersion.TLS_VERSION_V1_3.equals(tlsVersion)) {
			return TLS_V1_3;
		}
		return SSL;
	}

	public static boolean containsThis(String tlsVersion) {
		return TLS_VERSION_SET.contains(tlsVersion);
	}

	TLSVersion(int code, String version) {
		this.code = code;
		this.version = version;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
