package xdi2.server.servlet;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import xdi2.server.transport.AbstractHttpRequest;
import xdi2.server.transport.HttpRequest;

public final class ServletHttpRequest extends AbstractHttpRequest implements HttpRequest {

	private HttpServletRequest httpServletRequest;
	private String baseUri;
	private String requestPath;

	private ServletHttpRequest(HttpServletRequest httpServletRequest, String uri, String requestPath) { 

		this.httpServletRequest = httpServletRequest;
		this.baseUri = uri;
		this.requestPath = requestPath;
	}

	public static ServletHttpRequest fromHttpServletRequest(HttpServletRequest httpServletRequest) {

		String requestUri = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath(); 
		if (contextPath.endsWith("/")) contextPath = contextPath.substring(0, contextPath.length() - 1);

		String servletPath = httpServletRequest.getServletPath();
		if (servletPath.endsWith("/")) servletPath = servletPath.substring(0, servletPath.length() - 1);

		String requestPath = requestUri.substring(contextPath.length() + servletPath.length());
		if (! requestPath.startsWith("/")) requestPath = "/" + requestPath;

		String baseUri = httpServletRequest.getRequestURL().toString().substring(0, httpServletRequest.getRequestURL().length() - requestPath.length() + 1);
		if (baseUri.endsWith("/")) baseUri = baseUri.substring(0, baseUri.length() - 1);

		return new ServletHttpRequest(httpServletRequest, baseUri, requestPath);
	}

	public HttpServletRequest getHttpServletRequest() {

		return this.httpServletRequest;
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

	@Override
	public String getRemoteAddr() {

		return this.httpServletRequest.getRemoteAddr();
	}
}
