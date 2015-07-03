package xdi2.client.agent.target;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.syntax.XDIArc;

public interface AgentTarget {

	public AgentRoute route(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException;
}
