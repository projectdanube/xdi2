package xdi2.agent.impl;

import xdi2.agent.XDIAgent;
import xdi2.agent.routing.impl.http.XDIHttpDiscoveryAgentRouter;

public class XDIHttpDiscoveryAgent extends XDIBasicAgent implements XDIAgent {

	public XDIHttpDiscoveryAgent() {

		super(new XDIHttpDiscoveryAgentRouter());
	}
}
