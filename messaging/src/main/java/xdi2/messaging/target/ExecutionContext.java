package xdi2.messaging.target;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;
import xdi2.messaging.target.contributor.Contributor;
import xdi2.messaging.target.interceptor.Interceptor;

/**
 * Messaging tarets as well as contributors and interceptors can use the ExecutionContext
 * to store and share state. The ExecutionContext is created before a MessageEnvelope is
 * executed, and is deleted when execution of the MessageEnvelope is complete. It contains
 * the current position in the execution process, and attributes associated with various
 * stages.
 */
public final class ExecutionContext implements Serializable {

	private static final long serialVersionUID = 3238581605986543950L;

	/*
	 * Position
	 */

	/**
	 * The current position.
	 * This is either a MessagingTarget, a MessageEnvelope, a Message,
	 * an Operation, an Interceptor, or a Contributor.
	 */
	private Position<?> current, top;

	/*
	 * Attributes
	 */

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

	public ExecutionContext() { 

		this.current = new Position<ExecutionContext> (null, this, null);
		this.top = this.current;

		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
		this.messageAttributes = new HashMap<String, Object> ();
		this.operationAttributes = new HashMap<String, Object> ();
	}

	/*
	 * Position
	 */

	public void pushMessagingTarget(MessagingTarget messagingTarget, String comment) {

		this.pushPosition(messagingTarget, comment);
	}

	public void pushMessageEnvelope(MessageEnvelope messageEnvelope, String comment) {

		this.pushPosition(messageEnvelope, comment);
	}

	public void pushMessage(Message message, String comment) {

		this.pushPosition(message, comment);
	}

	public void pushOperation(Operation operation, String comment) {

		this.pushPosition(operation, comment);
	}

	public void pushInterceptor(Interceptor interceptor, String comment) {

		this.pushPosition(interceptor, comment);
	}

	public void pushContributor(Contributor contributor, String comment) {

		this.pushPosition(contributor, comment);
	}

	public void popMessagingTarget() {

		this.popPosition(MessagingTarget.class);
	}

	public void popMessageEnvelope() {

		this.popPosition(MessageEnvelope.class);
	}

	public void popMessage() {

		this.popPosition(Message.class);
	}

	public void popOperation() {

		this.popPosition(Operation.class);
	}

	public void popInterceptor() {

		this.popPosition(Interceptor.class);
	}

	public void popContributor() {

		this.popPosition(Contributor.class);
	}

	public MessagingTarget getMessagingTarget() {

		return this.findPosition(MessagingTarget.class);
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.findPosition(MessageEnvelope.class);
	}

	public Message getMessage() {

		return this.findPosition(Message.class);
	}

	public Operation getOperation() {

		return this.findPosition(Operation.class);
	}

	public Interceptor getInterceptor() {

		return this.findPosition(Interceptor.class);
	}

	public Contributor getContributor() {

		return this.findPosition(Contributor.class);
	}

	public boolean isTopPosition() {

		return this.current == this.top;
	}

	public String getPositionString() {

		StringBuffer buffer = new StringBuffer();

		for (Position<?> position = this.current; position != this.top; ) {

			Object object = position.object;

			buffer.insert(0, object.getClass().getSimpleName());
			if (position.parent != this.top) buffer.insert(0, "-->");

			position = position.parent;
		}

		return buffer.toString();
	}

	public String getTraceString() {

		return this.getTraceString(0, this.top);
	}

	private String getTraceString(int depth, Position<?> parent) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("\n");
		for (int i=0; i<depth; i++) buffer.append("  ");
		buffer.append(parent.toString());

		for (Position<?> position : parent.positions) {

			buffer.append(this.getTraceString(depth + 1, position));
		}

		return buffer.toString();
	}

	private <T> void pushPosition(T object, String comment) {

		this.current = new Position<T> (this.current, object, comment);
	}

	@SuppressWarnings("unchecked")
	private <T> void popPosition(Class<? extends T> clazz) {

		Position<T> position = (Position<T>) this.current;

		if (this.current == this.top) throw new IllegalStateException();

		this.current = position.parent;
	}

	@SuppressWarnings("unchecked")
	private <T> T findPosition(Class<? extends T> clazz) {

		for (Position<?> position = this.current; position != this.top; ) {

			if (clazz.isAssignableFrom(position.object.getClass())) return (T) position.object;

			position = position.parent;
		}

		return null;
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
	 * Helper classes
	 */

	private static final class Position<T> {

		private Position<?> parent;
		private T object;
		private String comment;
		
		private List<Position<?>> positions;

		private Position(Position<?> parent, T object, String comment) {

			this.parent = parent;
			this.object = object;
			this.comment = comment;

			this.positions = new ArrayList<Position<?>> ();

			if (parent != null) parent.positions.add(this);
		}
		
		@Override
		public String toString() {
			
			return this.object.getClass().getSimpleName() + (this.comment == null ? "" : " (" + this.comment + ")");
		}
	}
}
