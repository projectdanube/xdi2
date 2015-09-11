package xdi2.messaging.target.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.interceptor.Interceptor;

/**
 * Messaging targets as well as contributors and interceptors can use the ExecutionContext
 * to store and share state. The ExecutionContext is created before a MessageEnvelope is
 * executed, and is deleted when execution of the MessageEnvelope is complete. It contains
 * the current position in the execution process, and attributes associated with various
 * stages.
 */
public final class ExecutionContext implements Serializable {

	private static final long serialVersionUID = 3238581605986543950L;

	private static Logger log = LoggerFactory.getLogger(ExecutionContext.class.getName());

	/**
	 * This map is never reset.
	 */
	private Map<String, Object> executionContextAttributes;

	/**
	 * This map is reset before executing a MessageEnvelope.
	 */
	private Map<String, Object> messageEnvelopeAttributes;

	/**
	 * This map is reset before executing a Message in a MessageEnvelope.
	 */
	private Map<String, Object> messageAttributes;

	/**
	 * This map is reset before executing an Operation in a Message.
	 */
	private Map<String, Object> operationAttributes;

	/**
	 * The exception that happened during execution.
	 */
	private Xdi2MessagingException ex;

	/**
	 * Timestamp of the first push.
	 */
	private long firstPush;

	/**
	 * The current execution position.
	 * This is either a MessagingTarget, a MessageEnvelope, a Message,
	 * an Operation, an Interceptor, a Contributor, an XDIAddress,
	 * or an XDIStatement.
	 */
	private ExecutionPosition<?> currentExecutionPosition, topExecutionPosition, exceptionExecutionPosition;

	private ExecutionContext() { 

		this.executionContextAttributes = new HashMap<String, Object> ();
		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
		this.messageAttributes = new HashMap<String, Object> ();
		this.operationAttributes = new HashMap<String, Object> ();

		this.ex = null;
		this.firstPush = -1;

		this.currentExecutionPosition = new ExecutionPosition<ExecutionContext> (null, this, null);
		this.topExecutionPosition = this.currentExecutionPosition;
	}

	public static ExecutionContext createExecutionContext() {

		return new ExecutionContext();
	}

	/*
	 * Attributes
	 */

	public Object getExecutionContextAttribute(String key) {

		return this.executionContextAttributes.get(key);
	}

	public void putExecutionContextAttribute(String key, Object value) {

		if (value == null) 
			this.executionContextAttributes.remove(key);
		else
			this.executionContextAttributes.put(key, value);
	}

	public Map<String, Object> getExecutionContextAttributes() {

		return this.executionContextAttributes;
	}

	public void setExecutionContextAttributes(Map<String, Object> executionContextAttributes) {

		this.executionContextAttributes = executionContextAttributes;
	}

	public void resetExecutionContextAttributes() {

		this.executionContextAttributes = new HashMap<String, Object> ();
	}

	public Object getMessageEnvelopeAttribute(String key) {

		return this.messageEnvelopeAttributes.get(key);
	}

	public void putMessageEnvelopeAttribute(String key, Object value) {

		if (value == null) 
			this.messageEnvelopeAttributes.remove(key);
		else
			this.messageEnvelopeAttributes.put(key, value);
	}

	public Map<String, Object> getMessageEnvelopeAttributes() {

		return this.messageEnvelopeAttributes;
	}

	public void setMessageEnvelopeAttributes(Map<String, Object> messageEnvelopeAttributes) {

		this.messageEnvelopeAttributes = messageEnvelopeAttributes;
	}

	public void resetMessageEnvelopeAttributes() {

		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
	}

	public Object getMessageAttribute(String key) {

		return this.messageAttributes.get(key);
	}

	public void putMessageAttribute(String key, Object value) {

		if (value == null) 
			this.messageAttributes.remove(key);
		else
			this.messageAttributes.put(key, value);
	}

	public Map<String, Object> getMessageAttributes() {

		return this.messageAttributes;
	}

	public void setMessageAttributes(Map<String, Object> messageAttributes) {

		this.messageAttributes = messageAttributes;
	}

