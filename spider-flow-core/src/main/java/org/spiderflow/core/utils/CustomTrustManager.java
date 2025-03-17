package org.spiderflow.core.utils;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author yida
 * @package org.spiderflow.core.utils
 * @date 2024-09-23 16:25
 * @description Type your description over here.
 */
public class CustomTrustManager implements TrustManager, X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
