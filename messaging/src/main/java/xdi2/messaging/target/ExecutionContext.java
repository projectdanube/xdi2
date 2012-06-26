package xdi2.messaging.target;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * MessagingTargets as well as Interceptors can use the ExecutionContext to store
 * and share state. The ExecutionContext is created before a MessageEnvelope is executed,
 * and is deleted when execution of the MessageEnvelope is complete.
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

	public ExecutionContext() { 

		this.messageEnvelopeAttributes = new HashMap<String, Object> ();
		this.messageAttributes = new HashMap<String, Object> ();
		this.operationAttributes = new HashMap<String, Object> ();
	}

	public Map<String, Object> getMessageEnvelopeAttributes() {

		return this.messageEnvelopeAttributes;
	}

	public Map<String, Object> getMessageAttributes() {

		return this.messageAttributes;
	}

	public Map<String, Object> getOperationAttributes() {

		return this.operationAttributes;
	}

	public Object getMessageEnvelopeAttribute(String key) {

		return this.messageEnvelopeAttributes.get(key);
	}

	public void setMessageEnvelopeAttribute(String key, Object value) {

		this.messageEnvelopeAttributes.put(key, value);
	}

	public void clearMessageEnvelopeAttributes() {

		this.messageEnvelopeAttributes.clear();
	}

	public Object getMessageAttribute(String key) {

		return this.messageAttributes.get(key);
	}

	public void setMessageAttribute(String key, Object value) {

		this.messageAttributes.put(key, value);
	}

	public void clearMessageAttributes() {

		this.messageAttributes.clear();
	}

	public Object getOperationAttribute(String key) {

		return this.operationAttributes.get(key);
	}

	public void setOperationAttribute(String key, Object value) {

		this.operationAttributes.put(key, value);
	}

	public void clearOperationAttributes() {

		this.operationAttributes.clear();
	}
}
