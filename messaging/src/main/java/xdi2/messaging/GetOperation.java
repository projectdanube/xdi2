package xdi2.messaging;

import xdi2.core.Relation;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * A $get XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class GetOperation extends Operation {

	private static final long serialVersionUID = -1452297650590584104L;

	public static final XRI3Segment XRI_EXTENSION_BANG = new XRI3Segment("!");

	protected GetOperation(Message message, Relation relation) {

		super(message, relation);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an relation is a valid XDI $get operation.
	 * @param relation The relation to check.
	 * @return True if the relation is a valid XDI operation.
	 */
	public static boolean isValid(Relation relation) {

		if (! XRIUtil.startsWith(relation.getArcXri(), XDIMessagingConstants.XRI_S_GET)) return false;
		if (! XDIMessagingConstants.XRI_S_DO.equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI $get operation.
	 * @return The XDI $get operation.
	 */
	public static GetOperation fromMessageAndRelation(Message message, Relation relation) {

		if (! isValid(relation)) return null;

		//		if (GetExpOperation.isValid(relation)) return GetExpOperation.fromMessageAndRelation(message, relation);
		//		if (GetCmpOperation.isValid(relation)) return GetCmpOperation.fromMessageAndRelation(message, relation);

		return new GetOperation(message, relation);
	}

	/*
	 * Specific $get XDI operations
	 */

	/*	public static class GetExpOperation extends GetOperation {

		private static final long serialVersionUID = 5258097506386334653L;

		protected GetExpOperation(Message message, Relation relation) {

			super(message, relation);
		}

		public static boolean isValid(Relation relation) {

			if (! XRIUtil.startsWith(relation.getArcXri(), XDIMessagingConstants.XRI_S_GET)) return false;
			if (! XDIMessagingConstants.XRI_S_DO.equals(relation.getContextNode().getArcXri())) return false;

			return true;
		}

		public static GetExpOperation fromMessageAndRelation(Message message, Relation relation) {

			if (! isValid(relation)) return null;

			return new GetExpOperation(message, relation);
		}
	}*/
}
