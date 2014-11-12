package xdi2.core.features.nodetypes;

import xdi2.core.syntax.XDIAddress;

public interface XdiRoot extends XdiContext<XdiRoot> {

	/*
	 * Gettings roots under this root
	 */

	/**
	 * Returns an XDI peer root under this XDI root.
	 * @param address The address whose XDI peer root to find.
	 * @param create Whether the XDI peer root should be created, if it does not exist.
	 * @return The XDI peer root.
	 */
	public XdiPeerRoot getPeerRoot(XDIAddress XDIaddress, boolean create);

	/**
	 * Returns an XDI inner root under this XDI root.
	 * @param subject The subject address whose XDI inner root to find.
	 * @param predicate The predicate address whose XDI inner root to find.
	 * @param create Whether the XDI inner root should be created, if it does not exist.
	 * @return The XDI inner root.
	 */
	public XdiInnerRoot getInnerRoot(XDIAddress subject, XDIAddress predicate, boolean create);

	/**
	 * Returns an XDI root under this XDI root.
	 * @param address An address under this XDI root.
	 * @param create Whether the XDI root should be created, if it does not exist.
	 * @return The XDI root.
	 */
	public XdiRoot getRoot(XDIAddress XDIaddress, boolean create);
}
