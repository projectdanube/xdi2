package xdi2.transport.impl.http;

import java.io.IOException;
import java.io.InputStream;

import xdi2.transport.TransportRequest;

/**
 * This interface abstracts path information about a request to the server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public interface HttpTransportRequest extends TransportRequest {

	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST= "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_OPTIONS = "OPTIONS";

	public String getMethod();
	public String getBaseUri();
	public String getRequestPath();
	public String getParameter(String name);
	public String getHeader(String name);
	public String getContentType();

	public InputStream getBodyInputStream() throws IOException;

	public String getRemoteAddr();
}
