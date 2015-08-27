package xdi2.messaging.target.interceptor.impl.push;

import java.util.ArrayList;
import java.util.Collection;
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
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.manipulator.Manipulator;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;

public class BasicPushGateway implements PushGateway {

	private static final Logger log = LoggerFactory.getLogger(BasicPushGateway.class);

	private XDIAgent xdiAgent;
	private Collection<Manipulator> manipulators;

	public BasicPushGateway(XDIAgent xdiAgent, Collection<Manipulator> manipulators) {

		this.xdiAgent = xdiAgent;
		this.manipulators = manipulators;
	}

	public BasicPushGateway() {

		this(new XDIBasicAgent(), null);
	}

	@Override
	public void executePush(MessagingTarget messagingTarget, GenericLinkContract pushLinkContract, Set<Operation> pushLinkContractOperations, Map<Operation, XDIAddress> pushLinkContractXDIAddressMap, Map<Operation, List<XDIStatement>> pushLinkContractXDIStatementMap) throws Xdi2AgentException, Xdi2ClientException {

		List<Exception> exs = new ArrayList<Exception> ();

		for (XDIArc toPeerRootXDIArc : pushLinkContract.getPushToPeerRootXDIArcs()) {

			if (log.isDebugEnabled()) log.debug("Trying to push to " + toPeerRootXDIArc);

			try {

				// find route to this target

				XDIClientRoute<?> xdiClientRoute;

				try {

					xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);

					if (xdiClientRoute == null) {

						log.warn("No route for " + toPeerRootXDIArc + ". Skipping push command.");
						continue;
					}
				} catch (Xdi2AgentException ex) {

					log.error("Agent problem while routing to target " + toPeerRootXDIArc + ": " + ex.getMessage() + ". Skipping", ex);
					exs.add(ex);
					continue;
				} catch (Xdi2ClientException ex) {

					log.error("Client problem while routing to target " + toPeerRootXDIArc + ": " + ex.getMessage() + ". Skipping.", ex);
					exs.add(ex);
					continue;
				}

				// client construction step

				XDIClient<?> xdiClient = xdiClientRoute.constructXDIClient();

				// add manipulators if supported

				if (xdiClient instanceof XDIAbstractClient && this.getManipulators() != null) {

					((XDIAbstractClient<? extends MessagingResponse>) xdiClient).getManipulators().addManipulators(this.getManipulators());
				}

				// message envelope construction step

				MessageEnvelope messageEnvelope = xdiClientRoute.createMessageEnvelope();
				Message message = xdiClientRoute.createMessage(messageEnvelope, pushLinkContract.getAuthorizingAuthority());
				message.setFromPeerRootXDIArc(messagingTarget.getOwnerPeerRootXDIArc());
				message.setToPeerRootXDIArc(toPeerRootXDIArc);
				message.setLinkContract(pushLinkContract);

				for (Operation pushOperation : pushLinkContractOperations) {

					XDIAddress pushXDIAddress = pushLinkContractXDIAddressMap == null ? null : pushLinkContractXDIAddressMap.get(pushOperation);
					List<XDIStatement> pushXDIStatements = pushLinkContractXDIStatementMap == null ? null : pushLinkContractXDIStatementMap.get(pushOperation);

					if (pushXDIAddress != null) message.createOperation(pushOperation.getOperationXDIAddress(), pushXDIAddress);
					if (pushXDIStatements != null) message.createOperation(pushOperation.getOperationXDIAddress(), pushXDIStatements.iterator());
				}

				// send the message envelope

				xdiClient.send(messageEnvelope);

				// close the client
				// TODO: when do we close the client?

				if (xdiClient == null) xdiClient.close();

				// done

				if (log.isDebugEnabled()) log.debug("Successfully pushed to " + toPeerRootXDIArc);
			} catch (Exception ex) {

				log.warn("Failed to push to " + toPeerRootXDIArc + ": " + ex.getMessage() + ". Skipping.", ex);
				exs.add(ex);
			}
		}

		// raise exception if any

		if (exs.size() > 0) {

			if (exs.size() == 1) throw new Xdi2ClientException("Push " + pushLinkContractXDIAddressMap + " failed: " + exs.get(0).getMessage(), exs.get(0));

			throw new Xdi2ClientException("Multiple pushes " + pushLinkContractXDIAddressMap + " failed. First failed push is: " + exs.get(0), exs.get(0));
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

	public Collection<Manipulator> getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(Collection<Manipulator> manipulators) {

		this.manipulators = manipulators;
	}
}
