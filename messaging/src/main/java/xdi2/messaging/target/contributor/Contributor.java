package xdi2.messaging.target.contributor;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

public interface Contributor {

	/**
	 * Executes an XDI operation on an address.
	 * @param contributorXris The base XRIs on which the contributors are mounted.
	 * @param relativeAddress The target address relative to the contributor.
	 * @param targetAddress The target address.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnAddress(XDI3Segment[] contributorXris, XDI3Segment relativeTargetAddress, XDI3Segment targetAddress, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Executes an XDI operation on a statement.
	 * @param contributorXris The base XRIs on which the contributors are mounted.
	 * @param relativeTargetStatement The target statement relative to the contributor.
	 * @param targetStatement The target statement.
	 * @param operation The operation that is being executed.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object for the entire XDI message envelope.
	 * @return True, if the operation has been fully handled and the server should stop processing it.
	 */
	public boolean executeOnStatement(XDI3Segment[] contributorXris, XDI3Statement relativeTargetStatement, XDI3Statement targetStatement, Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
