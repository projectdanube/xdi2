package xdi2.core.features.policy.condition;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;

/**
 * An XDI $false condition, represented as an address.
 * 
 * @author markus
 */
public class FalseCondition extends Condition {

	private static final long serialVersionUID = 7489607090310647438L;

	protected FalseCondition(XDIAddress XDIaddress) {

		super(XDIaddress);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an address is a valid XDI $false condition.
	 * @param XDIaddress The address to check.
	 * @return True if the address is a valid XDI $false condition.
	 */
	public static boolean isValid(XDIAddress XDIaddress) {

		if (! XDIConstants.XDI_ADD_FALSE.equals(XDIaddress)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $false condition bound to a given address.
	 * @param XDIaddress The address that is an XDI $false condition.
	 * @return The XDI $false condition.
	 */
	public static FalseCondition fromAddress(XDIAddress XDIaddress) {

		if (! isValid(XDIaddress)) return null;

		return new FalseCondition(XDIaddress);
	}

	/**
	 * Factory method that creates an XDI $false condition.
	 * @return The XDI $false condition.
	 */
	public static FalseCondition create() {

		return new FalseCondition(XDIConstants.XDI_ADD_FALSE);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		return false;
	}
}