	public void resetMessageAttributes() {

		this.messageAttributes = new HashMap<String, Object> ();
	}

	public Object getOperationAttribute(String key) {

		return this.operationAttributes.get(key);
	}

	public void putOperationAttribute(String key, Object value) {

		if (value == null) 
			this.operationAttributes.remove(key);
		else
			this.operationAttributes.put(key, value);
	}

	public Map<String, Object> getOperationAttributes() {

		return this.operationAttributes;
	}

	public void setOperationAttributes(Map<String, Object> operationAttributes) {

		this.operationAttributes = operationAttributes;
	}

	public void resetOperationAttributes() {

		this.operationAttributes = new HashMap<String, Object> ();
	}

	/*
	 * Exception
	 */

	public Xdi2MessagingException getException() {

		return this.ex;
	}

	public Xdi2MessagingException processException(Throwable ex) {

		assert(ex != null);

		if (! (ex instanceof Xdi2MessagingException)) {

			ex = new Xdi2MessagingException(ex.getClass().getSimpleName() + " [" + ex.getMessage() + "]", ex, this);
		}

		if (log.isDebugEnabled()) {

			StringBuffer buffer = new StringBuffer();
			buffer.append("New Exception: " + ex.getClass().getSimpleName() + " [" + ex.getMessage() + "]. ");

			if (this.ex != null) {

				buffer.append("Current: "  + this.ex.getClass().getSimpleName() + " [" + this.ex.getMessage() + "]. ");
				buffer.append("Same? " + (ex == this.ex));
			}

			log.debug(buffer.toString());
		}

		if (this.ex != null) return this.ex;

		this.ex = (Xdi2MessagingException) ex;
		this.exceptionExecutionPosition = this.currentExecutionPosition;

		return this.ex;
	}

	/*
	 * Execution positions
	 */

	public void pushMessagingTarget(MessagingTarget messagingTarget, String comment) {

		this.pushExecutionPosition(messagingTarget, comment);
	}

	public void pushMessageEnvelope(MessageEnvelope messageEnvelope, String comment) {

		this.pushExecutionPosition(messageEnvelope, comment);
	}

	public void pushMessage(Message message, String comment) {

		this.pushExecutionPosition(message, comment);
	}

	public void pushOperation(Operation operation, String comment) {

		this.pushExecutionPosition(operation, comment);
	}

	public void pushTargetAddress(XDIAddress targetAddress, String comment) {

		this.pushExecutionPosition(targetAddress, comment);
	}

	public void pushTargetStatement(XDIStatement targetStatement, String comment) {

		this.pushExecutionPosition(targetStatement, comment);
	}

	public void pushInterceptor(Interceptor<MessagingTarget> interceptor, String comment) {

		this.pushExecutionPosition(interceptor, comment);
	}

	public void pushContributor(Contributor contributor, String comment) {

		this.pushExecutionPosition(contributor, comment);
	}

	public void popMessagingTarget() {

		this.popExecutionPosition(MessagingTarget.class);
	}

	public void popMessageEnvelope() {

		this.popExecutionPosition(MessageEnvelope.class);
	}

	public void popMessage() {

		this.popExecutionPosition(Message.class);
	}

	public void popOperation() {

		this.popExecutionPosition(Operation.class);
	}

	public void popTargetAddress() {

		this.popExecutionPosition(XDIAddress.class);
	}

	public void popTargetStatement() {

		this.popExecutionPosition(XDIStatement.class);
	}

	public void popInterceptor() {

		this.popExecutionPosition(Interceptor.class);
	}

	public void popContributor() {

		this.popExecutionPosition(Contributor.class);
	}

