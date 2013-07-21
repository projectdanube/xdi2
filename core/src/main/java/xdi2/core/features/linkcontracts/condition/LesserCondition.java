package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $lesser condition, represented as a statement.
 * 
 * @author markus
 */
public class LesserCondition extends Condition {

	private static final long serialVersionUID = -8479583948559334331L;

	protected LesserCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $lesser condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $lesser condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XRI_S_LESSER.equals(statement.getRelationArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $lesser condition bound to a given statement.
	 * @param statement The statement that is an XDI $lesser condition.
	 * @return The XDI $lesser condition.
	 */
	public static LesserCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new LesserCondition(statement);
	}

	public static LesserCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDIPolicyConstants.XRI_S_LESSER, object));
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

			String subjectLiteralData = subject.getLiteral().getLiteralData();
			String objectLiteralData = object.getLiteral().getLiteralData();

			return Boolean.valueOf(Integer.parseInt(subjectLiteralData) > Integer.parseInt(objectLiteralData));
		}

		return Boolean.FALSE;
	}
}
