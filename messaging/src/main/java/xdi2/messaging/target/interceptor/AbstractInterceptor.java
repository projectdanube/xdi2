package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractInterceptor implements Interceptor {

	private boolean enabled;

	public AbstractInterceptor() {

		this.enabled = true;
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
