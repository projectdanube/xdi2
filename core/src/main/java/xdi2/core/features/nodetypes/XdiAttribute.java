package xdi2.core.features.nodetypes;

public interface XdiAttribute extends XdiSubGraph<XdiAttribute> {

	/**
	 * Returns the XDI value of this XDI attribute.
	 * @param create Whether to create the value if it does not exist.
	 * @return The XDI value.
	 */
	public XdiValue getXdiValue(boolean create);
}
