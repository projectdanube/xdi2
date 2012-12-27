package xdi2.core.features.linkcontracts.condition;

import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.locator.ContextNodeLocator;
import xdi2.core.xri3.impl.XDI3Segment;

/**
 * An XDI $is condition, represented as a statement.
 * 
 * @author markus
 */
public class IsCondition extends Condition {

	private static final long serialVersionUID = 7506322819724395818L;

	protected IsCondition(Statement statement) {

		super(statement);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a statement is a valid XDI $is condition.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $is condition.
	 */
	public static boolean isValid(Statement statement) {

		if (! (statement instanceof RelationStatement)) return false;

		Relation relation = ((RelationStatement) statement).getRelation();

		if (! XDILinkContractConstants.XRI_S_IS.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $is condition bound to a given statement.
	 * @param statement The statement that is an XDI $is condition.
	 * @return The XDI $is condition.
	 */
	public static IsCondition fromStatement(Statement statement) {

		if (! isValid(statement)) return null;

		return new IsCondition(statement);
	}

	public static IsCondition fromSubjectAndObject(XDI3Segment subject, XDI3Segment object) {

		return fromStatement(StatementUtil.fromComponents(subject, XDILinkContractConstants.XRI_S_IS, object));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		XDI3Segment subjectXri = contextNodeLocator.getContextNodeXri(this.getStatement().getSubject());
		XDI3Segment objectXri = contextNodeLocator.getContextNodeXri(this.getStatement().getObject());

		if (subjectXri == null || objectXri == null) return false;

		return subjectXri.equals(objectXri);
	}
}
