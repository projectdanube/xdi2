package xdi2.client.impl.http;

import java.net.URL;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.CloudNumber;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class XDIHttpClientRoute extends XDIAbstractClientRoute<XDIHttpClient> implements XDIClientRoute<XDIHttpClient> {

	private CloudNumber cloudNumber;
	private URL xdiEndpointUrl;

	public XDIHttpClientRoute(CloudNumber cloudNumber, URL xdiEndpointUrl) {

		this.cloudNumber = cloudNumber;
		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	@Override
	public XDIHttpClient constructXDIClient() {

		return new XDIHttpClient(this.xdiEndpointUrl);
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		Message message = super.constructMessage(messageEnvelope);
		message.setToPeerRootXDIArc(this.cloudNumber.getPeerRootXDIArc());
		
		return message;
	}
}
