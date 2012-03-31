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
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;

/**
 * The AbstractMessagingTarget relieves subclasses from the following:
 * - Implementation of execute() with a message envelope (all messages
 *   in the envelope and all operations in the messages will be executed).
 * - Implementation of execute() with a message (all operations
 *   in the messages will be executed).
 * - Using a list of MessageEnvelopeInterceptors, MessageInterceptors and 
 *   OperationInterceptors
 * - Maintaining an "execution context" object where state can be kept between
 *   individual operations.
 * 
 * Subclasses must do the following:
 * - Implement execute() with an operation.
 * 
 * @author markus
 */
public abstract class AbstractMessagingTarget implements MessagingTarget {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingTarget.class);

	protected List<MessageEnvelopeInterceptor> messageEnvelopeInterceptors;
	protected List<MessageInterceptor> messageInterceptors;
	protected List<OperationInterceptor> operationInterceptors;
	protected List<ResultInterceptor> resultInterceptors;

	public AbstractMessagingTarget() {

		this.messageEnvelopeInterceptors = new ArrayList<MessageEnvelopeInterceptor> ();
		this.messageInterceptors = new ArrayList<MessageInterceptor> ();
		this.operationInterceptors = new ArrayList<OperationInterceptor> ();
		this.resultInterceptors = new ArrayList<ResultInterceptor> ();
	}

	public void init() throws Exception {

	}

	public void shutdown() throws Exception {

	}

	/**
	 * Executes a message envelope by executing all its messages.
	 * @param messageEnvelope The XDI message envelope containing XDI messages to be executed.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every execute() method.
	 * @return True, if the message envelope has been handled.
	 */
	public boolean execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (messageEnvelope == null) throw new NullPointerException();
		if (messageResult == null) throw new NullPointerException();
		if (executionContext == null) executionContext = new ExecutionContext();

		boolean handled = false;

		int i = 0;
		int messageCount = messageEnvelope.getMessageCount();
		int operationCount = messageEnvelope.getOperationCount();

		Graph targetGraph = messageEnvelope.getTargetGraph();

		try {

			// before message envelope

			this.before(messageEnvelope, executionContext);

			// execute message envelope interceptors (before)

			if (this.executeMessageEnvelopeInterceptorsBefore(messageEnvelope, messageResult, executionContext)) {

				return true;
			}

			// execute the message envelope

			for (Iterator<Message> messages = messageEnvelope.getMessages(); messages.hasNext(); ) {

				i++;
				Message message = messages.next();

				// before message

				this.before(message, executionContext);

				// execute message interceptors (before)

				if (this.executeMessageInterceptorsBefore(message, messageResult, executionContext)) {

					handled = true;
					continue;
				}

				// execute the message

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message " + i + "/" + messageCount + " (" + operationCount + " operations).");

				if (this.execute(message, targetGraph, messageResult, executionContext)) handled = true;

				// execute message interceptors (after)

				if (this.executeMessageInterceptorsAfter(message, messageResult, executionContext)) {

					handled = true;
					continue;
				}

				// after message

				this.after(message, executionContext);
			}

			// execute message envelope interceptors (after)

			if (this.executeMessageEnvelopeInterceptorsAfter(messageEnvelope, messageResult, executionContext)) {

				return true;
			}

			// after message envelope

			this.after(messageEnvelope, executionContext);

			// execute result interceptors

			if (messageResult != null) {

				this.executeResultInterceptors(messageResult.getGraph(), executionContext);
			}
		} catch (Exception ex) {

			// execute message envelope interceptors (after)

			this.executeMessageEnvelopeInterceptorsException(messageEnvelope, messageResult, executionContext, ex);

			// throw it

			String reason = ex.getMessage();
			if (reason == null || reason.equals("null")) reason = ex.getClass().getSimpleName();

			throw new Xdi2MessagingException("Exception while executing message envelope at message " + i + ": " + reason, ex);
		}

		// done

		return handled;
	}

	/**
	 * Executes a message by executing all its operations.
	 * @param message The XDI message containing XDI operations to be executed.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every execute() method.
	 * @return True, if the message has been handled.
	 */
	public boolean execute(Message message, Graph targetGraph, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (message == null) throw new NullPointerException();

		boolean handled = false;

		int i = 0;
		int operationCount = message.getOperationCount();

		try {

			for (Iterator<Operation> operations = message.getOperations(); operations.hasNext(); ) {

				i++;
				Operation operation = operations.next();
				operation = Operation.castOperation(operation);

				// before operation

				this.before(operation, executionContext);

				// execute operation interceptors (before)

				if (this.executeOperationInterceptorsBefore(operation, messageResult, executionContext)) {

					handled = true;
					continue;
				}

				// execute the operation

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation " + i + "/" + operationCount + " (" + operation.getOperationXri() + ") on " + operation.getOperationTargetXri() + ".");

				if (this.execute(operation, targetGraph, messageResult, executionContext)) handled = true;

				// execute operation interceptors (after)

				if (this.executeOperationInterceptorsAfter(operation, messageResult, executionContext)) {

					handled = true;
					continue;
				}

				// after operation

				this.after(operation, executionContext);
			}
		} catch (Exception ex) {

			String reason = ex.getMessage();
			if (reason == null || reason.equals("null")) reason = ex.getClass().getSimpleName();

			throw new Xdi2MessagingException("Exception while executing message at operation " + i + ": " + reason, ex);
		}

		// done

		return handled;
	}

	/**
	 * Executes an operation.
	 * @param operation The XDI operation.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every execute() method.
	 * @return True, if the operation has been handled.
	 */
	public boolean execute(final Operation operation, Graph targetGraph, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();

		boolean handled = false;

		XRI3Segment targetXri = operation.getOperationTargetXri();
		ContextNode targetContextNode = targetGraph.findContextNode(operation.getOperationTargetXri(), false);

		// execute the operation

		if (this.executeOperation(targetXri, targetContextNode, operation, messageResult, executionContext)) handled = true;

		// execute on the individual context nodes, relations and literals

		if (targetContextNode != null) {

			if (this.executeContextNodeHandlers(targetContextNode, operation, messageResult, executionContext)) handled = true;
		}

		// done

		return handled;
	}

	private final boolean executeOperation(XRI3Segment targetXri, ContextNode targetContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operation.getOperationTargetXri() + " (" + this.getClass().getName() + ").");

		if (operation instanceof GetOperation)
			return this.executeGetOperation(targetXri, targetContextNode, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOperation(targetXri, targetContextNode, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOperation(targetXri, targetContextNode, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOperation(targetXri, targetContextNode, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	private final boolean executeContextNodeHandlers(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		boolean handled = false;

		// look at this context node

		ContextNodeHandler contextNodeHandler = this.getContextNodeHandler(operationContextNode);

		if (contextNodeHandler != null) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operationContextNode.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");
			if (contextNodeHandler.executeOnContextNode(operationContextNode, operation, messageResult, executionContext)) handled = true;

			// look at relations

			for (Iterator<Relation> relations = operationContextNode.getRelations(); relations.hasNext(); ) {

				Relation operationRelation = relations.next();

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operationRelation.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");
				if (contextNodeHandler.executeOnRelation(operationContextNode, operationRelation, operation, messageResult, executionContext)) handled = true;
			}

			// look at literal

			for (Iterator<Literal> literals = new SingleItemIterator<Literal> (operationContextNode.getLiteral()); literals.hasNext(); ) {

				Literal operationLiteral = literals.next();

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on " + operationLiteral.getStatement() + " (" + contextNodeHandler.getClass().getName() + ").");
				if (contextNodeHandler.executeOnLiteral(operationContextNode, operationLiteral, operation, messageResult, executionContext)) handled = true;
			}
		}

		// look at inner context nodes

		for (Iterator<ContextNode> innerContextNodes = operationContextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();

			if (this.executeContextNodeHandlers(innerContextNode, operation, messageResult, executionContext)) handled = true;
		}

		return handled;
	}

	/*
	 * These are for being overridden by subclasses
	 */

	public boolean executeGetOperation(XRI3Segment targetXri, ContextNode targetContextNode, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOperation(XRI3Segment targetXri, ContextNode targetContextNode, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOperation(XRI3Segment targetXri, ContextNode targetContextNode, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOperation(XRI3Segment targetXri, ContextNode targetContextNode, MessageResult operationResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public ContextNodeHandler getContextNodeHandler(ContextNode operationContextNode) throws Xdi2MessagingException {

		return null;
	}

	/*
	 * Interceptors
	 */

	private boolean executeMessageEnvelopeInterceptorsBefore(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : this.messageEnvelopeInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

			if (messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeMessageEnvelopeInterceptorsAfter(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : this.messageEnvelopeInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

			if (messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private void executeMessageEnvelopeInterceptorsException(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

		for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : this.messageEnvelopeInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");

			try {

				messageEnvelopeInterceptor.exception(messageEnvelope, messageResult, executionContext, ex);
			} catch (Exception ex2) {

				if (log.isWarnEnabled()) log.warn(this.getClass().getSimpleName() + ": Exception during message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception): " + ex2.getMessage() + ".", ex2);
				continue;
			}
		}
	}

	private boolean executeMessageInterceptorsBefore(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (MessageInterceptor messageInterceptor : this.messageInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

			if (messageInterceptor.before(message, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeMessageInterceptorsAfter(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (MessageInterceptor messageInterceptor : this.messageInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

			if (messageInterceptor.after(message, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeOperationInterceptorsBefore(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (OperationInterceptor operationInterceptor : this.operationInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

			if (operationInterceptor.before(operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeOperationInterceptorsAfter(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (OperationInterceptor operationInterceptor : this.operationInterceptors) {

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

			if (operationInterceptor.after(operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	protected void executeResultInterceptors(Graph graph, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (this.getResultInterceptors().size() < 0) return;

		// execute result interceptors

		List<Statement> deleteStatements = new ArrayList<Statement> ();

		for (Iterator<Statement> statements = graph.getRootContextNode().getAllStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();

			for (ResultInterceptor resultInterceptor : this.resultInterceptors) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " on " + statement + ".");

				if (resultInterceptor.exclude(statement, executionContext)) {

					if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement " + statement + " has been excluded by interceptor " + resultInterceptor.getClass().getSimpleName() + ".");
					deleteStatements.add(statement);
					break;
				}
			}
		}

		for (Statement statement : deleteStatements) statement.delete();
	}

	/*
	 * Misc methods
	 */

	public void before(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.getOperationAttributes().clear();
	}

	public void before(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.getMessageAttributes().clear();
	}

	public void before(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.getMessageEnvelopeAttributes().clear();
	}

	public void after(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public List<MessageEnvelopeInterceptor> getMessageEnvelopeInterceptors() {

		return this.messageEnvelopeInterceptors;
	}

	public void setMessageEnvelopeInterceptors(List<MessageEnvelopeInterceptor> messageEnvelopeInterceptors) {

		this.messageEnvelopeInterceptors = messageEnvelopeInterceptors;
	}

	public List<MessageInterceptor> getMessageInterceptors() {

		return this.messageInterceptors;
	}

	public void setMessageInterceptors(List<MessageInterceptor> messageInterceptors) {

		this.messageInterceptors = messageInterceptors;
	}

	public List<OperationInterceptor> getOperationInterceptors() {

		return this.operationInterceptors;
	}

	public void setOperationInterceptors(List<OperationInterceptor> operationInterceptors) {

		this.operationInterceptors = operationInterceptors;
	}

	public List<ResultInterceptor> getResultInterceptors() {

		return this.resultInterceptors;
	}

	public void setResultInterceptors(List<ResultInterceptor> resultInterceptors) {

		this.resultInterceptors = resultInterceptors;
	}
}
