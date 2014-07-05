package xdi2.discovery;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import xdi2.client.constants.XDIClientConstants;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private CloudNumber cloudNumber;
	private PublicKey signaturePublicKey;
	private PublicKey encryptionPublicKey;
	private Map<XDI3Segment, String> endpointUris;

	private MessageEnvelope messageEnvelope;
	private MessageResult messageResult;

	public XDIDiscoveryResult() {

		this.cloudNumber = null;
		this.signaturePublicKey = null;
		this.encryptionPublicKey = null;
		this.endpointUris = new HashMap<XDI3Segment, String> ();

		this.messageEnvelope = null;
		this.messageResult = null;
	}

	void initFromRegistryAndAuthorityDiscoveryResult(XDIDiscoveryResult xdiDiscoveryResultRegistry, XDIDiscoveryResult xdiDiscoveryResultAuthority, XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		this.initFromRegistryMessageResult(xdiDiscoveryResultRegistry.getMessageEnvelope(), xdiDiscoveryResultRegistry.getMessageResult(), query, endpointUriTypes);
		this.initFromAuthorityMessageResult(xdiDiscoveryResultAuthority.getMessageEnvelope(), xdiDiscoveryResultAuthority.getMessageResult(), endpointUriTypes);
	}

	void initFromRegistryMessageResult(MessageEnvelope registryMessageEnvelope, MessageResult registryMessageResult, XDI3Segment query, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		this.messageEnvelope = registryMessageEnvelope;
		this.messageResult = registryMessageResult;

		// look into registry message result

		Graph registryMessageResultGraph = registryMessageResult.getGraph();

		// find cloud number

		XdiRoot xdiRoot = XdiLocalRoot.findLocalRoot(registryMessageResultGraph).getPeerRoot(query, false);
		if (xdiRoot == null) return;

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXri(((XdiPeerRoot) xdiRoot).getArcXri());
		}

		xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXri(((XdiPeerRoot) xdiRoot).getArcXri());
		}

		// find XDI endpoint uri

		endpointUriTypes = new XDI3Segment[] { XDIClientConstants.XRI_S_AS_XDI };

		for (XDI3Segment endpointUriType : endpointUriTypes) {

			XDI3Segment endpointUriXdiAttributeAddress = XDI3Util.concatXris(endpointUriType, XDIClientConstants.XRI_SS_AS_URI);
			XdiAttribute endpointUriXdiAttribute = xdiRoot.getXdiAttributeSingleton(endpointUriXdiAttributeAddress, false);
			if (endpointUriXdiAttribute == null) continue;

			endpointUriXdiAttribute = endpointUriXdiAttribute.dereference();

			XdiValue endpointUriXdiValue = endpointUriXdiAttribute.getXdiValue(false);
			if (endpointUriXdiValue == null) continue;

			Literal endpointUriLiteral = endpointUriXdiValue.getLiteral();
			if (endpointUriLiteral == null) continue;

			String endpointUri = endpointUriLiteral.getLiteralDataString();
			if (endpointUri == null) continue;

			this.endpointUris.put(endpointUriType, endpointUri);
		}
	}

	void initFromAuthorityMessageResult(MessageEnvelope authorityMessageEnvelope, MessageResult authorityMessageResult, XDI3Segment[] endpointUriTypes) throws Xdi2ClientException {

		this.messageEnvelope = authorityMessageEnvelope;
		this.messageResult = authorityMessageResult;

		// look into authority message result

		Graph authorityMessageResultGraph = authorityMessageResult.getGraph();

		// find cloud number

		XdiRoot xdiRoot = XdiLocalRoot.findLocalRoot(authorityMessageResultGraph).getSelfPeerRoot();
		if (xdiRoot == null) xdiRoot = XdiLocalRoot.findLocalRoot(authorityMessageResultGraph);
		if (xdiRoot == null) return;

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXri(((XdiPeerRoot) xdiRoot).getArcXri());
		}

		xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

		if (xdiRoot instanceof XdiPeerRoot && CloudNumber.isValid(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

			this.cloudNumber = CloudNumber.fromPeerRootXri(((XdiPeerRoot) xdiRoot).getArcXri());
		}

		// find cloud names


		// find authority

		XdiEntity authorityXdiEntity = XdiLocalRoot.findLocalRoot(authorityMessageResultGraph).getXdiEntity(this.cloudNumber.getXri(), false);
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

		// find endpoint uris

		if (endpointUriTypes != null) {

			for (XDI3Segment endpointUriType : endpointUriTypes) {

				XDI3Segment endpointUriXdiAttributeAddress = XDI3Util.concatXris(endpointUriType, XDIClientConstants.XRI_SS_AS_URI);
				XdiAttribute endpointUriXdiAttribute = authorityXdiEntity.getXdiAttributeSingleton(endpointUriXdiAttributeAddress, false);
				if (endpointUriXdiAttribute == null) continue;

				endpointUriXdiAttribute = endpointUriXdiAttribute.dereference();

				XdiValue endpointUriXdiValue = endpointUriXdiAttribute.getXdiValue(false);
				if (endpointUriXdiValue == null) continue;

				Literal endpointUriLiteral = endpointUriXdiValue.getLiteral();
				if (endpointUriLiteral == null) continue;

				String endpointUri = endpointUriLiteral.getLiteralDataString();
				if (endpointUri == null) continue;

				this.endpointUris.put(endpointUriType, endpointUri);
			}
		}
	}

	void initFromException(Xdi2ClientException ex) {

		// done

		this.messageResult = ex.getErrorMessageResult();
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

	public Map<XDI3Segment, String> getEndpointUris() {

		return this.endpointUris;
	}

	public String getXdiEndpointUri() {

		return this.getEndpointUris().get(XDIClientConstants.XRI_S_AS_XDI);
	}

	public String getDefaultEndpointUri() {

		return this.getEndpointUris().get(null);
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public MessageResult getMessageResult() {

		return this.messageResult;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getCloudNumber() + " (" + this.getXdiEndpointUri() + ")";
	}
}
