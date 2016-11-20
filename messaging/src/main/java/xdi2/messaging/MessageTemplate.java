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
	 * Checks if an XDI entity is a valid XDI message.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI message.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		return xdiEntity.getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, false) != null;
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
