package xdi2.client.impl.http.ssl;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XDI2X509TrustManager implements X509TrustManager {

	private static final Logger log = LoggerFactory.getLogger(XDI2X509TrustManager.class);

	private static List<X509TrustManager> tms;

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  

		CertificateException cex = null;
		RuntimeException rex = null;

		for (X509TrustManager tm : tms) {

			try {

				log.debug("Checking server certificate chain " + chain + " against " + tm);
				tm.checkClientTrusted(chain, authType);
				return;
			} catch (CertificateException ex) {

				cex = ex;
			} catch (RuntimeException ex) {

				rex = ex;
			}
		}

		if (cex != null) throw cex;
		if (rex != null) throw rex;
	}  

	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  

		CertificateException cex = null;
		RuntimeException rex = null;

		for (X509TrustManager tm : tms) {

			try {

				log.debug("Checking server certificate chain " + chain + " against " + tm);
				tm.checkServerTrusted(chain, authType);
				return;
			} catch (CertificateException ex) {

				cex = ex;
			} catch (RuntimeException ex) {

				rex = ex;
			}
		}

		if (cex != null) throw cex;
		if (rex != null) throw rex;
	}  

	@Override
	public X509Certificate[] getAcceptedIssuers() {  

		List<X509Certificate> list = new ArrayList<X509Certificate> ();
		for (X509TrustManager tm : tms) list.addAll(Arrays.asList(tm.getAcceptedIssuers()));

		return list.toArray(new X509Certificate[list.size()]);
	}  

	public static void enable() {

		try {

			tms = new ArrayList<X509TrustManager> ();

			// get default trust manager

			TrustManagerFactory tmf1 = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			tmf1.init((KeyStore) null);

			TrustManager tms1[] = tmf1.getTrustManagers();
			for (TrustManager tm : tms1) if (tm instanceof X509TrustManager) tms.add((X509TrustManager) tm);

			// create XDI2 trust manager

			KeyStore ks2 = KeyStore.getInstance("JKS", "SUN");
			ks2.load(XDI2X509TrustManager.class.getResourceAsStream("cacerts.jks"), "changeit".toCharArray());

			TrustManagerFactory tmf2 = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			tmf2.init(ks2);

			TrustManager tms2[] = tmf2.getTrustManagers();
			for (TrustManager tm : tms2) if (tm instanceof X509TrustManager) tms.add((X509TrustManager) tm);

			// set trust managers

			SSLContext sslContext;

			sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { new XDI2X509TrustManager() }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
}
