package xdi2.core.features.linkcontracts.condition;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.locator.ContextNodeLocator;
import xdi2.core.xri3.impl.XDI3Segment;

/**
 * An XDI $lesser condition, represented as a statement.
 * 
 * @author markus
 */
public class LesserCondition extends Condition {

	private static final long serialVersionUID = -8479583948559334331L;

	protected LesserCondition(Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $lesser condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $lesser condition.
	 */
	public static boolean isValid(Statement statement) {

		if (! (statement instanceof RelationStatement)) return false;

		Relation relation = ((RelationStatement) statement).getRelation();

		if (! XDILinkContractConstants.XRI_S_LESSER.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $lesser condition bound to a given statement.
	 * @param statement The statement that is an XDI $lesser condition.
	 * @return The XDI $lesser condition.
	 */
	public static LesserCondition fromStatement(Statement statement) {

		if (! isValid(statement)) return null;

		return new LesserCondition(statement);
	}

	public static LesserCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_LESSER, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		ContextNode subject = contextNodeLocator.locateContextNode(this.getStatement().getSubject());
		ContextNode object = contextNodeLocator.locateContextNode(this.getStatement().getObject());

		if (subject == null || object == null) return false;

		if (subject.containsLiteral()) {

			if (! object.containsLiteral()) return false;

			String subjectLiteralData = subject.getLiteral().getLiteralData();
			String objectLiteralData = object.getLiteral().getLiteralData();

			return Integer.parseInt(subjectLiteralData) < Integer.parseInt(objectLiteralData);
		}

		return false;
	}
}
