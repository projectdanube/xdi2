package xdi2.client.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;

/**
 * Manipulators can use the ManipulationContext to store and share state. The ManipulationContext
 * is created before a message is sent. It contains the current position in the manipulation process,
 * and attributes associated with various stages.
 */
public final class ManipulationContext implements Serializable {

	private static final long serialVersionUID = -4765997602206764806L;

	private static Logger log = LoggerFactory.getLogger(ManipulationContext.class.getName());

	/**
	 * This map is never reset.
	 */
	private Map<String, Object> manipulationContextAttributes;

	/**
	 * This map is reset before manipulating a MessageEnvelope.
	 */
	private Map<String, Object> messageEnvelopeAttributes;

	/**
	 * This map is reset before manipulating a Message in a MessageEnvelope.
	 */
	private Map<String, Object> messageAttributes;

	/**
	 * The exception that happened during manipulation.
	 */
	private Throwable ex;

	/**
	 * Timestamp of the first push.
	 */
	private long firstPush;

	/**
	 * The current manipulation position.
	 * This is either a MessageEnvelope, or a Message.
	 */
	private ManipulationPosition<?> currentManipulationPosition, topManipulationPosition, exceptionManipulationPosition;

	private ManipulationContext() { 

		this.manipulationContextAttributes = new HashMap<String, Object> ();
		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
		this.messageAttributes = new HashMap<String, Object> ();

		this.ex = null;
		this.firstPush = -1;

		this.currentManipulationPosition = new ManipulationPosition<ManipulationContext> (null, this, null);
		this.topManipulationPosition = this.currentManipulationPosition;
	}

	public static ManipulationContext createManipulationContext() {

		return new ManipulationContext();
	}

	/*
	 * Attributes
	 */

	public Object getManipulationContextAttribute(String key) {

		return this.manipulationContextAttributes.get(key);
	}

	public void putManipulationContextAttribute(String key, Object value) {

		if (value == null) 
			this.manipulationContextAttributes.remove(key);
		else
			this.manipulationContextAttributes.put(key, value);
	}

	public Map<String, Object> getManipulationContextAttributes() {

		return this.manipulationContextAttributes;
	}

	public void setManipulationContextAttributes(Map<String, Object> manipulationContextAttributes) {

		this.manipulationContextAttributes = manipulationContextAttributes;
	}

