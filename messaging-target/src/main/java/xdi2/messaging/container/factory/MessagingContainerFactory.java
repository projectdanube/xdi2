package xdi2.messaging.container.factory;

import java.util.Iterator;

import xdi2.core.syntax.XDIArc;

/**
 * A MessagingContainerFactory can dynamically create MessagingContainers to process incoming XDI messages..
 * 
 * @author Markus
 */
public interface MessagingContainerFactory {

	/*
	 * Init and shutdown
	 */

	/**
	 * This method gets called when the MessagingContainerFactory is initialized.
	 */
	public void init() throws Exception;

	/**
	 * This method gets called when the MessagingContainerFactory is no longer needed.
	 */
	public void shutdown() throws Exception;

	/*
	 * Maintenance methods
	 */

	/**
	 * Returns a list of all owner peer roots of the MessagingContainers this
	 * MessagingContainerFactory can create. Not all MessagingContainerFactorys may
	 * support this.
	 */
	public Iterator<XDIArc> getOwnerPeerRootXDIArcs();
}
