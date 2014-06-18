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
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.nodetypes.XdiRoot.MappingAbsoluteToRelativeXriIterator;
import xdi2.core.features.nodetypes.XdiSubGraph;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.IterableIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeXriIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

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

		XdiEntitySingleton xdiEntitySingleton = this.getXdiEntity().getXdiEntitySingleton(XdiEntitySingleton.createArcXri(XDIPolicyConstants.XRI_SS_IF), create);
		if (xdiEntitySingleton == null) return null;

		return PolicyRoot.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Adds a permission (one of $get, $set, $del, $copy, $move, $all) from this XDI link contract (template) to a target context node XRI.
	 * @param permissionXri The permission XRI.
	 * @param targetAddress The target context node XRI of the permission.
	 */
	public void setPermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// prepare the target address

		XdiRoot xdiRoot = this.getXdiSubGraph().findRoot();

		targetAddress = xdiRoot.relativeToAbsoluteXri(targetAddress);

		// if an arc to the given target context node exists with $all, then no other permission arc should be created

		if (this.getContextNode().containsRelation(XDILinkContractConstants.XRI_S_ALL, targetAddress)) return;

		// if a $all permission is added to the target node then all other permission arcs should be deleted

		if (permissionXri.equals(XDILinkContractConstants.XRI_S_ALL)) {

			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_GET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_SET, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_SET_DO, targetAddress);
			this.getContextNode().delRelation(XDILinkContractConstants.XRI_S_SET_REF, targetAddress);
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

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, true);
		if (xdiInnerRoot == null) return;

		targetStatement = xdiInnerRoot.relativeToAbsoluteStatementXri(targetStatement);

		// set the permission statement

		this.getContextNode().getGraph().setStatement(targetStatement);
	}

	public void setNegativePermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		this.setPermissionTargetStatement(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetStatement);
	}

	public void delPermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		if (permissionXri == null || targetAddress == null) throw new NullPointerException();

		// prepare the target address

		XdiRoot xdiRoot = this.getXdiSubGraph().findRoot();

		targetAddress = xdiRoot.relativeToAbsoluteXri(targetAddress);

		// delete the permission arc

		this.getContextNode().delRelation(permissionXri, targetAddress);
	}

	public void delNegativePermissionTargetAddress(XDI3Segment permissionXri, XDI3Segment targetAddress) {

		this.delPermissionTargetAddress(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetAddress);
	}

	public void delPermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		if (permissionXri == null || targetStatement == null) throw new NullPointerException();

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return;

		targetStatement = xdiInnerRoot.relativeToAbsoluteStatementXri(targetStatement);

		// delete the permission statement

		Statement statement = this.getContextNode().getGraph().getStatement(targetStatement);
		if (statement == null) return;

		statement.delete();
	}

	public void delNegativePermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		this.delPermissionTargetStatement(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri), targetStatement);
	}

	public IterableIterator<XDI3Segment> getPermissionTargetAddresses(XDI3Segment permissionXri) {

		if (permissionXri == null) throw new NullPointerException();

		// prepare the target address

		XdiRoot xdiRoot = this.getXdiSubGraph().findRoot();

		// return the target addresses

		return new MappingAbsoluteToRelativeXriIterator(
				xdiRoot,
				new MappingRelationTargetContextNodeXriIterator(
						this.getContextNode().getRelations(permissionXri)));
	}

	public IterableIterator<XDI3Segment> getNegativePermissionTargetAddresses(XDI3Segment permissionXri) {

		return this.getPermissionTargetAddresses(XDI3Util.concatXris(XDILinkContractConstants.XRI_S_NOT, permissionXri));
	}

	public boolean hasPermissionTargetStatement(XDI3Segment permissionXri, XDI3Statement targetStatement) {

		if (permissionXri == null || targetStatement == null) throw new NullPointerException();

		// prepare the target statement

		XdiInnerRoot xdiInnerRoot = this.getXdiEntity().getXdiInnerRoot(permissionXri, false);
		if (xdiInnerRoot == null) return false;

		targetStatement = xdiInnerRoot.relativeToAbsoluteStatementXri(targetStatement);

		// check if the target statement exists

		return this.getContextNode().getGraph().containsStatement(targetStatement);
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
