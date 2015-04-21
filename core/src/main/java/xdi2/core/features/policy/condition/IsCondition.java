package xdi2.core.features.policy.condition;

import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

/**
 * An XDI $is condition, represented as a statement.
 * 
 * @author markus
 */
public class IsCondition extends Condition {

	private static final long serialVersionUID = 7506322819724395818L;

	protected IsCondition(XDIStatement XDIstatement) {

		super(XDIstatement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $is condition.
	 * @param XDIstatement The statement to check.
	 * @return True if the statement is a valid XDI $is condition.
	 */
	public static boolean isValid(XDIStatement XDIstatement) {

		if (! XDIstatement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XDI_ADD_IS.equals(XDIstatement.getRelationXDIAddress())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $is condition bound to a given statement.
	 * @param XDIstatement The statement that is an XDI $is condition.
	 * @return The XDI $is condition.
	 */
	public static IsCondition fromStatement(XDIStatement XDIstatement) {

		if (! isValid(XDIstatement)) return null;

		return new IsCondition(XDIstatement);
	}

	public static IsCondition fromSubjectAndObject(XDIAddress subject, XDIAddress object) {

		return fromStatement(XDIStatement.fromRelationComponents(subject, XDIPolicyConstants.XDI_ADD_IS, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		// check if subject XRI and object XRI are the same

		XDIAddress subject = policyEvaluationContext.resolveXDIAddress(this.getXDIStatement().getSubject());
		XDIAddress object = policyEvaluationContext.resolveXDIAddress((XDIAddress) this.getXDIStatement().getObject());

		if (subject == null || object == null) return Boolean.FALSE;
		
		if (subject.equals(object)) return Boolean.TRUE;

		// done

		return Boolean.FALSE;
	}
}
