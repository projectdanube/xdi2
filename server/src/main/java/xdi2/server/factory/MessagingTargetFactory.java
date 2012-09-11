package xdi2.server.factory;

import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
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
	public void mountMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException;

	/**
	 * Updates a messaging target.
	 */
	public void updateMessagingTarget(EndpointRegistry endpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException;
}
