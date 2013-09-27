package xdi2.core.features.linkcontracts.condition;

import java.util.regex.Pattern;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI $matches condition, represented as a statement.
 * 
 * @author markus
 */
public class MatchesCondition extends Condition {

	private static final long serialVersionUID = -3144452704386786096L;

	protected MatchesCondition(XDI3Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $matches condition.
	 * @param statement The statement to check.
	 * @return True if the statement is a valid XDI $matches condition.
	 */
	public static boolean isValid(XDI3Statement statement) {

		if (! statement.isRelationStatement()) return false;

		if (! XDIPolicyConstants.XRI_S_MATCHES.equals(statement.getRelationArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $matches condition bound to a given statement.
	 * @param statement The statement that is an XDI $matches condition.
	 * @return The XDI $matches condition.
	 */
	public static MatchesCondition fromStatement(XDI3Statement statement) {

		if (! isValid(statement)) return null;

		return new MatchesCondition(statement);
	}

	public static MatchesCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(XDI3Statement.fromRelationComponents(subject, XDIPolicyConstants.XRI_S_MATCHES, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public Boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		ContextNode subject = policyEvaluationContext.getContextNode(this.getStatementXri().getSubject());
		ContextNode object = policyEvaluationContext.getContextNode((XDI3Segment) this.getStatementXri().getObject());

		if (subject == null || object == null) return Boolean.FALSE;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return Boolean.FALSE;

			String subjectLiteralData = subject.getLiteral().getLiteralDataString();
			String objectLiteralData = object.getLiteral().getLiteralDataString();

			if (subjectLiteralData == null || objectLiteralData == null) return Boolean.FALSE;

			return Boolean.valueOf(Pattern.matches(objectLiteralData, subjectLiteralData));
		}

		return Boolean.FALSE;
	}
}
