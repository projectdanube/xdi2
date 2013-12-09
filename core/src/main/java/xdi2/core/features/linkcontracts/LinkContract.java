package xdi2.core.features.linkcontracts;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeXriIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

/**
 * An XDI link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public abstract class LinkContract implements Serializable, Comparable<LinkContract> {

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

		if (xdiEntity == null) return false;

		return
				RootLinkContract.isValid(xdiEntity) ||
				PublicLinkContract.isValid(xdiEntity) ||
				GenericLinkContract.isValid(xdiEntity) ||
				LinkContractTemplate.isValid(xdiEntity) ||
				MetaLinkContract.isValid(xdiEntity);
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromXdiEntity(XdiEntity xdiEntity) {

		LinkContract linkContract = null;

		if ((linkContract = RootLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = PublicLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = GenericLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = LinkContractTemplate.fromXdiEntity(xdiEntity)) != null) return linkContract;
		if ((linkContract = MetaLinkContract.fromXdiEntity(xdiEntity)) != null) return linkContract;

		return null;
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
	 * Adds a permission (one of $get, $set, $del, $copy, $move, $all) from this XDI link contract to a target context node XRI.
	 * @param permissionXri The permission XRI.
	 * @param targetAddress The target context node XRI of the permission.
	 */
	public void setPermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// if an arc to the given target context node exists with $all, then no other permission arc should be created

		if (this.getContextNode().containsRelation(XDILinkContractConstants.XRI_S_ALL, targetAddress)) return;

		// if a $all permission is added to the target node then all other permission arcs should be deleted

		if (permissionXri.equals(XDILinkContractConstants.XRI_S_ALL)) {

			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_GET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_SET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_DEL, targetAddress);
		}

		// set the permission arc

		this.getContextNode().setRelation(permissionXri, targetAddress);
	}

	public void setNegativePermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		this.setPermissionTargetAddress(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetAddress);
	}

	public void setPermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		if (permissionXri == null || targetStatement == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, true);
		if (xdiInnerRoot == null) return;

		// set the permission statement

		xdiInnerRoot.setRelativeStatement(targetStatement);
	}

	public void setNegativePermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		this.setPermissionTargetStatement(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetStatement);
	}

	public void delPermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// delete the permission arc

		this.getContextNode().delRelation(permissionXri, targetAddress);
	}

	public void delNegativePermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		this.delPermissionTargetAddress(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetAddress);
	}

	public void delPermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		if (permissionXri == null || targetStatement == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return;

		// delete the permission statement

		Statement statement = xdiInnerRoot.getRelativeStatement(targetStatement);
		if (statement == null) return;

		statement.delete();
	}

	public void delNegativePermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		this.delPermissionTargetStatement(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetStatement);
	}

	public Iterator<XDI3Segment> getPermissionTargetAddresses(XDI3Segment permissionXri) {

		if (permissionXri == null) throw new NullPointerException();

		return new MappingRelationTargetContextNodeXriIterator(this.getContextNode().getRelations(permissionXri));
	}

	public Iterator<XDI3Segment> getNegativePermissionTargetAddresses(XDI3Segment permissionXri) {

		return this.getPermissionTargetAddresses(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri));
	}

	public boolean hasPermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		if (permissionXri == null || targetStatement == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return false;

		return xdiInnerRoot.containsRelativeStatement(targetStatement);
	}

	public boolean hasNegativePermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		return this.hasPermissionTargetStatement(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetStatement);
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
