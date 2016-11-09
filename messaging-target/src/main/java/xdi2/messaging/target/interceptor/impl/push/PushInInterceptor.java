package xdi2.messaging.target.interceptor.impl.push;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.index.Index;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.PushOperation;
import xdi2.messaging.operations.SetOperation;
import xdi2.messaging.response.FullMessagingResponse;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;
import xdi2.messaging.target.interceptor.impl.defer.DeferResultInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This interceptor can process $push operations.
 */
public class PushInInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor, Prototype<PushInInterceptor> {

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

		// done

		return this;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof PushOperation)) return InterceptorResult.DEFAULT;
		if (DeferResultInterceptor.hasDeferResult(executionContext, operation.getMessage())) return InterceptorResult.DEFAULT;

		// get pushed message(s)

		List<Message> pushedMessages = this.getPushedMessages(operation, executionContext);

		// send

		for (Message pushedMessage : pushedMessages) {

			this.processPush(pushedMessage, operation, operationResultGraph, executionContext);
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

	public List<Message> getPushedMessages(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		List<Message> pushedMessages = PushInInterceptor.getPushedMessages(executionContext);
		if (pushedMessages != null) return pushedMessages;

		if (pushedMessages == null && operation.getTargetXDIAddress() != null) pushedMessages = this.pushedMessageFromTargetXDIAddress(operation.getTargetXDIAddress(), executionContext);
		if (pushedMessages == null && operation.getTargetXdiInnerRoot() != null) pushedMessages = this.pushedMessagesFromTargetXdiInnerRoot(operation.getTargetXdiInnerRoot(), executionContext);
		if (pushedMessages == null) throw new Xdi2MessagingException("No pushed messages(s) in operation " + operation, null, executionContext);

		PushInInterceptor.putPushedMessages(executionContext, pushedMessages);

		return pushedMessages;
	}

	private List<Message> pushedMessageFromTargetXDIAddress(XDIAddress targetXDIAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		// TODO: can't obtain pushed message from target address

		return Collections.emptyList();
	}

	private List<Message> pushedMessagesFromTargetXdiInnerRoot(XdiInnerRoot targetXdiInnerRoot, ExecutionContext executionContext) throws Xdi2MessagingException {

		// get the inner graph

		Graph innerGraph = targetXdiInnerRoot.getInnerGraph();

		// clone pushed messages without new ID

		List<Message> pushedMessages = new ArrayList<Message> ();

		for (Message message : MessageEnvelope.fromGraph(innerGraph).getMessages()) {

			pushedMessages.add(MessagingCloneUtil.cloneMessage(message, false));
		}

		// return pushed messages

		return new IteratorListMaker<Message> (MessageEnvelope.fromGraph(innerGraph).getMessages()).list();
	}

	private void processPush(Message pushedMessage, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Preparing to process pushed message " + pushedMessage);

		// TODO: how exactly is the $push message a response, and to what?

		FullMessagingResponse pushedMessagingResponse = FullMessagingResponse.fromMessageEnvelope(operation.getMessageEnvelope());

		// TODO: correctly store statements of $set operation?

		Graph resultGraph = ((TransportMessagingResponse) pushedMessagingResponse).getResultGraph();

		if (resultGraph != null) {

			MessageEnvelope innerMe = MessageEnvelope.fromGraph(operation.getTargetXdiInnerRoot().getInnerGraph());
			Message innerM = innerMe == null ? null : innerMe.getMessages().next();
			Iterator<SetOperation> innerSOs = innerM == null ? new EmptyIterator<SetOperation> () : innerM.getSetOperations();
			SetOperation innerSO = innerSOs.next();
			XdiInnerRoot innerIR = innerSO == null ? null : innerSO.getTargetXdiInnerRoot();

			if (innerIR != null) {

				CopyUtil.copyGraph(innerIR.getInnerGraph(), this.getTargetGraph(executionContext), null);
			}
		}

		// TODO: correctly store any contracts we got?
		// see comment in pushedMessagingResponse.getLinkContracts()

		if (log.isDebugEnabled()) log.debug("Looking for link contracts from result graph of pushed messaging response " + pushedMessagingResponse.getResultGraph());

		for (LinkContract pushLinkContract : TransportMessagingResponse.getLinkContracts(pushedMessagingResponse)) {

			if (log.isDebugEnabled()) log.debug("Obtained link contract from result graph of pushed messaging response " + pushLinkContract);

			// write link contract and index into target graph

			CopyUtil.copyContextNode(pushLinkContract.getContextNode(), this.getTargetGraph(executionContext), null);
			XdiEntityCollection xdiLinkContractIndex = Index.getEntityIndex(this.getTargetGraph(executionContext), XDILinkContractConstants.XDI_ARC_CONTRACT, true);
			Index.setEntityIndexAggregation(xdiLinkContractIndex, pushLinkContract.getXdiEntity().getXDIAddress());
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph(ExecutionContext executionContext) {

		Graph targetGraph = this.getTargetGraph();
		if (targetGraph == null) targetGraph = executionContext.getCurrentGraph();
		if (targetGraph == null) throw new NullPointerException("No target graph.");

		return targetGraph;
	}

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_PUSHEDMESSAGES_PER_OPERATION = PushInInterceptor.class.getCanonicalName() + "#pushedmessagesperoperation";

	@SuppressWarnings("unchecked")
	public static List<Message> getPushedMessages(ExecutionContext executionContext) {

		return (List<Message>) executionContext.getOperationAttribute(EXECUTIONCONTEXT_KEY_PUSHEDMESSAGES_PER_OPERATION);
	}

	public static void putPushedMessages(ExecutionContext executionContext, List<Message> pushedMessages) {

		executionContext.putOperationAttribute(EXECUTIONCONTEXT_KEY_PUSHEDMESSAGES_PER_OPERATION, pushedMessages);
	}
}
