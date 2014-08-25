package xdi2.client.agent;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;

/**
 * An XDIAgent can intelligently construct and send XDI messages to obtain
 * desired data.
 * 
 * @author markus
 */
public interface XDIAgent {

	/**
	 * Given an address and an optional local graph, the agent will try to 
	 * obtain a context with the desired data.
	 * @param XDIaddress The address of the desired data.
	 * @param localGraph An optional local graph.
	 * @return
	 */
	public ContextNode get(XDIAddress XDIaddress, Graph localGraph) throws Xdi2AgentException, Xdi2ClientException;
}
