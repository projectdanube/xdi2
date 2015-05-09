package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $push XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class PushOperation extends Operation {

	private static final long serialVersionUID = 4953230035946917353L;

	protected PushOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $push operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $push operation.
	 */
	public static boolean isValid(Relation relation) {

		if (XDIAddressUtil.startsWithXDIAddress(relation.getXDIAddress(), XDIMessagingConstants.XDI_ADD_GET) == null) return false;
		if (! XdiEntitySingleton.createXDIArc(XDIMessagingConstants.XDI_ARC_DO).equals(relation.getContextNode().getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $push operation bound to a given relation.
	 * @param relation The relation that is an XDI $push operation.
	 * @return The XDI $push operation.
	 */
	public static PushOperation fromMessageAndRelation(Message message, Relation relation) {

		if (! isValid(relation)) return null;

		return new PushOperation(message, relation);
	}
}
