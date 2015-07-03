package xdi2.client.agent;

import xdi2.client.agent.target.AgentRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;

/**
 * An XDIAgent can intelligently construct and send XDI messages to obtain
 * desired data.
 * 
 * @author markus
 */
public interface XDIAgent {

	public AgentRoute route(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;

	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
}
