package xdi2.discovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.events.XDIDiscoverFromAuthorityEvent;
import xdi2.client.events.XDIDiscoverFromRegistryEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * Given a Cloud Name or discovery key, useful information such a Cloud Number, 
 * public keys, or additional services, can be discovered.
 * 
 * @author markus
 */
public class XDIDiscoveryClient {

	private static Logger log = LoggerFactory.getLogger(XDIDiscoveryClient.class.getName());

	public static final XDIHttpClient DEFAULT_XDI_CLIENT = new XDIHttpClient("http://mycloud.neustar.biz:12220/");

	private XDIHttpClient registryXdiClient;

	public XDIDiscoveryClient(XDIHttpClient registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}

	public XDIDiscoveryClient() {

		this(DEFAULT_XDI_CLIENT);
	}

	public XDIDiscoveryResult discover(XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResultRegistry = this.discoverFromRegistry(query, endpointUriTypes);

		if (xdiDiscoveryResultRegistry == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from registry for " + query);

			return null;
		}

		if (log.isDebugEnabled()) log.debug("Discovery result from registry: " + xdiDiscoveryResultRegistry);

		if (xdiDiscoveryResultRegistry.getXdiEndpointUri() == null || xdiDiscoveryResultRegistry.getCloudNumber() == null) {

			if (log.isDebugEnabled()) log.debug("No XDI endpoint URI or cloud number from registry for " + query);

			return xdiDiscoveryResultRegistry;
		}

		XDIDiscoveryResult xdiDiscoveryResultAuthority = this.discoverFromAuthority(xdiDiscoveryResultRegistry.getXdiEndpointUri(), xdiDiscoveryResultRegistry.getCloudNumber(), endpointUriTypes);

		if (xdiDiscoveryResultAuthority == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from authority for " + query);

			return xdiDiscoveryResultRegistry;
		}

		if (log.isDebugEnabled()) log.debug("Discovery result from authority: " + xdiDiscoveryResultAuthority);

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();
		xdiDiscoveryResult.initFromRegistryAndAuthorityDiscoveryResult(xdiDiscoveryResultRegistry, xdiDiscoveryResultAuthority, query, endpointUriTypes);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromRegistry(XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult();

		// send the registry message

		MessageEnvelope registryMessageEnvelope = new MessageEnvelope();
		Message registryMessage = registryMessageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		registryMessage.setLinkContractXri(XDILinkContractConstants.XRI_S_PUBLIC);
		registryMessage.createGetOperation(XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(query)));

		MessageResult registryMessageResult;

		try {

			XDIHttpClient registryXdiHttpClient = this.getRegistryXdiClient();

			registryMessageResult = registryXdiHttpClient.send(registryMessageEnvelope, null);
		} catch (Xdi2ClientException ex) {

			discoveryResult.initFromException(ex);

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message to XDI registry: " + ex.getMessage(), ex, null);
		}

		// parse the registry message result

		discoveryResult.initFromRegistryMessageResult(registryMessageEnvelope, registryMessageResult, query, endpointUriTypes);

		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromRegistryEvent(this, registryMessageEnvelope, discoveryResult, query));

		return discoveryResult;
	}

	public XDIDiscoveryResult discoverFromAuthority(String xdiEndpointUri, XDI3Segment cloudNumber, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult();

		// send the authority message

		MessageEnvelope authorityMessageEnvelope = new MessageEnvelope();
		Message authorityMessage = authorityMessageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		authorityMessage.setToAddress(XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(cloudNumber)));
		authorityMessage.setLinkContractXri(XDILinkContractConstants.XRI_S_PUBLIC_DO);
		//authorityMessage.createGetOperation(XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE));
		authorityMessage.createGetOperation(XDIAuthenticationConstants.XRI_S_PUBLIC_MSG_SIG_KEYPAIR_PUBLIC_KEY);
		authorityMessage.createGetOperation(XDIAuthenticationConstants.XRI_S_PUBLIC_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY);

		MessageResult authorityMessageResult;

		try {

			XDIHttpClient authorityXdiHttpClient = new XDIHttpClient(xdiEndpointUri);

			authorityMessageResult = authorityXdiHttpClient.send(authorityMessageEnvelope, null);
		} catch (Xdi2ClientException ex) {

			discoveryResult.initFromException(ex);

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send XDI message to XDI authority: " + ex.getMessage(), ex, null);
		}

		// parse the authority message result

		discoveryResult.initFromAuthorityMessageResult(authorityMessageEnvelope, authorityMessageResult, endpointUriTypes);

		// done

		this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromAuthorityEvent(this, authorityMessageEnvelope, discoveryResult, xdiEndpointUri));

		return discoveryResult;
	}

	public XDIHttpClient getRegistryXdiClient() {

		return this.registryXdiClient;
	}

	public void setRegistryXdiClient(XDIHttpClient registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}
}
