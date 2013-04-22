package xdi2.server.factory;

import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpEndpointRegistry;

/**
 * A MessagingTargetFactory can dynamically create MessagingTargets to process incoming XDI messages..
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
	public void mountMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException;

	/**
	 * Updates a messaging target.
	 */
	public void updateMessagingTarget(HttpEndpointRegistry httpEndpointRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException, Xdi2MessagingException;
}
