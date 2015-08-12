package xdi2.transport.impl.http.impl;

import java.io.IOException;

import xdi2.transport.impl.AbstractTransportResponse;
import xdi2.transport.impl.http.HttpTransportResponse;

public abstract class AbstractHttpTransportResponse extends AbstractTransportResponse implements HttpTransportResponse {

	@Override
	public void setContentType(String contentType) {

		this.setHeader("Content-Type", contentType);
	}

	@Override
	public void setContentLength(int contentLength) {

		this.setHeader("Content-Length", Integer.toString(contentLength));
	}

	@Override
	public void sendRedirect(int status, String location) throws IOException {

		this.setStatus(status);
		this.setHeader("Location", location);
		this.writeBody(location, true);
	}

	@Override
	public void sendError(int status, String message) throws IOException {

		this.setStatus(status);
		this.writeBody(message, true);
	}
}
