package xdi2.agent.routing;

import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.response.MessagingResponse;

public interface XDIAgentRouter <ROUTE extends XDIClientRoute<? extends CLIENT>, CLIENT extends XDIClient<? extends MessagingResponse>> {

	public ROUTE route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException;
}