	public MessagingTarget getCurrentMessagingTarget() {

		ExecutionPosition<MessagingTarget> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, MessagingTarget.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public List<MessagingTarget> getCurrentMessagingTargets() {

		List<ExecutionPosition<MessagingTarget>> executionPositions = this.findExecutionPositions(this.currentExecutionPosition, MessagingTarget.class);

		List<MessagingTarget> messagingTargets = new ArrayList<MessagingTarget> ();
		for (ExecutionPosition<MessagingTarget> executionPosition : executionPositions) messagingTargets.add(executionPosition.executionObject);

		return messagingTargets;
	}

	public MessageEnvelope getCurrentMessageEnvelope() {

		ExecutionPosition<MessageEnvelope> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, MessageEnvelope.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public List<MessageEnvelope> getCurrentMessageEnvelopes() {

		List<ExecutionPosition<MessageEnvelope>> executionPositions = this.findExecutionPositions(this.currentExecutionPosition, MessageEnvelope.class);

		List<MessageEnvelope> messageEnvelopes = new ArrayList<MessageEnvelope> ();
		for (ExecutionPosition<MessageEnvelope> executionPosition : executionPositions) messageEnvelopes.add(executionPosition.executionObject);

		return messageEnvelopes;
	}

	public Message getCurrentMessage() {

		ExecutionPosition<Message> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Message.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public List<Message> getCurrentMessages() {

		List<ExecutionPosition<Message>> executionPositions = this.findExecutionPositions(this.currentExecutionPosition, Message.class);

		List<Message> messages = new ArrayList<Message> ();
		for (ExecutionPosition<Message> executionPosition : executionPositions) messages.add(executionPosition.executionObject);

		return messages;
	}

	public Operation getCurrentOperation() {

		ExecutionPosition<Operation> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Operation.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public List<Operation> getCurrentOperations() {

		List<ExecutionPosition<Operation>> executionPositions = this.findExecutionPositions(this.currentExecutionPosition, Operation.class);

		List<Operation> operations = new ArrayList<Operation> ();
		for (ExecutionPosition<Operation> executionPosition : executionPositions) operations.add(executionPosition.executionObject);

		return operations;
	}

	public XDIAddress getCurrentTargetAddress() {

		ExecutionPosition<XDIAddress> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, XDIAddress.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDIStatement getCurrentTargetStatement() {

		ExecutionPosition<XDIStatement> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, XDIStatement.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Interceptor<MessagingTarget> getCurrentInterceptor() {

		ExecutionPosition<Interceptor> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Interceptor.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Contributor getCurrentContributor() {

		ExecutionPosition<Contributor> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Contributor.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public MessagingTarget getExceptionMessagingTarget() {

		ExecutionPosition<MessagingTarget> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, MessagingTarget.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public MessageEnvelope getExceptionMessageEnvelope() {

		ExecutionPosition<MessageEnvelope> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, MessageEnvelope.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Message getExceptionMessage() {

		ExecutionPosition<Message> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, Message.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Operation getExceptionOperation() {

		ExecutionPosition<Operation> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, Operation.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDIAddress getExceptionTargetAddress() {

		ExecutionPosition<XDIAddress> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, XDIAddress.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDIStatement getExceptionTargetStatement() {

		ExecutionPosition<XDIStatement> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, XDIStatement.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Interceptor<MessagingTarget> getExceptionInterceptor() {

		ExecutionPosition<Interceptor> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, Interceptor.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Contributor getExceptionContributor() {

		ExecutionPosition<Contributor> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, Contributor.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public boolean isTopExecutionPosition() {

		return this.currentExecutionPosition == this.topExecutionPosition;
	}

	private <T> void pushExecutionPosition(T object, String comment) {

		if (object == null) throw new NullPointerException();

		this.currentExecutionPosition = new ExecutionPosition<T> (this.currentExecutionPosition, object, comment);

		this.currentExecutionPosition.push = System.currentTimeMillis();
		if (this.firstPush == -1) this.firstPush = this.currentExecutionPosition.push;
	}

	private <T> void popExecutionPosition(Class<? extends T> clazz) {

		this.currentExecutionPosition.pop = System.currentTimeMillis();

		if (this.currentExecutionPosition == this.topExecutionPosition) throw new IllegalStateException("No more execution positions.");
		if (! clazz.isAssignableFrom(this.currentExecutionPosition.executionObject.getClass())) throw new IllegalStateException("Unexpected execution position class: " + this.currentExecutionPosition.executionObject.getClass().getSimpleName() + " (should be " + clazz.getSimpleName() + ").");

		this.currentExecutionPosition = this.currentExecutionPosition.parentExecutionPosition;
	}

	@SuppressWarnings("unchecked")
	private <T> List<ExecutionPosition<T>> findExecutionPositions(ExecutionPosition<?> startExecutionPosition, Class<? extends T> clazz) {

		List<ExecutionPosition<T>> executionPositions = new ArrayList<ExecutionPosition<T>> ();

		for (ExecutionPosition<?> executionPosition = startExecutionPosition; executionPosition != this.topExecutionPosition; ) {

			if (clazz.isAssignableFrom(executionPosition.executionObject.getClass())) executionPositions.add((ExecutionPosition<T>) executionPosition);

			executionPosition = executionPosition.parentExecutionPosition;
		}

		return executionPositions;
	}

	@SuppressWarnings("unchecked")
	private <T> ExecutionPosition<T> findExecutionPosition(ExecutionPosition<?> startExecutionPosition, Class<? extends T> clazz) {

		for (ExecutionPosition<?> executionPosition = startExecutionPosition; executionPosition != this.topExecutionPosition; ) {

			if (clazz.isAssignableFrom(executionPosition.executionObject.getClass())) return (ExecutionPosition<T>) executionPosition;

			executionPosition = executionPosition.parentExecutionPosition;
		}

		return null;
	}

	/*
	 * Tracing
	 */

	public String getTraceLine() {

		StringBuffer buffer = new StringBuffer();

		for (ExecutionPosition<?> executionPosition = this.exceptionExecutionPosition; executionPosition != this.topExecutionPosition; ) {

			Object executionObject = executionPosition.executionObject;

			buffer.insert(0, executionObject.getClass().getSimpleName());
			if (executionPosition.parentExecutionPosition != this.topExecutionPosition) buffer.insert(0, "-->");

			executionPosition = executionPosition.parentExecutionPosition;
		}

		return buffer.toString();
	}

	public String getTraceBlock() {

		return this.getTraceBlock(0, this.topExecutionPosition);
	}

	private String getTraceBlock(int depth, ExecutionPosition<?> parentExecutionPosition) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("\n");
		for (int i=0; i<depth; i++) buffer.append("  ");
		buffer.append(parentExecutionPosition.toString());

		if (parentExecutionPosition == this.currentExecutionPosition) buffer.append(" <-- (CURRENT)");
		if (parentExecutionPosition == this.exceptionExecutionPosition) buffer.append(" <-- (EXCEPTION)");

		for (ExecutionPosition<?> executionPosition : parentExecutionPosition.childExecutionPositions) {

			buffer.append(this.getTraceBlock(depth + 1, executionPosition));
		}

		return buffer.toString();
	}

	/*
	 * Helper classes
	 */

	private final class ExecutionPosition<T> {

		private ExecutionPosition<?> parentExecutionPosition;
		private T executionObject;
		private String comment;

		private long push;
		private long pop;

		private List<ExecutionPosition<?>> childExecutionPositions;

		private ExecutionPosition(ExecutionPosition<?> parentExecutionPosition, T executionObject, String comment) {

			this.parentExecutionPosition = parentExecutionPosition;
			this.executionObject = executionObject;
			this.comment = comment;

			this.push = -1;
			this.pop = -1;

			this.childExecutionPositions = new ArrayList<ExecutionPosition<?>> ();

			if (parentExecutionPosition != null) parentExecutionPosition.childExecutionPositions.add(this);
		}

		@Override
		public String toString() {

			StringBuffer buffer = new StringBuffer();
			if (ExecutionContext.this.firstPush != -1 && this.push != -1 && this.pop != -1) buffer.append("[" + (this.push - ExecutionContext.this.firstPush) + "-"+ (this.pop - ExecutionContext.this.firstPush) + "ms]  ");
			buffer.append(this.executionObject.getClass().getSimpleName());
			if (this.comment != null) buffer.append("  (" + this.comment + ")");

			return buffer.toString();
		}
	}
}
