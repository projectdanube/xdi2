package xdi2.messaging.container.impl.graph;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.container.AddressHandler;
import xdi2.messaging.container.MessagingContainer;
import xdi2.messaging.container.Prototype;
import xdi2.messaging.container.StatementHandler;
import xdi2.messaging.container.exceptions.Xdi2MessagingException;
import xdi2.messaging.container.execution.ExecutionContext;
import xdi2.messaging.container.execution.ExecutionResult;
import xdi2.messaging.container.impl.AbstractMessagingContainer;
import xdi2.messaging.container.interceptor.impl.HasInterceptor;
import xdi2.messaging.container.interceptor.impl.RefInterceptor;
import xdi2.messaging.operations.Operation;

/**
 * An XDI messaging container backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingContainer extends AbstractMessagingContainer implements Prototype<GraphMessagingContainer> {

	private static final Logger log = LoggerFactory.getLogger(GraphMessagingContainer.class);

	private Graph graph;
	private GraphContextHandler graphContextHandler;

	public GraphMessagingContainer(Graph graph) {

		super();

		this.graph = graph;
		this.graphContextHandler = new GraphContextHandler(graph);
	}

	public GraphMessagingContainer() {

		super();

		this.graph = null;
		this.graphContextHandler = null;
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();

		this.graph.close();
	}

	@Override
	public XDIArc getOwnerPeerRootXDIArc() {

		return GraphUtil.getOwnerPeerRootXDIArc(this.getGraph());
	}

	@Override
	public XDIAddress getOwnerXDIAddress() {

		return GraphUtil.getOwnerXDIAddress(this.getGraph());
	}

	@Override
	public void setOwnerPeerRootXDIArc(XDIArc ownerPeerRootXDIArc) {

		GraphUtil.setOwnerPeerRootXDIArc(this.getGraph(), ownerPeerRootXDIArc);
	}

	@Override
	public void setOwnerXDIAddress(XDIAddress ownerXDIAddress) {

		GraphUtil.setOwnerXDIAddress(this.getGraph(), ownerXDIAddress);
	}

	@Override
	public boolean ownsPeerRootXDIArc(XDIArc peerRootXDIArc) {

		return GraphUtil.ownsPeerRootXDIArc(this.getGraph(), peerRootXDIArc);
	}

	@Override
	public boolean before(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		this.graph.beginTransaction();

		return super.before(messageEnvelope, executionContext, executionResult);
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		this.graph.commitTransaction();

		super.after(messageEnvelope, executionContext, executionResult);
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, ExecutionResult executionResult, Xdi2MessagingException ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, executionContext, executionResult, ex);

		this.graph.rollbackTransaction();
	}

	@Override
	public AddressHandler getAddressHandler(XDIAddress XDIaddress) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	@Override
	public StatementHandler getStatementHandler(XDIStatement statement) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	/*
	 * Prototype
	 */

	@Override
	public GraphMessagingContainer instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new messaging container

		MessagingContainer messagingContainer = new GraphMessagingContainer();

		// instantiate new graph

		Graph graph;

		try {

			String identifier = XdiPeerRoot.createPeerRootXDIArc(prototypingContext.getOwnerXDIAddress()).toString();
			graph = this.getGraph().getGraphFactory().openGraph(identifier);

			if (log.isDebugEnabled()) log.debug("Opened graph " + graph.getClass().getCanonicalName() + " for " + identifier);
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Cannot open graph: " + ex.getMessage(), ex, null);
		}

		((GraphMessagingContainer) messagingContainer).setGraph(graph);

		// done

		return (GraphMessagingContainer) messagingContainer;
	}

	/*
	 * Convenience methods
	 */

	public static ContextNode get(Graph graph, XDIAddress XDIaddress) {

		GraphMessagingContainer graphMessagingContainer = new GraphMessagingContainer(graph);
		graphMessagingContainer.getInterceptors().addInterceptor(new RefInterceptor());
		graphMessagingContainer.getInterceptors().addInterceptor(new HasInterceptor());

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage();
		Operation operation = message.createGetOperation(XDIaddress);
		operation.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEREF, Boolean.TRUE);
		operation.setParameter(XDIMessagingConstants.XDI_ADD_OPERATION_PARAMETER_DEHAS, Boolean.TRUE);

		ExecutionContext executionContext = ExecutionContext.createExecutionContext();
		ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

		try {

			graphMessagingContainer.execute(message.getMessageEnvelope(), executionContext, executionResult);
		} catch (Xdi2MessagingException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
		
		Graph resultGraph = executionResult.makeLightMessagingResponse().getResultGraph();
		return resultGraph.getDeepContextNode(XDIaddress);
	}

	@SuppressWarnings("unchecked")
	public static <T extends XdiContext<T>> XdiContext<T> getXdiContext(Graph graph, XDIAddress XDIaddress) {

		ContextNode contextNode = get(graph, XDIaddress);
		if (contextNode == null) return null;

		return (XdiContext<T>) XdiAbstractContext.fromContextNode(contextNode);
	}

	/*
	 * Getters and setters
	 */

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
		this.graphContextHandler = new GraphContextHandler(graph);
	}
}
