package xdi2.core.features.linkcontracts.policystatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Statement;

public abstract class ConditionPolicyStatement extends PolicyStatement {

	private static final long serialVersionUID = 8705917224635642985L;

	private static final Logger log = LoggerFactory.getLogger(PolicyStatement.class);

	protected ConditionPolicyStatement(Relation relation) {

		super(relation);
	}

	/**
	 * Returns the XDI condition of the XDI policy statement.
	 * @return The XDI condition of the XDI policy statement.
	 */
	public Condition getCondition() {

		XDI3Statement statement;

		try {

			statement = StatementUtil.fromXriSegment(this.getRelation().getTargetContextNodeXri());
		} catch (Xdi2ParseException ex) {

			log.warn("No condition for policy statement: " + this.getRelation() + ": " + ex.getMessage(), ex);
			return null;
		}

		return Condition.fromStatement(statement);
	}
}
