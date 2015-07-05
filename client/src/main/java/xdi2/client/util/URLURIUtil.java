package xdi2.client.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLURIUtil {

	public static URL URL(String url) {

		if (url == null) return null;

		try {

			return new URL(url);
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public static URI URI(String uri) {

		if (uri == null) return null;

		try {

			return new URI(uri);
		} catch (URISyntaxException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public static URL URItoURL(URI uri) {

		if (uri == null) return null;

		try {

			return uri.toURL();
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public static URI URLtoURI(URL url) {

		if (url == null) return null;

		try {

			return url.toURI();
		} catch (URISyntaxException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}
}
