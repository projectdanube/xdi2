package xdi2.messaging.target.interceptor.impl.push;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.features.push.PushCommand;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;

public class BasicPushCommandExecutor implements PushCommandExecutor {

	private static final Logger log = LoggerFactory.getLogger(BasicPushCommandExecutor.class);

	private XDIAgent xdiAgent;

	public BasicPushCommandExecutor(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}

	public BasicPushCommandExecutor() {

		this.xdiAgent = new XDIBasicAgent();
	}

	@Override
	public void executePush(PushCommand pushCommand, Set<Operation> pushCommandOperations, Map<Operation, XDIAddress> pushCommandXDIAddressMap, Map<Operation, List<XDIStatement>> pushCommandXDIStatementMap) throws Xdi2AgentException, Xdi2ClientException {

		for (XDIAddress toXDIAddress : pushCommand.getToXDIAddresses()) {

			// find route to this target

			XDIClientRoute<?> route = this.getXdiAgent().route(toXDIAddress);

			if (route == null) {

				log.warn("No route for " + toXDIAddress + ". Skipping push command.");
				continue;
			}

			// client construction step

			XDIClient xdiClient = route.constructXDIClient();

			// message envelope construction step

			MessageEnvelope messageEnvelope = route.constructMessageEnvelope();
			Message message = route.constructMessage(messageEnvelope);

			for (Operation pushCommandOperation : pushCommandOperations) {

				XDIAddress pushCommandXDIAddress = pushCommandXDIAddressMap.get(pushCommandOperation);
				List<XDIStatement> pushCommandXDIStatements = pushCommandXDIStatementMap.get(pushCommandOperation);

				if (pushCommandXDIAddress != null) message.createOperation(pushCommandOperation.getOperationXDIAddress(), pushCommandXDIAddress);
				if (pushCommandXDIStatements != null) message.createOperation(pushCommandOperation.getOperationXDIAddress(), pushCommandXDIStatements.iterator());
			}

			// send the message envelope

			xdiClient.send(messageEnvelope);
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
