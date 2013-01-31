package xdi2.core.features.linkcontracts.policy;

import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.features.linkcontracts.condition.EqualsCondition;
import xdi2.core.features.linkcontracts.condition.IsCondition;
import xdi2.core.features.linkcontracts.policystatement.PolicyStatement;
import xdi2.core.features.linkcontracts.policystatement.TruePolicyStatement;
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

	public static PolicyStatement senderMatchesPolicyStatement(XDI3Segment sender) {

		Condition condition = IsCondition.fromSubjectAndObject(XDI3Segment.create("($from)"), sender);

		return TruePolicyStatement.fromCondition(condition);
	}

	public static PolicyStatement secretTokenMatchesPolicyStatement() {

		Condition condition = EqualsCondition.fromSubjectAndObject(XDI3Segment.create("($msg)$secret$!($token)"), XDI3Segment.create("$secret$!($token)"));

		return TruePolicyStatement.fromCondition(condition);
	}
}
