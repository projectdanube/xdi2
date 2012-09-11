package xdi2.messaging.target.interceptor;

import java.util.ArrayList;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Statement;
import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

public class InterceptorList extends ArrayList<Interceptor> {

	private static final long serialVersionUID = -2532712738486475044L;

	private static final Logger log = LoggerFactory.getLogger(InterceptorList.class);

	public InterceptorList() {

		super();
	}

	public void addInterceptor(Interceptor interceptor) {

		this.add(interceptor);
	}

	public void removeInterceptor(Interceptor interceptor) {

		this.remove(interceptor);
	}

	public String stringList() {

		StringBuffer buffer = new StringBuffer();

		for (Interceptor interceptor : this) {

			if (buffer.length() > 0) buffer.append(",");
			buffer.append(interceptor.getClass().getSimpleName());
		}

		return buffer.toString();
	}

	/*
	 * Methods for executing interceptors
	 */

	public void executeMessagingTargetInterceptorsInit(MessagingTarget messagingTarget) throws Exception {

		for (Iterator<MessagingTargetInterceptor> messagingTargetInterceptors = this.findMessagingTargetInterceptors(); messagingTargetInterceptors.hasNext(); ) {

			MessagingTargetInterceptor messagingTargetInterceptor = messagingTargetInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing messaging target interceptor " + messagingTargetInterceptor.getClass().getSimpleName() + " (init).");

			messagingTargetInterceptor.init(messagingTarget);
		}
	}

	public void executeMessagingTargetInterceptorsShutdown(MessagingTarget messagingTarget) throws Exception {

		for (Iterator<MessagingTargetInterceptor> messagingTargetInterceptors = this.findMessagingTargetInterceptors(); messagingTargetInterceptors.hasNext(); ) {

			MessagingTargetInterceptor messagingTargetInterceptor = messagingTargetInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing messaging target interceptor " + messagingTargetInterceptor.getClass().getSimpleName() + " (shutdown).");

			messagingTargetInterceptor.shutdown(messagingTarget);
		}
	}

	public boolean executeMessageEnvelopeInterceptorsBefore(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.findMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: before");

				if (messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public boolean executeMessageEnvelopeInterceptorsAfter(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.findMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: after");

				if (messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public void executeMessageEnvelopeInterceptorsException(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) throws Xdi2MessagingException {

		for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = this.findMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

			MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");

			try {

				executionContext.pushInterceptor(messageEnvelopeInterceptor, "MessageEnvelopeInterceptor: exception");

				messageEnvelopeInterceptor.exception(messageEnvelope, messageResult, executionContext, ex);
			} catch (Exception ex2) {

				if (log.isWarnEnabled()) log.warn("Exception during message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception): " + ex2.getMessage() + ".", ex2);
				continue;
			} finally {

				executionContext.popInterceptor();
			}
		}
	}

	public boolean executeMessageInterceptorsBefore(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageInterceptor> messageInterceptors = this.findMessageInterceptors(); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: before");

				if (messageInterceptor.before(message, messageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public boolean executeMessageInterceptorsAfter(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<MessageInterceptor> messageInterceptors = this.findMessageInterceptors(); messageInterceptors.hasNext(); ) {

			MessageInterceptor messageInterceptor = messageInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(messageInterceptor, "MessageInterceptor: after");

				if (messageInterceptor.after(message, messageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public boolean executeOperationInterceptorsBefore(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<OperationInterceptor> operationInterceptors = this.findOperationInterceptors(); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: before");

				if (operationInterceptor.before(operation, operationMessageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public boolean executeOperationInterceptorsAfter(Operation operation, MessageResult operationMessageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<OperationInterceptor> operationInterceptors = this.findOperationInterceptors(); operationInterceptors.hasNext(); ) {

			OperationInterceptor operationInterceptor = operationInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

			try {

				executionContext.pushInterceptor(operationInterceptor, "OperationInterceptor: after");

				if (operationInterceptor.after(operation, operationMessageResult, executionContext)) {

					if (log.isDebugEnabled()) log.debug("Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
					return true;
				}
			} finally {

				executionContext.popInterceptor();
			}
		}

		return false;
	}

	public XRI3Segment executeTargetInterceptorsAddress(XRI3Segment targetAddress, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = this.findTargetInterceptors(); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " on address " + targetAddress + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: address");

				targetAddress = targetInterceptor.targetAddress(targetAddress, operation, messageResult, executionContext);

				if (targetAddress == null) {

					if (log.isDebugEnabled()) log.debug("Address has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned address: " + targetAddress + ".");
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetAddress;
	}

	public Statement executeTargetInterceptorsStatement(Statement targetStatement, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<TargetInterceptor> targetInterceptors = this.findTargetInterceptors(); targetInterceptors.hasNext(); ) {

			TargetInterceptor targetInterceptor = targetInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing target interceptor " + targetInterceptor.getClass().getSimpleName() + " on statement " + targetStatement + ".");

			try {

				executionContext.pushInterceptor(targetInterceptor, "TargetInterceptor: statement");

				targetStatement = targetInterceptor.targetStatement(targetStatement, operation, messageResult, executionContext);

				if (targetStatement == null) {

					if (log.isDebugEnabled()) log.debug("Statement has been skipped by interceptor " + targetInterceptor.getClass().getSimpleName() + ".");
					return null;
				}

				if (log.isDebugEnabled()) log.debug("Interceptor " + targetInterceptor.getClass().getSimpleName() + " returned statement: " + targetStatement + ".");
			} finally {

				executionContext.popInterceptor();
			}
		}

		return targetStatement;
	}

	public void executeResultInterceptorsFinish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Iterator<ResultInterceptor> resultInterceptors = this.findResultInterceptors(); resultInterceptors.hasNext(); ) {

			ResultInterceptor resultInterceptor = resultInterceptors.next();

			if (log.isDebugEnabled()) log.debug("Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " (finish).");

			try {

				executionContext.pushInterceptor(resultInterceptor, "ResultInterceptor: finish");

				resultInterceptor.finish(messageResult, executionContext);
			} finally {

				executionContext.popInterceptor();
			}
		}
	}

	/*
	 * Methods for finding interceptors
	 */

	public Iterator<MessagingTargetInterceptor> findMessagingTargetInterceptors() {

		return new SelectingClassIterator<Interceptor, MessagingTargetInterceptor> (this.iterator(), MessagingTargetInterceptor.class);
	}

	public Iterator<MessageEnvelopeInterceptor> findMessageEnvelopeInterceptors() {

		return new SelectingClassIterator<Interceptor, MessageEnvelopeInterceptor> (this.iterator(), MessageEnvelopeInterceptor.class);
	}

	public Iterator<MessageInterceptor> findMessageInterceptors() {

		return new SelectingClassIterator<Interceptor, MessageInterceptor> (this.iterator(), MessageInterceptor.class);
	}

	public Iterator<OperationInterceptor> findOperationInterceptors() {

		return new SelectingClassIterator<Interceptor, OperationInterceptor> (this.iterator(), OperationInterceptor.class);
	}

	public Iterator<TargetInterceptor> findTargetInterceptors() {

		return new SelectingClassIterator<Interceptor, TargetInterceptor> (this.iterator(), TargetInterceptor.class);
	}

	public Iterator<ResultInterceptor> findResultInterceptors() {

		return new SelectingClassIterator<Interceptor, ResultInterceptor> (this.iterator(), ResultInterceptor.class);
	}
}
