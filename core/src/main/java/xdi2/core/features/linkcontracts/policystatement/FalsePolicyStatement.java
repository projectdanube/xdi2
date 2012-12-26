package xdi2.core.features.linkcontracts.policystatement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.linkcontracts.condition.Condition;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.locator.ContextNodeLocator;

/**
 * An XDI $false policy statement, represented as a relation.
 * 
 * @author markus
 */
public class FalsePolicyStatement extends PolicyStatement {

	private static final long serialVersionUID = -7397004800836677763L;

	private static final Logger log = LoggerFactory.getLogger(FalsePolicyStatement.class);

	protected FalsePolicyStatement(Relation relation) {

		super(relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a relation is a valid XDI $false policy statement.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $false policy statement.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIConstants.XRI_S_FALSE.equals(relation.getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $false policy statement bound to a given relation.
	 * @param relation The relation that is an XDI $false policy statement.
	 * @return The XDI $false policy statement.
	 */
	public static FalsePolicyStatement fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new FalsePolicyStatement(relation);
	}

	public static FalsePolicyStatement fromCondition(Condition condition) {

		return fromRelation(GraphUtil.relationFromComponents(XDIConstants.XRI_S_ROOT, XDIConstants.XRI_S_FALSE, condition.getStatement().toXriSegment()));
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(ContextNodeLocator contextNodeLocator) {

		log.debug("Evaluating " + this.getClass().getSimpleName() + ": " + this.getRelation());

		return false == this.getCondition().evaluate(contextNodeLocator);
	}
}
