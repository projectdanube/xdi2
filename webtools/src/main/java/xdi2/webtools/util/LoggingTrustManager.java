package xdi2.webtools.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * DO NOT USE THIS IN A PRODUCTION ENVIRONMENT !!
 */
public class LoggingTrustManager implements X509TrustManager, HostnameVerifier {

	private StringWriter stringWriter;
	private PrintWriter printWriter;

	private static X509TrustManager defaultX509TrustManager;  
	private static HostnameVerifier defaultHostnameVerifier;  

	static {

		try {

			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
			tmf.init((KeyStore) null);
			TrustManager tms[] = tmf.getTrustManagers();  

			defaultX509TrustManager = null;

			for (int i = 0; i < tms.length; i++) {  

				if (tms[i] instanceof X509TrustManager) defaultX509TrustManager = (X509TrustManager) tms[i];  
			}  

			if (defaultX509TrustManager == null) throw new Exception("Default TrustManager not found.");  

			defaultHostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public LoggingTrustManager() throws Exception {  

		this.stringWriter = new StringWriter();
		this.printWriter = new PrintWriter(this.stringWriter);

		SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { this }, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(this);
	}  

	@Override
	public X509Certificate[] getAcceptedIssuers() { return null; }

	@Override
	public void checkClientTrusted(X509Certificate[] certs, String authType) {

		try {

			defaultX509TrustManager.checkClientTrusted(certs, authType);
		} catch (Exception ex) {

			this.printWriter.println("!!! TLS ERROR: " + ex.getMessage());
		}
	}

	@Override
	public void checkServerTrusted(X509Certificate[] certs, String authType) {

		try {

			defaultX509TrustManager.checkServerTrusted(certs, authType);
		} catch (Exception ex) {

			this.printWriter.println("!!! TLS ERROR: " + ex.getMessage());
		}
	}

	@Override
	public boolean verify(String hostname, SSLSession session) {

		if (! defaultHostnameVerifier.verify(hostname, session)) {

			this.printWriter.println("!!! TLS ERROR: Unable to verify: " + hostname);
		}

		return true;
	}

	public static void disable() {

		try {

			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, new TrustManager[] { defaultX509TrustManager }, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			HttpsURLConnection.setDefaultHostnameVerifier(defaultHostnameVerifier);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	public StringBuffer getBuffer() {

		return this.stringWriter.getBuffer();
	}
}
