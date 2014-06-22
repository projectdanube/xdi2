package xdi2.webtools.util;

import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.impl.BasicLiteral;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class SecretTokenInsertingCopyStrategy extends CopyStrategy {

	private String secretToken;

	public SecretTokenInsertingCopyStrategy(String secretToken) {

		this.secretToken = secretToken;
	}

	@Override
	public Literal replaceLiteral(Literal literal) {

		if (literal.getContextNode().getXri().toString().contains(XDIAuthenticationConstants.XRI_S_SECRET_TOKEN.toString()) && "********".equals(literal.getLiteralData())) {

			return new BasicLiteral(this.secretToken);
		} else {

			return literal;
		}
	}
}
