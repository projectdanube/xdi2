package xdi2.server.factory;

import java.util.Iterator;

import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpMessagingTargetRegistry;

/**
 * A MessagingTargetFactory can dynamically create MessagingTargets to process incoming XDI messages..
 * 
 * @author Markus
 */
public interface MessagingTargetFactory {

	/*
	 * Init and shutdown
	 */

	/**
	 * This method gets called when the MessagingTargetFactory is initialized.
	 */
	public void init() throws Exception;

	/**
	 * This method gets called when the MessagingTargetFactory is no longer needed.
	 */
	public void shutdown() throws Exception;

	/*
	 * Basic methods for mounting and updating
	 */

	/**
	 * Mount a new MessagingTarget.
	 */
	public MessagingTarget mountMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath) throws Xdi2ServerException, Xdi2MessagingException;

	/**
	 * Updates a MessagingTarget.
	 */
	public MessagingTarget updateMessagingTarget(HttpMessagingTargetRegistry httpMessagingTargetRegistry, String messagingTargetFactoryPath, String requestPath, MessagingTarget messagingTarget) throws Xdi2ServerException, Xdi2MessagingException;

	/*
	 * Advanced methods
	 */

	/**
	 * Returns a list of all owner peer root XRIs of the MessagingTargets this
	 * MessagingTargetFactory can create. Not all MessagingTargetFactorys may
	 * support this.
	 */
	public Iterator<XDI3SubSegment> getOwnerPeerRootXris();

	/**
	 * Returns the request path at which this MessagingTargetFactory is able to
	 * mount a MessagingTarget with a given owner peer root XRI. Not all MessagingTargetFactorys
	 * may support this.
	 */
	public String getRequestPath(String messagingTargetFactoryPath, XDI3SubSegment ownerPeerRootXri);
}
