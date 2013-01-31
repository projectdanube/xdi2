package xdi2.messaging.target.impl.echo;

import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.StatementHandler;

public class EchoMessagingTarget extends AbstractMessagingTarget {

	public EchoMessagingTarget() {

	}

	@Override
	public void execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		CopyUtil.copyGraph(messageEnvelope.getGraph(), messageResult.getGraph(), null);
	}

	@Override
	public AddressHandler getAddressHandler(XDI3Segment targetAddress) throws Xdi2MessagingException {

		return null;
	}

	@Override
	public StatementHandler getStatementHandler(XDI3Statement targetStatement) throws Xdi2MessagingException {

		return null;
	}
}
