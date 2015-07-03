package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;

import xdi2.client.agent.XDIAgent;
import xdi2.core.features.push.PushCommand;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.Operation;

public class BasicPushCommandExecutor implements PushCommandExecutor {

	private XDIAgent xdiAgent;

	@Override
	public void executePush(PushCommand pushCommand, Map<Operation, XDIAddress> pushCommandXDIAddressMap, Map<Operation, List<XDIStatement>> pushCommandXDIStatementMap) {

		for (XDIAddress targetXDIAddress : pushCommand.getTargetXDIAddresses()) {
			
			this.getXdiAgent();
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}
}
