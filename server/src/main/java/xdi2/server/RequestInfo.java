package xdi2.server;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class encapsulates path information about a request to the server.
 * 
 * This is populated by the EndpointFilter and use by the EndpointServlet.
 * 
 * @author markus
 */
public class RequestInfo implements Serializable, Comparable<RequestInfo> {

	private static final long serialVersionUID = 5362137617270494532L;

	private String requestUri;
	private String contextPath;
	private String servletPath;
	private String requestPath;
	private String messagingTargetPath;

	private static Logger log = LoggerFactory.getLogger(RequestInfo.class.getName());

	public RequestInfo(String requestUri, String contextPath, String servletPath, String requestPath, String messagingTargetPath) {

		this.requestUri = requestUri;
		this.contextPath = contextPath;
		this.servletPath = servletPath;
		this.requestPath = requestPath;
		this.messagingTargetPath = messagingTargetPath;
	}

	public static RequestInfo parse(HttpServletRequest request) {

		String requestUri = request.getRequestURI();
		
		String contextPath = request.getContextPath(); 
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);
		
		String servletPath = request.getServletPath();
		if (servletPath.endsWith("/")) servletPath = servletPath.substring(0, servletPath.length() - 1);

		String requestPath = requestUri.substring(contextPath.length() + servletPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		log.debug("requestUri: " + requestUri);
		log.debug("contextPath: " + contextPath);
		log.debug("servletPath: " + servletPath);
		log.debug("requestPath: " + requestPath);

		return new RequestInfo(requestUri, contextPath, servletPath, requestPath, null);
	}

	public String getRequestUri() {

		return this.requestUri;
	}

	public void setRequestUri(String requestUri) {

		this.requestUri = requestUri;
	}

	public String getContextPath() {

		return this.contextPath;
	}

	public void setContextPath(String contextPath) {

		this.contextPath = contextPath;
	}

	public String getServletPath() {

		return this.servletPath;
	}

	public void setServletPath(String servletPath) {

		this.servletPath = servletPath;
	}

	public String getRequestPath() {

		return this.requestPath;
	}

	public void setRequestPath(String requestPath) {

		this.requestPath = requestPath;
	}

	public String getMessagingTargetPath() {

		return this.messagingTargetPath;
	}

	public void setMessagingTargetPath(String messagingTargetPath) {

		this.messagingTargetPath = messagingTargetPath;
	}

	@Override
	public String toString() {

		return this.requestUri;
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof RequestInfo)) return false;
		if (object == this) return true;

		RequestInfo other = (RequestInfo) object;

		return this.getRequestUri().equals(other.getRequestUri());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getRequestUri().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(RequestInfo other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getRequestUri().compareTo(other.getRequestUri())) != 0) return compare;

		return 0;
	}
}
