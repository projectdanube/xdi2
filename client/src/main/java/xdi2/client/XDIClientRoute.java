package xdi2.client;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public interface XDIClientRoute <CLIENT extends XDIClient> {

	public CLIENT constructXDIClient();
	public MessageEnvelope constructMessageEnvelope();
	public Message constructMessage(MessageEnvelope messageEnvelope);
}
