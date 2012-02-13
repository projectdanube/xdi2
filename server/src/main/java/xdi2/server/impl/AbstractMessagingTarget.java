package xdi2.server.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.Graph;
import xdi2.Statement;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.server.EndpointRegistry;
import xdi2.server.ExecutionContext;
import xdi2.server.MessagingTarget;
import xdi2.server.interceptor.MessageEnvelopeInterceptor;
import xdi2.server.interceptor.MessageInterceptor;
import xdi2.server.interceptor.OperationInterceptor;
import xdi2.server.interceptor.ResultInterceptor;

/**
 * The AbstractMessagingTarget relieves subclasses from the following:
 * - Implementation of execute() with a message envelope (all messages
 *   in the envelope and all operations in the messages will be executed).
 * - Implementation of execute() with a message (all operations
 *   in the messages will be executed).
 * - Using a list of MessageEnvlopeInterceptors, MessageInterceptors and 
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

	protected EndpointRegistry endpointRegistry;

	public AbstractMessagingTarget() {

		this.messageEnvelopeInterceptors = new ArrayList<MessageEnvelopeInterceptor> ();
		this.messageInterceptors = new ArrayList<MessageInterceptor> ();
		this.operationInterceptors = new ArrayList<OperationInterceptor> ();
		this.resultInterceptors = new ArrayList<ResultInterceptor> ();
	}

	public void init(EndpointRegistry endpointRegistry) throws Exception {

		this.endpointRegistry = endpointRegistry;
	}

	public void shutdown() throws Exception {

	}

	/**
	 * Executes a message envelope by executing all its messages.
	 * @param messageEnvelope The XDI message envelope containing XDI messages to be executed.
	 * @param messageResult The result produced by executing the message envelope.
	 * @return True, if the message envelope has been handled.
	 */
	public boolean execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (messageEnvelope == null) throw new NullPointerException();

		boolean handled = false;

		int i = 0;
		int messageCount = messageEnvelope.getMessageCount();
		int operationCount = messageEnvelope.getOperationCount();

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

				if (this.execute(message, messageResult, executionContext)) handled = true;

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
	 * execution of the message envelope begins and that will be passed into every 
	 * single execute() method.
	 * @return True, if the message has been handled.
	 */
	public boolean execute(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

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

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation " + i + "/" + operationCount + " (" + operation.getOperationXri() + ") from " + operation.getSender() + ".");

				if (this.execute(operation, messageResult, executionContext)) handled = true;

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

	/**
	 * This method MUST be overidden by subclasses to execute a single operation.
	 * @param message The XDI operation containing an XDI operation graph to be executed.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every 
	 * single execute() method.
	 * @return True, if the operation has been handled.
	 */
	public abstract boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

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
