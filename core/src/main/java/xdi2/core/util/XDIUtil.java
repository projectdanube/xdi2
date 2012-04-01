package xdi2.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public class XDIUtil {

	private XDIUtil() { }

	private static final Pattern PATTERN_DATA_URI = Pattern.compile("^data:(.*?),(.*)$");

	public static String dataXriSegmentToString(XRI3Segment xriSegment) {

		XRI3SubSegment xriSubSegment = (XRI3SubSegment) xriSegment.getFirstSubSegment();

		XRI3XRef xRef = (XRI3XRef) xriSubSegment.getXRef();
		if (xRef == null) throw new Xdi2RuntimeException("Invalid data URI: " + xriSubSegment);

		String iri = xRef.getIRI();		
		if (iri == null) throw new Xdi2RuntimeException("Invalid data URI: " + xriSubSegment);

		Matcher matcher = PATTERN_DATA_URI.matcher(iri);
		if (! matcher.matches()) throw new Xdi2RuntimeException("Invalid data URI: " + iri);

		String typeEncodingBase64 = matcher.group(1);
		String data = matcher.group(2);
		
		try {

			if (typeEncodingBase64.endsWith(";base64")) {

				return new String(Base64.decodeBase64(data.getBytes()), "UTF-8");
			} else {

				return data;
			}
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex);
		}
	}

	public static XRI3Segment stringToDataXriSegment(String string) {

		StringBuilder builder = new StringBuilder("data:");

		try {

			try {

				URI.create("data:," + string);

				builder.append(",");
				builder.append(string);
			} catch (IllegalArgumentException ex) {

				builder.append(";base64,");
				builder.append(new String(Base64.encodeBase64(string.getBytes("UTF-8")), "UTF-8"));
			}
		} catch (UnsupportedEncodingException ex) {

			throw new Xdi2RuntimeException(ex);
		}

		return new XRI3Segment("(" + builder.toString() + ")");
	}
}
