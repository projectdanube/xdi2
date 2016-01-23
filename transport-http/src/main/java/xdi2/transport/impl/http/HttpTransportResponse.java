package xdi2.transport.impl.http;

import java.io.IOException;

import xdi2.transport.TransportResponse;
import xdi2.transport.impl.uri.UriTransportResponse;

/**
 * This class represents an XDI response from an HTTP server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public abstract class HttpTransportResponse extends UriTransportResponse implements TransportResponse {

	public static final int SC_OK = 200;
	public static final int SC_FOUND = 302;
	public static final int SC_NOT_FOUND = 404;
	public static final int SC_INTERNAL_SERVER_ERROR = 500;

	public abstract void setStatus(int status);
	public abstract void setHeader(String name, String value);
	public abstract void writeBody(String string, boolean close) throws IOException;
	public abstract void writeBody(byte[] bytes, boolean close) throws IOException;

	public void setContentType(String contentType) {

		this.setHeader("Content-Type", contentType);
	}

	public void setContentLength(int contentLength) {

		this.setHeader("Content-Length", Integer.toString(contentLength));
	}

	public void sendRedirect(int status, String location) throws IOException {

		this.setStatus(status);
		this.setHeader("Location", location);
		this.writeBody(location, true);
	}

	public void sendError(int status, String message) throws IOException {

		this.setStatus(status);
		this.writeBody(message, true);
	}
}
