package xdi2.discovery;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.features.roots.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class XDIDiscovery {

	public static final XDIHttpClient DEFAULT_XDI_CLIENT = new XDIHttpClient("http://xri2xdi.net/");

	private XDIHttpClient xdiClient;

	public XDIDiscovery(XDIHttpClient xdiClient) {

		this.xdiClient = xdiClient;
	}

	public XDIDiscovery() {

		this(DEFAULT_XDI_CLIENT);
	}

	public XDIDiscoveryResult discover(String xri) throws Xdi2ClientException {

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createGetOperation(XDI3Segment.create(XdiPeerRoot.createPeerRootArcXri(XDI3Segment.create(xri))));

		// send the message

		XDIDiscoveryResult discoveryResult;

		try {

			MessageResult messageResult = this.xdiClient.send(messageEnvelope, null);
			discoveryResult = XDIDiscoveryResult.fromXriAndMessageResult(xri, messageResult);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message: " + ex.getMessage(), ex, null);
		}

		// done

		return discoveryResult;
	}

	public XDIHttpClient getXdiClient() {

		return this.xdiClient;
	}

	public void setXdiClient(XDIHttpClient xdiClient) {

		this.xdiClient = xdiClient;
	}
}
