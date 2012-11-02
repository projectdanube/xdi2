package xdi2.xri2xdi.resolution;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XriResolver {

	public static final String DEFAULT_XRI_PROXY = "https://xri.net/";
	public static final String DEFAULT_USER_AGENT = "XDI2 Java library";

	private static final String QUERY = "_xrd_r=application/xrd+xml;sep=true;nodefault_t=true&_xrd_t=xri://$xdi!($v!1)";

	private static final Logger log = LoggerFactory.getLogger(XriResolver.class);
	private static final SAXReader saxReader = new SAXReader();

	private String xriProxy;
	private String userAgent;

	public XriResolver(String xriProxy, String userAgent) {

		this.xriProxy = xriProxy;
		this.userAgent = userAgent;
	}

	public XriResolver() {

		this(DEFAULT_XRI_PROXY, DEFAULT_USER_AGENT);
	}

	public XriResolutionResult resolve(String xri) throws XriResolutionException {

		// prepare URL

		URL url;

		try {

			url = new URL(this.xriProxy + xri + "?" + QUERY);
		} catch (MalformedURLException ex) {

			throw new XriResolutionException("Malformed URL: " + ex.getMessage(), ex);
		}

		log.debug("Using URL " + url.toString());

		// initialize and open connection

		log.debug("Connecting...");

		URLConnection connection;

		try {

			connection = url.openConnection();
		} catch (Exception ex) {

			throw new XriResolutionException("Cannot open connection: " + ex.getMessage(), ex);
		}

		HttpURLConnection http = (HttpURLConnection) connection;
		int responseCode;
		String responseMessage;

		try {

			http.setDoInput(true);
			http.setDoOutput(false);
			http.setRequestProperty("User-Agent", this.userAgent);
			http.setRequestMethod("GET");

			responseCode = http.getResponseCode();
			responseMessage = http.getResponseMessage();
		} catch (Exception ex) {

			throw new XriResolutionException("Cannot initialize HTTP transport: " + ex.getMessage(), ex);
		}

		// check response code

		if (responseCode >= 300) {

			throw new XriResolutionException("HTTP code " + responseCode + " received: " + responseMessage);
		}

		// read the XRD

		Document document;
		XriResolutionResult resolutionResult;

		try {

			document = saxReader.read(http.getInputStream());
			resolutionResult = XriResolutionResult.fromXriAndDocument(xri, document);
		} catch (Exception ex) {

			throw new XriResolutionException("Cannot parse XML document: " + ex.getMessage(), ex);
		}

		http.disconnect();

		// check result

		log.debug("Status: (" + resolutionResult.getStatusCode() + ") " + resolutionResult.getStatus());

		if (resolutionResult.getStatusCode() != 100 && resolutionResult.getStatusCode() != 241) {

			throw new XriResolutionException(resolutionResult.getStatus());
		}

		// done

		if (log.isDebugEnabled()) log.debug("Successfully received result, " + resolutionResult.toString());

		return resolutionResult;
	}
}
