package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.XDI3Util;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $mod XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class ModOperation extends Operation {

	private static final long serialVersionUID = -5386430243720744523L;

	protected ModOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $mod operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $mod operation.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDI3Util.startsWith(relation.getArcXri(), XDIMessagingConstants.XRI_S_MOD)) return false;
		if (! XdiEntitySingleton.createArcXri(XDIMessagingConstants.XRI_SS_DO).equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $mod operation bound to a given relation.
	 * @param relation The relation that is an XDI $mod operation.
	 * @return The XDI $mod operation.
	 */
	public static ModOperation fromMessageAndRelation(Message message, Relation relation) {

		if (! isValid(relation)) return null;

		return new ModOperation(message, relation);
	}
}
