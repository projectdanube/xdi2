package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.features.remoteroots.RemoteRoots;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.AddressHandler;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.StatementHandler;

/**
 * An XDI messaging target backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingTarget extends AbstractMessagingTarget {

	private Graph graph;
	private GraphAddressHandler graphAddressHandler;
	private GraphStatementHandler graphStatementHandler;
	private GraphContextHandler graphContextHandler;

	public GraphMessagingTarget() {

		super();

		this.graph = null;
		this.graphAddressHandler = null;
		this.graphStatementHandler = null;
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
	public XRI3Segment getOwnerAuthority() {

		ContextNode selfRemoteRootContextNode = RemoteRoots.getSelfRemoteRootContextNode(this.getGraph());
		if (selfRemoteRootContextNode == null) return null;

		return selfRemoteRootContextNode.getXri();
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
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, messageResult, executionContext, ex);

		this.graph.rollbackTransaction();
	}

	@Override
	public AddressHandler getAddressHandler(XRI3Segment address) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	@Override
	public StatementHandler getStatementHandler(Statement statement) throws Xdi2MessagingException {

		return this.graphContextHandler;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public GraphContextHandler getGraphContextHandler() {

		return this.graphContextHandler;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
		this.graphAddressHandler = new GraphAddressHandler(graph);
		this.graphStatementHandler = new GraphStatementHandler(graph);
		this.graphContextHandler = new GraphContextHandler(graph);
	}
}
