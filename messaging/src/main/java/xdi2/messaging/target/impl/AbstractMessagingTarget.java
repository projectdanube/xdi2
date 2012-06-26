package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.AbstractStatement;
import xdi2.core.util.iterators.SelectingClassIterator;
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
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

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

	protected List<Interceptor> interceptors;

	public AbstractMessagingTarget() {

		this.interceptors = new ArrayList<Interceptor> ();
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

			this.executeResultInterceptorsFinish(messageResult, executionContext);
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

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation " + i + "/" + operationCount + " (" + operation.getOperationXri() + ").");

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

	/**
	 * Executes an operation.
	 * @param operation The XDI operation.
	 * @param messageResult The result produced by executing the message envelope.
	 * @param executionContext An "execution context" object that is created when
	 * execution of the message envelope begins and that will be passed into every execute() method.
	 * @return True, if the operation has been handled.
	 */
	public boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();

		boolean handled = false;

		XRI3Segment target = operation.getTarget();

		// check if the target is a statement or an address

		try {

			Statement targetStatement = AbstractStatement.fromXriSegment(target);

			// execute target interceptors (statement)

			targetStatement = this.executeTargetInterceptorsStatement(operation, targetStatement, executionContext);
			if (targetStatement == null) return true;

			// get a statement handler, and execute on the statement

			StatementHandler statementHandler = this.getStatementHandler(targetStatement);

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on statement " + targetStatement + " (" + statementHandler.getClass().getName() + ").");

			if (statementHandler.executeOnStatement(targetStatement, operation, messageResult, executionContext)) handled = true;
		} catch (Xdi2ParseException ex) {

			XRI3Segment targetAddress = target;

			// execute target interceptors (address)

			targetAddress = this.executeTargetInterceptorsAddress(operation, targetAddress, executionContext);
			if (targetAddress == null) return true;

			// execute on the address

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on address " + targetAddress + " (" + this.getClass().getName() + ").");

			if (this.executeOnAddress(targetAddress, operation, messageResult, executionContext)) handled = true;
		}

		// done

		return handled;
	}

	private final boolean executeOnAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation instanceof GetOperation)
			return this.executeGetOnAddress(targetAddress, messageResult, executionContext);
		else if (operation instanceof AddOperation)
			return this.executeAddOnAddress(targetAddress, messageResult, executionContext);
		else if (operation instanceof ModOperation)
			return this.executeModOnAddress(targetAddress, messageResult, executionContext);
		else if (operation instanceof DelOperation)
			return this.executeDelOnAddress(targetAddress, messageResult, executionContext);
		else
			throw new Xdi2MessagingException("Unknown operation: " + operation.getOperationXri());
	}

	/*
	 * These are for being overridden by subclasses
	 */

	public boolean executeGetOnAddress(XRI3Segment targetAddress, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeAddOnAddress(XRI3Segment targetAddress, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeModOnAddress(XRI3Segment targetAddress, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public boolean executeDelOnAddress(XRI3Segment targetAddress, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}

	public StatementHandler getStatementHandler(Statement targetStatement) throws Xdi2MessagingException {

		return null;
	}

	/*
	 * Interceptors
	 */

	private boolean executeMessageEnvelopeInterceptorsBefore(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

			if (messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeMessageEnvelopeInterceptorsAfter(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

			if (messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private void executeMessageEnvelopeInterceptorsException(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

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

		for (Iterator<MessageInterceptor> messageInterceptors = this.getMessageInterceptors(); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

			if (messageInterceptor.before(message, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeMessageInterceptorsAfter(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageInterceptor> messageInterceptors = this.getMessageInterceptors(); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

			if (messageInterceptor.after(message, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeOperationInterceptorsBefore(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<OperationInterceptor> operationInterceptors = this.getOperationInterceptors(); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

			if (operationInterceptor.before(operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private boolean executeOperationInterceptorsAfter(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<OperationInterceptor> operationInterceptors = this.getOperationInterceptors(); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

			if (operationInterceptor.after(operation, messageResult, executionContext)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
				return true;
			}
		}

		return false;
	}

	private Statement executeTargetInterceptorsStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = this.getTargetInterceptors(); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " on statement " + targetStatement + ".");

			targetStatement = targetInterceptor.targetStatement(operation, targetStatement, executionContext);

			if (targetStatement == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Statement has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
				return null;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned statement: " + targetStatement + ".");
		}

		return targetStatement;
	}

	private XRI3Segment executeTargetInterceptorsAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = this.getTargetInterceptors(); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " on address " + targetAddress + ".");

			targetAddress = targetInterceptor.targetAddress(operation, targetAddress, executionContext);

			if (targetAddress == null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Address has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
				return null;
			}

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned address: " + targetAddress + ".");
		}

		return targetAddress;
	}

	private void executeResultInterceptorsFinish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<ResultInterceptor> resultInterceptors = this.getResultInterceptors(); resultInterceptors.hasNext(); ) {

			ResultInterceptor resultInterceptor = resultInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");

			resultInterceptor.finish(messageResult, executionContext);
		}
	}

	/*
	 * Misc methods
	 */

	public void before(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.clearOperationAttributes();
	}

	public void before(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.clearMessageAttributes();
	}

	public void before(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		executionContext.clearMessageEnvelopeAttributes();
	}

	public void after(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public List<Interceptor> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {

		this.interceptors = interceptors;
	}

	public Iterator<MessageEnvelopeInterceptor> getMessageEnvelopeInterceptors() {

		return new SelectingClassIterator<Interceptor, MessageEnvelopeInterceptor> (this.interceptors.iterator(), MessageEnvelopeInterceptor.class);
	}

	public Iterator<MessageInterceptor> getMessageInterceptors() {

		return new SelectingClassIterator<Interceptor, MessageInterceptor> (this.interceptors.iterator(), MessageInterceptor.class);
	}

	public Iterator<OperationInterceptor> getOperationInterceptors() {

		return new SelectingClassIterator<Interceptor, OperationInterceptor> (this.interceptors.iterator(), OperationInterceptor.class);
	}

	public Iterator<TargetInterceptor> getTargetInterceptors() {

		return new SelectingClassIterator<Interceptor, TargetInterceptor> (this.interceptors.iterator(), TargetInterceptor.class);
	}

	public Iterator<ResultInterceptor> getResultInterceptors() {

		return new SelectingClassIterator<Interceptor, ResultInterceptor> (this.interceptors.iterator(), ResultInterceptor.class);
	}
}
