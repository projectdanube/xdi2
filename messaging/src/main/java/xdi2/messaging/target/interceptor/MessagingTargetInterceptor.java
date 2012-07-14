package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;

public interface MessagingTargetInterceptor extends Interceptor {

	/**
	 * This method gets called when the messaging target is initialized.
	 */
	public void init(MessagingTarget messagingTarget) throws Exception;

	/**
	 * This method gets called when the messaging target is no longer needed.
	 */
	public void shutdown(MessagingTarget messagingTarget) throws Exception;
}
