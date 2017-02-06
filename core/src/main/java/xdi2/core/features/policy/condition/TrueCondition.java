package xdi2.core.features.policy.condition;

import xdi2.core.constants.XDIConstants;
import xdi2.core.features.policy.evaluation.PolicyEvaluationContext;
import xdi2.core.syntax.XDIAddress;

/**
 * An XDI $true condition, represented as an address.
 * 
 * @author markus
 */
public class TrueCondition extends Condition {

	private static final long serialVersionUID = 3516171361647977310L;

	protected TrueCondition(XDIAddress XDIaddress) {

		super(XDIaddress);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an address is a valid XDI $true condition.
	 * @param XDIaddress The address to check.
	 * @return True if the address is a valid XDI $true condition.
	 */
	public static boolean isValid(XDIAddress XDIaddress) {

		if (! XDIConstants.XDI_ADD_TRUE.equals(XDIaddress)) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $true condition bound to a given address.
	 * @param XDIaddress The address that is an XDI $true condition.
	 * @return The XDI $true condition.
	 */
	public static TrueCondition fromAddress(XDIAddress XDIaddress) {

		if (! isValid(XDIaddress)) return null;

		return new TrueCondition(XDIaddress);
	}

	/**
	 * Factory method that creates an XDI $true condition.
	 * @return The XDI $true condition.
	 */
	public static TrueCondition create() {

		return new TrueCondition(XDIConstants.XDI_ADD_TRUE);
	}

	/*
	 * Instance methods
	 */

	@Override
	public boolean evaluateInternal(PolicyEvaluationContext policyEvaluationContext) {

		return true;
	}
}
