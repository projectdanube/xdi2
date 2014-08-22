package xdi2.core.features.linkcontracts;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.Statement;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.constants.XDIPolicyConstants;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeStatementXriIterator;
import xdi2.core.features.nodetypes.XdiSubGraph;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.AddressUtil;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeAddressIterator;
import xdi2.core.util.iterators.MappingStatementIterator;
import xdi2.core.util.iterators.SelectingNotImpliedStatementIterator;

/**
 * The base class for XDI link contracts and XDI link contract templates, represented as an XDI entity or variable.
 * 
 * @author markus
 */
public abstract class LinkContractBase implements Serializable, Comparable<LinkContractBase> {

	private static final long serialVersionUID = 1604380462449272148L;

	private XdiSubGraph<?> xdiSubGraph;

	protected LinkContractBase(XdiEntity xdiEntity) {

		this.xdiSubGraph = xdiEntity;
	}

	protected LinkContractBase(XdiVariable xdiVariable) {

		this.xdiSubGraph = xdiVariable;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI subgraph to which this XDI link contract (template) (template) is bound.
	 * @return An XDI subgraph that represents the XDI link contract (template).
	 */
	public XdiSubGraph<?> getXdiSubGraph() {

		return this.xdiSubGraph;
	}

	/**
	 * Returns the underlying XDI entity to which this XDI link contract (template) (template) is bound.
	 * @return An XDI entity that represents the XDI link contract (template).
	 */
	public XdiEntity getXdiEntity() {

		return (XdiEntity) this.xdiSubGraph;
	}

	/**
	 * Returns the underlying XDI variable to which this XDI link contract (template) (template) is bound.
	 * @return An XDI entity that represents the XDI link contract (template).
	 */
	public XdiVariable getXdiVariable() {

		return (XdiVariable) this.xdiSubGraph;
	}

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

		XdiEntitySingleton xdiEntitySingleton = this.getXdiEntity().getXdiEntitySingleton(XdiEntitySingleton.createarc(XDIPolicyConstants.XDI_ARC_IF), create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Adds a permission (one of $get, $set, $del, $copy, $move, $all) from this XDI link contract (template) to a target context node XRI.
	 * @param permissionXri The permission XRI.
	 * @param targetAddress The target context node XRI of the permission.
	 */
	public void setPermissionTargetAddress(XDIAddress permissionXri, XDIAddress targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// if an arc to the given target context node exists with $all, then no other permission arc should be created

		if (this.getContextNode().containsRelation(XDILinkContractConstants.XDI_ADD_ALL, targetAddress)) return;

		// if a $all permission is added to the target node then all other permission arcs should be deleted

		if (permissionXri.equals(XDILinkContractConstants.XDI_ADD_ALL)) {

			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_GET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET_DO, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_SET_REF, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XDI_ADD_DEL, targetAddress);
		}

		// set the permission arc

		this.getContextNode().setRelation(permissionXri, targetAddress);
	}

	public void setNegativePermissionTargetAddress(XDIAddress permissionXri, XDIAddress targetAddress) {

		this.setPermissionTargetAddress(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri), targetAddress);
	}

	public void setPermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		if (permissionXri == null || targetStatementXri == null) throw new NullPointerException();

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, true);
		if (xdiInnerRoot == null) return;

		// set the permission statement

		xdiInnerRoot.getContextNode().setStatement(targetStatementXri);
	}

	public void setNegativePermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		this.setPermissionTargetStatement(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri), targetStatementXri);
	}

	public void delPermissionTargetAddress(XDIAddress permissionXri, XDIAddress targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// delete the permission arc

		this.getContextNode().delRelation(permissionXri, targetAddress);
	}

	public void delNegativePermissionTargetAddress(XDIAddress permissionXri, XDIAddress targetAddress) {

		this.delPermissionTargetAddress(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri), targetAddress);
	}

	public void delPermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		if (permissionXri == null || targetStatementXri == null) throw new NullPointerException();

		// delete the permission statement

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return;

		Statement statement = xdiInnerRoot.getContextNode().getStatement(targetStatementXri);
		if (statement == null) return;

		statement.delete();
	}

	public void delNegativePermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		this.delPermissionTargetStatement(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri), targetStatementXri);
	}

	public IterableIterator<XDIAddress> getPermissionTargetAddresses(XDIAddress permissionXri) {

		if (permissionXri == null) throw new NullPointerException();

		// return the target addresses

		return new MappingRelationTargetContextNodeAddressIterator(
						this.getContextNode().getRelations(permissionXri));
	}

	public IterableIterator<XDIAddress> getNegativePermissionTargetAddresses(XDIAddress permissionXri) {

		return this.getPermissionTargetAddresses(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri));
	}

	public IterableIterator<XDIStatement> getPermissionTargetStatements(XDIAddress permissionXri) {

		if (permissionXri == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return new EmptyIterator<XDIStatement> ();

		// return the target statements

		return new MappingAbsoluteToRelativeStatementXriIterator(
				xdiInnerRoot,
				new MappingStatementIterator(
						new SelectingNotImpliedStatementIterator(
								xdiInnerRoot.getContextNode().getAllStatements())));
	}

	public boolean hasPermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		if (permissionXri == null || targetStatementXri == null) throw new NullPointerException();

		// find the inner root

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return false;

		// check if the target statement exists

		return xdiInnerRoot.getContextNode().containsStatement(targetStatementXri);
	}

	public boolean hasNegativePermissionTargetStatement(XDIAddress permissionXri, XDIStatement targetStatementXri) {

		return this.hasPermissionTargetStatement(AddressUtil.concatAddresses(XDILinkContractConstants.XDI_ADD_NOT, permissionXri), targetStatementXri);
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
