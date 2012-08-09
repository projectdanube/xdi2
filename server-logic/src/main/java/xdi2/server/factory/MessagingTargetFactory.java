package xdi2.server.factory;

import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.server.registry.EndpointRegistry;

/**
 * A MessagingTargetFactory can instantiate MessagingTargets for given request paths.
 * 
 * @author Markus
 */
public interface MessagingTargetFactory {

	/**
	 * This method gets called when the MessagingTargetFactory is initialized.
	 */
	public void init() throws Exception;

	/**
	 * This method gets called when the MessagingTargetFactory is no longer needed.
	 */
	public void shutdown() throws Exception;

	/**
	 * Mount a new MessagingTarget.
	 */
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2MessagingException;
}
