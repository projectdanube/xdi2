package xdi2.webtools.util;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.impl.BasicLiteralNode;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenInsertingCopyStrategy extends CopyStrategy {

	private String secretToken;

	public SecretTokenInsertingCopyStrategy(String secretToken) {

		this.secretToken = secretToken;
	}

	@Override
	public LiteralNode replaceLiteralNode(LiteralNode literal) {

		if (literal.getContextNode().getXDIAddress().toString().contains(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN.toString()) && "********".equals(literal.getLiteralData())) {

			return new BasicLiteralNode(this.secretToken);
		} else {

			return literal;
		}
	}
}
