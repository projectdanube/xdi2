package xdi2.messaging.target.impl;

import xdi2.core.Statement;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.exceptions.Xdi2MessagingException;

public interface Overlay {

	public AddressHandler getAddressHandler(XRI3Segment targetAddress) throws Xdi2MessagingException;
	public StatementHandler getStatementHandler(Statement targetStatement) throws Xdi2MessagingException;
}
