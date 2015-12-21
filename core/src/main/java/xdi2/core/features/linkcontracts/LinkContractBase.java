package xdi2.core.features.linkcontracts;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;
import xdi2.core.util.iterators.MappingXDIStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

/**
 * The base class for XDI link contracts and XDI link contract templates, represented as an XDI entity or variable.
 * 
 * @author markus
 */
public abstract class LinkContractBase <N extends XdiSubGraph<? super N>> implements Serializable, Comparable<LinkContractBase<N>> {

	private static final long serialVersionUID = 1604380462449272148L;

	/*
	 * Instance methods
	 */

	public abstract N getXdiSubGraph();

	/**
	 * Returns the underlying context node to which this XDI link contract (template) is bound.
	 * @return A context node that represents the XDI link contract (template).
	 */
	public ContextNode getContextNode() {

		return this.getXdiSubGraph().getContextNode();
	}

	/**
	 * Returns an existing XDI policy root in this XDI link contract (template), or creates a new one.
	 * @param create Whether to create an XDI policy root if it does not exist.
	 * @return The existing or newly created XDI policy root.
	 */
	public PolicyRoot getPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getXdiSubGraph().getXdiEntitySingleton(XDIPolicyConstants.XDI_ARC_IF, create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Returns an existing XDI defer policy root in this XDI link contract (template), or creates a new one.
	 * @param create Whether to create an XDI defer policy root if it does not exist.
	 * @return The existing or newly created XDI defer policy root.
	 */
	public PolicyRoot getDeferPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getXdiSubGraph().getXdiEntitySingleton(XDIPolicyConstants.XDI_ADD_DEFER, create);
		if (xdiEntitySingleton == null) return null;
		xdiEntitySingleton = xdiEntitySingleton.getXdiEntitySingleton(XDIPolicyConstants.XDI_ARC_IF, create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Returns an existing XDI defer push policy root in this XDI link contract (template), or creates a new one.
	 * @param create Whether to create an XDI defer push policy root if it does not exist.
	 * @return The existing or newly created XDI defer push policy root.
	 */
	public PolicyRoot getDeferPushPolicyRoot(boolean create) {

		XdiEntitySingleton xdiEntitySingleton = this.getXdiSubGraph().getXdiEntitySingleton(XDIPolicyConstants.XDI_ADD_DEFER_PUSH, create);
		if (xdiEntitySingleton == null) return null;
		xdiEntitySingleton = xdiEntitySingleton.getXdiEntitySingleton(XDIPolicyConstants.XDI_ARC_IF, create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Adds a permission (one of $get, $set, $del, $all) from this XDI link contract (template) to a target context node address.
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
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_DO, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_CONNECT, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SEND, targetXDIAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_PUSH, targetXDIAddress);
		}

		// set the permission arc

		this.getContextNode().setRelation(permissionXDIAddress, targetXDIAddress);
	}

	public void setNegativePermissionTargetXDIAddress(XDIAddress permissionXDIAddress, XDIAddress targetXDIAddress) {

		this.setPermissionTargetXDIAddress(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress), targetXDIAddress);
	}

