package xdi2.webtools.util;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.impl.DummyLiteralNode;
import xdi2.core.util.CopyUtil.AbstractCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenCensoringCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

	@Override
	public LiteralNode replaceLiteralNode(LiteralNode literal) {

		if (literal.getContextNode().getXDIAddress().toString().contains(XDISecurityConstants.XDI_ADD_SECRET_TOKEN.toString())) {

			return new DummyLiteralNode("********");
		} else {

			return null;
		}
	}
}
