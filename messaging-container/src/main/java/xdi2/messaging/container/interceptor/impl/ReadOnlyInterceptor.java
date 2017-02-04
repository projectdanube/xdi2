package xdi2.messaging.container.interceptor.impl;

import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.interceptor.TargetInterceptor;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;

/**
 * This interceptor throws an exception when $add, $mod or $del operations are attempted on given contexts of the target graph.
 * 
 * @author markus
 */
public class ReadOnlyInterceptor extends AbstractInterceptor<MessagingContainer> implements TargetInterceptor, Prototype<ReadOnlyInterceptor> {

	private XDIAddress[] readOnlyAddresses;

	public ReadOnlyInterceptor() {

		this.readOnlyAddresses = new XDIAddress[0];
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
	public XDIStatement targetStatement(XDIStatement targetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress;

		if (targetStatement.isContextNodeStatement()) 
			contextNodeXDIAddress = targetStatement.getTargetXDIAddress();
		else
			contextNodeXDIAddress = targetStatement.getContextNodeXDIAddress();

		this.checkReadOnly(operation, contextNodeXDIAddress, executionContext);

		return targetStatement;
	}

	@Override
	public XDIAddress targetAddress(XDIAddress targetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress contextNodeXDIAddress = targetAddress;
		
		this.checkReadOnly(operation, contextNodeXDIAddress, executionContext);

		return targetAddress;
	}

	private void checkReadOnly(Operation operation, XDIAddress contextNodeXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation) return;

		for (XDIAddress readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || XDIAddressUtil.startsWithXDIAddress(contextNodeXDIAddress, readOnlyAddress) != null) {

				throw new Xdi2MessagingException("This address is read-only: " + contextNodeXDIAddress, null, executionContext);
			}
		}
	}

	public XDIAddress[] getReadOnlyAddresses() {

		return this.readOnlyAddresses;
	}

	public void setReadOnlyAddresses(XDIAddress[] readOnlyAddresses) {

		this.readOnlyAddresses = readOnlyAddresses;
	}

	public void setReadOnlyAddresses(String[] readOnlyAddresses) {

		this.readOnlyAddresses = new XDIAddress[readOnlyAddresses.length];
		for (int i=0; i<this.readOnlyAddresses.length; i++) this.readOnlyAddresses[i] = XDIAddress.create(readOnlyAddresses[i]);
	}
}