	public void setPermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		if (permissionXDIAddress == null || targetXDIStatement == null) throw new NullPointerException();

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionXDIAddress, true);
		if (xdiInnerRoot == null) return;

		// set the permission statement

		xdiInnerRoot.getContextNode().setStatement(targetXDIStatement);
	}

	public void setNegativePermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		this.setPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress), targetXDIStatement);
	}

	public void delPermissionTargetXDIAddress(XDIAddress permissionXDIAddress, XDIAddress targetXDIAddress) {

		if (permissionXDIAddress == null || targetXDIAddress == null) throw new NullPointerException();

		// delete the permission arc

		this.getContextNode().delRelation(permissionXDIAddress, targetXDIAddress);
	}

	public void delNegativePermissionTargetXDIAddress(XDIAddress permissionXDIAddress, XDIAddress targetXDIAddress) {

		this.delPermissionTargetXDIAddress(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress), targetXDIAddress);
	}

	public void delPermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		if (permissionXDIAddress == null || targetXDIStatement == null) throw new NullPointerException();

		// delete the permission statement

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionXDIAddress, false);
		if (xdiInnerRoot == null) return;

		Statement statement = xdiInnerRoot.getContextNode().getStatement(targetXDIStatement);
		if (statement == null) return;

		statement.delete();
	}

	public void delNegativePermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		this.delPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress), targetXDIStatement);
	}

	public IterableIterator<XDIAddress> getPermissionTargetXDIAddresses(XDIAddress permissionXDIAddress) {

		if (permissionXDIAddress == null) throw new NullPointerException();

		// return the target addresses

		return new MappingRelationTargetXDIAddressIterator(
				this.getContextNode().getRelations(permissionXDIAddress));
	}

	public IterableIterator<XDIAddress> getNegativePermissionTargetXDIAddresses(XDIAddress permissionXDIAddress) {

		return this.getPermissionTargetXDIAddresses(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress));
	}

	public Map<XDIAddress, List<XDIAddress>> getAllPermissionTargetXDIAddresses() {

		Map<XDIAddress, List<XDIAddress>> result = new HashMap<XDIAddress, List<XDIAddress>> ();

		// return all target addresses

		for (XDIAddress permissionXDIAddress : XDILinkContractConstants.XDI_ADD_PERMISSIONS) {

			IterableIterator<XDIAddress> permissionTargetXDIAddresses = this.getPermissionTargetXDIAddresses(permissionXDIAddress);

			if (permissionTargetXDIAddresses.hasNext()) {

				List<XDIAddress> list = new IteratorListMaker<XDIAddress> (permissionTargetXDIAddresses).list();
				result.put(permissionXDIAddress, list);
			}
		}

		return result;
	}

	public Map<XDIAddress, List<XDIAddress>> getAllNegativePermissionTargetXDIAddresses() {

		Map<XDIAddress, List<XDIAddress>> result = new HashMap<XDIAddress, List<XDIAddress>> ();

		// return all target addresses

		for (XDIAddress permissionXDIAddress : XDILinkContractConstants.XDI_ADD_PERMISSIONS) {

			IterableIterator<XDIAddress> permissionTargetXDIAddresses = this.getNegativePermissionTargetXDIAddresses(permissionXDIAddress);

			if (permissionTargetXDIAddresses.hasNext()) {

				List<XDIAddress> list = new IteratorListMaker<XDIAddress> (permissionTargetXDIAddresses).list();
				result.put(permissionXDIAddress, list);
			}
		}

		return result;
	}

	public IterableIterator<XDIStatement> getPermissionTargetXDIStatements(XDIAddress permissionXDIAddress) {

		if (permissionXDIAddress == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionXDIAddress, false);
		if (xdiInnerRoot == null) return new EmptyIterator<XDIStatement> ();

		// return the target statements

		return new MappingAbsoluteToRelativeXDIStatementIterator(
				xdiInnerRoot,
				new MappingXDIStatementIterator(
						new SelectingNotImpliedStatementIterator(
								xdiInnerRoot.getContextNode().getAllStatements())));
	}

	public IterableIterator<XDIStatement> getNegativePermissionTargetXDIStatements(XDIAddress permissionXDIAddress) {

		return this.getPermissionTargetXDIStatements(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress));
	}

	public Map<XDIAddress, List<XDIStatement>> getAllPermissionTargetXDIStatements() {

		Map<XDIAddress, List<XDIStatement>> result = new HashMap<XDIAddress, List<XDIStatement>> ();

		// return all target statements

		for (XDIAddress permissionXDIAddress : XDILinkContractConstants.XDI_ADD_PERMISSIONS) {

			IterableIterator<XDIStatement> permissionTargetXDIStatements = this.getPermissionTargetXDIStatements(permissionXDIAddress);

			if (permissionTargetXDIStatements.hasNext()) {

				List<XDIStatement> list = new IteratorListMaker<XDIStatement> (permissionTargetXDIStatements).list();
				result.put(permissionXDIAddress, list);
			}
		}

		return result;
	}

	public Map<XDIAddress, List<XDIStatement>> getAllNegativePermissionTargetXDIStatements() {

		Map<XDIAddress, List<XDIStatement>> result = new HashMap<XDIAddress, List<XDIStatement>> ();

		// return all target statements

		for (XDIAddress permissionXDIAddress : XDILinkContractConstants.XDI_ADD_PERMISSIONS) {

			IterableIterator<XDIStatement> permissionTargetXDIStatements = this.getNegativePermissionTargetXDIStatements(permissionXDIAddress);

			if (permissionTargetXDIStatements.hasNext()) {

				List<XDIStatement> list = new IteratorListMaker<XDIStatement> (permissionTargetXDIStatements).list();
				result.put(permissionXDIAddress, list);
			}
		}

		return result;
	}

	public boolean hasPermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		if (permissionXDIAddress == null || targetXDIStatement == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiSubGraph().getXdiInnerRoot(permissionXDIAddress, false);
		if (xdiInnerRoot == null) return false;

		// check if the target statement exists

		return xdiInnerRoot.getContextNode().containsStatement(targetXDIStatement);
	}

	public boolean hasNegativePermissionTargetXDIStatement(XDIAddress permissionXDIAddress, XDIStatement targetXDIStatement) {

		return this.hasPermissionTargetXDIStatement(XDIAddressUtil.concatXDIAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXDIAddress), targetXDIStatement);
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

		LinkContractBase<?> other = (LinkContractBase<?>) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(LinkContractBase<N> other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
