package xdi2.messaging.target.impl;

import xdi2.messaging.target.Extension;


public class AbstractExtension <CONTAINER> implements Extension<CONTAINER> {

	public final static int DEFAULT_INIT_PRIORITY = 10;
	public final static int DEFAULT_SHUTDOWN_PRIORITY = 10;

	private int initPriority;
	private int shutdownPriority;
	private boolean enabled;
	private boolean disabledForOperation;
	private boolean disabledForMessage;
	private boolean disabledForMessageEnvelope;

	public AbstractExtension(int initPriority, int shutdownPriority) {

		this.initPriority = initPriority;
		this.shutdownPriority = shutdownPriority;
		this.enabled = true;
		this.disabledForOperation = false;
		this.disabledForMessage = false;
		this.disabledForMessageEnvelope = false;
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
	public boolean skip() {

		if (! this.enabled) return true;
		if (this.disabledForOperation) return true;
		if (this.disabledForMessage) return true;
		if (this.disabledForMessageEnvelope) return true;

		return false;
	}

	@Override
	public boolean getEnabled() {

		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	@Override
	public boolean getDisabledForOperation() {

		return this.disabledForOperation;
	}

	@Override
	public void setDisabledForOperation(boolean disabledForOperation) {

		this.disabledForOperation = disabledForOperation;
	}

	@Override
	public boolean getDisabledForMessage() {

		return this.disabledForMessage;
	}

	@Override
	public void setDisabledForMessage(boolean disabledForMessage) {

		this.disabledForMessage = disabledForMessage;
	}

	@Override
	public boolean getDisabledForMessageEnvelope() {

		return this.disabledForMessageEnvelope;
	}

	@Override
	public void setDisabledForMessageEnvelope(boolean disabledForMessageEnvelope) {

		this.disabledForMessageEnvelope = disabledForMessageEnvelope;
	}
}
