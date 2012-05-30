package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;

/**
 * A messaging target that can combine multiple other messaging targets.
 * 
 * Incoming messages will be sequentially applied to all individual targets, and
 * a single message result will be produced.
 * 
 * @author markus
 */
public class CompoundMessagingTarget extends AbstractMessagingTarget {

	private static final Log log = LogFactory.getLog(CompoundMessagingTarget.class);

	public static final String MODE_ALL = "all";
	public static final String MODE_FIRST_HANDLED = "first-handled";
	public static final String MODE_WRITE_FIRST_HANDLED = "write-first-handled";

	private String mode;
	private List<AbstractMessagingTarget> messagingTargets;

	public CompoundMessagingTarget() {

		this.mode = MODE_ALL;
		this.messagingTargets = new ArrayList<AbstractMessagingTarget> ();
	}

	@Override
	public void init() throws Exception {

		super.init();

		// add compound interceptors

		this.getMessageEnvelopeInterceptors().add(new CompoundMessageEnvelopeInterceptor());
		this.getMessageInterceptors().add(new CompoundMessageInterceptor());
		this.getOperationInterceptors().add(new CompoundOperationInterceptor());
		this.getResultInterceptors().add(new CompoundResultInterceptor());
	}

	/**
	 * We execute an operation by letting all our individual messaging targets
	 * execute it.
	 */
	@Override
	public boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		boolean handled = false;

		for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

			AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

			try {

				if (messagingTarget.execute(operation, messageResult, executionContext)) handled = true;
				if (handled && operation.isWriteOperation() && this.mode.equalsIgnoreCase(MODE_WRITE_FIRST_HANDLED)) break;
				if (handled && this.mode.equalsIgnoreCase(MODE_FIRST_HANDLED)) break;
			} catch (Exception ex) { 

				if (ex instanceof Xdi2MessagingException) 
					throw (Xdi2MessagingException) ex;
				else 
					throw new Xdi2MessagingException(ex);
			}
		}

		return handled;
	}

	public String getMode() {

		return this.mode;
	}

	public void setMode(String mode) {

		this.mode = mode;
	}

	public List<AbstractMessagingTarget> getMessagingTargets() {

		return this.messagingTargets;
	}

	public void setMessagingTargets(List<AbstractMessagingTarget> messagingTargets) {

		this.messagingTargets = messagingTargets;
	}

	private class CompoundMessageEnvelopeInterceptor implements MessageEnvelopeInterceptor {

		public boolean before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message envelope interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : messagingTarget.getMessageEnvelopeInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (before).");

					if (messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}

		public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message envelope interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : messagingTarget.getMessageEnvelopeInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (after).");

					if (messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Message envelope has been fully handled by interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}

		public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

			// execute all message envelope interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (MessageEnvelopeInterceptor messageEnvelopeInterceptor : messagingTarget.getMessageEnvelopeInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing message envelope interceptor " + messageEnvelopeInterceptor.getClass().getSimpleName() + " (exception).");

					messageEnvelopeInterceptor.exception(messageEnvelope, messageResult, executionContext, ex);
				}
			}
		}
	}

	private class CompoundMessageInterceptor implements MessageInterceptor {

		public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (MessageInterceptor messageInterceptor : messagingTarget.getMessageInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (before).");

					if (messageInterceptor.before(message, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}

		public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (MessageInterceptor messageInterceptor : messagingTarget.getMessageInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing message interceptor " + messageInterceptor.getClass().getSimpleName() + " (after).");

					if (messageInterceptor.after(message, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Message has been fully handled by interceptor " + messageInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}
	}

	private class CompoundOperationInterceptor implements OperationInterceptor {

		public boolean before(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all operation interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (OperationInterceptor operationInterceptor : messagingTarget.getOperationInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (before).");

					if (operationInterceptor.before(operation, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}

		public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all operation interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (OperationInterceptor operationInterceptor : messagingTarget.getOperationInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing operation interceptor " + operationInterceptor.getClass().getSimpleName() + " (after).");

					if (operationInterceptor.after(operation, messageResult, executionContext)) {

						if (log.isDebugEnabled()) log.debug("Operation has been fully handled by interceptor " + operationInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}
	}

	private class CompoundResultInterceptor implements ResultInterceptor {

		public boolean exclude(Statement statement, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all result interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (ResultInterceptor resultInterceptor : messagingTarget.getResultInterceptors()) {

					if (log.isDebugEnabled()) log.debug("Executing result interceptor " + resultInterceptor.getClass().getSimpleName() + " on address " + statement + ".");

					if (resultInterceptor.exclude(statement, executionContext)) {

						if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Result " + statement + " has been excluded by interceptor " + resultInterceptor.getClass().getSimpleName() + ".");
						return true;
					}
				}
			}

			return false;
		}
	}

	@Override
	public void before(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(messageEnvelope, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.before(messageEnvelope, executionContext);
	}

	@Override
	public void before(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(message, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.before(message, executionContext);
	}

	@Override
	public void before(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.before(operation, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.before(operation, executionContext);
	}

	@Override
	public void after(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(messageEnvelope, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.after(messageEnvelope, executionContext);
	}

	@Override
	public void after(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(message, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.after(message, executionContext);
	}

	@Override
	public void after(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

		super.after(operation, executionContext);

		for (AbstractMessagingTarget messagingTarget : this.messagingTargets) messagingTarget.after(operation, executionContext);
	}
}
