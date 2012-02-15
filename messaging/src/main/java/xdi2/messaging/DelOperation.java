package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * A $del XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class DelOperation extends Operation {

	private static final long serialVersionUID = 5732150427365911411L;

	protected DelOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $del operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI operation.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDIMessagingConstants.XRI_SS_DEL.equals(relation.getArcXri())) return false;
		if (! XDIMessagingConstants.XRI_SS_DO.equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI $del operation.
	 * @return The XDI $del operation.
	 */
	public static DelOperation fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new DelOperation(null, relation);
	}
}
