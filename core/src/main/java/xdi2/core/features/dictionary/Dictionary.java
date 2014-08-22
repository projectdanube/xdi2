package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingContextNodeAddressIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for dictionary XRIs
	 */

	public static XDIArc instanceXriToDictionaryXri(XDIArc instanceXri) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + instanceXri + ")");
	}

	public static XDIArc dictionaryXriToInstanceXri(XDIArc dictionaryXri) {

		if (! XDIConstants.CS_CLASS_UNRESERVED.equals(dictionaryXri.getCs())) return null;
		if (! dictionaryXri.hasXRef()) return null;

		return XDIArc.create(dictionaryXri.getXRef().getValue());
	}

	public static XDIArc nativeIdentifierToInstanceXri(String nativeIdentifier) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + nativeIdentifier + ")");
	}

	public static String instanceXriToNativeIdentifier(XDIArc instanceXri) {

		if (! instanceXri.hasXRef()) return null;

		return instanceXri.getXRef().getValue();
	}

	/*
	 * Methods for types of context nodes.
	 */

	public static Iterator<XDIAddress> getContextNodeTypes(ContextNode contextNode) {

		return new MappingContextNodeAddressIterator(new MappingRelationTargetContextNodeIterator(contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE)));
	}

	public static XDIAddress getContextNodeType(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE).getTargetContextNodeAddress();
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
