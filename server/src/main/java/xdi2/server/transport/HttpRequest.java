package xdi2.server.transport;

import java.io.IOException;
import java.io.InputStream;

import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpEndpointRegistry;

/**
 * This interface abstracts path information about a request to the server.
 * This is used by the HttpTransport.
 * 
 * @author markus
 */
public interface HttpRequest {

	public String getBaseUri();
	public String getRequestPath();
	public String getParameter(String name);
	public String getHeader(String name);
	public String getContentType();
	public int getContentLength();

	public void lookup(HttpEndpointRegistry httpEndpointRegistry) throws Xdi2ServerException, Xdi2MessagingException;
	public String getMessagingTargetPath();
	public MessagingTarget getMessagingTarget();

	public InputStream getBodyInputStream() throws IOException;

	public String getRemoteAddr();
}
