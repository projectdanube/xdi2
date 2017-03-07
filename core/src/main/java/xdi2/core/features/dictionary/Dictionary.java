package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingContextNodeXDIAddressIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for dictionary identifiers
	 */

	public static XDIArc instanceXDIArcToDictionaryXDIArc(XDIArc instanceXDIArc) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + instanceXDIArc + ")");
	}

	public static XDIArc dictionaryXDIArcToInstanceXDIArc(XDIArc dictionaryXDIArc) {

		if (! XDIConstants.CS_CLASS_UNRESERVED.equals(dictionaryXDIArc.getCs())) return null;
		if (! dictionaryXDIArc.hasXRef()) return null;

		return XDIArc.create(dictionaryXDIArc.getXRef().getValue());
	}

	public static XDIArc nativeIdentifierToInstanceXDIArc(String nativeIdentifier) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + nativeIdentifier + ")");
	}

	public static String instanceXDIArcToNativeIdentifier(XDIArc instanceXDIArc) {

		if (! instanceXDIArc.hasXRef()) return null;

		return instanceXDIArc.getXRef().getValue();
	}

	/*
	 * Methods for types of context nodes.
	 */

	public static Iterator<XDIAddress> getContextNodeTypes(ContextNode contextNode) {

		Iterator<Relation> contextNodeTypeRelations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE);

		return new MappingContextNodeXDIAddressIterator(new MappingRelationTargetContextNodeIterator(contextNodeTypeRelations));
	}

	public static XDIAddress getContextNodeType(ContextNode contextNode) {

		Relation contextNodeTypeRelation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE);
		if (contextNodeTypeRelation == null) return null;

		return contextNodeTypeRelation.getTargetXDIAddress();
	}

	public static boolean isContextNodeType(ContextNode contextNode, XDIAddress type) {

		return contextNode.containsRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, type);
	}

	public static void setContextNodeType(ContextNode contextNode, XDIAddress type) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, type);
	}

	public static void delContextNodeType(ContextNode contextNode, XDIAddress type) {

		contextNode.delRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE, type);
	}

	public static void delContextNodeTypes(ContextNode contextNode) {

		contextNode.delRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE);
	}

	public static void replaceContextNodeType(ContextNode contextNode, XDIAddress type) {

		delContextNodeTypes(contextNode);
		setContextNodeType(contextNode, type);
	}
}
