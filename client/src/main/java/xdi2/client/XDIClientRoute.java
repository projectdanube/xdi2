package xdi2.client;

import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public interface XDIClientRoute <CLIENT extends XDIClient> {

	public XDIArc getToPeerRootXDIArc();

	public CLIENT constructXDIClient();
	public MessageEnvelope constructMessageEnvelope();
	public Message constructMessage(MessageEnvelope messageEnvelope, XDIAddress senderXDIAddress);
	public Message constructMessage(MessageEnvelope messageEnvelope);
}
