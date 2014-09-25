package xdi2.messaging.target.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.DoOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ContextHandler;
import xdi2.messaging.target.impl.graph.GraphContextHandler;
import xdi2.messaging.target.interceptor.impl.MessagePolicyInterceptor;
import xdi2.messaging.target.interceptor.impl.ToInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;

public abstract class AbstractContextHandler implements ContextHandler {

	private static final Logger log = LoggerFactory.getLogger(AbstractContextHandler.class);

	public AbstractContextHandler() {

	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeOnAddress(XDIAddress targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on address

		if (operation instanceof GetOperation)
			this.executeGetOnAddress(targetAddress, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnAddress(targetAddress, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnAddress(targetAddress, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnAddress(targetAddress, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnAddress(targetAddress, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnAddress(targetAddress, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnAddress(XDIAddress targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeSetOnAddress(XDIAddress targetAddress, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnAddress(XDIAddress targetAddress, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target address

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(targetAddress, null, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnAddress(targetAddress, setOperation, feedbackMessageResult, executionContext);
		} else {

			throw new Xdi2MessagingException("Target statement exists already: " + operation, null, executionContext);
		}
	}

	public void executeModOnAddress(XDIAddress targetAddress, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target address

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(targetAddress, null, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			throw new Xdi2MessagingException("Target address does not exist: " + operation, null, executionContext);
		} else {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnAddress(targetAddress, setOperation, feedbackMessageResult, executionContext);
		}
	}

	public void executeDelOnAddress(XDIAddress targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnAddress(XDIAddress targetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeOnStatement(XDIStatement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// execute on statement

		if (operation instanceof GetOperation)
			this.executeGetOnStatement(targetStatement, (GetOperation) operation, messageResult, executionContext);
		else if (operation instanceof SetOperation)
			this.executeSetOnStatement(targetStatement, (SetOperation) operation, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			this.executeAddOnStatement(targetStatement, (AddOperation) operation, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			this.executeModOnStatement(targetStatement, (ModOperation) operation, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			this.executeDelOnStatement(targetStatement, (DelOperation) operation, messageResult, executionContext);
		else if (operation instanceof DoOperation)
			this.executeDoOnStatement(targetStatement, (DoOperation) operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXDIAddress(), null, executionContext);
	}

	public void executeGetOnStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeSetOnStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeSetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeSetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeSetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeAddOnStatement(XDIStatement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeAddOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeAddOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeAddOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeModOnStatement(XDIStatement targetStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeModOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeModOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeModOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown statement type: " + targetStatement.getClass().getCanonicalName(), null, executionContext);
	}

	public void executeDelOnStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDelOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDelOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDelOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	public void executeDoOnStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetStatement.isContextNodeStatement())
			this.executeDoOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isRelationStatement())
			this.executeDoOnRelationStatement(targetStatement, operation, messageResult, executionContext);
		else if (targetStatement.isLiteralStatement())
			this.executeDoOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Invalid statement: " + targetStatement, null, executionContext);
	}

	/*
	 * Operations on context node statements
	 */

	public void executeGetOnContextNodeStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// run a $get on the address, then filter for the statement

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnContextNodeStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnContextNodeStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// a $set on a context node statement is the same as a $set on the address

		XDIAddress targetAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeSetOnAddress(targetAddress, operation, messageResult, executionContext);
	}

	public void executeAddOnContextNodeStatement(XDIStatement contextNodeStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target statement

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(contextNodeStatement.getContextNodeXDIAddress(), null, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnContextNodeStatement(contextNodeStatement, setOperation, feedbackMessageResult, executionContext);
		} else {

			throw new Xdi2MessagingException("Target statement exists already: " + operation, null, executionContext);
		}
	}

	public void executeModOnContextNodeStatement(XDIStatement contextNodeStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target address

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(contextNodeStatement.getContextNodeXDIAddress(), null, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			throw new Xdi2MessagingException("Target statement does not exist: " + operation, null, executionContext);
		} else {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnContextNodeStatement(contextNodeStatement, setOperation, feedbackMessageResult, executionContext);
		}
	}

	public void executeDelOnContextNodeStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// a $del on a context node statement is the same as a $del on the address

		XDIAddress targetAddress = XDIAddressUtil.concatXDIAddresses(targetStatement.getContextNodeXDIAddress(), targetStatement.getContextNodeXDIArc());

		this.executeDelOnAddress(targetAddress, operation, messageResult, executionContext);
	}

	public void executeDoOnContextNodeStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on relation statements
	 */

	public void executeGetOnRelationStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// run a $get on the address, then filter for the statement

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnRelationStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnRelationStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnRelationStatement(XDIStatement relationStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target statement

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(null, relationStatement, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnRelationStatement(relationStatement, setOperation, feedbackMessageResult, executionContext);
		} else {

			throw new Xdi2MessagingException("Target statement exists already: " + operation, null, executionContext);
		}
	}

	public void executeModOnRelationStatement(XDIStatement relationStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target statement

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(null, relationStatement, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			throw new Xdi2MessagingException("Target statement does not exist: " + operation, null, executionContext);
		} else {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnRelationStatement(relationStatement, setOperation, feedbackMessageResult, executionContext);
		}
	}

	public void executeDelOnRelationStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnRelationStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Operations on literal statements
	 */

	public void executeGetOnLiteralStatement(XDIStatement targetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// run a $get on the address, then filter for the statement

		XDIAddress targetAddress = targetStatement.getContextNodeXDIAddress();

		MessageResult tempMessageResult = new MessageResult();

		this.executeGetOnAddress(targetAddress, operation, tempMessageResult, executionContext);

		new GraphContextHandler(tempMessageResult.getGraph()).executeGetOnLiteralStatement(targetStatement, operation, messageResult, executionContext);
	}

	public void executeSetOnLiteralStatement(XDIStatement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeAddOnLiteralStatement(XDIStatement literalStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target statement

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(null, literalStatement, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnLiteralStatement(literalStatement, setOperation, feedbackMessageResult, executionContext);
		} else {

			throw new Xdi2MessagingException("Target statement exists already: " + operation, null, executionContext);
		}
	}

	public void executeModOnLiteralStatement(XDIStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// $get feedback on target statement

		MessageResult feedbackMessageResult = feedbackGetTargetAddressOrStatement(null, literalStatement, operation, executionContext);

		if (feedbackMessageResult.isEmpty()) {

			throw new Xdi2MessagingException("Target statement does not exist: " + operation, null, executionContext);
		} else {

			// execute a $set operation

			SetOperation setOperation = /* TODO */ null;

			this.executeSetOnLiteralStatement(literalStatement, setOperation, feedbackMessageResult, executionContext);
		}
	}

	public void executeDelOnLiteralStatement(XDIStatement targetStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void executeDoOnLiteralStatement(XDIStatement targetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	/*
	 * Feedback methods
	 */

	private static MessageResult feedbackGetTargetAddressOrStatement(XDIAddress targetAddress, XDIStatement targetStatement, Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (targetAddress == null && targetStatement == null) throw new IllegalArgumentException();
		if (targetAddress != null && targetStatement != null) throw new IllegalArgumentException();

		if (log.isDebugEnabled()) log.debug("Initiating $get feedback to get target address/statement: " + targetAddress + ", " + targetStatement);

		// prepare messaging target

		AbstractMessagingTarget messagingTarget = (AbstractMessagingTarget) executionContext.getCurrentMessagingTarget();

		// prepare feedback message result

		MessageResult feedbackMessageResult = new MessageResult();

		// prepare feedback message

		Message feedbackMessage = new MessageEnvelope().createMessage(operation.getSenderXDIAddress());

		Operation feedbackOperation = null;

		if (targetAddress != null) feedbackOperation = feedbackMessage.createGetOperation(targetAddress);
		if (targetStatement != null) feedbackOperation = feedbackMessage.createGetOperation(targetStatement);

		feedbackOperation.setParameter(GetOperation.XDI_ADD_PARAMETER_DEREF, Boolean.TRUE);

		// feedback

		Map<String, Object> messageAttributes = null;
		Map<String, Object> operationAttributes = null;

		try {

			// before feedback: tweak the execution context and messaging target

			ToInterceptor toInterceptor = messagingTarget.getInterceptors().getInterceptor(ToInterceptor.class);
			if (toInterceptor != null) toInterceptor.setDisabledForMessage(feedbackMessage);

			LinkContractInterceptor linkContractInterceptor = messagingTarget.getInterceptors().getInterceptor(LinkContractInterceptor.class);
			if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(feedbackMessage);

			MessagePolicyInterceptor messagePolicyInterceptor = messagingTarget.getInterceptors().getInterceptor(MessagePolicyInterceptor.class);
			if (messagePolicyInterceptor != null) messagePolicyInterceptor.setDisabledForMessage(feedbackMessage);

			messageAttributes = executionContext.getMessageAttributes();
			operationAttributes = executionContext.getOperationAttributes();

			// execute feedback messages

			messagingTarget.execute(feedbackMessage, feedbackMessageResult, executionContext);
		} finally {

			// after feedback: restore the execution context and messaging target

			if (messageAttributes != null) executionContext.setMessageAttributes(messageAttributes);
			if (operationAttributes != null) executionContext.setOperationAttributes(operationAttributes);
		}

		// done

		if (log.isDebugEnabled()) log.debug("Completed $get feedback to get target address/statement: " + targetAddress + ", " + targetStatement + ", message result: " + feedbackMessageResult);

		return feedbackMessageResult;
	}
}
