package xdi2.messaging.target.impl.graph;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.StatementHandler;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.AbstractMessagingTarget;

/**
 * An XDI messaging target backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingTarget extends AbstractMessagingTarget implements Prototype<GraphMessagingTarget> {

	private static final Logger log = LoggerFactory.getLogger(GraphMessagingTarget.class);

	private Graph graph;
	private GraphContextHandler graphContextHandler;

	public GraphMessagingTarget() {

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
	public void setOwnerPeerRootXDIArc(XDIArc ownerPeerRootXDIArc) {

		GraphUtil.setOwnerPeerRootXDIArc(this.getGraph(), ownerPeerRootXDIArc);
	}

	@Override
	public void before(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(messageEnvelope, executionResult, executionContext);

		this.graph.beginTransaction();
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(messageEnvelope, executionResult, executionContext);

		this.graph.commitTransaction();
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionResult executionResult, ExecutionContext executionContext, Xdi2MessagingException ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, executionResult, executionContext, ex);

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

	@Override
	public GraphMessagingTarget instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new messaging target

		MessagingTarget messagingTarget = new GraphMessagingTarget();

		// instantiate new graph

		Graph graph;

		try {

			String identifier = XdiPeerRoot.createPeerRootXDIArc(prototypingContext.getOwnerXDIAddress()).toString();
			graph = this.getGraph().getGraphFactory().openGraph(identifier);

			if (log.isDebugEnabled()) log.debug("Opened graph " + graph.getClass().getCanonicalName() + " for " + identifier);
		} catch (IOException ex) {

			throw new Xdi2MessagingException("Cannot open graph: " + ex.getMessage(), ex, null);
		}

		((GraphMessagingTarget) messagingTarget).setGraph(graph);

		// done

		return (GraphMessagingTarget) messagingTarget;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public GraphContextHandler getGraphContextHandler() {

		return this.graphContextHandler;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
		//		this.graphAddressHandler = new GraphAddressHandler(graph);
		//		this.graphStatementHandler = new GraphStatementHandler(graph);
		this.graphContextHandler = new GraphContextHandler(graph);
	}
}
