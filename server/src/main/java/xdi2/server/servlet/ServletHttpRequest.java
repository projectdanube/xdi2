package xdi2.server.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import xdi2.server.transport.AbstractHttpRequest;
import xdi2.server.transport.HttpRequest;

public final class ServletHttpRequest extends AbstractHttpRequest implements HttpRequest {

	private HttpServletRequest httpServletRequest;
	private String uri;
	private String requestPath;

	private ServletHttpRequest(HttpServletRequest httpServletRequest, String uri, String requestPath) { 

		this.httpServletRequest = httpServletRequest;
		this.uri = uri;
		this.requestPath = requestPath;
	}

	public static ServletHttpRequest fromHttpServletRequest(HttpServletRequest httpServletRequest) {

		String uri = httpServletRequest.getRequestURL().toString();

		String requestUri = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath(); 
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);

		String servletPath = httpServletRequest.getServletPath();
		if (servletPath.endsWith("/")) servletPath = servletPath.substring(0, servletPath.length() - 1);

		String requestPath = requestUri.substring(contextPath.length() + servletPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		return new ServletHttpRequest(httpServletRequest, uri, requestUri);
	}

	public HttpServletRequest getHttpServletRequest() {
	
		return this.httpServletRequest;
	}

	@Override
	public String getUri() {

		return this.uri;
	}

	@Override
	public String getRequestPath() {

		return this.requestPath;
	}

	@Override
	public String getParameter(String name) {

		return this.httpServletRequest.getParameter(name);
	}

	@Override
	public String getHeader(String name) {

		return this.httpServletRequest.getHeader(name);
	}

	@Override
	public String getContentType() {

		return this.httpServletRequest.getContentType();
	}

	@Override
	public int getContentLength() {

		return this.httpServletRequest.getContentLength();
	}

	@Override
	public InputStream getBodyInputStream() throws IOException {

		return this.httpServletRequest.getInputStream();
	}
}
