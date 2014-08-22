package xdi2.webtools.util;

import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.impl.BasicLiteral;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenCensoringCopyStrategy extends CopyStrategy {

	@Override
	public Literal replaceLiteral(Literal literal) {

		if (literal.getContextNode().getAddress().toString().contains(XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN.toString())) {

			return new BasicLiteral("********");
		} else {

			return literal;
		}
	};
}
