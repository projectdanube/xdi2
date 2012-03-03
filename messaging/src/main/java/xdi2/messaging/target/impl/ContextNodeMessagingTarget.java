package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.ContextNodeInterceptor;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * The ContextMessagingTarget allows subclasses to return ContextHandler
 * implementations for context nodes in the operation.
 * 
 * Subclasses must return appropriate ContextHandlers for the given
 * context nodes of the operation. To do this, subclasses override
 * the getContextHandler() method. 
 * 
 * @author markus
 */
public abstract class ContextNodeMessagingTarget extends AbstractMessagingTarget {

	private static final Logger log = LoggerFactory.getLogger(ContextNodeMessagingTarget.class);

	protected List<ContextNodeInterceptor> contextInterceptors;

	public ContextNodeMessagingTarget() {

		super();

		this.contextInterceptors = new ArrayList<ContextNodeInterceptor> ();
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public final boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();

		// look at the operation

		Relation operationRelation = operation.getRelation();
		ContextNode operationContextNode = operationRelation.follow();
		if (operationContextNode == null) throw new Xdi2MessagingException("Invalid relation " + operationRelation.getArcXri() + " to " + operationRelation.getRelationXri());

		// create a copy of the operation context node without $msg subsegments

		Graph copyGraph = MemoryGraphFactory.getInstance().openGraph();
		ContextNode copyContextNode = CopyUtil.copyContextNode(operationContextNode, copyGraph, new CopyStrategy () {

			public ContextNode replaceContextNode(ContextNode contextNode) {

				if (XDIMessagingConstants.XRI_SS_MSG.equals(contextNode.getArcXri())) return null;

				return contextNode;
			}
		});

		// execute the operation

		return this.executeContextNodeHandlers(copyContextNode, operation, messageResult, executionContext);
	}

	private boolean executeContextNodeHandlers(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNodeHandler contextNodeHandler;
		boolean handled = false;

		// look at this context node

		contextNodeHandler = this.getContextNodeHandler(operation, operationContextNode);

		if (contextNodeHandler != null) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operationContextNode.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");

			if (this.executeContextNodeInterceptorsBefore(operationContextNode, operation, messageResult, executionContext)) return true;
			if (contextNodeHandler.executeContextNode(operation, messageResult, executionContext)) handled = true;
			if (this.executeContextNodeInterceptorsAfter(operationContextNode, operation, messageResult, executionContext)) return true;

			// look at relations

			for (Iterator<Relation> relations = operationContextNode.getRelations(); relations.hasNext(); ) {

				Relation relation = relations.next();

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + relation.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");

				if (contextNodeHandler.executeRelation(relation, operation, messageResult, executionContext)) handled = true;
			}

			// look at literal

			for (Iterator<Literal> literals = new SingleItemIterator<Literal> (operationContextNode.getLiteral()); literals.hasNext(); ) {

				Literal literal = literals.next();

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + literal.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");

				if (contextNodeHandler.executeLiteral(literal, operation, messageResult, executionContext)) handled = true;
			}
		} else {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No context node handler for " + operation.getOperationXri() + " on " + operationContextNode.getXri());
		}

		// look at inner context nodes

		for (Iterator<ContextNode> innerContextNodes = operationContextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();

			if (this.executeContextNodeHandlers(innerContextNode, operation, messageResult, executionContext)) handled = true;
		}

		return handled;
	}

	/*
	 * Interceptors
	 */

	private boolean executeContextNodeInterceptorsBefore(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ContextNodeInterceptor contextNodeInterceptor : this.contextInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getName() + " on " + contextNode.getStatement() + " (before).");

			if (contextNodeInterceptor.before(contextNode, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Context node " + contextNode.getStatement() + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeContextNodeInterceptorsAfter(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ContextNodeInterceptor contextNodeInterceptor : this.contextInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getName() + " on " + contextNode.getStatement() + " (after).");

			if (contextNodeInterceptor.after(contextNode, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Context node " + contextNode.getStatement() + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * This method should be overridden by a subclass.
	 */

	public abstract ContextNodeHandler getContextNodeHandler(Operation operation, ContextNode contextNode) throws Xdi2MessagingException;

	/*
	 * Misc methods
	 */

	public List<ContextNodeInterceptor> getResourceInterceptors() {

		return this.contextInterceptors;
	}

	public void setResourceInterceptors(List<ContextNodeInterceptor> resourceInterceptors) {

		this.contextInterceptors = resourceInterceptors;
	}
}
