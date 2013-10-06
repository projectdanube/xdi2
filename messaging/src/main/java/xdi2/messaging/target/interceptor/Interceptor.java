package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;

public interface Interceptor {

	/*
	 * Init and shutdown
	 */

	public void init(MessagingTarget messagingTarget) throws Exception;
	public void shutdown(MessagingTarget messagingTarget) throws Exception;

	/*
	 * Enabled?
	 */

	public boolean isEnabled();
	public void setEnabled(boolean enabled);
}
