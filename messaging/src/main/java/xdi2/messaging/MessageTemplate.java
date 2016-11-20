package xdi2.messaging;

import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message, represented as a context node.
 * 
 * @author markus
 */
public final class MessageTemplate extends MessageBase<XdiEntity> {

	private static final long serialVersionUID = -908172536340407558L;

	private XdiEntitySingleton.Variable xdiEntitySingletonVariable;

	protected MessageTemplate(XdiEntitySingleton.Variable xdiEntitySingletonVariable) {

		if (xdiEntitySingletonVariable == null) throw new NullPointerException();

		this.xdiEntitySingletonVariable = xdiEntitySingletonVariable;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI message template.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI message template.
	 */
	public static boolean isValid(XdiEntitySingleton.Variable xdiVariable) {

		if (! xdiVariable.getXDIArc().equals(XDIMessagingConstants.XDI_ADD_V_MSG)) return false;

		return true;
	}

	public static boolean isValid(XdiEntity xdiEntity) {

		if (! (xdiEntity instanceof XdiEntitySingleton.Variable)) return false;

		return isValid((XdiEntitySingleton.Variable) xdiEntity); 
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param xdiEntitySingletonVariable The XDI entity singleton variable that is an XDI message template.
	 * @return The XDI message template.
	 */
	public static MessageTemplate fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable xdiEntitySingletonVariable) {

		if (! isValid(xdiEntitySingletonVariable)) return null;

		return new MessageTemplate(xdiEntitySingletonVariable);
	}

	public static MessageTemplate fromXdiEntity(XdiEntity xdiEntity) {

		if (! (xdiEntity instanceof XdiEntitySingleton.Variable)) return null;

		return fromXdiEntitySingletonVariable((XdiEntitySingleton.Variable) xdiEntity); 
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity singleton variable to which this XDI message is bound.
	 * @return An XDI entity singleton variable that represents the XDI message.
	 */
	public XdiEntitySingleton.Variable getXdiEntitySingletonVariable() {

		return this.xdiEntitySingletonVariable;
	}

	@Override
	public XdiEntitySingleton.Variable getXdiSubGraph() {

		return this.xdiEntitySingletonVariable;
	}

	public XdiInnerRoot getXdiInnerRoot() {

		XdiRoot xdiRoot = this.getXdiEntitySingletonVariable().findRoot();
		if (! (xdiRoot instanceof XdiInnerRoot)) return null;

		return (XdiInnerRoot) xdiRoot;
	}
}
