package xdi2.webtools.util;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.impl.BasicLiteralNode;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenCensoringCopyStrategy extends CopyStrategy {

	@Override
	public LiteralNode replaceLiteralNode(LiteralNode literal) {

		if (literal.getContextNode().getXDIAddress().toString().contains(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN.toString())) {

			return new BasicLiteralNode("********");
		} else {

			return literal;
		}
	};
}
