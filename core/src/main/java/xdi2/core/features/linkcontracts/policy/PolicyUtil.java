package xdi2.core.features.linkcontracts.policy;

import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.condition.GenericCondition;
import xdi2.core.features.linkcontracts.condition.IsCondition;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.linkcontracts.operator.TrueOperator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * Various utility methods for working with XDI policies.
 * 
 * @author markus
 */
public final class PolicyUtil {

	private PolicyUtil() { }

	/*
	 * Helper methods to create various XDI policy statements.
	 */

	public static Operator createSenderIsOperator(Policy policy, XDIAddress sender) {

		Condition condition = IsCondition.fromSubjectAndObject(
				XDIAddress.create("{$from}"), 
				sender);

		return TrueOperator.createTrueOperator(policy, condition);
	}

	public static Operator createSecretTokenValidOperator(Policy policy) {

		Condition condition = GenericCondition.fromStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("{$msg}" + XDIAuthenticationConstants.XDI_ADD_SECRET_TOKEN_VALID + "&"), 
				Boolean.TRUE));

		return TrueOperator.createTrueOperator(policy, condition);
	}

	public static Operator createSignatureValidOperator(Policy policy) {

		Condition condition = GenericCondition.fromStatement(XDIStatement.fromLiteralComponents(
				XDIAddress.create("{$msg}" + XDIAuthenticationConstants.XDI_ADD_SIGNATURE_VALID + "&"), 
				Boolean.TRUE));

		return TrueOperator.createTrueOperator(policy, condition);
	}
}
