package org.spiderflow.core.utils;

import org.spiderflow.core.http.TLSVersion;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-08-20 16:23
 * @description Type your description over here.
 */
public class SSLHelper {
	public static SSLSocketFactory buildSSLSocketFactory() {
		return buildSSLSocketFactory(null);
	}

	public static SSLSocketFactory buildSSLSocketFactory(TLSVersion tlsVersion) {
		TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new X509Certificate[0];
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}};

		if (null == tlsVersion) {
			tlsVersion = TLSVersion.TLS_VERSION_DEFAULT;
		}
		try {
			SSLContext sslContext = SSLContext.getInstance(tlsVersion.getVersion());
			sslContext.init(null, trustManagers, new java.security.SecureRandom());
			SSLSocketFactory result = sslContext.getSocketFactory();
			return result;
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException("Failed to create a SSL socket factory", e);
		}
	}
}
