package xdi2.messaging.target.impl;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.request.RequestMessage;
import xdi2.messaging.request.RequestMessageEnvelope;
import xdi2.messaging.target.Extension;


public class AbstractExtension <CONTAINER> implements Extension<CONTAINER> {

	private static final Logger log = LoggerFactory.getLogger(AbstractExtension.class);

	public final static int DEFAULT_INIT_PRIORITY = 10;
	public final static int DEFAULT_SHUTDOWN_PRIORITY = 10;

	private int initPriority;
	private int shutdownPriority;
	private boolean disabled;
	private Set<RequestMessageEnvelope> disabledForMessageEnvelope;
	private Set<RequestMessage> disabledForMessage;
	private Set<Operation> disabledForOperation;

	public AbstractExtension(int initPriority, int shutdownPriority) {

		this.initPriority = initPriority;
		this.shutdownPriority = shutdownPriority;
		this.disabled = false;
		this.disabledForMessageEnvelope = new HashSet<RequestMessageEnvelope> ();
		this.disabledForMessage = new HashSet<RequestMessage> ();
		this.disabledForOperation = new HashSet<Operation> ();
	}

	public AbstractExtension() {

		this(DEFAULT_INIT_PRIORITY, DEFAULT_SHUTDOWN_PRIORITY);
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(CONTAINER container) throws Exception {

	}

	@Override
	public void shutdown(CONTAINER container) throws Exception {

	}

	@Override
	public int getInitPriority() {

		return this.initPriority;
	}

	@Override
	public int getShutdownPriority() {

		return this.shutdownPriority;
	}

	/*
	 * Enabled?
	 */

	@Override
	public boolean skip(ExecutionContext executionContext) {

		if (this.disabled) return true;

		if (executionContext != null) {

			boolean disabledForMessageEnvelope = false;
			boolean disabledForMessage = false;
			boolean disabledForOperation = false;

			for (RequestMessageEnvelope currentMessageEnvelope : executionContext.getCurrentMessageEnvelopes()) disabledForMessageEnvelope |= this.disabledForMessageEnvelope.contains(currentMessageEnvelope);
			for (RequestMessage currentMessage : executionContext.getCurrentMessages()) disabledForMessage |= this.disabledForMessage.contains(currentMessage);
			for (Operation currentOperation : executionContext.getCurrentOperations()) disabledForOperation |= this.disabledForOperation.contains(currentOperation);

			if (log.isDebugEnabled() && disabledForMessageEnvelope) log.debug("Disabled " + this + " on message envelope ? " + disabledForMessageEnvelope);
			if (log.isDebugEnabled() && disabledForMessage) log.debug("Disabled " + this + " on message ? " + disabledForMessage);
			if (log.isDebugEnabled() && disabledForOperation) log.debug("Disabled " + this + " on operation ? " + disabledForOperation);

			if (disabledForMessageEnvelope) return true;
			if (disabledForMessage) return true;
			if (disabledForOperation) return true;
		}

		return false;
	}

	@Override
	public void setDisabled() {

		this.disabled = true;
	}

	@Override
	public void clearDisabled() {

		this.disabled = false;
	}

	@Override
	public void setDisabledForMessageEnvelope(RequestMessageEnvelope messageEnvelope) {

		if (log.isDebugEnabled()) log.debug("Set disabled " + this + " on message envelope " + messageEnvelope);

		this.disabledForMessageEnvelope.add(messageEnvelope);
	}

	@Override
	public void clearDisabledForMessageEnvelope(RequestMessageEnvelope messageEnvelope) {

		if (! this.disabledForMessageEnvelope.contains(messageEnvelope)) return;
		
		if (log.isDebugEnabled()) log.debug("Clear disabled " + this + " on message envelope " + messageEnvelope);

		this.disabledForMessageEnvelope.remove(messageEnvelope);
	}

	@Override
	public void setDisabledForMessage(RequestMessage message) {

		if (log.isDebugEnabled()) log.debug("Set disabled " + this + " on message " + message);

		this.disabledForMessage.add(message);
	}

	@Override
	public void clearDisabledForMessage(RequestMessage message) {

		if (! this.disabledForMessage.contains(message)) return;

		if (log.isDebugEnabled()) log.debug("Clear disabled " + this + " on message " + message);

		this.disabledForMessage.remove(message);
	}

	@Override
	public void setDisabledForOperation(Operation operation) {

		if (log.isDebugEnabled()) log.debug("Set disabled " + this + " on operation " + operation);

		this.disabledForOperation.add(operation);
	}

	@Override
	public void clearDisabledForOperation(Operation operation) {

		if (! this.disabledForOperation.contains(operation)) return;

		if (log.isDebugEnabled()) log.debug("Clear disabled " + this + " on operation " + operation);

		this.disabledForOperation.remove(operation);
	}
}
