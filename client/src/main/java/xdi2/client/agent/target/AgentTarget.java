package xdi2.client.agent.target;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.syntax.XDIArc;

public interface AgentTarget {

	public AgentConnection connect(XDIArc targetPeerRootXDIArc) throws Xdi2AgentException, Xdi2ClientException;
}
