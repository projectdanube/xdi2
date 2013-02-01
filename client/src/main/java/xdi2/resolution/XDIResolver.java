package xdi2.resolution;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class XDIResolver {

	public static final XDIHttpClient DEFAULT_XDI_CLIENT = new XDIHttpClient("http://xri2xdi.net/");

	private XDIHttpClient xdiClient;

	public XDIResolver(XDIHttpClient xdiClient) {

		this.xdiClient = xdiClient;
	}

	public XDIResolver() {

		this(DEFAULT_XDI_CLIENT);
	}

	public XDIResolutionResult resolve(String xri) throws Xdi2ClientException {

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createGetOperation(XDI3Segment.create("(" + xri + ")"));

		// send the message

		XDIResolutionResult resolutionResult;

		try {

			MessageResult messageResult = this.xdiClient.send(messageEnvelope, null);
			resolutionResult = XDIResolutionResult.fromXriAndMessageResult(xri, messageResult);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message: " + ex.getMessage(), ex, null);
		}

		// done

		return resolutionResult;
	}

	public XDIHttpClient getXdiClient() {

		return this.xdiClient;
	}

	public void setXdiClient(XDIHttpClient xdiClient) {

		this.xdiClient = xdiClient;
	}
}
