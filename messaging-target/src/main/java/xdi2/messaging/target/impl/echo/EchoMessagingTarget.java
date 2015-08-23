package xdi2.messaging.target.impl.echo;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
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
	public void execute(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		Graph finishedResultGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyGraph(messageEnvelope.getGraph(), finishedResultGraph, null);

		executionResult.finish(finishedResultGraph);
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
