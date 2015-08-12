package xdi2.transport.impl.http.impl.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import xdi2.transport.impl.http.HttpTransportResponse;
import xdi2.transport.impl.http.impl.AbstractHttpTransportResponse;

public class ServletHttpTransportResponse extends AbstractHttpTransportResponse implements HttpTransportResponse {

	private HttpServletResponse httpServletResponse;

	private ServletHttpTransportResponse(HttpServletResponse httpServletResponse) { 

		this.httpServletResponse = httpServletResponse;
	}

	/*
	 * Static methods
	 */

	public static ServletHttpTransportResponse fromHttpServletResponse(HttpServletResponse httpServletResponse) {

		return new ServletHttpTransportResponse(httpServletResponse);
	}

	/*
	 * Instance methods
	 */

	public HttpServletResponse getHttpServletResponse() {

		return this.httpServletResponse;
	}

	@Override
	public void setStatus(int sc) {

		this.getHttpServletResponse().setStatus(sc);
	}

	@Override
	public void setContentType(String type) {

		this.getHttpServletResponse().setContentType(type);
	}

	@Override
	public void setContentLength(int len) {

		this.getHttpServletResponse().setContentLength(len);
	}

	@Override
	public void setHeader(String name, String value) {

		this.getHttpServletResponse().setHeader(name, value);
	}

	@Override
	public void sendRedirect(String location) throws IOException {

		this.getHttpServletResponse().sendRedirect(location);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {

		this.getHttpServletResponse().sendError(sc, msg);
	}

	@Override
	public Writer getBodyWriter() throws IOException {

		return this.getHttpServletResponse().getWriter();
	}

	@Override
	public OutputStream getBodyOutputStream() throws IOException {

		return this.getHttpServletResponse().getOutputStream();
	}
}
