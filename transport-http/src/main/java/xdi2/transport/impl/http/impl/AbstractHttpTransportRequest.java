package xdi2.transport.impl.http.impl;

import xdi2.transport.impl.AbstractTransportRequest;
import xdi2.transport.impl.http.HttpTransportRequest;

public abstract class AbstractHttpTransportRequest extends AbstractTransportRequest implements HttpTransportRequest {

	@Override
	public String getContentType() {

		return this.getHeader("Content-Type");
	}

	@Override
	public String toString() {

		return this.getMethod() + " " + this.getRequestPath() + " (" + this.getRemoteAddr() + ")";
	}
}
