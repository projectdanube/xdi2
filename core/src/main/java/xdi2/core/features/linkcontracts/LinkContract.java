package xdi2.core.features.linkcontracts;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI link contract, represented as a context node.
 * 
 * @author markus
 */
public final class LinkContract implements Serializable, Comparable<LinkContract> {

	private static final long serialVersionUID = 1604380462449272148L;

	private XdiEntity xdiEntity;

	protected LinkContract(XdiEntity xdiEntity) {

		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		return xdiEntity.getBaseArcXri().equals(XDILinkContractConstants.XRI_SS_DO);
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new LinkContract(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity to which this XDI link contract is bound.
	 * @return An XDI entity that represents the XDI link contract.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	/**
	 * Returns the underlying context node to which this XDI link contract is bound.
	 * @return A context node that represents the XDI link contract.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntity().getContextNode();
	}

	/**
	 * Returns an existing XDI root policy in this XDI link contract, or creates a new one.
	 * @param create Whether to create an XDI root policy if it does not exist.
	 * @return The existing or newly created XDI root policy.
	 */
	public PolicyRoot getPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getXdiEntity().getXdiEntitySingleton(XDIPolicyConstants.XRI_SS_IF, create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Adds a permission (one of $get, $add, $mod, $del, $copy, $move, $all) from this XDI link contract to a target context node XRI.
	 * @param permissionXri The permission XRI.
	 * @param targetContextNodeXri The target context node XRI of the permission.
	 */
	public void addPermission(XDI3Segment permissionXri, XDI3Segment targetContextNodeXri) {

		if (permissionXri == null || targetContextNodeXri == null) throw new NullPointerException();

		// if the same permission arc exists for the same target context node, then a new arc should not be added

		if (this.getContextNode().containsRelation(permissionXri, targetContextNodeXri)) return;

		// if an arc to the given target context node exists with $all, then no other permission arc should be created

		if (this.getContextNode().containsRelation(XDILinkContractConstants.XRI_S_ALL, targetContextNodeXri)) return;

		// if a $all permission is added to the target node then all other permission arcs should be deleted

		if (permissionXri.equals(XDILinkContractConstants.XRI_S_ALL)) {

			this.getContextNode().deleteRelation(XDILinkContractConstants.XRI_S_GET, targetContextNodeXri);
			this.getContextNode().deleteRelation(XDILinkContractConstants.XRI_S_ADD, targetContextNodeXri);
			this.getContextNode().deleteRelation(XDILinkContractConstants.XRI_S_MOD, targetContextNodeXri);
			this.getContextNode().deleteRelation(XDILinkContractConstants.XRI_S_DEL, targetContextNodeXri);
		}

		// create the permission arc

		this.getContextNode().createRelation(permissionXri, targetContextNodeXri);
	}

	public void removePermission(XDI3Segment permissionXri, XDI3Segment targetContextNodeXri) {

		if (permissionXri == null || targetContextNodeXri == null) throw new NullPointerException();

		// delete the permission arc

		this.getContextNode().deleteRelation(permissionXri, targetContextNodeXri);
	}

	public Iterator<ContextNode> getNodesWithPermission(XDI3Segment permissionXri) {

		return new MappingRelationTargetContextNodeIterator(this.getContextNode().getRelations(permissionXri));
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

		if (object == null || !(object instanceof LinkContract)) return false;
		if (object == this) return true;

		LinkContract other = (LinkContract) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(LinkContract other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
