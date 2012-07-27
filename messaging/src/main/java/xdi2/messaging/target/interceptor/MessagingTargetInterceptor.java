package xdi2.messaging.target.interceptor;

import xdi2.messaging.target.MessagingTarget;

/**
 * Interceptor that is executed when a messaging target is initialized and shut down.
 * 
 * @author markus
 */
public interface MessagingTargetInterceptor extends Interceptor {

	/**
	 * This method gets called when the messaging target is initialized.
	 */
	public void init(MessagingTarget messagingTarget) throws Exception;

	/**
	 * This method gets called when the messaging target is shut down.
	 */
	public void shutdown(MessagingTarget messagingTarget) throws Exception;
}
