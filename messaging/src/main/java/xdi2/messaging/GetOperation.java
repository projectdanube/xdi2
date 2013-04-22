package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $get XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class GetOperation extends Operation {

	private static final long serialVersionUID = -1452297650590584104L;

	public static final XDI3SubSegment XRI_PARAMETER_DEREF = XDI3SubSegment.create("$deref");
	public static final XDI3SubSegment XRI_PARAMETER_PROXY = XDI3SubSegment.create("$proxy");

	protected GetOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $get operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI $get operation.
	 */
	public static boolean isValid(Relation relation) {

		if (! XDI3Util.startsWith(relation.getArcXri(), XDIMessagingConstants.XRI_S_GET)) return false;
		if (! XdiEntitySingleton.createArcXri(XDIMessagingConstants.XRI_SS_DO).equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI $get operation bound to a given relation.
	 * @param relation The relation that is an XDI $get operation.
	 * @return The XDI $get operation.
	 */
	public static GetOperation fromMessageAndRelation(Message message, Relation relation) {

		if (! isValid(relation)) return null;

		return new GetOperation(message, relation);
	}
}
