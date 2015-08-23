package xdi2.messaging.target.interceptor.impl.send;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.impl.SetLinkContractMessageManipulator;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor can process $send operations.
 */
public class SendInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor, Prototype<SendInterceptor> {

	private XDIAgent xdiAgent;

	public SendInterceptor(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}

	public SendInterceptor() {

		this(new XDIBasicAgent());
	}

	/*
	 * Prototype
	 */

	@Override
	public SendInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		SendInterceptor contributor = new SendInterceptor();

		// set the agent

		contributor.setXdiAgent(this.getXdiAgent());

		// done

		return contributor;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof SendOperation)) return InterceptorResult.DEFAULT;

		// get link contract template(s)

		List<Message> messages = this.getMessages(operation, executionContext);

		// send

		for (Message message : messages) {

			this.send(message, operation, operationResultGraph, executionContext);
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

	public List<Message> getMessages(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		List<Message> messages = SendInterceptor.getMessages(executionContext);
		if (messages != null) return messages;

		if (messages == null && operation.getTargetXDIAddress() != null) messages = this.messageFromTargetXDIAddress(operation.getTargetXDIAddress(), executionContext);
		if (messages == null && operation.getTargetXdiInnerRoot() != null) messages = this.messagesFromTargetXdiInnerRoot(operation.getTargetXdiInnerRoot(), executionContext);
		if (messages == null) throw new Xdi2MessagingException("No messages(s) in operation " + operation, null, executionContext);

		SendInterceptor.putMessages(executionContext, messages);

		return messages;
	}

	private List<Message> messageFromTargetXDIAddress(XDIAddress targetXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// use agent to obtain message

		XDIAddress messageXDIaddress = targetXDIAddress;

		ContextNode messageContextNode;

		try {

			messageContextNode = this.getXdiAgent().get(
					messageXDIaddress,
					new SetLinkContractMessageManipulator(PublicLinkContract.class));
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Unable to obtain message at address " + targetXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		}

		if (messageContextNode == null) throw new Xdi2MessagingException("Cannot find message at address " + targetXDIAddress, null, executionContext);

		XdiEntity messageXdiEntity = XdiAbstractEntity.fromContextNode(messageContextNode);
		if (messageXdiEntity == null) throw new Xdi2MessagingException("Invalid message context node at address " + targetXDIAddress, null, executionContext);

		Message message = Message.fromXdiEntity(messageXdiEntity);
		if (message == null) throw new Xdi2MessagingException("Invalid message at address " + targetXDIAddress, null, executionContext);

		// clone message with new ID

		message = MessagingCloneUtil.cloneMessage(message, true);

		// done

		return Collections.singletonList(message);
	}

	private List<Message> messagesFromTargetXdiInnerRoot(XdiInnerRoot targetXdiInnerRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		// get the inner graph

		Graph innerGraph = targetXdiInnerRoot.getInnerGraph();

		// clone messages without new ID

		List<Message> messages = new ArrayList<Message> ();

		for (Message message : MessageEnvelope.fromGraph(innerGraph).getMessages()) {

			messages.add(MessagingCloneUtil.cloneMessage(message, true));
		}

		// return messages

		return new IteratorListMaker<Message> (MessageEnvelope.fromGraph(innerGraph).getMessages()).list();
	}

	private void send(Message message, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find route for message

		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		XDIClientRoute<? extends XDIClient> xdiClientRoute;

		try {

			xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);
		} catch (Xdi2AgentException ex) {

			throw new Xdi2MessagingException("Agent problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Client problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}

		// send the message

		XDIClient xdiClient = xdiClientRoute.constructXDIClient();

		try {

			MessagingResponse forwardingMessagingResponse = xdiClient.send(message.getMessageEnvelope());
			CopyUtil.copyGraph(forwardingMessagingResponse.getResultGraph(), operationResultGraph, null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while sending message " + message + " to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
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

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_MESSAGES_PER_OPERATION = SendInterceptor.class.getCanonicalName() + "#messagesperoperation";
	private static final String EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION = SendInterceptor.class.getCanonicalName() + "#newidperoperation";

	@SuppressWarnings("unchecked")
	public static List<Message> getMessages(ExecutionContext executionContext) {

		return (List<Message>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_MESSAGES_PER_OPERATION);
	}

	public static void putMessages(ExecutionContext executionContext, List<Message> messages) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_MESSAGES_PER_OPERATION, messages);
	}

	public static Boolean getNewId(ExecutionContext executionContext) {

		return (Boolean) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION);
	}

	public static void putNewId(ExecutionContext executionContext, Boolean newId) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_NEWID_PER_OPERATION, newId);
	}
}