	public void resetManipulationContextAttributes() {

		this.manipulationContextAttributes = new HashMap<String, Object> ();
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

	/*
	 * Exception
	 */

	public Throwable getException() {

		return this.ex;
	}

	public Throwable processException(Throwable ex) {

		assert(ex != null);

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

		this.ex = ex;
		this.exceptionManipulationPosition = this.currentManipulationPosition;

		return this.ex;
	}

	/*
	 * Manipulation positions
	 */

	public void pushMessageEnvelope(MessageEnvelope messageEnvelope, String comment) {

		this.pushManipulationPosition(messageEnvelope, comment);
	}

	public void pushMessage(Message message, String comment) {

		this.pushManipulationPosition(message, comment);
	}

	public void popMessageEnvelope() {

		this.popManipulationPosition(MessageEnvelope.class);
	}

	public void popMessage() {

		this.popManipulationPosition(Message.class);
	}

	public MessageEnvelope getCurrentMessageEnvelope() {

		ManipulationPosition<MessageEnvelope> manipulationPosition = this.findManipulationPosition(this.currentManipulationPosition, MessageEnvelope.class);

		return manipulationPosition == null ? null : manipulationPosition.manipulationObject;
	}

	public List<MessageEnvelope> getCurrentMessageEnvelopes() {

		List<ManipulationPosition<MessageEnvelope>> manipulationPositions = this.findManipulationPositions(this.currentManipulationPosition, MessageEnvelope.class);

		List<MessageEnvelope> messageEnvelopes = new ArrayList<MessageEnvelope> ();
		for (ManipulationPosition<MessageEnvelope> manipulationPosition : manipulationPositions) messageEnvelopes.add(manipulationPosition.manipulationObject);

		return messageEnvelopes;
	}

	public Message getCurrentMessage() {

		ManipulationPosition<Message> manipulationPosition = this.findManipulationPosition(this.currentManipulationPosition, Message.class);

		return manipulationPosition == null ? null : manipulationPosition.manipulationObject;
	}

	public List<Message> getCurrentMessages() {

		List<ManipulationPosition<Message>> manipulationPositions = this.findManipulationPositions(this.currentManipulationPosition, Message.class);

		List<Message> messages = new ArrayList<Message> ();
		for (ManipulationPosition<Message> manipulationPosition : manipulationPositions) messages.add(manipulationPosition.manipulationObject);

		return messages;
	}

	public Operation getCurrentOperation() {

		ManipulationPosition<Operation> manipulationPosition = this.findManipulationPosition(this.currentManipulationPosition, Operation.class);

		return manipulationPosition == null ? null : manipulationPosition.manipulationObject;
	}

	public MessageEnvelope getExceptionMessageEnvelope() {

		ManipulationPosition<MessageEnvelope> manipulationPosition = this.findManipulationPosition(this.exceptionManipulationPosition, MessageEnvelope.class);

		return manipulationPosition == null ? null : manipulationPosition.manipulationObject;
	}

	public Message getExceptionMessage() {

		ManipulationPosition<Message> manipulationPosition = this.findManipulationPosition(this.exceptionManipulationPosition, Message.class);

		return manipulationPosition == null ? null : manipulationPosition.manipulationObject;
	}

	public boolean isTopManipulationPosition() {

		return this.currentManipulationPosition == this.topManipulationPosition;
	}

	private <T> void pushManipulationPosition(T object, String comment) {

		if (object == null) throw new NullPointerException();

		this.currentManipulationPosition = new ManipulationPosition<T> (this.currentManipulationPosition, object, comment);

		this.currentManipulationPosition.push = System.currentTimeMillis();
		if (this.firstPush == -1) this.firstPush = this.currentManipulationPosition.push;
	}

	private <T> void popManipulationPosition(Class<? extends T> clazz) {

		this.currentManipulationPosition.pop = System.currentTimeMillis();

		if (this.currentManipulationPosition == this.topManipulationPosition) throw new IllegalStateException("No more manipulation positions.");
		if (! clazz.isAssignableFrom(this.currentManipulationPosition.manipulationObject.getClass())) throw new IllegalStateException("Unexpected manipulation position class: " + this.currentManipulationPosition.manipulationObject.getClass().getSimpleName() + " (should be " + clazz.getSimpleName() + ").");

		this.currentManipulationPosition = this.currentManipulationPosition.parentManipulationPosition;
	}

	@SuppressWarnings("unchecked")
	private <T> List<ManipulationPosition<T>> findManipulationPositions(ManipulationPosition<?> startManipulationPosition, Class<? extends T> clazz) {

		List<ManipulationPosition<T>> manipulationPositions = new ArrayList<ManipulationPosition<T>> ();

		for (ManipulationPosition<?> manipulationPosition = startManipulationPosition; manipulationPosition != this.topManipulationPosition; ) {

			if (clazz.isAssignableFrom(manipulationPosition.manipulationObject.getClass())) manipulationPositions.add((ManipulationPosition<T>) manipulationPosition);

			manipulationPosition = manipulationPosition.parentManipulationPosition;
		}

		return manipulationPositions;
	}

	@SuppressWarnings("unchecked")
	private <T> ManipulationPosition<T> findManipulationPosition(ManipulationPosition<?> startManipulationPosition, Class<? extends T> clazz) {

		for (ManipulationPosition<?> manipulationPosition = startManipulationPosition; manipulationPosition != this.topManipulationPosition; ) {

			if (clazz.isAssignableFrom(manipulationPosition.manipulationObject.getClass())) return (ManipulationPosition<T>) manipulationPosition;

			manipulationPosition = manipulationPosition.parentManipulationPosition;
		}

		return null;
	}

	/*
	 * Tracing
	 */

	public String getTraceLine() {

		StringBuffer buffer = new StringBuffer();

		for (ManipulationPosition<?> manipulationPosition = this.exceptionManipulationPosition; manipulationPosition != this.topManipulationPosition; ) {

			Object manipulationObject = manipulationPosition.manipulationObject;

			buffer.insert(0, manipulationObject.getClass().getSimpleName());
			if (manipulationPosition.parentManipulationPosition != this.topManipulationPosition) buffer.insert(0, "-->");

			manipulationPosition = manipulationPosition.parentManipulationPosition;
		}

		return buffer.toString();
	}

	public String getTraceBlock() {

		return this.getTraceBlock(0, this.topManipulationPosition);
	}

	private String getTraceBlock(int depth, ManipulationPosition<?> parentManipulationPosition) {

		StringBuffer buffer = new StringBuffer();

		buffer.append("\n");
		for (int i=0; i<depth; i++) buffer.append("  ");
		buffer.append(parentManipulationPosition.toString());

		if (parentManipulationPosition == this.currentManipulationPosition) buffer.append(" <-- (CURRENT)");
		if (parentManipulationPosition == this.exceptionManipulationPosition) buffer.append(" <-- (EXCEPTION)");

		for (ManipulationPosition<?> manipulationPosition : parentManipulationPosition.childManipulationPositions) {

			buffer.append(this.getTraceBlock(depth + 1, manipulationPosition));
		}

		return buffer.toString();
	}

	/*
	 * Helper classes
	 */

	private final class ManipulationPosition<T> {

		private ManipulationPosition<?> parentManipulationPosition;
		private T manipulationObject;
		private String comment;

		private long push;
		private long pop;

		private List<ManipulationPosition<?>> childManipulationPositions;

		private ManipulationPosition(ManipulationPosition<?> parentManipulationPosition, T manipulationObject, String comment) {

			this.parentManipulationPosition = parentManipulationPosition;
			this.manipulationObject = manipulationObject;
			this.comment = comment;

			this.push = -1;
			this.pop = -1;

			this.childManipulationPositions = new ArrayList<ManipulationPosition<?>> ();

			if (parentManipulationPosition != null) parentManipulationPosition.childManipulationPositions.add(this);
		}

		@Override
		public String toString() {

			StringBuffer buffer = new StringBuffer();
			if (ManipulationContext.this.firstPush != -1 && this.push != -1 && this.pop != -1) buffer.append("[" + (this.push - ManipulationContext.this.firstPush) + "-"+ (this.pop - ManipulationContext.this.firstPush) + "ms]  ");
			buffer.append(this.manipulationObject.getClass().getSimpleName());
			if (this.comment != null) buffer.append("  (" + this.comment + ")");

			return buffer.toString();
		}
	}
}
