package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeXDIStatementIterator;
import xdi2.core.features.nodetypes.XdiSubGraph;
import xdi2.core.features.policy.PolicyRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeXDIAddressIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

/**
 * The base class for XDI link contracts and XDI link contract templates, represented as an XDI entity or variable.
 * 
 * @author markus
 */
public abstract class LinkContractBase implements Serializable, Comparable<LinkContractBase> {

	private static final long serialVersionUID = 1604380462449272148L;

	/*
	 * Instance methods
	 */

	public abstract XdiSubGraph<?> getXdiSubGraph();

	/**
	 * Returns the underlying context node to which this XDI link contract (template) is bound.
	 * @return A context node that represents the XDI link contract (template).
	 */
	public ContextNode getContextNode() {

		return this.getXdiSubGraph().getContextNode();
	}

	/**
	 * Returns an existing XDI root policy in this XDI link contract (template), or creates a new one.
	 * @param create Whether to create an XDI root policy if it does not exist.
	 * @return The existing or newly created XDI root policy.
	 */
	public PolicyRoot getPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getXdiSubGraph().getXdiEntitySingleton(XdiEntitySingleton.createEntitySingletonXDIArc(XDIPolicyConstants.XDI_ARC_IF), create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Adds a permission (one of $get, $set, $del, $copy, $move, $all) from this XDI link contract (template) to a target context node address.
	 * @param permissionXDIAddress The permission address.
	 * @param targetXDIAddress The target context node address of the permission.
	 */
	public void setPermissionTargetXDIAddress(XDIAddress permissionXDIAddress, XDIAddress targetXDIAddress) {

		if (permissionXDIAddress == null || targetXDIAddress == null) throw new NullPointerException();

		// if an arc to the given target context node exists with $all, then no other permission arc should be created

		if (this.getContextNode().containsRelation(XDILinkContractConstants.XDI_ADD_ALL, targetXDIAddress)) return;

		// if a $all permission is added to the target node then all other permission arcs should be deleted

		if (permissionXDIAddress.equals(XDILinkContractConstants.XDI_ADD_ALL)) {

			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_GET, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET_DO, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET_REF, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_DEL, targetXDIAddress);
		}

		// set the permission arc

		this.getContextNode().setRelation(permissionXDIAddress, targetXDIAddress);
	}

	public void setNegativePermissionTargetXDIAddress(XDIAddress permissionAddress, XDIAddress targetAddress) {

		this.setPermissionTargetXDIAddress(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress), targetAddress);
	}

	public void setPermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		if (permissionAddress == null || targetStatementAddress == null) throw new NullPointerException();

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionAddress, true);
		if (xdiInnerRoot == null) return;

		// set the permission statement

		xdiInnerRoot.getContextNode().setStatement(targetStatementAddress);
	}

	public void setNegativePermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		this.setPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress), targetStatementAddress);
	}

	public void delPermissionTargetXDIAddress(XDIAddress permissionAddress, XDIAddress targetAddress) {

		if (permissionAddress == null || targetAddress == null) throw new NullPointerException();

		// delete the permission arc

		this.getContextNode().delRelation(permissionAddress, targetAddress);
	}

	public void delNegativePermissionTargetXDIAddress(XDIAddress permissionAddress, XDIAddress targetAddress) {

		this.delPermissionTargetXDIAddress(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress), targetAddress);
	}

	public void delPermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		if (permissionAddress == null || targetStatementAddress == null) throw new NullPointerException();

		// delete the permission statement

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionAddress, false);
		if (xdiInnerRoot == null) return;

		Statement statement = xdiInnerRoot.getContextNode().getStatement(targetStatementAddress);
		if (statement == null) return;

		statement.delete();
	}

	public void delNegativePermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		this.delPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress), targetStatementAddress);
	}

	public IterableIterator<XDIAddress> getPermissionTargetXDIAddresses(XDIAddress permissionAddress) {

		if (permissionAddress == null) throw new NullPointerException();

		// return the target addresses

		return new MappingRelationTargetContextNodeXDIAddressIterator(
						this.getContextNode().getRelations(permissionAddress));
	}

	public IterableIterator<XDIAddress> getNegativePermissionTargetXDIAddresses(XDIAddress permissionAddress) {

		return this.getPermissionTargetXDIAddresses(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress));
	}

	public IterableIterator<XDIStatement> getPermissionTargetXDIStatements(XDIAddress permissionAddress) {

		if (permissionAddress == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionAddress, false);
		if (xdiInnerRoot == null) return new EmptyIterator<XDIStatement> ();

		// return the target statements

		return new MappingAbsoluteToRelativeXDIStatementIterator(
				xdiInnerRoot,
				new MappingXDIStatementIterator(
						new SelectingNotImpliedStatementIterator(
								xdiInnerRoot.getContextNode().getAllStatements())));
	}

	public boolean hasPermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		if (permissionAddress == null || targetStatementAddress == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionAddress, false);
		if (xdiInnerRoot == null) return false;

		// check if the target statement exists

		return xdiInnerRoot.getContextNode().containsStatement(targetStatementAddress);
	}

	public boolean hasNegativePermissionTargetXDIStatement(XDIAddress permissionAddress, XDIStatement targetStatementAddress) {

		return this.hasPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionAddress), targetStatementAddress);
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

		if (object == null || !(object instanceof LinkContractBase)) return false;
		if (object == this) return true;

		LinkContractBase other = (LinkContractBase) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(LinkContractBase other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
