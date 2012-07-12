package xdi2.xri2xdi.resolution;

import java.io.Serializable;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;

public class XriResolutionResult implements Serializable {

	private static final long serialVersionUID = 5566913185681521133L;

	private static final Namespace NAMESPACE_XRD = new Namespace(null, "xri://$xrd*($v*2.0)");
	private static final QName QNAME_STATUS = new QName("Status", NAMESPACE_XRD);
	private static final QName QNAME_CANONICALID = new QName("CanonicalID", NAMESPACE_XRD);
	private static final QName QNAME_SERVICE = new QName("Service", NAMESPACE_XRD);
	private static final QName QNAME_URI = new QName("URI", NAMESPACE_XRD);
	private static final QName QNAME_TYPE = new QName("Type", NAMESPACE_XRD);
	private static final String ATTRIBUTE_CODE = "code";

	public static final String STRING_TYPE_XDI = "xri://$xdi!($v!1)";

	private String xri;
	private String status;
	private int statusCode;
	private String inumber;
	private String xdiUri;

	private XriResolutionResult(String xri, String status, int statusCode, String inumber, String xdiUri) {

		this.xri = xri;
		this.status = status;
		this.statusCode = statusCode;
		this.inumber = inumber;
		this.xdiUri = xdiUri;
	}

	/**
	 * Parses a XriResolutionResult from a DOM4j XRD document.
	 * @return The XriResolutionResult.
	 */
	public static XriResolutionResult fromXriAndDocument(String xri, Document document) {

		Element rootElement = document.getRootElement();

		Element statusElement = rootElement.element(QNAME_STATUS);
		String status = statusElement == null ? null : statusElement.getTextTrim();
		int statusCode = statusElement == null ? -1 : Integer.parseInt(statusElement.attributeValue(ATTRIBUTE_CODE));

		Element canonicalIdElement = rootElement.element(QNAME_CANONICALID);
		String inumber = canonicalIdElement == null ? null : canonicalIdElement.getTextTrim();

		Element serviceElement = rootElement.element(QNAME_SERVICE);
		Element uriElement = serviceElement == null ? null : serviceElement.element(QNAME_URI);
		Element typeElement = serviceElement == null ? null : serviceElement.element(QNAME_TYPE);
		String uri = uriElement == null ? null : uriElement.getTextTrim();
		String type = typeElement == null ? null : typeElement.getTextTrim();

		String xdiUri = STRING_TYPE_XDI.equals(type) ? uri : null;

		return new XriResolutionResult(xri, status, statusCode, inumber, xdiUri);
	}

	public String getXri() {

		return this.xri;
	}

	public int getStatusCode() {

		return this.statusCode;
	}

	public String getStatus() {

		return this.status;
	}

	public String getInumber() {

		return this.inumber;
	}

	public String getXdiUri() {

		return this.xdiUri;
	}

	@Override
	public String toString() {

		return this.inumber + " / " + this.xdiUri;
	}
}
