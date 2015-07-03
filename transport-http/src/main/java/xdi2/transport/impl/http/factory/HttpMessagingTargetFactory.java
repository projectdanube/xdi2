package xdi2.transport.impl.http.factory;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.factory.MessagingTargetFactory;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;

/**
 * A MessagingTargetFactory can dynamically create MessagingTargets to process incoming XDI messages..
 * 
 * @author Markus
 */
public interface HttpMessagingTargetFactory extends MessagingTargetFactory {

	/*
	 * Basic methods for mounting and updating
	 */

	/**
	 * Mount a new MessagingTarget.
	 */
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2TransportException, Xdi2MessagingException;
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired) throws Xdi2TransportException, Xdi2MessagingException;

	/**
	 * Updates a MessagingTarget.
	 */
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException;
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, boolean checkDisabled, boolean checkExpired, MessagingTarget messagingTarget) throws Xdi2TransportException, Xdi2MessagingException;

	/*
	 * Maintenance methods
	 */

	/**
	 * Returns the request path at which this MessagingTargetFactory is able to
	 * mount a MessagingTarget with a given owner peer root XRI. Not all MessagingTargetFactorys
	 * may support this.
	 */
	public String getRequestPath(String messagingTargetFactoryPath, XDIArc ownerPeerRootXDIArc);
}
