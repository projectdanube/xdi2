package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingContextNodeXDIAddressIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for dictionary XRIs
	 */

	public static XDIArc instanceAddressToDictionaryAddress(XDIArc instanceAddress) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + instanceAddress + ")");
	}

	public static XDIArc dictionaryAddressToInstanceAddress(XDIArc dictionaryAddress) {

		if (! XDIConstants.CS_CLASS_UNRESERVED.equals(dictionaryAddress.getCs())) return null;
		if (! dictionaryAddress.hasXRef()) return null;

		return XDIArc.create(dictionaryAddress.getXRef().getValue());
	}

	public static XDIArc nativeIdentifierToInstanceAddress(String nativeIdentifier) {

		return XDIArc.create("" + XDIConstants.CS_CLASS_UNRESERVED + "(" + nativeIdentifier + ")");
	}

	public static String instanceAddressToNativeIdentifier(XDIArc instanceAddress) {

		if (! instanceAddress.hasXRef()) return null;

		return instanceAddress.getXRef().getValue();
	}

	/*
	 * Methods for types of context nodes.
	 */

	public static Iterator<XDIAddress> getContextNodeTypes(ContextNode contextNode) {

		return new MappingContextNodeXDIAddressIterator(new MappingRelationTargetContextNodeIterator(contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS_TYPE)));
	}

	public static XDIAddress getContextNodeType(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS_TYPE).getTargetContextNodeXDIAddress();
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
