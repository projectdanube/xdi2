package xdi2.server.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.ContextNode;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.Statement;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.server.EndpointRegistry;
import xdi2.server.ExecutionContext;
import xdi2.server.interceptor.ResourceInterceptor;
import xdi2.util.iterators.SingleItemIterator;

/**
 * The ResourceMessagingTarget allows subclasses to return ResourceHandler
 * implementations for the following types of operation statements:
 * - Statements with a subject only.
 * - Statements with a subject and predicate.
 * - Statements with a subject, predicate and reference.
 * - Statements with a subject, predicate and literal.
 * - Statements with a subject, predicate and inner graph.
 * - As a special case, a ResourceHandler may also be returned for operations without
 * any operation statements.
 * 
 * Subclasses must do the following:
 * - Return appropriate ResourceHandlers for the given operation statements. To do
 * this, subclasses override the getResource() methods. Note that sometimes no
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
	public void init(EndpointRegistry endpointRegistry) throws Exception {

		super.init(endpointRegistry);
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

		// execute the operation

		return this.executeResourceHandlers(operationContextNode, operation, messageResult, executionContext);
	}

	private boolean executeResourceHandlers(ContextNode contextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ResourceHandler resourceHandler;
		boolean handled = false;

		// look at this context node

		if (contextNode.isEmpty()) {

			resourceHandler = getResourceHandler(operation, contextNode);

			if (resourceHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + contextNode.getStatement() + " (" + resourceHandler.getClass().getSimpleName() + ").");
				handled = handled || resourceHandler.execute(operation, messageResult, executionContext);

				return handled;
			}
		}
		
		// look at context nodes
		
		for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();

			handled = handled || this.executeResourceHandlers(innerContextNode, operation, messageResult, executionContext);
		}

		// look at relations

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();
			resourceHandler = getResourceHandler(operation, relation);

			if (resourceHandler != null) {
				
				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + relation.getStatement() + " (" + resourceHandler.getClass().getSimpleName() + ").");
				handled = handled || resourceHandler.execute(operation, messageResult, executionContext);
			}
		}

		// look at literal

		for (Iterator<Literal> literals = new SingleItemIterator<Literal> (contextNode.getLiteral()); literals.hasNext(); ) {

			Literal literal = literals.next();
			resourceHandler = this.getResourceHandler(operation, literal);

			if (resourceHandler != null) {
				
				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + literal.getStatement() + " (" + resourceHandler.getClass().getSimpleName() + ").");
				handled = handled || resourceHandler.execute(operation, messageResult, executionContext);
			}
		}

		return handled;
	}

	/*
	 * Interceptors
	 */

	private boolean executeResourceInterceptorsBefore(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ResourceInterceptor contextNodeInterceptor : this.resourceInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getSimpleName() + " on " + statement + " (before).");

			if (contextNodeInterceptor.before(statement, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement " + statement + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeResourceInterceptorsAfter(Statement statement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (ResourceInterceptor contextNodeInterceptor : this.resourceInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing resource interceptor " + contextNodeInterceptor.getClass().getSimpleName() + " on " + statement + " (after).");

			if (contextNodeInterceptor.after(statement, operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement " + statement + " has been fully handled by interceptor " + contextNodeInterceptor.getClass().getSimpleName() + ".");
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
