package xdi2.client;

import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.Manipulator;
import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public interface XDIClientRoute <CLIENT extends XDIClient> {

	public XDIArc getToPeerRootXDIArc();
	public CLIENT constructXDIClient();

	public MessageEnvelope createMessageEnvelope();
	public Message createMessage(MessageEnvelope messageEnvelope, XDIAddress senderXDIAddress, long index);
	public Message createMessage(MessageEnvelope messageEnvelope, XDIAddress senderXDIAddress);
	public Message createMessage(MessageEnvelope messageEnvelope);
	public Message createMessage(XDIAddress senderXDIAddress);
	public Message createMessage();

	/*
	 * $get helper methods
	 */

	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress, Manipulator... manipulators) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress, Manipulator... manipulators) throws Xdi2AgentException, Xdi2ClientException;
	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException;
}
