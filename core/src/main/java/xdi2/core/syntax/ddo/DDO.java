package xdi2.core.syntax.ddo;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.dictionary.Dictionary;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.DID;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingRelationTargetXDIAddressIterator;

/**
 * A DDO (DID descriptor object), represented as an XDI entity.
 * 
 * @author markus
 */
public class DDO implements Serializable, Comparable<DDO> {

	private static final long serialVersionUID = -6471547640184381528L;

	public static final XDIAddress XDI_ADD_CONTROL = XDIAddress.create("#control");
	public static final XDIAddress XDI_ADD_GUARDIAN = XDIAddress.create("#guardian");
	public static final XDIAddress XDI_ADD_SERVICE = XDIAddress.create("#service");
	public static final XDIArc XDI_ARC_AS_URI = XDIArc.create("<$uri>");

	private XdiEntity xdiEntity;

	private DDO(XdiEntity xdiEntity) {

		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	public static DDO create(DID did) {

		XdiEntity xdiEntity = XdiAbstractEntity.fromXDIAddress(did.getXDIAddress());

		return new DDO(xdiEntity);
	}

	/**
	 * Checks if an XDI entity is a valid DDO.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid DDO.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity == null) return false;

		if (! DID.isValid(xdiEntity.getXDIAddress())) return false;

		return true;
	}

	/**
	 * Factory method that creates a DDO bound to a given XDI entity.
	 * @param XDI entity The XDI entity that is an DDO.
	 * @return The DDO.
	 */
	public static DDO fromXdiEntity(XdiEntity xdiEntity) {

		if (xdiEntity == null) throw new NullPointerException();

		if (! isValid(xdiEntity)) return null;

		return new DDO(xdiEntity);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity to which this DDO is bound.
	 * @return An XDI entity that represents the DDO.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	/**
	 * Returns the underlying XDI entity to which this DDO is bound.
	 * @return An XDI entity that represents the DDO.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntity().getContextNode();
	}

	public XDIAddress getXDIAddress() {

		return this.getContextNode().getXDIAddress();
	}

	public void setDID(DID did) {

		if (did.equals(this.getDID())) return;

		ContextNode newContextNode = this.getContextNode().getGraph().setDeepContextNode(did.getXDIAddress());
		CopyUtil.copyContextNodeContents(this.getContextNode(), newContextNode, null);

		this.getContextNode().delete();
		this.xdiEntity = XdiAbstractEntity.fromContextNode(newContextNode);
	}

	public DID getDID() {

		return DID.fromXDIAddress(this.getXDIAddress());
	}

	public XDIAddress getType() {

		return Dictionary.getContextNodeType(this.getContextNode());
	}

	public void setType(XDIAddress type) {

		for (Character cs : XDIConstants.CS_ARRAY) {

			if (type.toString().equals(cs.toString())) {

				Dictionary.delContextNodeTypes(this.getContextNode());

				XDIAddress newXDIaddress = XDIAddress.create(cs + this.getXDIAddress().toString().substring(1));
				DID newDID = DID.fromXDIAddress(newXDIaddress);
				this.setDID(newDID);

				return;
			}
		}

		Dictionary.setContextNodeType(this.getContextNode(), type);
	}

	public List<XDIAddress> getControl() {

		return new IteratorListMaker<XDIAddress> (new MappingRelationTargetXDIAddressIterator(this.getXdiEntity().getContextNode().getRelations(XDI_ADD_CONTROL))).list();
	}

	public void addControl(DID control) {

		this.getXdiEntity().getContextNode().setRelation(XDI_ADD_CONTROL, control.getXDIAddress());
	}

	public void addControl(XDIAddress control) {

		this.getXdiEntity().getContextNode().setRelation(XDI_ADD_CONTROL, control);
	}

	public DID[] getEquivalentDIDs() {

		List<DID> equivalentDIDs = new ArrayList<DID> ();

		for (Iterator<ContextNode> contextNodes = Equivalence.getIdentityContextNodes(this.getContextNode()); contextNodes.hasNext();) {

			DID equivalentDID = DID.fromXDIAddress(contextNodes.next().getXDIAddress());
			if (equivalentDID == null) continue;

			equivalentDIDs.add(equivalentDID);
		}

		return equivalentDIDs.toArray(new DID[equivalentDIDs.size()]);
	}

	public void addEquivalentDID(DID equivalentDID) {

		Equivalence.setIdentityContextNode(this.getContextNode(), equivalentDID.getXDIAddress());
	}

	public void addEquivalentDID(XDIAddress equivalentDID) {

		Equivalence.setIdentityContextNode(this.getContextNode(), equivalentDID);
	}

	public void delEquivalentDID(DID equivalentDID) {

		Equivalence.delIdentityContextNode(this.getContextNode(), equivalentDID.getXDIAddress());
	}

	public DID getGuardian() {

		Relation guardianRelation = this.getContextNode().getRelation(XDI_ADD_GUARDIAN);
		if (guardianRelation == null) return null;

		XDIAddress guardianXDIAddress = guardianRelation.getTargetXDIAddress();
		if (guardianXDIAddress == null) return null;

		return DID.fromXDIAddress(guardianXDIAddress);
	}

	public void setGuardian(DID guardianDID) {

		this.getContextNode().delRelations(XDI_ADD_GUARDIAN);
		this.getContextNode().setRelation(XDI_ADD_GUARDIAN, guardianDID.getXDIAddress());
	}

	public Map<XDIAddress, String> getServices() {

		XdiEntity serviceXdiEntity = this.getXdiEntity().getXdiEntity(XDI_ADD_SERVICE, false);
		if (serviceXdiEntity == null) return Collections.emptyMap();

		// TODO

		return null;
	}

	public void addService(XDIAddress service, URI uri) {

		if (service == null) throw new NullPointerException();
		if (uri == null) throw new NullPointerException();

		XdiEntity serviceXdiEntity = this.getXdiEntity().getXdiEntity(XDI_ADD_SERVICE, true);

		XdiAttribute serviceXdiAttribute = serviceXdiEntity.getXdiAttribute(service, true).getXdiAttribute(XDI_ARC_AS_URI, true);
		serviceXdiAttribute.setLiteralString(uri.toString());
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

		if (object == null || !(object instanceof DDO)) return false;
		if (object == this) return true;

		DDO other = (DDO) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(DDO other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
