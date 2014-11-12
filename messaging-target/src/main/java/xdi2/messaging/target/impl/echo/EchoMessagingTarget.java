package xdi2.messaging.target.impl.echo;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.StatementHandler;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.AbstractMessagingTarget;

public class EchoMessagingTarget extends AbstractMessagingTarget {

	public EchoMessagingTarget() {

	}

	@Override
	public void execute(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		CopyUtil.copyGraph(messageEnvelope.getGraph(), executionResult.getResultGraph(), null);
	}

	@Override
	public AddressHandler getAddressHandler(XDIAddress targetAddress) throws Xdi2MessagingException {

		return null;
	}

	@Override
	public StatementHandler getStatementHandler(XDIStatement targetStatement) throws Xdi2MessagingException {

		return null;
	}
}
