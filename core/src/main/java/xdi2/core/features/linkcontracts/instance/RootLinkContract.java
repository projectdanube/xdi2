package xdi2.core.features.linkcontracts.instance;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.GraphUtil;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI root link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class RootLinkContract extends GenericLinkContract {

	private static final long serialVersionUID = 2104767228107704809L;

	protected RootLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI root link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI root link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (! GenericLinkContract.isValid(xdiEntity)) return false;

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (GenericLinkContract.getAuthorizingAuthority(xdiEntity.getXri()) == null) return false;
			if (GenericLinkContract.getRequestingAuthority(xdiEntity.getXri()) == null) return false;
			if (GenericLinkContract.getTemplateAuthorityAndId(xdiEntity.getXri()) != null) return false;

			if (! GenericLinkContract.getAuthorizingAuthority(xdiEntity.getXri()).equals(GenericLinkContract.getRequestingAuthority(xdiEntity.getXri()))) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			return false;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI root link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI root link contract.
	 * @return The XDI root link contract.
	 */
	public static RootLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new RootLinkContract(xdiEntity);
	}

	public static XDI3Segment createRootLinkContractXri(XDI3Segment ownerXri) {

		return GenericLinkContract.createGenericLinkContractXri(ownerXri, ownerXri, null);
	}

	/**
	 * Factory method that finds or creates an XDI root link contract for a graph.
	 * @return The XDI root link contract.
	 */
	public static RootLinkContract findRootLinkContract(Graph graph, boolean create) {

		XDI3Segment ownerXri = GraphUtil.getOwnerXri(graph);
		if (ownerXri == null) return null;

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(graph, ownerXri, ownerXri, null, create);
		if (genericLinkContract == null) return null;

		return fromXdiEntity(genericLinkContract.getXdiEntity());
	}
}
