package xdi2.core.features.push;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

/**
 * An XDI push command, represented as an XDI entity.
 * 
 * @author markus
 */
public class PushCommand implements Serializable {

	private static final long serialVersionUID = -797143187462571586L;

	public static final XDIArc XDI_ARC_PUSH = XDIArc.create("$push");
	public static final XDIAddress XDI_ADD_PUSH = XDIAddress.fromComponent(XDI_ARC_PUSH);

	public static final XDIArc XDI_ARC_AS_PUSH = XdiAttributeSingleton.createXDIArc(XDI_ARC_PUSH);
	public static final XDIAddress XDI_ADD_AS_PUSH = XDIAddress.fromComponent(XDI_ARC_AS_PUSH);

	public static final XDIArc XDI_ARC_AC_PUSH = XdiAttributeCollection.createXDIArc(XDI_ARC_PUSH);
	public static final XDIAddress XDI_ADD_AC_PUSH = XDIAddress.fromComponent(XDI_ARC_AC_PUSH);

	public static final XDIAddress XDI_ADD_TARGET = XDIAddress.create("$is()");

	private XdiEntity xdiEntity;

	protected PushCommand(XdiEntity xdiEntity) {

		if (xdiEntity == null) throw new NullPointerException();

		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI push command.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI push command.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton)
			return ((XdiEntitySingleton) xdiEntity).getXDIArc().equals(XDI_ARC_AS_PUSH);
		else if (xdiEntity instanceof XdiEntityInstanceUnordered)
			return ((XdiEntityInstanceUnordered) xdiEntity).getXdiCollection().getXDIArc().equals(XDI_ARC_AC_PUSH);
		else if (xdiEntity instanceof XdiEntityInstanceOrdered)
			return ((XdiEntityInstanceOrdered) xdiEntity).getXdiCollection().getXDIArc().equals(XDI_ARC_AC_PUSH);

		return false;
	}

	/**
	 * Factory method that creates an XDI push command bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI push command.
	 * @return The XDI push command.
	 */
	public static PushCommand fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new PushCommand(xdiEntity);
	}

	/**
	 * Factory method that finds or creates an XDI push command for a context.
	 * @return The XDI push command.
	 */
	public static PushCommand findPushCommand(XdiContext<?> xdiContext, boolean create) {

		XdiEntity xdiEntity = xdiContext.getXdiEntitySingleton(XDI_ARC_AS_PUSH, create);
		if (xdiEntity == null) return null;

		return new PushCommand(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity to which this XDI push command is bound.
	 * @return An XDI entity that represents the XDI push command.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	/**
	 * Returns the underlying context node to which this XDI push command is bound.
	 * @return A context node that represents the XDI push command.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntity().getContextNode();
	}

	public ReadOnlyIterator<XDIAddress> getTargetXDIAddresses() {

		return new ReadOnlyIterator<XDIAddress> (
				new MappingRelationTargetXDIAddressIterator(
						this.getContextNode().getRelations(XDI_ADD_TARGET)));

	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof XdiContext)) return false;
		if (object == this) return true;

		PushCommand other = (PushCommand) object;

		// two push commands are equal if their context nodes are equal

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}
}
