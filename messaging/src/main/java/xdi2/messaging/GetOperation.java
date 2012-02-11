package xdi2.messaging;

import xdi2.Relation;
import xdi2.util.XDIConstants;

/**
 * A $get XDI operation, represented as a relation.
 * 
 * @author markus
 */
public class GetOperation extends Operation {

	private static final long serialVersionUID = -1452297650590584104L;

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

		if (! XDIConstants.XRI_SS_GET.equals(relation.getArcXri())) return false;
		if (! XDIConstants.XRI_SS_DO.equals(relation.getContextNode().getArcXri())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI operation bound to a given relation.
	 * @param relation The relation that is an XDI $get operation.
	 * @return The XDI $get operation.
	 */
	public static GetOperation fromRelation(Relation relation) {

		if (! isValid(relation)) return null;

		return new GetOperation(null, relation);
	}
}
