package xdi2.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

import javax.servlet.http.HttpServletResponse;

import xdi2.server.transport.AbstractHttpResponse;
import xdi2.server.transport.HttpResponse;

public class ServletHttpResponse extends AbstractHttpResponse implements HttpResponse {

	private HttpServletResponse httpServletResponse;

	private ServletHttpResponse(HttpServletResponse httpServletResponse) { 

		this.httpServletResponse = httpServletResponse;
	}

	public static ServletHttpResponse fromHttpServletResponse(HttpServletResponse httpServletResponse) {

		return new ServletHttpResponse(httpServletResponse);
	}

	public HttpServletResponse getHttpServletResponse() {

		return this.httpServletResponse;
	}

	@Override
	public void setStatus(int sc) {

		this.httpServletResponse.setStatus(sc);
	}

	@Override
	public void setContentType(String type) {

		this.httpServletResponse.setContentType(type);
	}

	@Override
	public void setContentLength(int len) {

		this.httpServletResponse.setContentLength(len);
	}

	@Override
	public void setHeader(String name, String value) {

		this.httpServletResponse.setHeader(name, value);
	}

	@Override
	public void sendRedirect(String location) throws IOException {

		this.httpServletResponse.sendRedirect(location);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {

		this.httpServletResponse.sendError(sc, msg);
	}

	@Override
	public Writer getBodyWriter() throws IOException {

		return this.httpServletResponse.getWriter();
	}

	@Override
	public OutputStream getBodyOutputStream() throws IOException {

		return this.httpServletResponse.getOutputStream();
	}
}
