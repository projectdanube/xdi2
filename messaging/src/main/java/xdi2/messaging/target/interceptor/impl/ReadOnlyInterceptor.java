package xdi2.messaging.target.interceptor.impl;

import xdi2.core.Statement;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

public class ReadOnlyInterceptor extends AbstractInterceptor implements TargetInterceptor {

	private XRI3Segment[] readOnlyAddresses;

	public ReadOnlyInterceptor() {

		this.readOnlyAddresses = new XRI3Segment[0];
	}

	@Override
	public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetStatement.getSubject());

		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetAddress);

		return targetAddress;
	}

	private void checkReadOnly(Operation operation, XRI3Segment address) throws Xdi2MessagingException {

		if (operation.isReadOperation()) return;

		for (XRI3Segment readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || startsWith(address, readOnlyAddress)) {

				throw new Xdi2MessagingException("This address is read-only: " + address, null, operation);
			}
		}
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

	public void setReadOnlyAddresses(String[] readOnlyAddresses) {

		this.readOnlyAddresses = new XRI3Segment[readOnlyAddresses.length];
		for (int i=0; i<this.readOnlyAddresses.length; i++) this.readOnlyAddresses[i] = new XRI3Segment(readOnlyAddresses[i]);
	}
}
