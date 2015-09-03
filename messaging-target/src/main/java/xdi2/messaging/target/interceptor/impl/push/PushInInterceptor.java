package xdi2.messaging.target.interceptor.impl.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphAware;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.response.FullMessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor can process $push operations.
 */
public class PushInInterceptor extends AbstractInterceptor<MessagingTarget> implements GraphAware, OperationInterceptor, Prototype<PushInInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(PushInInterceptor.class);

	private Graph targetGraph;

	public PushInInterceptor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public PushInInterceptor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public PushInInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new interceptor

		PushInInterceptor interceptor = new PushInInterceptor();

		// set the graph

		interceptor.setTargetGraph(this.getTargetGraph());

		// done

		return interceptor;
	}

	/*
	 * GraphAware
	 */

	@Override
	public void setGraph(Graph graph) {

		if (this.getTargetGraph() == null) this.setTargetGraph(graph);
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof PushOperation)) return InterceptorResult.DEFAULT;

		// get forwarding message(s)

		List<Message> pushMessages = this.getPushMessages(operation, executionContext);

		// send

		for (Message pushMessage : pushMessages) {

			this.processPush(pushMessage, operation, operationResultGraph, executionContext);
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

	public List<Message> getPushMessages(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		List<Message> pushMessages = PushInInterceptor.getPushMessages(executionContext);
		if (pushMessages != null) return pushMessages;

		if (pushMessages == null && operation.getTargetXDIAddress() != null) pushMessages = this.pushMessageFromTargetXDIAddress(operation.getTargetXDIAddress(), executionContext);
		if (pushMessages == null && operation.getTargetXdiInnerRoot() != null) pushMessages = this.pushMessagesFromTargetXdiInnerRoot(operation.getTargetXdiInnerRoot(), executionContext);
		if (pushMessages == null) throw new Xdi2MessagingException("No push messages(s) in operation " + operation, null, executionContext);

		PushInInterceptor.putPushMessages(executionContext, pushMessages);

		return pushMessages;
	}

	private List<Message> pushMessageFromTargetXDIAddress(XDIAddress targetXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// TODO: can't obtain push message from target address

		return Collections.emptyList();
	}

	private List<Message> pushMessagesFromTargetXdiInnerRoot(XdiInnerRoot targetXdiInnerRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		// get the inner graph

		Graph innerGraph = targetXdiInnerRoot.getInnerGraph();

		// clone push messages without new ID

		List<Message> pushMessages = new ArrayList<Message> ();

		for (Message message : MessageEnvelope.fromGraph(innerGraph).getMessages()) {

			pushMessages.add(MessagingCloneUtil.cloneMessage(message, true));
		}

		// return forwarding messages

		return new IteratorListMaker<Message> (MessageEnvelope.fromGraph(innerGraph).getMessages()).list();
	}

	private void processPush(Message pushMessage, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Preparing to process push message " + pushMessage);

		// TODO: how exactly is the $push message a response, and to what?

		FullMessagingResponse pushMessagingResponse = FullMessagingResponse.fromMessageEnvelope(pushMessage.getMessageEnvelope());

		// TODO: correctly store any push contracts we got? 
		// TODO: use feedback message? or have member field private Graph targetGraph; ?
		// TODO: or have the XDIClient put it into our "origin" graph by adding a originGraph parameter to XDIClient?

		if ((executionContext.getCurrentMessagingTarget() instanceof GraphMessagingTarget)) {

			GraphMessagingTarget graphMessagingTarget = ((GraphMessagingTarget) executionContext.getCurrentMessagingTarget());

			for (LinkContract pushLinkContract : pushMessagingResponse.getPushLinkContracts()) {

				CopyUtil.copyContextNode(pushLinkContract.getContextNode(), graphMessagingTarget.getGraph(), null);
			}
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_PUSHMESSAGES_PER_OPERATION = PushInInterceptor.class.getCanonicalName() + "#pushmessagesperoperation";

	@SuppressWarnings("unchecked")
	public static List<Message> getPushMessages(ExecutionContext executionContext) {

		return (List<Message>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_PUSHMESSAGES_PER_OPERATION);
	}

	public static void putPushMessages(ExecutionContext executionContext, List<Message> pushMessages) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_PUSHMESSAGES_PER_OPERATION, pushMessages);
	}
}
