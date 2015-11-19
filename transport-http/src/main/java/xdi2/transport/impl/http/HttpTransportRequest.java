package xdi2.transport.impl.http;

import java.io.IOException;
import java.io.InputStream;

import xdi2.transport.TransportRequest;
import xdi2.transport.impl.uri.UriTransportRequest;

/**
 * This class represents an XDI request to an HTTP server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public abstract class HttpTransportRequest extends UriTransportRequest implements TransportRequest {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST= "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_OPTIONS = "OPTIONS";

	public abstract String getMethod();
	public abstract String getBaseUri();
	public abstract String getRequestPath();
	public abstract String getParameter(String name);
	public abstract String getHeader(String name);

	public String getContentType() {

		return this.getHeader("Content-Type");
	}

	public abstract InputStream getBodyInputStream() throws IOException;

	public abstract String getRemoteAddr();

	@Override
	public String toString() {

		return this.getMethod() + " " + this.getRequestPath() + " (" + this.getRemoteAddr() + ")";
	}
}
