package xdi2.resolution;

import java.net.URL;

import xdi2.client.http.XDIHttpClient;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.util.XDIMessagingConstants;

public class XdiResolver {

	public static final String DEFAULT_XDI_PROXY = "http://xri2xdi.net/";

	private XDIHttpClient xdiClient;
	private String xdiProxy;

	public XdiResolver(String xdiProxy, XDIHttpClient xdiClient) {

		this.xdiProxy = xdiProxy;
		this.xdiClient = xdiClient;
	}

	public XdiResolver() {

		this(DEFAULT_XDI_PROXY, new XDIHttpClient());
	}

	public XdiResolutionResult resolve(String xri) throws Xdi2MessagingException {

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createGetOperation(new XRI3Segment("(" + xri + ")"));

		// send the message

		XdiResolutionResult resolutionResult;

		try {

			this.xdiClient.setUrl(new URL(this.xdiProxy));
			MessageResult messageResult = this.xdiClient.send(messageEnvelope, null);
			resolutionResult = XdiResolutionResult.fromXriAndMessageResult(xri, messageResult);
		} catch (Xdi2MessagingException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot send XDI message: " + ex.getMessage(), ex);
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

	public String getXdiProxy() {

		return this.xdiProxy;
	}

	public void setXdiProxy(String xdiProxy) {

		this.xdiProxy = xdiProxy;
	}
}
