package xdi2.messaging.target.interceptor.impl.send;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
import xdi2.client.manipulator.impl.SetLinkContractMessageManipulator;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.index.Index;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageBase;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageTemplate;
import xdi2.messaging.instantiation.MessageInstantiation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.response.FullMessagingResponse;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.target.interceptor.impl.defer.DeferResultInterceptor;
import xdi2.messaging.target.interceptor.impl.linkcontract.LinkContractInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor can process $send operations.
 */
public class SendInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor, Prototype<SendInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(SendInterceptor.class);

	private XDIAgent xdiAgent;
	private Collection<Manipulator> manipulators;

	public SendInterceptor(XDIAgent xdiAgent, Collection<Manipulator> manipulators) {

		this.xdiAgent = xdiAgent;
		this.manipulators = manipulators;
	}

	public SendInterceptor() {

		this(new XDIBasicAgent(), new ArrayList<Manipulator> ());
	}

	/*
	 * Prototype
	 */

	@Override
	public SendInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		SendInterceptor interceptor = new SendInterceptor();

		// set the agent and manipulators

		interceptor.setXdiAgent(this.getXdiAgent());
		interceptor.setManipulators(this.getManipulators());

		// done

		return interceptor;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof SendOperation)) return InterceptorResult.DEFAULT;
		if (DeferResultInterceptor.hasDeferResult(executionContext, operation.getMessage())) return InterceptorResult.DEFAULT;

		// get forwarding message(s) or message template(s)

		List<MessageBase<?>> forwardingMessagesOrMessageTemplates = this.getForwardingMessagesOrMessageTemplates(operation, executionContext);
		if (log.isDebugEnabled()) log.debug("Trying to forward " + forwardingMessagesOrMessageTemplates.size() + " messages.");

		// send

		for (MessageBase<?> forwardingMessageOrMessageTemplate : forwardingMessagesOrMessageTemplates) {

			this.processSend(forwardingMessageOrMessageTemplate, (SendOperation) operation, operationResultGraph, executionContext);
		}

		// done

		return InterceptorResult.SKIP_MESSAGING_TARGET;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Helper methods
	 */

	public List<MessageBase<?>> getForwardingMessagesOrMessageTemplates(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		List<MessageBase<?>> forwardingMessagesOrMessageTemplates = SendInterceptor.getForwardingMessagesOrMessageTemplates(executionContext);
		if (forwardingMessagesOrMessageTemplates != null) return forwardingMessagesOrMessageTemplates;

		forwardingMessagesOrMessageTemplates = new ArrayList<MessageBase<?>> ();

		if (operation.getTargetXDIAddress() != null) forwardingMessagesOrMessageTemplates.add(this.forwardingMessageOrMessageTemplateFromTargetXDIAddress(operation.getTargetXDIAddress(), executionContext));
		if (operation.getTargetXdiInnerRoot() != null) forwardingMessagesOrMessageTemplates.addAll(this.forwardingMessagesOrMessageTemplatesFromTargetXdiInnerRoot(operation.getTargetXdiInnerRoot(), executionContext));
		if (forwardingMessagesOrMessageTemplates.isEmpty()) throw new Xdi2MessagingException("No forwarding messages(s) in operation " + operation, null, executionContext);

		SendInterceptor.putForwardingMessagesOrMessageTemplates(executionContext, forwardingMessagesOrMessageTemplates);

		return forwardingMessagesOrMessageTemplates;
	}

	private MessageBase<?> forwardingMessageOrMessageTemplateFromTargetXDIAddress(XDIAddress targetXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// use agent to obtain forwarding message

		XDIAddress forwardingMessageOrMessageTemplateXDIaddress = targetXDIAddress;

		ContextNode forwardingMessageOrMessageTemplateContextNode;

		try {

			// add manipulators

			Collection<Manipulator> manipulators = new ArrayList<Manipulator> ();
			// TODO: is it okay that we set the public link contract here, or are we supposed to rely on the XDIAgent's configuration for that?
			manipulators.add(new SetLinkContractMessageManipulator(PublicLinkContract.class));
			if (this.getManipulators() != null) manipulators.addAll(this.getManipulators());

			// get

			forwardingMessageOrMessageTemplateContextNode = this.getXdiAgent().get(forwardingMessageOrMessageTemplateXDIaddress, manipulators);
		} catch (Xdi2AgentException ex) {

			throw new Xdi2MessagingException("Agent problem while getting forwarding message or message template at address " + targetXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Client problem while getting forwarding message or message template at address " + targetXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		}

		// read forwarding message or message template

		if (forwardingMessageOrMessageTemplateContextNode == null) throw new Xdi2MessagingException("Cannot get forwarding message at address " + targetXDIAddress, null, executionContext);

		XdiEntity forwardingMessageOrMessageTemplateXdiEntity = XdiAbstractEntity.fromContextNode(forwardingMessageOrMessageTemplateContextNode);
		if (forwardingMessageOrMessageTemplateXdiEntity == null) throw new Xdi2MessagingException("Invalid forwarding message context node or message template context node at address " + targetXDIAddress, null, executionContext);

		MessageBase<?> forwardingMessageOrMessageTemplate = MessageBase.fromXdiEntity(forwardingMessageOrMessageTemplateXdiEntity);
		if (forwardingMessageOrMessageTemplate == null) throw new Xdi2MessagingException("Invalid forwarding message or message template at address " + targetXDIAddress, null, executionContext);

		// done

		return forwardingMessageOrMessageTemplate;
	}

	private List<? extends MessageBase<?>> forwardingMessagesOrMessageTemplatesFromTargetXdiInnerRoot(XdiInnerRoot targetXdiInnerRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		// get the inner graph

		Graph innerGraph = targetXdiInnerRoot.getInnerGraph();

		// return forwarding messages

		return new IteratorListMaker<Message> (MessageEnvelope.fromGraph(innerGraph).getMessages()).list();
	}

	private void processSend(MessageBase<?> forwardingMessageOrMessageTemplate, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (forwardingMessageOrMessageTemplate instanceof Message) {

			this.processSendMessage((Message) forwardingMessageOrMessageTemplate, operation, operationResultGraph, executionContext);
		} else if (forwardingMessageOrMessageTemplate instanceof MessageTemplate) {

			this.processSendMessageTemplate((MessageTemplate) forwardingMessageOrMessageTemplate, operation, operationResultGraph, executionContext);
		} else {

			throw new Xdi2RuntimeException("Unexpected message (template): " + forwardingMessageOrMessageTemplate.getClass().getSimpleName());
		}
	}

	private void processSendMessage(Message forwardingMessage, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Preparing to send forwarding message or message templates " + forwardingMessage);

		// clone forwarding message without new ID

		forwardingMessage = MessagingCloneUtil.cloneMessage(forwardingMessage, false);

		// find route for forwarding message or message template

		XDIArc toPeerRootXDIArc = forwardingMessage.getToPeerRootXDIArc();

		if (toPeerRootXDIArc == null) {

			throw new Xdi2MessagingException("Cannot route message without TO peer root.", null, executionContext);
		}

		XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> xdiClientRoute;

		try {

			xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);
		} catch (Xdi2AgentException ex) {

			throw new Xdi2MessagingException("Agent problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Client problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}

		if (xdiClientRoute == null) throw new Xdi2MessagingException("No route for " + toPeerRootXDIArc, null, executionContext);

		// disable link contracts if the forwarding message is routed back to us
		// TODO: instead of disabling it, we should use the outer message's link contract
		// TODO: can we also disable authentication in the forwarding message? e.g. a signature

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();

		if (forwardingMessage.getToPeerRootXDIArc() != null && forwardingMessage.getToPeerRootXDIArc().equals(messagingTarget.getOwnerPeerRootXDIArc())) {

			if (messagingTarget instanceof AbstractMessagingTarget) {

				LinkContractInterceptor linkContractInterceptor = ((AbstractMessagingTarget) messagingTarget).getInterceptors().getInterceptor(LinkContractInterceptor.class);
				if (linkContractInterceptor != null) linkContractInterceptor.setDisabledForMessage(forwardingMessage);
			}
		}

		// send the forwarding message

		XDIClient<? extends MessagingResponse> xdiClient = xdiClientRoute.constructXDIClient();

		try {

			// add manipulators

			if (xdiClient instanceof XDIAbstractClient) {

				Collection<Manipulator> manipulators = new ArrayList<Manipulator> ();
				if (this.getManipulators() != null) manipulators.addAll(this.getManipulators());

				((XDIAbstractClient<? extends MessagingResponse>) xdiClient).getManipulators().addManipulators(manipulators);
			}

			// send

			MessagingResponse forwardingMessagingResponse = xdiClient.send(forwardingMessage.getMessageEnvelope());

			if ((executionContext.getCurrentMessagingTarget() instanceof GraphMessagingTarget)) {

				GraphMessagingTarget graphMessagingTarget = ((GraphMessagingTarget) executionContext.getCurrentMessagingTarget());

				// TODO: correctly store any push contracts we got? 
				// TODO: use feedback message? or have member field private Graph targetGraph; ?
				// TODO: or have the XDIClient put it into our "origin" graph by adding a originGraph parameter to XDIClient?

				if (log.isDebugEnabled()) log.debug("Looking for deferred push link contracts from result graph of forwarding messaging response: " + forwardingMessagingResponse.getResultGraph());

				for (LinkContract pushLinkContract : FullMessagingResponse.getDeferredPushLinkContracts(forwardingMessagingResponse)) {

					if (log.isDebugEnabled()) log.debug("Obtained push link contract from result graph of forwarding messaging response " + pushLinkContract);

					// write push link contract and index into graph

					CopyUtil.copyContextNode(pushLinkContract.getContextNode(), graphMessagingTarget.getGraph(), null);
					XdiEntityCollection xdiLinkContractIndex = Index.getEntityIndex(graphMessagingTarget.getGraph(), XDILinkContractConstants.XDI_ARC_CONTRACT, true);
					Index.setEntityIndexAggregation(xdiLinkContractIndex, pushLinkContract.getXdiEntity().getXDIAddress());
				}

				// TODO: correctly store any contracts we got?
				// see comment in pushedMessagingResponse.getLinkContracts()

				if (log.isDebugEnabled()) log.debug("Looking for link contracts from result graph of forwarding messaging response " + forwardingMessagingResponse.getResultGraph());

				for (LinkContract linkContract : TransportMessagingResponse.getLinkContracts(forwardingMessagingResponse)) {

					if (log.isDebugEnabled()) log.debug("Obtained link contract from result graph of forwarding messaging response " + linkContract);

					// write link contract and index into graph

					CopyUtil.copyContextNode(linkContract.getContextNode(), graphMessagingTarget.getGraph(), null);
					XdiEntityCollection xdiLinkContractIndex = Index.getEntityIndex(graphMessagingTarget.getGraph(), XDILinkContractConstants.XDI_ARC_CONTRACT, true);
					Index.setEntityIndexAggregation(xdiLinkContractIndex, linkContract.getXdiEntity().getXDIAddress());
				}
			}

			// TODO: what if we get a FutureMessagingResponse from an XDIWebSocketClient?

			CopyUtil.copyGraph(forwardingMessagingResponse.getGraph(), operationResultGraph, null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while sending forwarding message " + forwardingMessage + " to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}
	}

	private void processSendMessageTemplate(MessageTemplate forwardingMessageTemplate, SendOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// set variable values

		Map<XDIArc, Object> variableValues = operation.getVariableValues();

		variableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM, operation.getMessage().getToXDIAddress());
		variableValues.put(XDILinkContractConstants.XDI_ARC_V_FROM_ROOT, operation.getMessage().getToPeerRootXDIArc());
		variableValues.put(XDILinkContractConstants.XDI_ARC_V_TO, operation.getMessage().getToPeerRootXDIArc());

		// instantiate message

		MessageInstantiation messageInstantiation = new MessageInstantiation(forwardingMessageTemplate);
		messageInstantiation.setVariableValues(variableValues);

		Message forwardingMessage;

		try {

			forwardingMessage = messageInstantiation.execute();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot instantiate message: " + ex.getMessage(), ex, executionContext);
		}

		// send message

		this.processSend(forwardingMessage, operation, operationResultGraph, executionContext);
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

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_FORWARDINGMESSAGESORMESSAGETEMPLATES_PER_OPERATION = SendInterceptor.class.getCanonicalName() + "#forwardingmessagesormessagetemplatesperoperation";
	private static final String EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION = SendInterceptor.class.getCanonicalName() + "#newidperoperation";

	@SuppressWarnings("unchecked")
	public static List<MessageBase<?>> getForwardingMessagesOrMessageTemplates(ExecutionContext executionContext) {

		return (List<MessageBase<?>>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_FORWARDINGMESSAGESORMESSAGETEMPLATES_PER_OPERATION);
	}

	public static void putForwardingMessagesOrMessageTemplates(ExecutionContext executionContext, List<MessageBase<?>> forwardingMessages) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_FORWARDINGMESSAGESORMESSAGETEMPLATES_PER_OPERATION, forwardingMessages);
	}

	public static Boolean getNewId(ExecutionContext executionContext) {

		return (Boolean) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION);
	}

	public static void putNewId(ExecutionContext executionContext, Boolean newId) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION, newId);
	}
}
