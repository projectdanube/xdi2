package xdi2.messaging.target.impl.graph;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.features.roots.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.StatementHandler;

/**
 * An XDI messaging target backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingTarget extends AbstractMessagingTarget implements Prototype<GraphMessagingTarget> {

	private Graph graph;
	//	private GraphAddressHandler graphAddressHandler;
	//	private GraphStatementHandler graphStatementHandler;
	private GraphContextHandler graphContextHandler;

	public GraphMessagingTarget() {

		super();

		this.graph = null;
		//		this.graphAddressHandler = null;
		//		this.graphStatementHandler = null;
		this.graphContextHandler = null;
	}

	@Override
	public void init() throws Exception {

		super.init();

		// read owner
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();

		this.graph.close();
	}

	@Override
	public XDI3Segment getOwnerAddress() {

		XdiPeerRoot selfPeerRoot = XdiLocalRoot.findLocalRoot(this.getGraph()).getSelfPeerRoot();
		if (selfPeerRoot == null) return null;

		return selfPeerRoot.getContextNode().getXri();
	}

	@Override
	public void before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(messageEnvelope, messageResult, executionContext);

		this.graph.beginTransaction();
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(messageEnvelope, messageResult, executionContext);

		this.graph.commitTransaction();
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Xdi2MessagingException ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, messageResult, executionContext, ex);

		this.graph.rollbackTransaction();
	}

	@Override
	public AddressHandler getAddressHandler(XDI3Segment address) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	@Override
	public StatementHandler getStatementHandler(XDI3Statement statement) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	@Override
	public GraphMessagingTarget instanceFor(PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new messaging target

		MessagingTarget messagingTarget = new GraphMessagingTarget();

		// instantiate new graph

		Graph graph;

		try {

			String identifier = XdiPeerRoot.createPeerRootArcXri(prototypingContext.getOwner()).toString();

			graph = this.getGraph().getGraphFactory().openGraph(identifier);
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
