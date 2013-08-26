package xdi2.discovery;

import xdi2.client.XDIClient;
import xdi2.client.events.XDIDiscoverFromEndpointUriEvent;
import xdi2.client.events.XDIDiscoverFromXriEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

public class XDIDiscovery {

	public static final XDIHttpClient DEFAULT_REGISTRY_XDI_CLIENT = new XDIHttpClient("http://mycloud.neustar.biz:12220/");

	private XDIHttpClient registryXdiClient;

	public XDIDiscovery(XDIHttpClient registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}

	public XDIDiscovery() {

		this(DEFAULT_REGISTRY_XDI_CLIENT);
	}

	public XDIDiscoveryResult discoverFromXri(XDI3Segment xri) throws Xdi2ClientException {

		XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(xri);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createGetOperation(XDI3Segment.fromComponent(peerRootArcXri));

		// send the message

		MessageResult messageResult;
		XDIDiscoveryResult discoveryResult;

		try {

			messageResult = this.getRegistryXdiClient().send(messageEnvelope, null);

			discoveryResult = XDIDiscoveryResult.fromXriAndMessageResult(xri, messageResult);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message: " + ex.getMessage(), ex, null);
		}

		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromXriEvent(this, messageEnvelope, discoveryResult, xri));

		return discoveryResult;
	}

	public XDIDiscoveryResult discoverFromEndpointUri(String endpointUri) throws Xdi2ClientException {

		// prepare XDI client

		XDIClient xdiClient = new XDIHttpClient(endpointUri);

		// prepare message envelope

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createGetOperation(XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE));

		// send the message

		MessageResult messageResult;
		XDIDiscoveryResult discoveryResult;

		try {

			messageResult = xdiClient.send(messageEnvelope, null);

			XDI3Segment xri = messageResult.getGraph().getDeepRelation(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF).getTargetContextNodeXri();

			discoveryResult = XDIDiscoveryResult.fromXriAndMessageResult(xri, messageResult);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message: " + ex.getMessage(), ex, null);
		}

		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromEndpointUriEvent(this, messageEnvelope, discoveryResult, endpointUri));

		return discoveryResult;
	}

	public XDIHttpClient getRegistryXdiClient() {

		return this.registryXdiClient;
	}

	public void setRegistryXdiClient(XDIHttpClient xdiClient) {

		this.registryXdiClient = xdiClient;
	}
}
