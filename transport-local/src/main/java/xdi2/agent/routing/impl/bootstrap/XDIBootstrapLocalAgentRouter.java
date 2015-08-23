package xdi2.agent.routing.impl.bootstrap;

import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.local.XDILocalAgentRouter;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.client.impl.local.XDILocalClientRoute;
import xdi2.core.bootstrap.XDIBootstrap;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;

// TODO: it would be nice if this was accessible from xdi2-client
public class XDIBootstrapLocalAgentRouter extends XDILocalAgentRouter implements XDIAgentRouter<XDILocalClientRoute, XDILocalClient> {

	private static XDIArc BOOTSTRAP_OWNER_PEER_ROOT_XDI_ARC;

	static {

		BOOTSTRAP_OWNER_PEER_ROOT_XDI_ARC = GraphUtil.getOwnerPeerRootXDIArc(XDIBootstrap.BOOTSTRAP_GRAPH);
	}

	public XDIBootstrapLocalAgentRouter() {

		super(XDIBootstrap.BOOTSTRAP_GRAPH);
	}

	@Override
	protected XDIArc overrideToPeerRootXDIArc(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		if (! toPeerRootXDIArc.hasXRef() || ! toPeerRootXDIArc.getXRef().hasXDIArc()) {

			throw new Xdi2AgentException("TO peer root is not a peer root: " + toPeerRootXDIArc);
		}

		if (! XDIConstants.CS_CLASS_RESERVED.equals(toPeerRootXDIArc.getXRef().getXDIArc().getCs())) return null;

		return BOOTSTRAP_OWNER_PEER_ROOT_XDI_ARC;
	}
}
