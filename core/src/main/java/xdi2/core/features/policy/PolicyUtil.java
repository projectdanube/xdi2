package xdi2.core.features.policy;

import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.policy.condition.Condition;
import xdi2.core.features.policy.condition.GenericCondition;
import xdi2.core.features.policy.condition.IsCondition;
import xdi2.core.features.policy.operator.Operator;
import xdi2.core.features.policy.operator.TrueOperator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

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

	public static Operator createSenderIsOperator(Policy policy, XDI3Segment sender) {

		Condition condition = IsCondition.fromSubjectAndObject(
				XDI3Segment.create("{$from}"), 
				sender);

		return TrueOperator.createTrueOperator(policy, condition);
	}

	public static Operator createSecretTokenValidOperator(Policy policy) {

		Condition condition = GenericCondition.fromStatement(XDI3Statement.fromLiteralComponents(
				XDI3Segment.create("{$msg}" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN_VALID + "&"), 
				Boolean.TRUE));

		return TrueOperator.createTrueOperator(policy, condition);
	}

	public static Operator createSignatureValidOperator(Policy policy) {

		Condition condition = GenericCondition.fromStatement(XDI3Statement.fromLiteralComponents(
				XDI3Segment.create("{$msg}" + XDIAuthenticationConstants.XRI_S_SIGNATURE_VALID + "&"), 
				Boolean.TRUE));

		return TrueOperator.createTrueOperator(policy, condition);
	}
}
