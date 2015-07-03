package xdi2.agent;

import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

/**
 * An XDIAgent can intelligently construct and send XDI messages to obtain
 * desired data.
 * 
 * @author markus
 */
public interface XDIAgent {

	public XDIClientRoute<?> route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<?> route(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<?> route(MessageEnvelope messageEnvelope) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<?> route(Message message) throws Xdi2AgentException, Xdi2ClientException;

	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
}
