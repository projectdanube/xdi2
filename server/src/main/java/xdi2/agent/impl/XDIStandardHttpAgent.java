package xdi2.agent.impl;

import xdi2.agent.XDIAgent;
import xdi2.agent.routing.impl.bootstrap.XDIBootstrapLocalAgentRouter;
import xdi2.agent.routing.impl.http.XDIHttpDiscoveryAgentRouter;

public class XDIStandardHttpAgent extends XDIBasicAgent implements XDIAgent {

	public XDIStandardHttpAgent() {

		super(new XDIBootstrapLocalAgentRouter(), new XDIHttpDiscoveryAgentRouter());
	}
}
