package xdi2.core.features;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiContext;

// TODO: use this as a super class of LinkContractBase, Message, etc.,
// and use encapsulate() instead of fromContextNode(..)
public abstract class EncapsulatesContextNode<CONTEXT extends XdiContext<?>> {

	private CONTEXT xdiContext;

	public CONTEXT getXdiContext() {

		return this.xdiContext;
	}

	public ContextNode getContextNode() {

		return this.xdiContext.getContextNode();
	}

	@SuppressWarnings("unchecked")
	public static <E extends EncapsulatesContextNode<CONTEXT>, CONTEXT extends XdiContext<? extends CONTEXT>> E encapsulate(ContextNode contextNode, Class<E> clazz, Class<CONTEXT> clazz2) {

		E encapsulatesContextNode;

		try {

			Method encapsulateMethod = clazz.getMethod("encapsulate", ContextNode.class);
			if (encapsulateMethod == null) return null;

			encapsulatesContextNode = (E) encapsulateMethod.invoke(null, contextNode);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {

			encapsulatesContextNode = null;
		}

		return encapsulatesContextNode;
	}
}
