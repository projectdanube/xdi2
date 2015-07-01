package xdi2.discovery;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import xdi2.client.constants.XDIClientConstants;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.exceptions.Xdi2DiscoveryException;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private CloudNumber cloudNumber;
	private PublicKey signaturePublicKey;
	private PublicKey encryptionPublicKey;
	private Map<XDIAddress, URI> endpointUris;
	private URL xdiEndpointUrl;

	private MessageEnvelope messageEnvelope;
	private MessagingResponse messagingResponse;

	public XDIDiscoveryResult() {

		this.cloudNumber = null;
		this.signaturePublicKey = null;
		this.encryptionPublicKey = null;
		this.endpointUris = new HashMap<XDIAddress, URI> ();
		this.xdiEndpointUrl = null;

		this.messageEnvelope = null;
		this.messagingResponse = null;
	}

	void initFromRegistryAndAuthorityDiscoveryResult(XDIDiscoveryResult xdiDiscoveryResultRegistry, XDIDiscoveryResult xdiDiscoveryResultAuthority, XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2ClientException {

		this.initFromRegistryMessagingResponse(xdiDiscoveryResultRegistry.getMessageEnvelope(), xdiDiscoveryResultRegistry.getMessagingResponse(), query, endpointUriTypes);
		this.initFromAuthorityMessagingResponse(xdiDiscoveryResultAuthority.getMessageEnvelope(), xdiDiscoveryResultAuthority.getMessagingResponse(), endpointUriTypes);
	}

	void initFromRegistryMessagingResponse(MessageEnvelope registryMessageEnvelope, MessagingResponse registryMessagingResponse, XDIAddress query, XDIAddress[] endpointUriTypes) throws Xdi2ClientException {

		this.messageEnvelope = registryMessageEnvelope;
		this.messagingResponse = registryMessagingResponse;

		Graph registryResultGraph = registryMessagingResponse.getResultGraph();

		// find cloud number

		XdiRoot xdiRoot = XdiCommonRoot.findCommonRoot(registryResultGraph).getPeerRoot(query, false);
		if (xdiRoot == null) return;

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXDIAddressOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXDIArc(((XdiPeerRoot) xdiRoot).getXDIArc());
		}

		xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXDIAddressOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXDIArc(((XdiPeerRoot) xdiRoot).getXDIArc());
		}

		// only look for the XDI endpoint URI

		endpointUriTypes = new XDIAddress[] { XDIClientConstants.XDI_ADD_AS_XDI };

		// init endpoint URIs

		initEndpointUris(xdiRoot, endpointUriTypes);
	}

	void initFromAuthorityMessagingResponse(MessageEnvelope authorityMessageEnvelope, MessagingResponse authorityMessagingResponse, XDIAddress[] endpointUriTypes) throws Xdi2ClientException {

		this.messageEnvelope = authorityMessageEnvelope;
		this.messagingResponse = authorityMessagingResponse;

		Graph authorityResultGraph = authorityMessagingResponse.getResultGraph();

		// find cloud number

		XdiRoot xdiRoot = XdiCommonRoot.findCommonRoot(authorityResultGraph).getSelfPeerRoot();
		if (xdiRoot == null) xdiRoot = XdiCommonRoot.findCommonRoot(authorityResultGraph);
		if (xdiRoot == null) return;

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXDIAddressOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXDIArc(((XdiPeerRoot) xdiRoot).getXDIArc());
		}

		xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXDIAddressOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXDIArc(((XdiPeerRoot) xdiRoot).getXDIArc());
		}

		// find cloud names


		// find authority

		XdiEntity authorityXdiEntity = XdiCommonRoot.findCommonRoot(authorityResultGraph).getXdiEntity(this.cloudNumber.getXDIAddress(), false);
		if (authorityXdiEntity == null) return;

		// find signature public key

		try {

			this.signaturePublicKey = Keys.getSignaturePublicKey(authorityXdiEntity);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Invalid signature public key: " + ex.getMessage(), ex, null);
		}

		// find encryption public key

		try {

			this.encryptionPublicKey = Keys.getEncryptionPublicKey(authorityXdiEntity);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Invalid encryption public key: " + ex.getMessage(), ex, null);
		}

		// init endpoint uris

		this.initEndpointUris(authorityXdiEntity, endpointUriTypes);
	}

	void initFromException(Xdi2ClientException ex) {

		// done

		this.messagingResponse = ex.getMessagingResponse();
	}

	/*
	 * Getters
	 */

	public CloudNumber getCloudNumber() {

		return this.cloudNumber;
	}

	public PublicKey getSignaturePublicKey() {

		return this.signaturePublicKey;
	}

	public PublicKey getEncryptionPublicKey() {

		return this.encryptionPublicKey;
	}

	public Map<XDIAddress, URI> getEndpointUris() {

		return this.endpointUris;
	}

	public URL getXdiEndpointUrl() {

		return this.xdiEndpointUrl;
	}

	public URI getDefaultEndpointUri() {

		return this.getEndpointUris().get(null);
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}

	/*
	 * Helper methods
	 */

	private void initEndpointUris(XdiContext<?> xdiContext, XDIAddress[] endpointUriTypes) throws Xdi2DiscoveryException {

		if (endpointUriTypes == null) {

			this.endpointUris.clear();
			return;
		}

		for (XDIAddress endpointUriType : endpointUriTypes) {

			XDIAddress endpointUriXdiAttributeAddress = XDIAddressUtil.concatXDIAddresses(endpointUriType, XDIClientConstants.XDI_ARC_AS_URI);
			XdiAttribute endpointUriXdiAttribute = xdiContext.getXdiAttributeSingleton(endpointUriXdiAttributeAddress, false);
			if (endpointUriXdiAttribute == null) continue;

			endpointUriXdiAttribute = endpointUriXdiAttribute.dereference();

			LiteralNode endpointUriLiteral = endpointUriXdiAttribute.getLiteralNode();
			if (endpointUriLiteral == null) continue;

			String endpointUri = endpointUriLiteral.getLiteralDataString();
			if (endpointUri == null) continue;

			this.endpointUris.put(endpointUriType, URI.create(endpointUri));
		}

		// XDI endpoint URI/URL

		URI xdiEndpointUri = this.getEndpointUris().get(XDIClientConstants.XDI_ADD_AS_XDI);

		try {

			this.xdiEndpointUrl = xdiEndpointUri == null ? null : xdiEndpointUri.toURL();
		} catch (MalformedURLException ex) {

			throw new Xdi2DiscoveryException("Malformed XDI endpoint URL: " + xdiEndpointUri);
		}
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getCloudNumber() + " (" + this.getXdiEndpointUrl() + ")";
	}
}
