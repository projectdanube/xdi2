package xdi2.server.transport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * This interface abstracts path information about a response from the server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public interface HttpResponse {

	public static final int SC_OK = 200;
	public static final int SC_NOT_FOUND = 404;
	public static final int SC_INTERNAL_SERVER_ERROR = 500;

	public void setStatus(int sc);
	public void setContentType(String type);
	public void setContentLength(int len);
	public void setHeader(String name, String value);

	public void sendRedirect(String location) throws IOException;
	public void sendError(int sc, String msg) throws IOException;

	public Writer getBodyWriter() throws IOException;
	public OutputStream getBodyOutputStream() throws IOException;
}
