package xdi2.messaging.target.interceptor.impl;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * This interceptor throws an exception when $add, $mod or $del operations are attempted on certain XDI addresses.
 * 
 * @author markus
 */
public class ReadOnlyInterceptor extends AbstractInterceptor implements TargetInterceptor, Prototype<ReadOnlyInterceptor> {

	private XDI3Segment[] readOnlyAddresses;

	public ReadOnlyInterceptor() {

		this.readOnlyAddresses = new XDI3Segment[0];
	}

	/*
	 * Prototype
	 */

	@Override
	public ReadOnlyInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDI3Statement targetStatement(XDI3Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetStatement.getSubject(), executionContext);

		return targetStatement;
	}

	@Override
	public XDI3Segment targetAddress(XDI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.checkReadOnly(operation, targetAddress, executionContext);

		return targetAddress;
	}

	private void checkReadOnly(Operation operation, XDI3Segment address, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation.isReadOperation()) return;

		for (XDI3Segment readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || startsWith(address, readOnlyAddress)) {

				throw new Xdi2MessagingException("This address is read-only: " + address, null, executionContext);
			}
		}
	}

	private static boolean startsWith(XDI3Segment whole, XDI3Segment part) {

		if (part.getNumSubSegments() > whole.getNumSubSegments()) return false;

		for (int i=0; i<part.getNumSubSegments(); i++) {

			if (! part.getSubSegment(i).equals(whole.getSubSegment(i))) return false;
		}

		return true;
	}

	public XDI3Segment[] getReadOnlyAddresses() {

		return this.readOnlyAddresses;
	}

	public void setReadOnlyAddresses(XDI3Segment[] readOnlyAddresses) {

		this.readOnlyAddresses = readOnlyAddresses;
	}

	public void setReadOnlyAddresses(String[] readOnlyAddresses) {

		this.readOnlyAddresses = new XDI3Segment[readOnlyAddresses.length];
		for (int i=0; i<this.readOnlyAddresses.length; i++) this.readOnlyAddresses[i] = XDI3Segment.create(readOnlyAddresses[i]);
	}
}
