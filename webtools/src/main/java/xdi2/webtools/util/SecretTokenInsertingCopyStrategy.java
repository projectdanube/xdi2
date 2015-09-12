package xdi2.webtools.util;

import xdi2.core.LiteralNode;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.impl.DummyLiteralNode;
import xdi2.core.util.CopyUtil.AbstractCopyStrategy;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenInsertingCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

	private String secretToken;

	public SecretTokenInsertingCopyStrategy(String secretToken) {

		this.secretToken = secretToken;
	}

	@Override
	public LiteralNode replaceLiteralNode(LiteralNode literal) {

		if (literal.getContextNode().getXDIAddress().toString().contains(XDISecurityConstants.XDI_ADD_SECRET_TOKEN.toString()) && "********".equals(literal.getLiteralData())) {

			return new DummyLiteralNode(this.secretToken);
		} else {

			return literal;
		}
	}
}
