package xdi2.discovery;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.constants.XDIClientConstants;
import xdi2.client.events.XDIDiscoverFromAuthorityEvent;
import xdi2.client.events.XDIDiscoverFromRegistryEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.exceptions.Xdi2DiscoveryException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.client.util.URLURIUtil;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.discovery.cache.DiscoveryCacheKey;
import xdi2.discovery.cache.DiscoveryCacheProvider;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.TransportMessagingResponse;

/**
 * Given a Cloud Name or discovery key, useful information such a Cloud Number, 
 * public keys, or additional services, can be discovered.
 * 
 * @author markus
 */
public class XDIDiscoveryClient {

	private static Logger log = LoggerFactory.getLogger(XDIDiscoveryClient.class.getName());

	public static final XDIHttpClient XDI2_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient NEUSTAR_PROD_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient NEUSTAR_OTE_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient LEOLA_NYMBLE_DISCOVERY_XDI_CLIENT;

	public static final XDIDiscoveryClient XDI2_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient NEUSTAR_PROD_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient NEUSTAR_OTE_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient NEUSTAR_STAGE_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient LEOLA_NYMBLE_DISCOVERY_CLIENT;

	public static final XDIHttpClient DEFAULT_XDI_CLIENT;
	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT;

	public static final DiscoveryCacheProvider DEFAULT_DISCOVERY_CACHE_PROVIDER;

	private XDIClient<? extends TransportMessagingResponse> registryXdiClient;
	private DiscoveryCacheProvider discoveryCacheProvider;

