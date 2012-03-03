package xdi2.messaging.target.interceptor.impl;

import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractContextNodeInterceptor;
import xdi2.messaging.target.interceptor.ContextNodeInterceptor;

public class ReadOnlyContextNodeInterceptor extends AbstractContextNodeInterceptor implements ContextNodeInterceptor {

	private XRI3Segment[] readOnlyAddresses;

	public ReadOnlyContextNodeInterceptor() {

		this.readOnlyAddresses = new XRI3Segment[0];
	}

	@Override
	public boolean before(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation.isReadOperation()) return false;

		XRI3Segment contextNodeXri = contextNode.getXri();

		for (XRI3Segment readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || startsWith(contextNodeXri, readOnlyAddress)) {

				String message = "This address is read-only: " + contextNodeXri;
				message += ".";

				throw new Xdi2MessagingException(message);
			}
		}

		return false;
	}

	private static boolean startsWith(XRI3Segment whole, XRI3Segment part) {

		if (part.getNumSubSegments() > whole.getNumSubSegments()) return false;

		for (int i=0; i<part.getNumSubSegments(); i++) {

			if (! part.getSubSegment(i).equals(whole.getSubSegment(i))) return false;
		}

		return true;
	}

	public XRI3Segment[] getReadOnlyAddresses() {

		return this.readOnlyAddresses;
	}

	public void setReadOnlyAddresses(XRI3Segment[] readOnlyAddresses) {

		this.readOnlyAddresses = readOnlyAddresses;
	}
}
