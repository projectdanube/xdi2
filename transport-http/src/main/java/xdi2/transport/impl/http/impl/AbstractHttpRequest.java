package xdi2.transport.impl.http.impl;

import xdi2.transport.impl.AbstractRequest;
import xdi2.transport.impl.http.HttpRequest;

public abstract class AbstractHttpRequest extends AbstractRequest implements HttpRequest {

	@Override
	public String toString() {

		return this.getRequestPath() + " (" + this.getRemoteAddr() + ")";
	}
}
