package xdi2.messaging.target;

public class AbstractExtension implements Extension {

	public final static int DEFAULT_INIT_PRIORITY = 10;
	public final static int DEFAULT_SHUTDOWN_PRIORITY = 10;

	private int initPriority;
	private int shutdownPriority;
	private boolean enabled;

	public AbstractExtension(int initPriority, int shutdownPriority) {

		this.initPriority = initPriority;
		this.shutdownPriority = shutdownPriority;
		this.enabled = true;
	}

	public AbstractExtension() {

		this(DEFAULT_INIT_PRIORITY, DEFAULT_SHUTDOWN_PRIORITY);
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

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
	public boolean isEnabled() {

		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}
}
