package xdi2.core.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

/**
 * Various utility methods for working with XDI.
 * 
 * @author markus
 */
public class XDIUtil {

	private static final Logger log = LoggerFactory.getLogger(XDIUtil.class);

	private static final Pattern PATTERN_DATA_URI = Pattern.compile("^(.*?),(.*)$");

	private XDIUtil() { }

	public static boolean isDataXriSegment(XRI3Segment xriSegment) {

		try {

			dataXriSegmentToString(xriSegment);
		} catch (Exception ex) {

			return false;
		}

		return true;
	}

	public static String dataXriSegmentToString(XRI3Segment xriSegment) {

		if (log.isTraceEnabled()) log.trace("Converting from data URI: " + xriSegment.toString());

		XRI3SubSegment xriSubSegment = (XRI3SubSegment) xriSegment.getFirstSubSegment();
		if (xriSubSegment == null) throw new Xdi2RuntimeException("Invalid data URI (no subsegment): " + xriSubSegment);

		XRI3XRef xRef = (XRI3XRef) xriSubSegment.getXRef();
		if (xRef == null) throw new Xdi2RuntimeException("Invalid data URI (no xref): " + xriSubSegment);

		String iri = xRef.getIRI();		
		if (iri == null) throw new Xdi2RuntimeException("Invalid data URI (no iri): " + xriSubSegment);

		URI uri = URI.create(iri);
		if (! uri.getScheme().equals("data")) throw new Xdi2RuntimeException("Invalid data URI scheme: " + uri);

		Matcher matcher = PATTERN_DATA_URI.matcher(uri.getSchemeSpecificPart());
		if (! matcher.matches()) throw new Xdi2RuntimeException("Invalid data URI: " + uri);

		String typeEncodingBase64 = matcher.group(1);
		String data = matcher.group(2);

		try {

			if (typeEncodingBase64.endsWith(";base64")) {

				return new String(Base64.decodeBase64(data.getBytes()), "UTF-8");
			} else {

				return data;
			}
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex);
		}
	}

	public static XRI3Segment stringToDataXriSegment(String string, boolean base64) {

		if (log.isTraceEnabled()) log.trace("Converting to data URI: " + string);

		String dataUri;

		try {

			if (base64) {

				dataUri = makeDataUri(";base64," + new String(Base64.encodeBase64(string.getBytes("UTF-8")), "UTF-8"));
			} else {

				dataUri = makeDataUri("," + string);
			}
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex);
		}

		return new XRI3Segment("(" + dataUri + ")");
	}

	private static String makeDataUri(String string) throws URISyntaxException {

		return 
				new URI("data", string, null).toString()
				.replace("/", "%2F")
				.replace("(", "%28")
				.replace(")", "%29");
	}
}
