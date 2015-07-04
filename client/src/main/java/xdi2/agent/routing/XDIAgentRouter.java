package xdi2.agent.routing;

import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.syntax.XDIArc;

public interface XDIAgentRouter <ROUTE extends XDIClientRoute<CLIENT>, CLIENT extends XDIClient> {

	public ROUTE route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException;
}
