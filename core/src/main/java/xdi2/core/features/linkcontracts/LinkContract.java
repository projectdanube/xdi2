package xdi2.core.features.linkcontracts;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.features.linkcontracts.util.XDILinkContractPermission;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * An XDI link contract, represented as a context node.
 * 
 * @author markus
 */
public final class LinkContract implements Serializable,
		Comparable<LinkContract> {

	private static final long serialVersionUID = 1604380462449272148L;

	private ContextNode contextNode;

	private LinkContract(ContextNode contextNode) {
		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI link contract.
	 * 
	 * @param contextNode
	 *            The context node to check.
	 * @return True if the context node is a valid XDI link contract.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XDILinkContractConstants.XRI_SS_DO.equals(contextNode
				.getArcXri());
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given context
	 * node.
	 * 
	 * @param contextNode
	 *            The context node that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromContextNode(ContextNode contextNode) {

		if (!isValid(contextNode))
			return null;

		LinkContract lc = new LinkContract(contextNode);
		//lc.addAuthenticationFunction();
		return lc;
	}

	/**
	 * Factory method that creates an XDI link contract bound to a given context
	 * node.
	 * 
	 * @param contextNode
	 *            The context node that is an XDI link contract.
	 * @return The XDI link contract.
	 */
	public static LinkContract fromContextNode(ContextNode contextNode,
			boolean withAuthFunc) {

		if (!isValid(contextNode))
			return null;

		LinkContract lc = new LinkContract(contextNode);
		if (withAuthFunc) {
			lc.addAuthenticationFunction();
		}
		return lc;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying context node to which this XDI link contract is
	 * bound.
	 * 
	 * @return A context node that represents the XDI link contract.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	// ...
	// here go methods for working with the link contract ...
	// these methods should operate on the link contract's context node
	// ...

	/**
	 * Returns an existing XDI policy in this XDI link contract, or creates a
	 * new one.
	 * 
	 * @param create
	 *            Whether to create an XDI policy if it does not exist.
	 * @return The existing or newly created XDI policy.
	 */
	public Policy getPolicy(boolean create) {

		ContextNode contextNode = this.getContextNode().getContextNode(
				XDILinkContractConstants.XRI_SS_IF);
		if (contextNode == null && create)
			contextNode = this.getContextNode().createContextNode(
					XDILinkContractConstants.XRI_SS_IF);
		if (contextNode == null)
			return null;

		return new Policy(this, contextNode);
	}

	/**
	 * Adds a assignment relationship of a LinkContract to a target assignee
	 * node
	 * 
	 * @param assignee
	 *            The context node to whom this LinkContract is being assigned
	 *            to
	 * @return NONE
	 */

	public boolean addAssignee(ContextNode assignee) {
		boolean status = false;
		if (assignee == null) {
			// TBD
			// write error in debug log
			return status;
		}

		// one cannot add this Link Contract node as assignee to itself

		if (assignee.equals(contextNode)) {
			return false;
		}

		try {
			if (contextNode.createRelation(
					XDILinkContractConstants.XRI_S_IS_DO, assignee) != null) {

				status = true;
			} else {
				// TBD
				// write error in debug log

			}
		} catch (Xdi2GraphException relationExists) {

		}
		return status;
	}

	/**
	 * Removes an assignment relationship of a LinkContract from an assignee
	 * node
	 * 
	 * @param assignee
	 *            The context node who will not longer have access to this
	 *            LinkContract
	 * @return NONE
	 */
	public boolean removeAssignee(ContextNode assignee) {
		boolean status = false;
		if (assignee == null) {
			// TBD
			// write error in debug log
			return status;
		}
		Iterator<Relation> allRelations = contextNode.getRelations();
		Relation r = null;
		for (; allRelations.hasNext();) {
			r = allRelations.next();
			ContextNode target = r.follow();
			if (target.equals(assignee)) {

				// write debug log with information about the relation XRI that
				// is removed
				// TBD
				status = true;
				break;
			}
		}
		if (status) {
			contextNode.deleteRelation(XDILinkContractConstants.XRI_S_IS_DO,
					r.getTargetContextNodeXri());
		}
		return status;
	}

	/**
	 * Gets all assignees of this Link Contract node
	 * 
	 * @return an iterator over the list of assignees
	 */

	public Iterator<ContextNode> getAssignees() {

		List<ContextNode> assignees = new ArrayList<ContextNode>();
		Iterator<Relation> allRelations = contextNode.getRelations();
		for (; allRelations.hasNext();) {
			Relation r = allRelations.next();

			if (r.getArcXri().equals(XDILinkContractConstants.XRI_S_IS_DO)) {
				ContextNode assignee = r.follow();
				assignees.add(assignee);
			}
		}
		if (assignees.isEmpty()) {
			return new EmptyIterator<ContextNode>();
		} else {
			return new SelectingIterator<ContextNode>(assignees.iterator()) {

				@Override
				public boolean select(ContextNode contextNode) {

					return true;
				}
			};
		}

	}

	/**
	 * Adds a permission (one of GET, ADD, MOD, DEL, COPY , MOVE , ALL) from
	 * this Link Contract node to a target node
	 * 
	 * @param permission
	 *            The enum value of permission
	 * @param targetNode
	 *            The target node where the permission relation arc will
	 *            terminate
	 * @return NONE
	 */
	public boolean addPermission(XDILinkContractPermission permission,
			ContextNode targetNode) {
		boolean status = false;
		XRI3Segment perm = null;

		// one cannot add an authorization permission to the Link Contract Node
		// itself

		if (targetNode.equals(contextNode)) {
			return false;
		}

		// if the same permission arc exists for the same target node, then a
		// new arc should not be added

		for (Iterator<ContextNode> iter = this
				.getNodesWithPermission(permission); iter.hasNext();) {
			ContextNode t = iter.next();
			if (t.equals(targetNode)) {
				return true;
			}
		}

		// if an arc to the given target node exists with $all, then no other
		// permission arc should be allowed

		for (Iterator<ContextNode> iter = this
				.getNodesWithPermission(XDILinkContractPermission.LC_OP_ALL); iter
				.hasNext();) {
			ContextNode t = iter.next();
			if (t.equals(targetNode)) {
				return true;
			}
		}
		// if a $all permission is added to the target node then all other
		// permission arcs should be deleted
		if (permission == XDILinkContractPermission.LC_OP_ALL) {
			this.removePermission(XDILinkContractPermission.LC_OP_GET,
					targetNode);
			this.removePermission(XDILinkContractPermission.LC_OP_ADD,
					targetNode);
			this.removePermission(XDILinkContractPermission.LC_OP_MOD,
					targetNode);
			this.removePermission(XDILinkContractPermission.LC_OP_DEL,
					targetNode);
		}

		switch (permission) {
		case LC_OP_GET:
			perm = XDILinkContractConstants.XRI_S_GET;
			break;
		case LC_OP_ADD:
			perm = XDILinkContractConstants.XRI_S_ADD;
			break;
		case LC_OP_MOD:
			perm = XDILinkContractConstants.XRI_S_MOD;
			break;
		case LC_OP_DEL:
			perm = XDILinkContractConstants.XRI_S_DEL;
			break;
		case LC_OP_ALL:
			perm = XDILinkContractConstants.XRI_S_ALL;
			break;

		default:
			// TBD
			// debug log
			break;

		}
		if (null != perm) {
			try {
				if (null != contextNode.createRelation(perm, targetNode)) {
					status = true;
				}
			} catch (Xdi2GraphException relationExists) {

			}
		}
		return status;

	}

	public boolean removePermission(XDILinkContractPermission permission,
			ContextNode targetNode) {
		boolean status = false;
		XRI3Segment perm = null;
		switch (permission) {
		case LC_OP_GET:
			perm = XDILinkContractConstants.XRI_S_GET;
			break;
		case LC_OP_ADD:
			perm = XDILinkContractConstants.XRI_S_ADD;
			break;
		case LC_OP_MOD:
			perm = XDILinkContractConstants.XRI_S_MOD;
			break;
		case LC_OP_DEL:
			perm = XDILinkContractConstants.XRI_S_DEL;
			break;
		case LC_OP_ALL:
			perm = XDILinkContractConstants.XRI_S_ALL;
			break;

		default:
			// TBD
			// debug log
			break;

		}
		if (null == perm) {
			return status;
		}
		Iterator<Relation> allRelations = contextNode.getRelations();
		Relation r = null;
		for (; allRelations.hasNext();) {
			r = allRelations.next();
			// System.out.println("Arc XRI=" + r.getArcXri());
			// System.out.println("Relation XRI=" + r.getRelationXri());
			// System.out.println("Context Node=" + r.getContextNode());
			// System.out.println("XDI Statement=" + r.getStatement());
			if (r.getArcXri().equals(perm)) {
				ContextNode nodeWithPermission = r.follow();
				if (nodeWithPermission.equals(targetNode)) {
					status = true;
					break;
				}

			}
		}
		if (status) {
			contextNode.deleteRelation(r.getArcXri(), r.getTargetContextNodeXri());
		}
		return status;

	}

	public Iterator<ContextNode> getNodesWithPermission(
			XDILinkContractPermission permission) {

		List<ContextNode> nodesWithPermission = new ArrayList<ContextNode>();
		XRI3Segment perm = null;
		switch (permission) {
		case LC_OP_GET:
			perm = XDILinkContractConstants.XRI_S_GET;
			break;
		case LC_OP_ADD:
			perm = XDILinkContractConstants.XRI_S_ADD;
			break;
		case LC_OP_MOD:
			perm = XDILinkContractConstants.XRI_S_MOD;
			break;
		case LC_OP_DEL:
			perm = XDILinkContractConstants.XRI_S_DEL;
			break;
		case LC_OP_ALL:
			perm = XDILinkContractConstants.XRI_S_ALL;
			break;

		default:
			// TBD
			// debug log
			break;

		}
		if (null == perm) {
			return new EmptyIterator<ContextNode>();
		}
		Iterator<Relation> allRelations = contextNode.getRelations();

		for (; allRelations.hasNext();) {
			Relation r = allRelations.next();
			if (r.getArcXri().equals(perm)) {
				ContextNode nodeWithPermission = r.follow();
				nodesWithPermission.add(nodeWithPermission);
			}
		}
		return new SelectingIterator<ContextNode>(
				nodesWithPermission.iterator()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return true;
			}
		};

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

		if (object == null || !(object instanceof LinkContract))
			return false;
		if (object == this)
			return true;

		LinkContract other = (LinkContract) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(LinkContract other) {

		if (other == this || other == null)
			return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}

	public void addAuthenticationFunction() {
		Policy policy = getPolicy(true);
		AndExpression andN = policy.getAndNode(true);		
		andN.addLiteralExpression("xdi.getGraphValue(\"$secret$!($token)\") == xdi.getMessageProperty(\"$secret$!($token)\")");
	}

}
