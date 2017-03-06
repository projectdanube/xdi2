package xdi2.transport.impl.http.impl.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.transport.TransportRequest;
import xdi2.transport.impl.http.HttpTransportRequest;

public final class ServletHttpTransportRequest extends HttpTransportRequest implements TransportRequest {

	private static final Logger log = LoggerFactory.getLogger(ServletHttpTransportRequest.class);

	private HttpServletRequest httpServletRequest;
	private String baseUri;
	private String requestPath;

	private ServletHttpTransportRequest(HttpServletRequest httpServletRequest, String baseUri, String requestPath) { 

		this.httpServletRequest = httpServletRequest;
		this.baseUri = baseUri;
		this.requestPath = requestPath;
	}

	/*
	 * Static methods
	 */

	public static ServletHttpTransportRequest fromHttpServletRequest(HttpServletRequest httpServletRequest) {

		// determine request path

		String requestUri = httpServletRequest.getRequestURI();
		if (log.isDebugEnabled()) log.debug("Request URI: " + requestUri);

		String contextPath = httpServletRequest.getContextPath(); 
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);

		String servletPath = httpServletRequest.getServletPath();
		if (servletPath.endsWith("/")) servletPath = servletPath.substring(0, servletPath.length() - 1);

		String requestPath = requestUri.substring(contextPath.length() + servletPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		try {

			requestPath = URLDecoder.decode(requestPath, "UTF-8");
			if (log.isDebugEnabled()) log.debug("Request Path: " + requestPath);
		} catch (UnsupportedEncodingException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		// determine base URI

		String baseUri = httpServletRequest.getRequestURL().toString().substring(0, httpServletRequest.getRequestURL().length() - requestPath.length() + 1);
		if (baseUri.endsWith("/")) baseUri = baseUri.substring(0, baseUri.length() - 1);
		if (log.isDebugEnabled()) log.debug("Base URI: " + baseUri);

		// done

		return new ServletHttpTransportRequest(httpServletRequest, baseUri, requestPath);
	}

	/*
	 * Instance methods
	 */

	public HttpServletRequest getHttpServletRequest() {

		return this.httpServletRequest;
	}

	@Override
	public String getMethod() {

		return this.getHttpServletRequest().getMethod();
	}

	@Override
	public String getBaseUri() {

		return this.baseUri;
	}

	@Override
	public String getRequestPath() {

		return this.requestPath;
	}

	@Override
	public String getParameter(String name) {

		return this.getHttpServletRequest().getParameter(name);
	}

	@Override
	public String getHeader(String name) {

		return this.getHttpServletRequest().getHeader(name);
	}

	@Override
	public String getContentType() {

		return this.getHttpServletRequest().getContentType();
	}

	@Override
	public InputStream getBodyInputStream() throws IOException {

		return this.getHttpServletRequest().getInputStream();
	}

	@Override
	public String getRemoteAddr() {

		return this.getHttpServletRequest().getRemoteAddr();
	}
}
