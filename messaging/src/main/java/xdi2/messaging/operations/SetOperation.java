package xdi2.messaging.operations;

import xdi2.core.Relation;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.MessageBase;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $set XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class SetOperation extends Operation {

	private static final long serialVersionUID = -9053418535565359957L;

	protected SetOperation(MessageBase<?> messageBase, Relation relation) {

		super(messageBase, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $set operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $set operation.
	 */
	public static boolean isValid(Relation relation) {

		if (XDIAddressUtil.startsWithXDIAddress(relation.getXDIAddress(), XDIMessagingConstants.XDI_ADD_SET) == null) return false;
		if (! XDIMessagingConstants.XDI_ARC_DO.equals(relation.getContextNode().getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $set operation bound to a given relation.
	 * @param relation The relation that is an XDI $set operation.
	 * @return The XDI $set operation.
	 */
	public static SetOperation fromMessageBaseAndRelation(MessageBase<?> messageBase, Relation relation) {

		if (! isValid(relation)) return null;

		return new SetOperation(messageBase, relation);
	}
}
