package xdi2.discovery;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.constants.XDIClientConstants;
import xdi2.client.events.XDIDiscoverFromAuthorityEvent;
import xdi2.client.events.XDIDiscoverFromRegistryEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.exceptions.Xdi2DiscoveryException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

/**
 * Given a Cloud Name or discovery key, useful information such a Cloud Number, 
 * public keys, or additional services, can be discovered.
 * 
 * @author markus
 */
public class XDIDiscoveryClient {

	private static Logger log = LoggerFactory.getLogger(XDIDiscoveryClient.class.getName());

	public static final XDIHttpClient NEUSTAR_PROD_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient NEUSTAR_OTE_DISCOVERY_XDI_CLIENT;
	public static final XDIHttpClient NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT;

	public static final XDIDiscoveryClient NEUSTAR_PROD_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient NEUSTAR_OTE_DISCOVERY_CLIENT;
	public static final XDIDiscoveryClient NEUSTAR_STAGE_DISCOVERY_CLIENT;

	public static final XDIHttpClient DEFAULT_XDI_CLIENT;
	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT;
	public static final Cache DEFAULT_REGISTRY_CACHE;
	public static final Cache DEFAULT_AUTHORITY_CACHE;

	private XDIHttpClient registryXdiClient;
	private Cache registryCache;
	private Cache authorityCache;

