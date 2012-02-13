package xdi2.messaging.target.interceptor.impl;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.AbstractResourceInterceptor;
import xdi2.messaging.target.interceptor.ResourceInterceptor;

public class ReadOnlyResourceInterceptor extends AbstractResourceInterceptor implements ResourceInterceptor {

	private XRI3[] readOnlyAddresses;

	public ReadOnlyResourceInterceptor() {

		this.readOnlyAddresses = new XRI3[0];
	}

	@Override
	public boolean before(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation.isReadOperation()) return false;

		for (XRI3 readOnlyAddress : this.readOnlyAddresses) {

			if (readOnlyAddress == null || statement.getXRI3().startsWith(readOnlyAddress)) {

				String message = "This address is read-only: " + statement.toString();
				message += ".";

				throw new Xdi2MessagingException(message);
			}
		}
		
		return false;
	}

	public XRI3[] getReadOnlyAddresses() {

		return this.readOnlyAddresses;
	}

	public void setReadOnlyAddresses(XRI3[] readOnlyAddresses) {

		this.readOnlyAddresses = readOnlyAddresses;
	}
}
