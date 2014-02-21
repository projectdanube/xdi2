package xdi2.discovery;

import java.io.Serializable;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.constants.XDIClientConstants;
import xdi2.client.events.XDIDiscoverFromAuthorityEvent;
import xdi2.client.events.XDIDiscoverFromRegistryEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.PublicLinkContract;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

/**
 * Given a Cloud Name or discovery key, useful information such a Cloud Number, 
 * public keys, or additional services, can be discovered.
 * 
 * @author markus
 */
public class XDIDiscoveryClient {

	private static Logger log = LoggerFactory.getLogger(XDIDiscoveryClient.class.getName());

	public static final XDIHttpClient NEUSTAR_PROD_DISCOVERY_XDI_CLIENT = new XDIHttpClient("http://xdidiscoveryservice.xdi.net:12220/");
	public static final XDIHttpClient NEUSTAR_OTE_DISCOVERY_XDI_CLIENT = new XDIHttpClient("http://xdidiscoveryserviceote.xdi.net:12220/");

	public static final XDIHttpClient DEFAULT_XDI_CLIENT = NEUSTAR_PROD_DISCOVERY_XDI_CLIENT;
	public static final Cache DEFAULT_REGISTRY_CACHE;
	public static final Cache DEFAULT_AUTHORITY_CACHE;

	static {

		CacheManager cacheManager = CacheManager.create(XDIDiscoveryClient.class.getResourceAsStream("ehcache.xml"));
		cacheManager.addCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-registry-cache");
		cacheManager.addCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-authority-cache");
		DEFAULT_REGISTRY_CACHE = cacheManager.getCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-registry-cache");
		DEFAULT_AUTHORITY_CACHE = cacheManager.getCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-authority-cache");
	}

	private XDIHttpClient registryXdiClient;
	private Cache registryCache;
	private Cache authorityCache;

	public XDIDiscoveryClient(XDIHttpClient registryXdiClient, Cache registryCache, Cache authorityCache) {

		this.registryXdiClient = registryXdiClient;
		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
	}

	public XDIDiscoveryClient(XDIHttpClient registryXdiClient) {

		this(registryXdiClient, DEFAULT_REGISTRY_CACHE, DEFAULT_AUTHORITY_CACHE);
	}

	public XDIDiscoveryClient(String registryEndpointUri, Cache registryCache, Cache authorityCache) {

		this(new XDIHttpClient(registryEndpointUri), registryCache, authorityCache);
	}

	public XDIDiscoveryClient(String registryEndpointUri) {

		this(new XDIHttpClient(registryEndpointUri));
	}

	public XDIDiscoveryClient() {

		this(NEUSTAR_PROD_DISCOVERY_XDI_CLIENT);
	}

