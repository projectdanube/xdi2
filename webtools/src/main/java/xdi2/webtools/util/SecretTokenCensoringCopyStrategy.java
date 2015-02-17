package xdi2.webtools.util;

import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.impl.DummyLiteral;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenCensoringCopyStrategy extends CopyStrategy {

	@Override
	public Literal replaceLiteral(Literal literal) {

		if (literal.getContextNode().getXDIAddress().toString().contains(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN.toString())) {

			return new DummyLiteral("********");
		} else {

			return literal;
		}
	}
}
