package xdi2.messaging.target.interceptor.impl;

import xdi2.core.Statement;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor throws an exception when $add, $mod or $del operations are attempted on certain XDI addresses.
 * 
 * @author markus
 */
public class ReadOnlyInterceptor extends AbstractInterceptor implements TargetInterceptor {

	private XRI3Segment[] readOnlyAddresses;

	public ReadOnlyInterceptor() {

		this.readOnlyAddresses = new XRI3Segment[0];
	}

	@Override
	public Statement targetStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetStatement.getSubject(), executionContext);

		return targetStatement;
	}

	@Override
	public XRI3Segment targetAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetAddress, executionContext);

		return targetAddress;
	}

	private void checkReadOnly(Operation operation, XRI3Segment address, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation.isReadOperation()) return;

		for (XRI3Segment readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || startsWith(address, readOnlyAddress)) {

				throw new Xdi2MessagingException("This address is read-only: " + address, null, executionContext);
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
