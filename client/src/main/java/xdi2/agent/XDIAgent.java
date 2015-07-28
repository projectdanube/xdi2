package xdi2.agent;

import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.Manipulator;
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

	public XDIClientRoute<? extends XDIClient> route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<? extends XDIClient> route(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<? extends XDIClient> route(MessageEnvelope messageEnvelope) throws Xdi2AgentException, Xdi2ClientException;
	public XDIClientRoute<? extends XDIClient> route(Message message) throws Xdi2AgentException, Xdi2ClientException;

	/*
	 * $get helper methods
	 */

	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress, Manipulator... manipulators) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress, Manipulator... manipulators) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
}
