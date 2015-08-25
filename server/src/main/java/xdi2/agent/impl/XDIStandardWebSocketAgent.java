package xdi2.agent.impl;

import xdi2.agent.XDIAgent;
import xdi2.agent.routing.impl.bootstrap.XDIBootstrapLocalAgentRouter;
import xdi2.agent.routing.impl.websocket.XDIWebSocketDiscoveryAgentRouter;

public class XDIStandardWebSocketAgent extends XDIBasicAgent implements XDIAgent {

	public XDIStandardWebSocketAgent() {

		super(new XDIBootstrapLocalAgentRouter(), new XDIWebSocketDiscoveryAgentRouter());
	}
}
