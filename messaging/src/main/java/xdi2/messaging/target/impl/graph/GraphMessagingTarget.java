package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.impl.StatementHandler;

/**
 * An XDI messaging target backed by some implementation of the Graph interface.
 * 
 * @author markus
 */
public class GraphMessagingTarget extends AbstractMessagingTarget {

	private Graph graph;
	private GraphStatementHandler graphStatementHandler;

	public GraphMessagingTarget() {

		super();

		this.graph = null;
		this.graphStatementHandler = null;
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
	public XRI3Segment getOwner() {

		Relation relation = this.getGraph().getRootContextNode().getRelation(XDIDictionaryConstants.XRI_S_IS_IS);
		if (relation == null) return null;

		return relation.getRelationXri();
	}

	@Override
	public void before(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(messageEnvelope, executionContext);

		this.graph.beginTransaction();
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(messageEnvelope, executionContext);

		this.graph.commitTransaction();
	}

	@Override
	public void exception(MessageEnvelope messageEnvelope, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

		super.exception(messageEnvelope, executionContext, ex);

		this.graph.rollbackTransaction();
	}

	@Override
	public boolean executeGetOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetAddress, false);

		if (contextNode != null) {

			CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeDelOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetAddress, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + targetAddress, null, operation);

		contextNode.delete();

		return true;
	}

	@Override
	public StatementHandler getStatementHandler(Statement statement) throws Xdi2MessagingException {

		return this.graphStatementHandler;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public void setGraph(Graph graph) {

		this.graph = graph;
		this.graphStatementHandler = new GraphStatementHandler(graph);
	}
}
