package xdi2.client.http.ssl;

import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class XDI2X509TrustManager implements X509TrustManager {

	private static List<X509TrustManager> tms;

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  

		CertificateException ex = null;

		for (X509TrustManager tm : tms) {

			try {

				tm.checkClientTrusted(chain, authType);
				return;
			} catch (CertificateException ex2) {

				ex = ex2;
			}
		}

		if (ex != null) throw ex;
	}  


	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  

		CertificateException ex = null;

		for (X509TrustManager tm : tms) {

			try {

				tm.checkServerTrusted(chain, authType);
				return;
			} catch (CertificateException ex2) {

				ex = ex2;
			}
		}

		if (ex != null) throw ex;
	}  

	@Override
	public X509Certificate[] getAcceptedIssuers() {  

		List<X509Certificate> list = new ArrayList<X509Certificate> ();
		for (X509TrustManager tm : tms) list.addAll(Arrays.asList(tm.getAcceptedIssuers()));

		return list.toArray(new X509Certificate[list.size()]);
	}  

	public static void enable() throws Exception {

		tms = new ArrayList<X509TrustManager> ();

		// get default trust manager

		TrustManagerFactory tmf1 = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		tmf1.init((KeyStore) null);

		TrustManager tms1[] = tmf1.getTrustManagers();
		for (TrustManager tm : tms1) if (tm instanceof X509TrustManager) tms.add((X509TrustManager) tm);

		// create XDI2 trust manager

		KeyStore ks2 = KeyStore.getInstance("JKS");
		ks2.load(XDI2X509TrustManager.class.getResourceAsStream("cacerts"), "changeit".toCharArray());

		TrustManagerFactory tmf2 = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
		tmf2.init(ks2);

		TrustManager tms2[] = tmf2.getTrustManagers();
		for (TrustManager tm : tms2) if (tm instanceof X509TrustManager) tms.add((X509TrustManager) tm);

		// set trust managers

		SSLContext sslContext;

		sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { new XDI2X509TrustManager() }, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}
}
