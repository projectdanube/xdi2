package xdi2.messaging.target.impl.graph;

import java.util.HashMap;

import javax.security.auth.Subject;
import javax.sql.rowset.Predicate;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.impl.XRI3;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractMessagingTarget;
import xdi2.messaging.target.impl.StatementHandler;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;

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

		this.getMessageEnvelopeInterceptors().add(new TransactionMessageEnvelopeInterceptor());
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();

		this.graph.close();
	}

	@Override
	public boolean executeGetOperation(XRI3Segment targetXri, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetXri, false);

		if (contextNode != null) {

			CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeDelOperation(XRI3Segment targetXri, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + targetXri);

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

	@Override
	public void before(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(messageEnvelope, executionContext);

		GraphExecutionContext.setAffectedSubjectsPerMessageEnvelope(executionContext, new HashMap<XRI3, Subject> ());
	}

	@Override
	public void before(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(message, executionContext);
	}

	@Override
	public void before(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(operation, executionContext);

		GraphExecutionContext.setAffectedSubjectsPerOperation(executionContext, new HashMap<XRI3, Subject> ());
		GraphExecutionContext.setVariableSubjectsPerOperation(executionContext, new HashMap<XRI3Segment, Subject> ());
		GraphExecutionContext.setVariablePredicatesPerOperation(executionContext, new HashMap<XRI3Segment, Predicate> ());
	}

	private class TransactionMessageEnvelopeInterceptor implements MessageEnvelopeInterceptor {

		public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			GraphMessagingTarget.this.graph.beginTransaction();

			return false;
		}

		public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			GraphMessagingTarget.this.graph.commitTransaction();

			return false;
		}

		public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

			GraphMessagingTarget.this.graph.rollbackTransaction();
		}
	}
}
