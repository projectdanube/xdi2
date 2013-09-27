package xdi2.core.features.linkcontracts.condition;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $is condition, represented as a statement.
 * 
 * @author markus
 */
public class IsCondition extends Condition {

	private static final long serialVersionUID = 7506322819724395818L;

	protected IsCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $is condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $is condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XRI_S_IS.equals(statement.getRelationArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $is condition bound to a given statement.
	 * @param statement The statement that is an XDI $is condition.
	 * @return The XDI $is condition.
	 */
	public static IsCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new IsCondition(statement);
	}

	public static IsCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(XDI3Statement.fromRelationComponents(subject, XDIPolicyConstants.XRI_S_IS, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		// check if subject XRI and object XRI are the same

		XDI3Segment subject = policyEvaluationContext.getContextNodeXri(this.getStatementXri().getSubject());
		XDI3Segment object = policyEvaluationContext.getContextNodeXri((XDI3Segment) this.getStatementXri().getObject());

		if (subject == null || object == null) return Boolean.FALSE;
		
		if (subject.equals(object)) return Boolean.TRUE;

		// done

		return Boolean.FALSE;
	}
}
