package xdi2.transport.impl.http;

import java.io.IOException;

import xdi2.transport.TransportResponse;

/**
 * This interface abstracts path information about a response from the server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public interface HttpTransportResponse extends TransportResponse {

	public static final int SC_OK = 200;
	public static final int SC_FOUND = 302;
	public static final int SC_NOT_FOUND = 404;
	public static final int SC_INTERNAL_SERVER_ERROR = 500;

	public void setStatus(int status);
	public void setContentType(String contentType);
	public void setContentLength(int contentLength);
	public void setHeader(String name, String value);

	public void sendRedirect(int status, String location) throws IOException;
	public void sendError(int status, String message) throws IOException;

	public void writeBody(String string, boolean close) throws IOException;
	public void writeBody(byte[] bytes, boolean close) throws IOException;
}
