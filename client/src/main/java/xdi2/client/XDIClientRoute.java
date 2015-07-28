package xdi2.client;

import xdi2.core.syntax.XDIArc;

public interface XDIClientRoute <CLIENT extends XDIClient> {

	public XDIArc getToPeerRootXDIArc();
	public CLIENT constructXDIClient();
}