	static {

		CacheManager cacheManager = CacheManager.create(XDIDiscoveryClient.class.getResourceAsStream("ehcache.xml"));
		cacheManager.addCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-registry-cache");
		cacheManager.addCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-authority-cache");
		DEFAULT_REGISTRY_CACHE = cacheManager.getCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-registry-cache");
		DEFAULT_AUTHORITY_CACHE = cacheManager.getCache(XDIDiscoveryClient.class.getCanonicalName() + "-default-authority-cache");

		try {

			NEUSTAR_PROD_DISCOVERY_XDI_CLIENT = new XDIHttpClient(new URL("https://xdidiscoveryservice.xdi.net/"));
			NEUSTAR_OTE_DISCOVERY_XDI_CLIENT = new XDIHttpClient(new URL("https://xdidiscoveryserviceote.xdi.net/"));
			NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT = new XDIHttpClient(new URL("https://xdidiscovery-stg.cloudnames.biz/"));
		} catch (MalformedURLException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}

		NEUSTAR_PROD_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_PROD_DISCOVERY_XDI_CLIENT);
		NEUSTAR_OTE_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_OTE_DISCOVERY_XDI_CLIENT);
		NEUSTAR_STAGE_DISCOVERY_CLIENT = new XDIDiscoveryClient(NEUSTAR_STAGE_DISCOVERY_XDI_CLIENT);

		DEFAULT_XDI_CLIENT = NEUSTAR_PROD_DISCOVERY_XDI_CLIENT;
		DEFAULT_DISCOVERY_CLIENT = NEUSTAR_PROD_DISCOVERY_CLIENT;
	}

	public XDIDiscoveryClient(XDIHttpClient registryXdiClient, Cache registryCache, Cache authorityCache) {

		this.registryXdiClient = registryXdiClient;
		this.registryCache = registryCache;
		this.authorityCache = authorityCache;
	}

	public XDIDiscoveryClient(XDIHttpClient registryXdiClient) {

		this(registryXdiClient, DEFAULT_REGISTRY_CACHE, DEFAULT_AUTHORITY_CACHE);
	}

	public XDIDiscoveryClient(URL registryEndpointUrl, Cache registryCache, Cache authorityCache) {

		this(new XDIHttpClient(registryEndpointUrl), registryCache, authorityCache);
	}

	public XDIDiscoveryClient(URL registryEndpointUrl) {

		this(new XDIHttpClient(registryEndpointUrl));
	}

	public XDIDiscoveryClient(String registryEndpointUrl) {

		this(new XDIHttpClient(registryEndpointUrl));
	}

	public XDIDiscoveryClient() {

		this(NEUSTAR_PROD_DISCOVERY_XDI_CLIENT);
	}

	public XDIDiscoveryResult discover(XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		// first discover from registry

		XDIDiscoveryResult xdiDiscoveryResultRegistry = this.discoverFromRegistry(query, endpointUriTypes);

		if (xdiDiscoveryResultRegistry == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from registry for " + query);
			return null;
		}

		if (xdiDiscoveryResultRegistry.getXdiEndpointUrl() == null || xdiDiscoveryResultRegistry.getCloudNumber() == null) {

			if (log.isDebugEnabled()) log.debug("No XDI endpoint URI or cloud number from registry for " + query);
			return xdiDiscoveryResultRegistry;
		}

		// then discover from authority

		XDIDiscoveryResult xdiDiscoveryResultAuthority = this.discoverFromAuthority(xdiDiscoveryResultRegistry.getXdiEndpointUrl(), xdiDiscoveryResultRegistry.getCloudNumber(), endpointUriTypes);

		if (xdiDiscoveryResultAuthority == null) {

			if (log.isDebugEnabled()) log.debug("No discovery result from authority for " + query);
			return xdiDiscoveryResultRegistry;
		}

		// return a single discovery result from the two individual ones

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();
		xdiDiscoveryResult.initFromRegistryAndAuthorityDiscoveryResult(xdiDiscoveryResultRegistry, xdiDiscoveryResultAuthority, query, endpointUriTypes);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromRegistry(XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();

		// check registry cache

		Graph registryResultGraph = null;

		DiscoveryCacheKey registryDiscoveryCacheKey = DiscoveryCacheKey.build(query, this.getRegistryXdiClient(), null);
		Element registryMessageResultElement = null;

		if (this.getRegistryCache() != null) registryMessageResultElement = this.getRegistryCache().get(registryDiscoveryCacheKey);
		if (registryMessageResultElement != null) registryResultGraph = (Graph) registryMessageResultElement.getObjectValue();

		MessageEnvelope registryMessageEnvelope = null;

		if (registryResultGraph != null) {

			if (log.isDebugEnabled()) log.debug("Registry cache HIT: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");
		} else {

			if (log.isDebugEnabled()) log.debug("Registry cache MISS: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");

			// send the registry message

			registryMessageEnvelope = new MessageEnvelope();
			Message registryMessage = registryMessageEnvelope.createMessage(null);
			registryMessage.createGetOperation(XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(query)));

			try {

				XDIHttpClient registryXdiHttpClient = this.getRegistryXdiClient();

				registryResultGraph = registryXdiHttpClient.send(registryMessageEnvelope).getResultGraph();
			} catch (Xdi2ClientException ex) {

				xdiDiscoveryResult.initFromException(ex);
				throw ex;
			} catch (Exception ex) {

				throw new Xdi2DiscoveryException("Cannot send XDI message to XDI registry: " + ex.getMessage(), ex);
			}

			// save in cache

			if (this.getRegistryCache() != null) {

				if (log.isDebugEnabled()) log.debug("Registry cache PUT: " + registryDiscoveryCacheKey + " (" + this.getRegistryCache() + ")");
				this.getRegistryCache().put(new Element(registryDiscoveryCacheKey, registryResultGraph));
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoverEvent(new XDIDiscoverFromRegistryEvent(this, registryMessageEnvelope, xdiDiscoveryResult, query));
		}

		// init the registry message result

		xdiDiscoveryResult.initFromRegistryResultGraph(registryMessageEnvelope, registryResultGraph, query, endpointUriTypes);

		// cloud number check

		if (CloudNumber.isValid(query) && xdiDiscoveryResult.getCloudNumber() != null) {

			if (! xdiDiscoveryResult.getCloudNumber().getXDIAddress().equals(query)) throw new Xdi2DiscoveryException("Queried cloud number " + query + " does not match discovered cloud number " + xdiDiscoveryResult.getCloudNumber().getXDIAddress());
		}

		// done

		if (log.isDebugEnabled()) log.debug("Discovery result from registry: " + xdiDiscoveryResult);

		return xdiDiscoveryResult;
	}

	public XDIDiscoveryResult discoverFromAuthority(URL xdiEndpointUrl, CloudNumber cloudNumber, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException, Xdi2ClientException {

		XDIDiscoveryResult xdiDiscoveryResult = new XDIDiscoveryResult();

		// check authority cache

		Graph authorityResultGraph = null;

		DiscoveryCacheKey authorityDiscoveryCacheKey = DiscoveryCacheKey.build(cloudNumber, xdiEndpointUrl, endpointUriTypes);
		Element authorityResultGraphElement = null;

		if (this.getAuthorityCache() != null) authorityResultGraphElement = this.getAuthorityCache().get(authorityDiscoveryCacheKey);
		if (authorityResultGraphElement != null) authorityResultGraph = (Graph) authorityResultGraphElement.getObjectValue();

		MessageEnvelope authorityMessageEnvelope = null;

		if (authorityResultGraph != null) {

			if (log.isDebugEnabled()) log.debug("Authority cache HIT: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");
		} else {

			if (log.isDebugEnabled()) log.debug("Authority cache MISS: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");

			// send the authority message

			authorityMessageEnvelope = new MessageEnvelope();
			Message authorityMessage = authorityMessageEnvelope.createMessage(null);
			authorityMessage.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
			authorityMessage.setLinkContract(PublicLinkContract.class);
			authorityMessage.createGetOperation(XDIStatement.fromRelationComponents(XDIConstants.XDI_ADD_ROOT, XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_VARIABLE));
			authorityMessage.createGetOperation(XDIStatement.fromRelationComponents(cloudNumber.getXDIAddress(), XDIDictionaryConstants.XDI_ADD_IS_REF, XDIConstants.XDI_ADD_VARIABLE));
			authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PUBLIC_KEY));
			authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), XDIAuthenticationConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PUBLIC_KEY));

			if (endpointUriTypes != null) {

				for (XDIAddress endpointUriType : endpointUriTypes) {

					authorityMessage.createGetOperation(XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), endpointUriType, XDIClientConstants.XDI_ADD_AS_URI));
				}
			}

			try {

				XDIHttpClient authorityXdiHttpClient = new XDIHttpClient(xdiEndpointUrl);

				authorityResultGraph = authorityXdiHttpClient.send(authorityMessageEnvelope).getResultGraph();
			} catch (Xdi2ClientException ex) {

				xdiDiscoveryResult.initFromException(ex);
				throw ex;
			} catch (Exception ex) {

				throw new Xdi2DiscoveryException("Cannot send XDI message to XDI authority: " + ex.getMessage(), ex);
			}

			// save in cache

			if (this.getAuthorityCache() != null) {

				if (log.isDebugEnabled()) log.debug("Authority cache PUT: " + authorityDiscoveryCacheKey + " (" + this.getAuthorityCache() + ")");
				this.getAuthorityCache().put(new Element(authorityDiscoveryCacheKey, authorityResultGraph));
			}

			// fire event

			this.getRegistryXdiClient().fireDiscoverEvent(new XDIDiscoverFromAuthorityEvent(this, authorityMessageEnvelope, xdiDiscoveryResult, xdiEndpointUrl));
		}

		// init the authority message result

		xdiDiscoveryResult.initFromAuthorityResultGraph(authorityMessageEnvelope, authorityResultGraph, endpointUriTypes);

		// cloud number check

		if (! xdiDiscoveryResult.getCloudNumber().getXDIAddress().equals(cloudNumber.getXDIAddress())) throw new Xdi2DiscoveryException("Queried cloud number " + cloudNumber.getXDIAddress() + " does not match discovered cloud number " + xdiDiscoveryResult.getCloudNumber().getXDIAddress());

		// done

		if (log.isDebugEnabled()) log.debug("Discovery result from authority: " + xdiDiscoveryResult);

		return xdiDiscoveryResult;
	}

	/*
	 * Helper classes and methods
	 */

	private static class DiscoveryCacheKey implements Serializable {

		private static final long serialVersionUID = -2109761083423630152L;

		private XDIAddress query;
		private URL xdiEndpointUrl;
		private Set<XDIAddress> endpointUriTypes;

		public DiscoveryCacheKey(XDIAddress query, URL xdiEndpointUrl, Set<XDIAddress> endpointUriTypes) {

			this.query = query;
			this.xdiEndpointUrl = xdiEndpointUrl;
			this.endpointUriTypes = endpointUriTypes;
		}

		private static DiscoveryCacheKey build(XDIAddress query, XDIHttpClient registryXdiHttpClient, XDIAddress[] endpointUriTypes) {

			return new DiscoveryCacheKey(query, registryXdiHttpClient.getXdiEndpointUrl(), endpointUriTypes == null ? null : new HashSet<XDIAddress> (Arrays.asList(endpointUriTypes)));
		}

		private static DiscoveryCacheKey build(CloudNumber cloudNumber, URL xdiEndpointUri, XDIAddress[] endpointUriTypes) {

			return new DiscoveryCacheKey(cloudNumber.getXDIAddress(), xdiEndpointUri, endpointUriTypes == null ? null : new HashSet<XDIAddress> (Arrays.asList(endpointUriTypes)));
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;

			result = prime * result + ((this.query == null) ? 0 : this.query.hashCode());
			result = prime * result + ((this.xdiEndpointUrl == null) ? 0 : this.xdiEndpointUrl.hashCode());
			result = prime * result + ((this.endpointUriTypes == null) ? 0 : this.endpointUriTypes.hashCode());

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

			if (this.xdiEndpointUrl == null) {

				if (other.xdiEndpointUrl != null) return false;
			} else if (! this.xdiEndpointUrl.equals(other.xdiEndpointUrl)) return false;

			if (this.endpointUriTypes == null) {

				if (other.endpointUriTypes != null) return false;
			} else if (! this.endpointUriTypes.equals(other.endpointUriTypes)) return false;

			return true;
		}

		@Override
		public String toString() {

			return "DiscoveryCacheKey [query=" + this.query + ", xdiEndpointUri=" + this.xdiEndpointUrl + ", endpointUriTypes=" + this.endpointUriTypes + "]";
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
