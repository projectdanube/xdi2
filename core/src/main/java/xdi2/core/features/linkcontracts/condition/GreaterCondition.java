package xdi2.core.features.linkcontracts.condition;

import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $greater condition, represented as a statement.
 * 
 * @author markus
 */
public class GreaterCondition extends Condition {

	private static final long serialVersionUID = 2302071980940540935L;

	protected GreaterCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $greater condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $greater condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDILinkContractConstants.XRI_S_GREATER.equals(statement.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $greater condition bound to a given statement.
	 * @param statement The statement that is an XDI $greater condition.
	 * @return The XDI $greater condition.
	 */
	public static GreaterCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new GreaterCondition(statement);
	}

	public static GreaterCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_GREATER, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		throw new Xdi2RuntimeException("Not implemented.");
	}
}
