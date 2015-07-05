package xdi2.messaging.target.factory;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;

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
	 * Maintenance methods
	 */

	/**
	 * Returns a list of all owner peer root XRIs of the MessagingTargets this
	 * MessagingTargetFactory can create. Not all MessagingTargetFactorys may
	 * support this.
	 */
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs();
}
