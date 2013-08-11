package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.impl.AbstractLiteral;
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
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $greater condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XRI_S_GREATER.equals(statement.getRelationArcXri())) return false;

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

		return fromStatement(StatementUtil.fromComponents(subject, XDIPolicyConstants.XRI_S_GREATER, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		ContextNode subject = policyEvaluationContext.getContextNode(this.getStatement().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode((XDI3Segment) this.getStatement().getObject());

		if (subject == null || object == null) return Boolean.FALSE;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return Boolean.FALSE;

			Object subjectLiteralData = subject.getLiteral().getLiteralData();
			Object objectLiteralData = object.getLiteral().getLiteralData();

			return Boolean.valueOf(AbstractLiteral.LITERALDATACOMPARATOR.compare(subjectLiteralData, objectLiteralData) > 0);
		}

		return Boolean.FALSE;
	}
}
