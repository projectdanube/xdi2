package xdi2.server.transport;

import java.io.IOException;
import java.io.InputStream;

import xdi2.messaging.transport.Request;

/**
 * This interface abstracts path information about a request to the server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public interface HttpRequest extends Request {

	public String getBaseUri();
	public String getRequestPath();
	public String getParameter(String name);
	public String getHeader(String name);
	public String getContentType();
	public int getContentLength();

	public InputStream getBodyInputStream() throws IOException;

	public String getRemoteAddr();
}
