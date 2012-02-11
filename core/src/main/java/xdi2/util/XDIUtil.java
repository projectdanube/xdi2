package xdi2.util;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import xdi2.exceptions.Xdi2RuntimeException;
import xdi2.xri3.impl.XRI3SubSegment;

public class XDIUtil {

	private static final Random random;

	static {

		random = new Random();
	}

	private XDIUtil() { }

	public static XRI3SubSegment newInumber() {

		final String hex = "0123456789abcdef";
		StringBuilder inumber = new StringBuilder('!');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));
		inumber.append('.');
		for (int i=0; i<4; i++) inumber.append(hex.charAt(random.nextInt(hex.length())));

		return new XRI3SubSegment(inumber.toString());
	}

	public static String dataUriToString(String dataUri) {

		URI uri = URI.create(dataUri);

		if (! uri.getScheme().equals("data")) throw new Xdi2RuntimeException("URI has no data scheme: " + dataUri);

		String authority = uri.getAuthority();

		String[] strings = authority.split(",");
		if (strings.length != 1) throw new Xdi2RuntimeException("Invalid data URI: " + dataUri);

		String typeEncodingBase64 = strings[0];
		String data = strings[1];

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

	public static String stringToDataUri(String string) {

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

		return builder.toString();
	}
}
