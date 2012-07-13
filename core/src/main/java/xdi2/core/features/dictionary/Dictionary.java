package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.MappingRelationRelationXriIterator;
import xdi2.core.xri3.impl.XRI3Segment;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for types.
	 */

	public static Iterator<XRI3Segment> getContextNodeTypes(ContextNode contextNode) {

		return new MappingRelationRelationXriIterator(contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS));
	}

	public static XRI3Segment getContextNodeType(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS).getRelationXri();
	}

	public static boolean isContextNodeType(ContextNode contextNode, XRI3Segment type) {

		return contextNode.containsRelation(XDIDictionaryConstants.XRI_S_IS, type);
	}

	public static void addContextNodeType(ContextNode contextNode, XRI3Segment type) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, type);
	}

	public static void removeContextNodeType(ContextNode contextNode, XRI3Segment type) {

		contextNode.deleteRelation(XDIDictionaryConstants.XRI_S_IS, type);
	}

	public static void removeContextNodeTypes(ContextNode contextNode) {

		contextNode.deleteRelations(XDIDictionaryConstants.XRI_S_IS);
	}

	public static void setContextNodeType(ContextNode contextNode, XRI3Segment type) {

		removeContextNodeTypes(contextNode);
		addContextNodeType(contextNode, type);
	}
}
