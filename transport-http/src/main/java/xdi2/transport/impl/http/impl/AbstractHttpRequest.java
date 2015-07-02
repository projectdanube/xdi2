package xdi2.transport.impl.http.impl;

import xdi2.transport.impl.AbstractTransportRequest;
import xdi2.transport.impl.http.HttpTransportRequest;

public abstract class AbstractHttpRequest extends AbstractTransportRequest implements HttpTransportRequest {

	@Override
	public String toString() {

		return this.getRequestPath() + " (" + this.getRemoteAddr() + ")";
	}
}
