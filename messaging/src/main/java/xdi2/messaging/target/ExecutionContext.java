package xdi2.messaging.target;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.contributor.Contributor;
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

	/**
	 * This map is preserved during the entire execution of a MessageEnvelope.
	 */
	private Map<String, Object> messageEnvelopeAttributes;

	/**
	 * This map is emptied before a Message in a MessageEnvelope is executed.
	 */
	private Map<String, Object> messageAttributes;

	/**
	 * This map is emptied before an Operation in a Message is executed.
	 */
	private Map<String, Object> operationAttributes;

	/**
	 * The exception that happened during execution.
	 */
	private Xdi2MessagingException ex;

	/**
	 * The current execution position.
	 * This is either a MessagingTarget, a MessageEnvelope, a Message,
	 * an Operation, an Interceptor, or a Contributor.
	 */
	private ExecutionPosition<?> currentExecutionPosition, topExecutionPosition, exceptionExecutionPosition;

	public ExecutionContext() { 

		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
		this.messageAttributes = new HashMap<String, Object> ();
		this.operationAttributes = new HashMap<String, Object> ();

		this.ex = null;

		this.currentExecutionPosition = new ExecutionPosition<ExecutionContext> (null, this, null);
		this.topExecutionPosition = this.currentExecutionPosition;
	}

	/*
	 * Attributes
	 */

	public Object getMessageEnvelopeAttribute(String key) {

		return this.messageEnvelopeAttributes.get(key);
	}

	public void putMessageEnvelopeAttribute(String key, Object value) {

		if (value == null) 
			this.messageEnvelopeAttributes.remove(key);
		else
			this.messageEnvelopeAttributes.put(key, value);
	}

	public void clearMessageEnvelopeAttributes() {

		this.messageEnvelopeAttributes.clear();
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

	public void clearMessageAttributes() {

		this.messageAttributes.clear();
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

	public void clearOperationAttributes() {

		this.operationAttributes.clear();
	}

	/*
	 * Exception
	 */

	public Xdi2MessagingException getException() {

		return this.ex;
	}

	public Xdi2MessagingException processException(Exception ex) {

		if (! (ex instanceof Xdi2MessagingException)) {

			ex = new Xdi2MessagingException(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage(), ex, this);
		}

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

	public void pushTargetAddress(XDI3Segment targetAddress, String comment) {

		this.pushExecutionPosition(targetAddress, comment);
	}

	public void pushTargetStatement(XDI3Statement targetStatement, String comment) {

		this.pushExecutionPosition(targetStatement, comment);
	}

	public void pushInterceptor(Interceptor interceptor, String comment) {

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

		this.popExecutionPosition(XDI3Segment.class);
	}

	public void popTargetStatement() {

		this.popExecutionPosition(XDI3Statement.class);
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

	public MessageEnvelope getCurrentMessageEnvelope() {

		ExecutionPosition<MessageEnvelope> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, MessageEnvelope.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Message getCurrentMessage() {

		ExecutionPosition<Message> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Message.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Operation getCurrentOperation() {

		ExecutionPosition<Operation> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, Operation.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDI3Segment getCurrentTargetAddress() {

		ExecutionPosition<XDI3Segment> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, XDI3Segment.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDI3Statement getCurrentTargetStatement() {

		ExecutionPosition<XDI3Statement> executionPosition = this.findExecutionPosition(this.currentExecutionPosition, XDI3Statement.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Interceptor getCurrentInterceptor() {

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

	public XDI3Segment getExceptionTargetAddress() {

		ExecutionPosition<XDI3Segment> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, XDI3Segment.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public XDI3Statement getExceptionTargetStatement() {

		ExecutionPosition<XDI3Statement> executionPosition = this.findExecutionPosition(this.exceptionExecutionPosition, XDI3Statement.class);

		return executionPosition == null ? null : executionPosition.executionObject;
	}

	public Interceptor getExceptionInterceptor() {

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

		this.currentExecutionPosition = new ExecutionPosition<T> (this.currentExecutionPosition, object, comment);
	}

	@SuppressWarnings("unchecked")
	private <T> void popExecutionPosition(Class<? extends T> clazz) {

		ExecutionPosition<T> executionPosition = (ExecutionPosition<T>) this.currentExecutionPosition;

		if (this.currentExecutionPosition == this.topExecutionPosition) throw new IllegalStateException();

		this.currentExecutionPosition = executionPosition.parentExecutionPosition;
	}

	@SuppressWarnings("unchecked")
	private <T> ExecutionPosition<T> findExecutionPosition(ExecutionPosition<?> startExecutionPosition, Class<? extends T> clazz) {

		for (ExecutionPosition<?> executionPosition = startExecutionPosition; executionPosition != this.topExecutionPosition; ) {

			if (clazz.isAssignableFrom(executionPosition.executionObject.getClass())) return (ExecutionPosition<T>) executionPosition;

			executionPosition = executionPosition.parentExecutionPosition;
		}

		return null;
	}

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

	private static final class ExecutionPosition<T> {

		private ExecutionPosition<?> parentExecutionPosition;
		private T executionObject;
		private String comment;

		private List<ExecutionPosition<?>> childExecutionPositions;

		private ExecutionPosition(ExecutionPosition<?> parentExecutionPosition, T executionObject, String comment) {

			this.parentExecutionPosition = parentExecutionPosition;
			this.executionObject = executionObject;
			this.comment = comment;

			this.childExecutionPositions = new ArrayList<ExecutionPosition<?>> ();

			if (parentExecutionPosition != null) parentExecutionPosition.childExecutionPositions.add(this);
		}

		@Override
		public String toString() {

			return this.executionObject.getClass().getSimpleName() + (this.comment == null ? "" : " (" + this.comment + ")");
		}
	}
}
