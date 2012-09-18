package xdi2.messaging.target.contributor;

import xdi2.core.Statement;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public interface Contributor {

	/**
	 * Executes an XDI operation on an address.
	 * @param contributorXri The base XRI on which the contributor is mounted.
	 * @param relativeAddress The target address relative to the contributor.
	 * @param targetAddress The target address.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnAddress(XRI3Segment contributorXri, XRI3Segment relativeTargetAddress, XRI3Segment targetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a statement.
	 * @param contributorXri The base XRI on which the contributor is mounted.
	 * @param relativeTargetStatement The target statement relative to the contributor.
	 * @param targetStatement The target statement.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnStatement(XRI3Segment contributorXri, Statement relativeTargetStatement, Statement targetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
