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
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.ResourceInterceptor;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * The ResourceMessagingTarget allows subclasses to return ResourceHandler
 * implementations for context nodes, relation and literals in the operation.
 * 
 * Subclasses must do the following:
 * - Return appropriate ResourceHandlers for the given context nodes, relations and literals.
 * To do this, subclasses override the getResource() methods. Note that sometimes no
 * resource can be identified by a given operation statement, in which case a
 * subclass may simply return null.
 * 
 * @author markus
 */
public abstract class ResourceMessagingTarget extends AbstractMessagingTarget {

	private static final Logger log = LoggerFactory.getLogger(ResourceMessagingTarget.class);

	protected List<ResourceInterceptor> resourceInterceptors;

	public ResourceMessagingTarget() {

		super();

		this.resourceInterceptors = new ArrayList<ResourceInterceptor> ();
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

		return this.executeResourceHandlers(copyContextNode, operation, messageResult, executionContext);
	}

	private boolean executeResourceHandlers(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ResourceHandler resourceHandler;
		boolean handled = false;

		// look at this context node

		if (operationContextNode.isEmpty()) {

			resourceHandler = this.getResourceHandler(operation, operationContextNode);

			if (resourceHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operationContextNode.getStatement() + " (" + resourceHandler.getClass().getName() + ").");

				if (this.executeResourceInterceptorsBefore(operationContextNode.getStatement(), operation, messageResult, executionContext)) return true;
				if (resourceHandler.execute(operation, messageResult, executionContext)) handled = true;
				if (this.executeResourceInterceptorsAfter(operationContextNode.getStatement(), operation, messageResult, executionContext)) return true;

				return handled;
			} else {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No resource handler for " + operation.getOperationXri() + " on " + operationContextNode.getStatement());
			}
		}

		// look at context nodes

		for (Iterator<ContextNode> contextNodes = operationContextNode.getContextNodes(); contextNodes.hasNext(); ) {

			ContextNode contextNode = contextNodes.next();

			if (XDIMessagingConstants.XRI_SS_MSG.equals(contextNode.getArcXri())) continue;
			if (this.executeResourceHandlers(contextNode, operation, messageResult, executionContext)) handled = true;
		}

		// look at relations

		for (Iterator<Relation> relations = operationContextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();
			resourceHandler = this.getResourceHandler(operation, relation);

			if (resourceHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + relation.getStatement() + " (" + resourceHandler.getClass().getName() + ").");

				if (this.executeResourceInterceptorsBefore(relation.getStatement(), operation, messageResult, executionContext)) continue;
				if (resourceHandler.execute(operation, messageResult, executionContext)) handled = true;
				if (this.executeResourceInterceptorsAfter(relation.getStatement(), operation, messageResult, executionContext)) continue;
			} else {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No resource handler for " + operation.getOperationXri() + " on " + relation.getStatement());
			}
		}

		// look at literal

		for (Iterator<Literal> literals = new SingleItemIterator<Literal> (operationContextNode.getLiteral()); literals.hasNext(); ) {

			Literal literal = literals.next();
			resourceHandler = this.getResourceHandler(operation, literal);

			if (resourceHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + literal.getStatement() + " (" + resourceHandler.getClass().getName() + ").");

				if (this.executeResourceInterceptorsBefore(literal.getStatement(), operation, messageResult, executionContext)) continue;
				if (resourceHandler.execute(operation, messageResult, executionContext)) handled = true;
				if (this.executeResourceInterceptorsAfter(literal.getStatement(), operation, messageResult, executionContext)) continue;
			} else {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No resource handler for " + operation.getOperationXri() + " on " + literal.getStatement());
			}
		}

		return handled;
	}

	/*
	 * Interceptors
	 */

	private boolean executeResourceInterceptorsBefore(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ResourceInterceptor contextNodeInterceptor : this.resourceInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getName() + " on " + statement + " (before).");

			if (contextNodeInterceptor.before(statement, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement " + statement + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeResourceInterceptorsAfter(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ResourceInterceptor contextNodeInterceptor : this.resourceInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getName() + " on " + statement + " (after).");

			if (contextNodeInterceptor.after(statement, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement " + statement + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getName() + ".");
				return true;
			}
		}

		return false;
	}

	/*
	 * One or more of the following methods should be overridden by a subclass.
	 */

	public ResourceHandler getResourceHandler(Operation operation, ContextNode contextNode) throws Xdi2MessagingException {

		return null;
	}

	public ResourceHandler getResourceHandler(Operation operation, Relation relation) throws Xdi2MessagingException {

		return null;
	}

	public ResourceHandler getResourceHandler(Operation operation, Literal literal) throws Xdi2MessagingException {

		return null;
	}

	/*
	 * Misc methods
	 */

	public List<ResourceInterceptor> getResourceInterceptors() {

		return this.resourceInterceptors;
	}

	public void setResourceInterceptors(List<ResourceInterceptor> resourceInterceptors) {

		this.resourceInterceptors = resourceInterceptors;
	}
}
