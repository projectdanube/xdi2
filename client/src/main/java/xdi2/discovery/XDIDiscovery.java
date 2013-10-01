package xdi2.discovery;

import xdi2.client.events.XDIDiscoverFromEndpointUriEvent;
import xdi2.client.events.XDIDiscoverFromXriEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
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

	public XDIDiscoveryResult discoverFromRegistry(XDI3Segment query, boolean discoverPublicKey, String[] discoverServices) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult(query);

		// discover from registry
		
		this.discoverFromRegistry(discoveryResult, query);

		// optionally discover from authority
		
		if (discoverPublicKey || discoverServices != null) {

			if (discoveryResult.getXdiEndpointUri() == null) {
				
				throw new Xdi2ClientException("Could not discover XDI endpoint URI from " + query, null, null);
			}
			
			this.discoverFromAuthority(discoveryResult, discoveryResult.getXdiEndpointUri(), discoverPublicKey, discoverServices);
		}

		// done
		
		return discoveryResult;
	}

	public XDIDiscoveryResult discoverFromAuthority(String xdiEndpointUri, boolean discoverPublicKey, String discoverServices[]) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult(null);

		// discover from authority
		
		this.discoverFromAuthority(discoveryResult, xdiEndpointUri, discoverPublicKey, discoverServices);

		// done
		
		return discoveryResult;
	}
	
	private void discoverFromRegistry(XDIDiscoveryResult discoveryResult, XDI3Segment query) throws Xdi2ClientException {

		// send the registry message

		MessageEnvelope registryMessageEnvelope = new MessageEnvelope();
		Message registryMessage = registryMessageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		registryMessage.createGetOperation(XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(query)));

		MessageResult registryMessageResult;

		try {

			registryMessageResult = this.getRegistryXdiClient().send(registryMessageEnvelope, null);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message to XDI registry: " + ex.getMessage(), ex, null);
		}

		// parse the registry message result

		discoveryResult.initFromRegistryMessageResult(registryMessageResult);
		
		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromXriEvent(this, registryMessageEnvelope, discoveryResult, query));
	}

	private void discoverFromAuthority(XDIDiscoveryResult discoveryResult, String xdiEndpointUri, boolean discoverPublicKey, String discoverServices[]) throws Xdi2ClientException {

		// send the authority message

		MessageEnvelope authorityMessageEnvelope = new MessageEnvelope();
		Message authorityMessage = authorityMessageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		authorityMessage.createGetOperation(XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE));
		authorityMessage.createGetOperation(XDI3Segment.create("$public<$key>"));

		MessageResult authorityMessageResult;

		try {

			XDIHttpClient authorityXdiHttpClient = new XDIHttpClient();

			authorityMessageResult = authorityXdiHttpClient.send(authorityMessageEnvelope, null);
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message to XDI authority: " + ex.getMessage(), ex, null);
		}

		// parse the authority message result

		discoveryResult.initFromAuthorityMessageResult(authorityMessageResult);

		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromEndpointUriEvent(this, authorityMessageEnvelope, discoveryResult, xdiEndpointUri));
	}

	public XDIHttpClient getRegistryXdiClient() {

		return this.registryXdiClient;
	}

	public void setRegistryXdiClient(XDIHttpClient registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}
}
