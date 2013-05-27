package xdi2.core.features.linkcontracts.policy;

import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.condition.GenericCondition;
import xdi2.core.features.linkcontracts.condition.IsCondition;
import xdi2.core.features.linkcontracts.operator.Operator;
import xdi2.core.features.linkcontracts.operator.TrueOperator;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;

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

		Condition condition = GenericCondition.fromStatement(StatementUtil.fromLiteralComponents(
				XDI3Segment.create("{$msg}" + XDIAuthenticationConstants.XRI_S_SECRET_TOKEN_VALID), 
				"true"));

		return TrueOperator.createTrueOperator(policy, condition);
	}
}