	public XDIDiscoveryResult discover(XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		// first discover from registry

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

		// then discover from authority

		XDIDiscoveryResult xdiDiscoveryResultAuthority = this.discoverFromAuthority(xdiDiscoveryResultRegistry.getXdiEndpointUri(), xdiDiscoveryResultRegistry.getCloudNumber(), endpointUriTypes);

		if (xdiDiscoveryResultAuthority == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from authority for " + query);

			return xdiDiscoveryResultRegistry;
		}

		if (log.isDebugEnabled()) log.debug("Discovery result from authority: " + xdiDiscoveryResultAuthority);

		// return a single discovery result from the two individual ones

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();
		xdiDiscoveryResult.initFromRegistryAndAuthorityDiscoveryResult(xdiDiscoveryResultRegistry, xdiDiscoveryResultAuthority, query, endpointUriTypes);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromRegistry(XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult();

		// check registry cache

		MessageResult registryMessageResult = null;

		DiscoveryCacheKey registryDiscoveryCacheKey = makeDiscoveryCacheKey(query, this.getRegistryXdiClient());
		Element registryMessageResultElement = null;

		if (this.getRegistryCache() != null) registryMessageResultElement = this.getRegistryCache().get(registryDiscoveryCacheKey);
		if (registryMessageResultElement != null) registryMessageResult = (MessageResult) registryMessageResultElement.getObjectValue();

		MessageEnvelope registryMessageEnvelope = null;

		if (registryMessageResult != null) {

			if (log.isDebugEnabled()) log.debug("Registry cache HIT: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");
		} else {

			if (log.isDebugEnabled()) log.debug("Registry cache MISS: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");

			// send the registry message

			registryMessageEnvelope = new MessageEnvelope();
			Message registryMessage = registryMessageEnvelope.createMessage(null);
			registryMessage.createGetOperation(XDI3Segment.fromComponent(XdiPeerRoot.createPeerRootArcXri(query)));

			try {

				XDIHttpClient registryXdiHttpClient = this.getRegistryXdiClient();

				registryMessageResult = registryXdiHttpClient.send(registryMessageEnvelope, null);
			} catch (Xdi2ClientException ex) {

				discoveryResult.initFromException(ex);

				throw ex;
			} catch (Exception ex) {

				throw new Xdi2ClientException("Cannot send XDI message to XDI registry: " + ex.getMessage(), ex, null);
			}

			// save in cache

			if (this.getRegistryCache() != null) {

				if (log.isDebugEnabled()) log.debug("Registry cache PUT: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");

				this.getRegistryCache().put(new Element(registryDiscoveryCacheKey, registryMessageResult));
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromRegistryEvent(this, registryMessageEnvelope, discoveryResult, query));
		}

		// parse the registry message result

		discoveryResult.initFromRegistryMessageResult(registryMessageEnvelope, registryMessageResult, query, endpointUriTypes);

		// done

		return discoveryResult;
	}

	public XDIDiscoveryResult discoverFromAuthority(String xdiEndpointUri, CloudNumber cloudNumber, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		XDIDiscoveryResult discoveryResult = new XDIDiscoveryResult();

		// check authority cache

		MessageResult authorityMessageResult = null;

		DiscoveryCacheKey authorityDiscoveryCacheKey = makeDiscoveryCacheKey(cloudNumber, xdiEndpointUri);
		Element authorityMessageResultElement = null;

		if (this.getAuthorityCache() != null) authorityMessageResultElement = this.getAuthorityCache().get(authorityDiscoveryCacheKey);
		if (authorityMessageResultElement != null) authorityMessageResult = (MessageResult) authorityMessageResultElement.getObjectValue();

		MessageEnvelope authorityMessageEnvelope = null;

		if (authorityMessageResult != null) {

			if (log.isDebugEnabled()) log.debug("Authority cache HIT: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");
		} else {

			if (log.isDebugEnabled()) log.debug("Authority cache MISS: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");

			// send the authority message

			authorityMessageEnvelope = new MessageEnvelope();
			Message authorityMessage = authorityMessageEnvelope.createMessage(null);
			authorityMessage.setToPeerRootXri(cloudNumber.getPeerRootXri());
			authorityMessage.setLinkContractXri(PublicLinkContract.createPublicLinkContractXri(cloudNumber.getXri()));
			authorityMessage.createGetOperation(XDI3Statement.fromRelationComponents(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE));
			authorityMessage.createGetOperation(XDI3Statement.fromRelationComponents(cloudNumber.getXri(), XDIDictionaryConstants.XRI_S_IS_REF, XDIConstants.XRI_S_VARIABLE));
			authorityMessage.createGetOperation(XDI3Util.concatXris(cloudNumber.getXri(), XDIAuthenticationConstants.XRI_S_MSG_SIG_KEYPAIR_PUBLIC_KEY));
			authorityMessage.createGetOperation(XDI3Util.concatXris(cloudNumber.getXri(), XDIAuthenticationConstants.XRI_S_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY));

			if (endpointUriTypes != null) {

				for (XDI3Segment endpointUriType : endpointUriTypes) {

					authorityMessage.createGetOperation(XDI3Util.concatXris(cloudNumber.getXri(), endpointUriType, XDIClientConstants.XRI_S_AS_URI));
				}
			}

			try {

				XDIHttpClient authorityXdiHttpClient = new XDIHttpClient(xdiEndpointUri);

				authorityMessageResult = authorityXdiHttpClient.send(authorityMessageEnvelope, null);
			} catch (Xdi2ClientException ex) {

				discoveryResult.initFromException(ex);

				throw ex;
			} catch (Exception ex) {

				throw new Xdi2ClientException("Cannot send XDI message to XDI authority: " + ex.getMessage(), ex, null);
			}

			// save in cache

			if (this.getAuthorityCache() != null) {

				if (log.isDebugEnabled()) log.debug("Authority cache PUT: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");

				this.getAuthorityCache().put(new Element(authorityDiscoveryCacheKey, authorityMessageResult));
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoveryEvent(new XDIDiscoverFromAuthorityEvent(this, authorityMessageEnvelope, discoveryResult, xdiEndpointUri));
		}

		// parse the authority message result

		discoveryResult.initFromAuthorityMessageResult(authorityMessageEnvelope, authorityMessageResult, endpointUriTypes);

		// done

		return discoveryResult;
	}

	/*
	 * Helper classes and methods
	 */

	private static DiscoveryCacheKey makeDiscoveryCacheKey(XDI3Segment query, XDIHttpClient registryXdiHttpClient) {

		return new DiscoveryCacheKey(query, registryXdiHttpClient.getEndpointUri().toString());
	}

	private static DiscoveryCacheKey makeDiscoveryCacheKey(CloudNumber cloudNumber, String xdiEndpointUri) {

		return new DiscoveryCacheKey(cloudNumber.getXri(), xdiEndpointUri);
	}

	private static class DiscoveryCacheKey implements Serializable {

		private static final long serialVersionUID = -2109761083423630152L;

		private XDI3Segment query;
		private String xdiEndpointUri;

		public DiscoveryCacheKey(XDI3Segment query, String xdiEndpointUri) {

			this.query = query;
			this.xdiEndpointUri = xdiEndpointUri;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + ((this.query == null) ? 0 : this.query.hashCode());
			result = prime * result + ((this.xdiEndpointUri == null) ? 0 : this.xdiEndpointUri.hashCode());

			return result;
		}

		@Override
		public boolean equals(Object obj) {

			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;

			DiscoveryCacheKey other = (DiscoveryCacheKey) obj;

			if (this.query == null) {

				if (other.query != null) return false;
			} else if (! this.query.equals(other.query)) return false;

			if (this.xdiEndpointUri == null) {

				if (other.xdiEndpointUri != null) return false;
			} else if (! this.xdiEndpointUri.equals(other.xdiEndpointUri)) return false;

			return true;
		}

		@Override
		public String toString() {

			return "DiscoveryCacheKey [query=" + this.query + ", xdiEndpointUri=" + this.xdiEndpointUri + "]";
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIHttpClient getRegistryXdiClient() {

		return this.registryXdiClient;
	}

	public void setRegistryXdiClient(XDIHttpClient registryXdiClient) {

		this.registryXdiClient = registryXdiClient;
	}

	public Cache getRegistryCache() {

		return this.registryCache;
	}

	public void setRegistryCache(Cache registryCache) {

		this.registryCache = registryCache;
	}

	public Cache getAuthorityCache() {

		return this.authorityCache;
	}

	public void setAuthorityCache(Cache authorityCache) {

		this.authorityCache = authorityCache;
	}
}