	static {

		DEFAULT_DISCOVERY_CACHE_PROVIDER = DiscoveryCacheProvider.get();

		XDI2_DISCOVERY_XDI_CLIENT = new XDIHttpClient(URLURIUtil.URI("https://registry.xdi2.org/"));
		NEUSTAR_PROD_DISCOVERY_XDI_CLIENT = new XDIHttpClient(URLURIUtil.URI("https://xdidiscoveryservice.xdi.net/"));
		NEUSTAR_OTE_DISCOVERY_XDI_CLIENT = new XDIHttpClient(URLURIUtil.URI("https://xdidiscoveryserviceote.xdi.net/"));
		NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT = new XDIHttpClient(URLURIUtil.URI("https://xdidiscovery-stg.cloudnames.biz/"));
		LEOLA_NYMBLE_DISCOVERY_XDI_CLIENT = new XDIHttpClient(URLURIUtil.URI("http://xdi.nymble.me/"));

		XDI2_DISCOVERY_CLIENT = new XDIDiscoveryClient(XDI2_DISCOVERY_XDI_CLIENT);
		NEUSTAR_PROD_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_PROD_DISCOVERY_XDI_CLIENT);
		NEUSTAR_OTE_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_OTE_DISCOVERY_XDI_CLIENT);
		NEUSTAR_STAGE_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT);
		LEOLA_NYMBLE_DISCOVERY_CLIENT = new XDIDiscoveryClient(LEOLA_NYMBLE_DISCOVERY_XDI_CLIENT);

		DEFAULT_XDI_CLIENT = XDI2_DISCOVERY_XDI_CLIENT;
		DEFAULT_DISCOVERY_CLIENT = XDI2_DISCOVERY_CLIENT;
	}

	public XDIDiscoveryClient(XDIClient<TransportMessagingResponse> registryXdiClient, DiscoveryCacheProvider discoveryCacheProvider) {

		this.registryXdiClient = registryXdiClient;
		this.discoveryCacheProvider = discoveryCacheProvider;

		if (log.isDebugEnabled()) log.debug("Initializing discovery with client " + (registryXdiClient == null ? null : registryXdiClient.getClass().getSimpleName()) + " and cache " + (discoveryCacheProvider == null ? null : discoveryCacheProvider.getClass().getSimpleName()));
	}

	public XDIDiscoveryClient(XDIClient<TransportMessagingResponse> registryXdiClient) {

		this(registryXdiClient, DEFAULT_DISCOVERY_CACHE_PROVIDER);
	}

	public XDIDiscoveryClient(URI registryEndpointUri, DiscoveryCacheProvider discoveryCacheProvider) {

		this(new XDIHttpClient(registryEndpointUri), discoveryCacheProvider);
	}

	public XDIDiscoveryClient(URI registryEndpointUri) {

		this(new XDIHttpClient(registryEndpointUri));
	}

	public XDIDiscoveryClient(String registryEndpointUri) {

		this(new XDIHttpClient(registryEndpointUri));
	}

	public XDIDiscoveryClient() {

		this(DEFAULT_XDI_CLIENT);
	}

	public XDIDiscoveryResult discover(XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		// first discover from registry

		XDIDiscoveryResult xdiDiscoveryResultRegistry = this.discoverFromRegistry(query, endpointUriTypes);

		if (xdiDiscoveryResultRegistry == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from registry for " + query);
			return null;
		}

		if (xdiDiscoveryResultRegistry.getXdiEndpointUri() == null || xdiDiscoveryResultRegistry.getCloudNumber() == null) {

			if (log.isDebugEnabled()) log.debug("No XDI endpoint URI or cloud number from registry for " + query);
			return xdiDiscoveryResultRegistry;
		}

		// then discover from authority

		XDIDiscoveryResult xdiDiscoveryResultAuthority = this.discoverFromAuthority(xdiDiscoveryResultRegistry.getXdiEndpointUri(), xdiDiscoveryResultRegistry.getCloudNumber(), endpointUriTypes);

		if (xdiDiscoveryResultAuthority == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from authority for " + query);
			return xdiDiscoveryResultRegistry;
		}

		// return a single discovery result from the two individual ones

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();
		xdiDiscoveryResult.initFromRegistryAndAuthorityDiscoveryResult(xdiDiscoveryResultRegistry, xdiDiscoveryResultAuthority, query, endpointUriTypes);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discover(XDIAddress query, XDIAddress endpointUriType) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discover(query, new XDIAddress[] { endpointUriType });
	}

	public XDIDiscoveryResult discover(XDIAddress query) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discover(query, (XDIAddress[]) null);
	}

	public XDIDiscoveryResult discoverFromRegistry(XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();

		// check registry cache

		TransportMessagingResponse registryMessagingResponse = null;

		DiscoveryCacheKey registryDiscoveryCacheKey = DiscoveryCacheKey.build(query, this.getRegistryXdiClient(), null);

		synchronized(this) {

			if (this.getDiscoveryCacheProvider() != null) registryMessagingResponse = (TransportMessagingResponse) this.getDiscoveryCacheProvider().getRegistry(registryDiscoveryCacheKey);
		}

		MessageEnvelope registryMessageEnvelope = null;

		if (registryMessagingResponse != null) {

			if (log.isDebugEnabled()) log.debug("Registry cache HIT: " + registryDiscoveryCacheKey);
		} else {

			if (log.isDebugEnabled()) log.debug("Registry cache MISS: " + registryDiscoveryCacheKey);

			// send the registry message

			registryMessageEnvelope = new MessageEnvelope();
			Message registryMessage = registryMessageEnvelope.createMessage(null);
			registryMessage.createGetOperation(XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(query)));
			registryMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(query, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY));
			registryMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(query, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY));

			try {

				XDIClient<? extends TransportMessagingResponse> registryXdiClient = this.getRegistryXdiClient();

				registryMessagingResponse = registryXdiClient.send(registryMessageEnvelope);
			} catch (Xdi2ClientException ex) {

				xdiDiscoveryResult.initFromException(ex);
				throw ex;
			} catch (Exception ex) {

				throw new Xdi2DiscoveryException("Cannot send XDI message to XDI registry: " + ex.getMessage(), ex);
			}

			// save in cache

			if (this.getDiscoveryCacheProvider() != null) {

				if (log.isDebugEnabled()) log.debug("Registry cache PUT: " + registryDiscoveryCacheKey);
				this.getDiscoveryCacheProvider().putRegistry(registryDiscoveryCacheKey, registryMessagingResponse);
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoverEvent(new XDIDiscoverFromRegistryEvent(this, registryMessageEnvelope, xdiDiscoveryResult, query));
		}

		// init the registry discovery result

		xdiDiscoveryResult.initFromRegistryMessagingResponse(registryMessageEnvelope, registryMessagingResponse, query, endpointUriTypes);

		// cloud number check

		if (CloudNumber.isValid(query) && xdiDiscoveryResult.getCloudNumber() != null) {

			if (! xdiDiscoveryResult.getCloudNumber().getXDIAddress().equals(query)) throw new Xdi2DiscoveryException("Queried cloud number " + query + " does not match discovered cloud number " + xdiDiscoveryResult.getCloudNumber().getXDIAddress());
		}

		// done

		if (log.isDebugEnabled()) log.debug("Discovery result from registry: " + xdiDiscoveryResult);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromRegistry(XDIAddress query, XDIAddress endpointUriType) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discoverFromRegistry(query, new XDIAddress[] { endpointUriType });
	}

	public XDIDiscoveryResult discoverFromRegistry(XDIAddress query) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discoverFromRegistry(query, (XDIAddress[]) null);
	}

	public XDIDiscoveryResult discoverFromAuthority(URI xdiEndpointUri, CloudNumber cloudNumber, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();

		// check authority cache

		TransportMessagingResponse authorityMessagingResponse = null;

		DiscoveryCacheKey authorityDiscoveryCacheKey = DiscoveryCacheKey.build(cloudNumber, xdiEndpointUri, endpointUriTypes);

		synchronized(this) {

			if (this.getDiscoveryCacheProvider() != null) authorityMessagingResponse = (TransportMessagingResponse) this.getDiscoveryCacheProvider().getAuthority(authorityDiscoveryCacheKey);
		}

		MessageEnvelope authorityMessageEnvelope = null;

		if (authorityMessagingResponse != null) {

			if (log.isDebugEnabled()) log.debug("Authority cache HIT: " + authorityDiscoveryCacheKey);
		} else {

			if (log.isDebugEnabled()) log.debug("Authority cache MISS: " + authorityDiscoveryCacheKey);

			// send the authority message

			authorityMessageEnvelope = new MessageEnvelope();
			Message authorityMessage = authorityMessageEnvelope.createMessage(null);
			authorityMessage.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
			authorityMessage.setLinkContractClass(PublicLinkContract.class);
			authorityMessage.createGetOperation(XDIStatement.fromRelationComponents(XDIConstants.XDI_ADD_ROOT, XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_COMMON_VARIABLE));
			authorityMessage.createGetOperation(XDIStatement.fromRelationComponents(cloudNumber.getXDIAddress(), XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_COMMON_VARIABLE));
			authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY));
			authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY));

			if (endpointUriTypes != null) {

				for (XDIAddress endpointUriType : endpointUriTypes) {

					authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(XDIAddress.fromComponent(cloudNumber.getPeerRootXDIArc()), endpointUriType, XDIClientConstants.XDI_ADD_AS_URI));
				}
			}

			try {

				XDIHttpClient authorityXdiHttpClient = new XDIHttpClient(xdiEndpointUri);

				authorityMessagingResponse = authorityXdiHttpClient.send(authorityMessageEnvelope);
			} catch (Xdi2ClientException ex) {

				xdiDiscoveryResult.initFromException(ex);
				throw ex;
			} catch (Exception ex) {

				throw new Xdi2DiscoveryException("Cannot send XDI message to XDI authority: " + ex.getMessage(), ex);
			}

			// save in cache

			if (this.getDiscoveryCacheProvider() != null) {

				if (log.isDebugEnabled()) log.debug("Authority cache PUT: " + authorityDiscoveryCacheKey);
				this.getDiscoveryCacheProvider().putAuthority(authorityDiscoveryCacheKey, authorityMessagingResponse);
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoverEvent(new XDIDiscoverFromAuthorityEvent(this, authorityMessageEnvelope, xdiDiscoveryResult, xdiEndpointUri));
		}

		// init the authority discovery result

		xdiDiscoveryResult.initFromAuthorityMessagingResponse(authorityMessageEnvelope, authorityMessagingResponse, endpointUriTypes);

		// cloud number check

		if (! xdiDiscoveryResult.getCloudNumber().getXDIAddress().equals(cloudNumber.getXDIAddress())) throw new Xdi2DiscoveryException("Queried cloud number " + cloudNumber.getXDIAddress() + " does not match discovered cloud number " + xdiDiscoveryResult.getCloudNumber().getXDIAddress());

		// done

		if (log.isDebugEnabled()) log.debug("Discovery result from authority: " + xdiDiscoveryResult);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromAuthority(URI xdiEndpointUri, CloudNumber cloudNumber, XDIAddress endpointUriType) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discoverFromAuthority(xdiEndpointUri, cloudNumber, new XDIAddress[] { endpointUriType });
	}

	public XDIDiscoveryResult discoverFromAuthority(URI xdiEndpointUri, CloudNumber cloudNumber) throws Xdi2DiscoveryException, Xdi2ClientException {

		return this.discoverFromAuthority(xdiEndpointUri, cloudNumber, (XDIAddress[]) null);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return String.valueOf(this.getRegistryXdiClient());
	}

	/*
	 * Getters and setters
	 */

	public XDIClient<? extends TransportMessagingResponse> getRegistryXdiClient() {

		return this.registryXdiClient;
	}

	public void setRegistryXdiClient(XDIClient<? extends TransportMessagingResponse> registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}

	public DiscoveryCacheProvider getDiscoveryCacheProvider() {

		return this.discoveryCacheProvider;
	}

	public void setDiscoveryCacheProvider(DiscoveryCacheProvider discoveryCacheProvider) {

		this.discoveryCacheProvider = discoveryCacheProvider;
	}
}
