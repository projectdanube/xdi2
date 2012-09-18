package xdi2.messaging.target;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.NoDuplicatesCopyStrategy;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.iterators.InsertableIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.contributor.ContributorMap;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.InterceptorList;

/**
 * The AbstractMessagingTarget provides the following functionality:
 * - Implementation of execute() with a message envelope (all messages
 *   in the envelope and all operations in the messages will be executed).
 * - Implementation of execute() with a message (all operations
 *   in the messages will be executed).
 * - Support for interceptors and contributors.
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

	private XRI3Segment ownerAuthority;
	private InterceptorList interceptors;
	private ContributorMap contributors;

	public AbstractMessagingTarget() {

		this.ownerAuthority = null;
		this.interceptors = new InterceptorList();
		this.contributors = new ContributorMap();
	}

	@Override
	public void init() throws Exception {

		if (log.isDebugEnabled()) log.debug("Initializing " + this.getClass().getSimpleName() + " [" + this.getInterceptors().size() + " interceptors: " + this.getInterceptors().stringList() + "] [" + this.getContributors().size() + " contributors: " + this.getContributors().stringList() + "].");

		// execute messaging target interceptors (init)

		this.getInterceptors().executeMessagingTargetInterceptorsInit(this);
	}

	@Override
	public void shutdown() throws Exception {

		if (log.isDebugEnabled()) log.debug("Shutting down " + this.getClass().getSimpleName() + ".");

		// execute messaging target interceptors (shutdown)

		this.getInterceptors().executeMessagingTargetInterceptorsShutdown(this);
	}

	/**
	 * Executes a message envelope by executing all its messages.
	 * @param messageEnvelope The XDI message envelope containing XDI messages to be executed.
	 * @param messageResult The message result.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	@Override
	public void execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (messageEnvelope == null) throw new NullPointerException();
		if (messageResult == null) throw new NullPointerException();
		if (executionContext == null) executionContext = new ExecutionContext();

		executionContext.pushMessagingTarget(this, null);
		executionContext.pushMessageEnvelope(messageEnvelope, null);

		int i = 0;
		int messageCount = messageEnvelope.getMessageCount();
		int operationCount = messageEnvelope.getOperationCount();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message envelope.");

		try {

			// clear execution context

			executionContext.clearMessageEnvelopeAttributes();

			// before message envelope

			this.before(messageEnvelope, messageResult, executionContext);

			// execute message envelope interceptors (before)

			if (this.getInterceptors().executeMessageEnvelopeInterceptorsBefore(messageEnvelope, messageResult, executionContext)) {

				return;
			}

			// execute the message envelope

			Iterator<Message> messages = messageEnvelope.getMessages();

			while (messages.hasNext()) {

				i++;
				Message message = messages.next();

				// clear execution context

				executionContext.clearMessageAttributes();

				// before message

				this.before(message, messageResult, executionContext);

				// execute message interceptors (before)

				if (this.getInterceptors().executeMessageInterceptorsBefore(message, messageResult, executionContext)) {

					continue;
				}

				// execute the message

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing message " + i + "/" + messageCount + " (" + operationCount + " operations).");

				try {

					executionContext.pushMessage(message, Integer.toString(i));

					this.execute(message, messageResult, executionContext);
				} finally {

					executionContext.popMessage();
				}

				// execute message interceptors (after)

				if (this.getInterceptors().executeMessageInterceptorsAfter(message, messageResult, executionContext)) {

					continue;
				}

				// after message

				this.after(message, messageResult, executionContext);
			}

			// execute message envelope interceptors (after)

			if (this.getInterceptors().executeMessageEnvelopeInterceptorsAfter(messageEnvelope, messageResult, executionContext)) {

				return;
			}

			// after message envelope

			this.after(messageEnvelope, messageResult, executionContext);

			// execute result interceptors (finish)

			this.getInterceptors().executeResultInterceptorsFinish(messageResult, executionContext);
		} catch (Exception ex) {

			// check exception

			if (! (ex instanceof Xdi2MessagingException)) {

				ex = new Xdi2MessagingException(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(), ex, null);
			}

			// execute message envelope interceptors (exception)

			this.getInterceptors().executeMessageEnvelopeInterceptorsException(messageEnvelope, messageResult, executionContext, ex);

			// exception in message envelope

			this.exception(messageEnvelope, messageResult, executionContext, ex);

			// throw it

			throw (Xdi2MessagingException) ex;
		} finally {

			executionContext.popMessageEnvelope();
			executionContext.popMessagingTarget();

			if (log.isDebugEnabled()) log.debug("Trace: " + executionContext.getTraceString());
		}
	}

	/**
	 * Executes a message by executing all its operations.
	 * @param message The XDI message containing XDI operations to be executed.
	 * @param messageResult The message result.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	public void execute(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (message == null) throw new NullPointerException();
		if (messageResult == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		int i = 0;
		int operationCount = message.getOperationCount();

		InsertableIterator<Operation> operations = new InsertableIterator<Operation> (message.getOperations(), false);

		while (operations.hasNext()) {

			i++;
			Operation operation = operations.next();
			operation = Operation.castOperation(operation);

			try {

				// clear execution context

				executionContext.clearOperationAttributes();

				// prepare message result

				MessageResult operationMessageResult = new MessageResult();

				// before operation

				this.before(operation, operationMessageResult, executionContext);

				// execute operation interceptors (before)

				if (this.getInterceptors().executeOperationInterceptorsBefore(operation, operationMessageResult, executionContext)) {

					continue;
				}

				// execute the operation

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing operation " + i + "/" + operationCount + " (" + operation.getOperationXri() + ").");

				try {

					executionContext.pushOperation(operation, Integer.toString(i));

					this.execute(operation, operationMessageResult, executionContext);
				} finally {

					executionContext.popOperation();
				}

				// execute operation interceptors (after)

				if (this.getInterceptors().executeOperationInterceptorsAfter(operation, operationMessageResult, executionContext)) {

					continue;
				}

				// after operation

				this.after(operation, operationMessageResult, executionContext);

				// finish message result

				CopyUtil.copyGraph(operationMessageResult.getGraph(), messageResult.getGraph(), new NoDuplicatesCopyStrategy(messageResult.getGraph()));
			} catch (Exception ex) {

				// check exception

				if (! (ex instanceof Xdi2MessagingException)) {

					ex = new Xdi2MessagingException(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(), ex, executionContext);
				}

				// throw it

				throw (Xdi2MessagingException) ex;
			}
		}
	}

	/**
	 * Executes an operation.
	 * @param operation The XDI operation.
	 * @param operationMessageResult The operation's message result.
	 * @param executionContext An "execution context" object that carries state between
	 * messaging targets, interceptors and contributors.
	 */
	public void execute(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (operation == null) throw new NullPointerException();
		if (operationMessageResult == null) throw new NullPointerException();
		if (executionContext == null) throw new NullPointerException();

		XRI3Segment target = operation.getTarget();

		// check if the target is a statement or an address

		Statement targetStatement = null;
		XRI3Segment targetAddress = null;

		try {

			targetStatement = StatementUtil.fromXriSegment(target);
		} catch (Xdi2ParseException ex) {

			targetAddress = target;
		}

		// execute on address or statement

		if (targetStatement == null) {

			// execute target interceptors (address)

			if ((targetAddress = this.getInterceptors().executeTargetInterceptorsAddress(targetAddress, operation, operationMessageResult, executionContext)) == null) {

				return;
			}

			// execute contributors (address)

			if (this.getContributors().executeContributorsAddress(targetAddress, targetAddress, operation, operationMessageResult, executionContext)) {

				return;
			}

			// get an address handler, and execute on the address

			AddressHandler addressHandler = this.getAddressHandler(targetAddress);

			if (addressHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on target address " + targetAddress + " (" + addressHandler.getClass().getName() + ").");

				addressHandler.executeOnAddress(targetAddress, operation, operationMessageResult, executionContext);
			} else {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No address handler for target address " + targetAddress + ".");
			}
		} else {

			// execute target interceptors (statement)

			if ((targetStatement = this.getInterceptors().executeTargetInterceptorsStatement(targetStatement, operation, operationMessageResult, executionContext)) == null) {

				return;
			}

			// execute contributors (statement)

			if (this.getContributors().executeContributorsStatement(targetStatement, targetStatement, operation, operationMessageResult, executionContext)) {

				return;
			}

			// get a statement handler, and execute on the statement

			StatementHandler statementHandler = this.getStatementHandler(targetStatement);

			if (statementHandler != null) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing " + operation.getOperationXri() + " on target statement " + targetStatement + " (" + statementHandler.getClass().getName() + ").");

				statementHandler.executeOnStatement(targetStatement, operation, operationMessageResult, executionContext);
			} else {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": No statement handler for target statement " + targetStatement + ".");
			}
		}
	}

	/*
	 * These are for being overridden by subclasses
	 */

	public void before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void before(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void after(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

	}

	public AddressHandler getAddressHandler(XRI3Segment targetAddress) throws Xdi2MessagingException {

		return null;
	}

	public StatementHandler getStatementHandler(Statement targetStatement) throws Xdi2MessagingException {

		return null;
	}

	/*
	 * Getters and setters
	 */

	@Override
	public XRI3Segment getOwnerAuthority() {

		return this.ownerAuthority;
	}

	public void setOwnerAuthority(XRI3Segment ownerAuthority) {

		this.ownerAuthority = ownerAuthority;
	}

	public InterceptorList getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList interceptors) {

		this.interceptors = interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {

		this.interceptors.clear();
		this.interceptors.addAll(interceptors);
	}

	public ContributorMap getContributors() {

		return this.contributors;
	}

	public void setContributors(ContributorMap contributors) {

		this.contributors = contributors;
	}

	public void setContributors(Map<XRI3Segment, List<Contributor>> contributors) {

		this.contributors.clear();
		this.contributors.putAll(contributors);
	}
}
