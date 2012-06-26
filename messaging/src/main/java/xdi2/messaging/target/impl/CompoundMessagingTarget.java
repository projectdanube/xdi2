package xdi2.messaging.target.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.interceptor.MessageEnvelopeInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.ResultInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;

/**
 * A messaging target that can combine multiple other messaging targets.
 * 
 * Incoming messages will be sequentially applied to all individual targets, and
 * a single message result will be produced.
 * 
 * @author markus
 */
public class CompoundMessagingTarget extends AbstractMessagingTarget {

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

		this.getInterceptors().add(new CompoundMessageEnvelopeInterceptor());
		this.getInterceptors().add(new CompoundMessageInterceptor());
		this.getInterceptors().add(new CompoundOperationInterceptor());
		this.getInterceptors().add(new CompoundTargetInterceptor());
		this.getInterceptors().add(new CompoundResultInterceptor());
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

				for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = messagingTarget.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

					MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();
					if (messageEnvelopeInterceptor.before(messageEnvelope, messageResult, executionContext)) return true;
				}
			}

			return false;
		}

		public boolean after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message envelope interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = messagingTarget.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

					MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();
					if (messageEnvelopeInterceptor.after(messageEnvelope, messageResult, executionContext)) return true;
				}
			}

			return false;
		}

		public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex) {

			// execute all message envelope interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<MessageEnvelopeInterceptor> messageEnvelopeInterceptors = messagingTarget.getMessageEnvelopeInterceptors(); messageEnvelopeInterceptors.hasNext(); ) {

					MessageEnvelopeInterceptor messageEnvelopeInterceptor = messageEnvelopeInterceptors.next();
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

				for (Iterator<MessageInterceptor> messageInterceptors = messagingTarget.getMessageInterceptors(); messageInterceptors.hasNext(); ) {

					MessageInterceptor messageInterceptor = messageInterceptors.next();

					if (messageInterceptor.before(message, messageResult, executionContext)) return true;
				}
			}

			return false;
		}

		public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all message interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<MessageInterceptor> messageInterceptors = messagingTarget.getMessageInterceptors(); messageInterceptors.hasNext(); ) {

					MessageInterceptor messageInterceptor = messageInterceptors.next();

					if (messageInterceptor.after(message, messageResult, executionContext)) return true;
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

				for (Iterator<OperationInterceptor> operationInterceptors = messagingTarget.getOperationInterceptors(); operationInterceptors.hasNext(); ) {

					OperationInterceptor operationInterceptor = operationInterceptors.next();
					if (operationInterceptor.before(operation, messageResult, executionContext)) return true;
				}
			}

			return false;
		}

		public boolean after(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all operation interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<OperationInterceptor> operationInterceptors = messagingTarget.getOperationInterceptors(); operationInterceptors.hasNext(); ) {

					OperationInterceptor operationInterceptor = operationInterceptors.next();
					if (operationInterceptor.after(operation, messageResult, executionContext)) return true;
				}
			}

			return false;
		}
	}

	private class CompoundTargetInterceptor implements TargetInterceptor {

		public Statement targetStatement(Operation operation, Statement targetStatement, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all target interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<TargetInterceptor> targetInterceptors = messagingTarget.getTargetInterceptors(); targetInterceptors.hasNext(); ) {

					TargetInterceptor targetInterceptor = targetInterceptors.next();
					targetStatement = targetInterceptor.targetStatement(operation, targetStatement, executionContext);
				}
			}

			return targetStatement;
		}

		public XRI3Segment targetAddress(Operation operation, XRI3Segment targetAddress, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all target interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<TargetInterceptor> targetInterceptors = messagingTarget.getTargetInterceptors(); targetInterceptors.hasNext(); ) {

					TargetInterceptor targetInterceptor = targetInterceptors.next();
					targetAddress = targetInterceptor.targetAddress(operation, targetAddress, executionContext);
				}
			}

			return targetAddress;
		}
	}

	private class CompoundResultInterceptor implements ResultInterceptor {

		public void finish(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			// execute all result interceptors

			for (int i=0; i<CompoundMessagingTarget.this.messagingTargets.size(); i++) {

				AbstractMessagingTarget messagingTarget = CompoundMessagingTarget.this.messagingTargets.get(i);

				for (Iterator<ResultInterceptor> resultInterceptors = messagingTarget.getResultInterceptors(); resultInterceptors.hasNext(); ) {

					ResultInterceptor resultInterceptor = resultInterceptors.next();

					resultInterceptor.finish(messageResult, executionContext);
				}
			}
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
