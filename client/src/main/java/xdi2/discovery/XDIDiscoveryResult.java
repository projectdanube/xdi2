package xdi2.discovery;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.MessageResult;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private XDI3Segment cloudNumber;
	private String xdiEndpointUri;
	private PublicKey publicKey;
	private Map<String, String> services;

	private MessageResult registryMessageResult;
	private MessageResult authorityMessageResult;

	public XDIDiscoveryResult() {

		this.cloudNumber = null;
		this.xdiEndpointUri = null;
		this.publicKey = null;
		this.services = null;

		this.registryMessageResult = null;
		this.authorityMessageResult = null;
	}

	public void initFromRegistryMessageResult(MessageResult registryMessageResult, XDI3Segment query) throws Xdi2ClientException {

		Graph registryMessageResultGraph = registryMessageResult.getGraph();

		// look into registry message result

		if (registryMessageResult != null) {

			// find cloud number

			XdiRoot xdiRoot = XdiLocalRoot.findLocalRoot(registryMessageResultGraph).findPeerRoot(query, false);
			if (xdiRoot == null) return;

			if (xdiRoot instanceof XdiPeerRoot && XDI3Util.isCloudNumber(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

				this.cloudNumber = ((XdiPeerRoot) xdiRoot).getXriOfPeerRoot();
			}

			xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

			if (xdiRoot instanceof XdiPeerRoot && XDI3Util.isCloudNumber(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

				this.cloudNumber = ((XdiPeerRoot) xdiRoot).getXriOfPeerRoot();
			}

			// find XDI endpoint uri

			XdiAttribute xdiEndpointUriXdiAttribute = XdiAttributeSingleton.fromContextNode(xdiRoot.getContextNode().getDeepContextNode(XDIConstants.XRI_S_XDI_URI));
			xdiEndpointUriXdiAttribute = xdiEndpointUriXdiAttribute == null ? null : xdiEndpointUriXdiAttribute.dereference();

			XdiValue xdiEndpointUriXdiValue = xdiEndpointUriXdiAttribute == null ? null : xdiEndpointUriXdiAttribute.getXdiValue(false);
			xdiEndpointUriXdiValue = xdiEndpointUriXdiValue == null ? null : xdiEndpointUriXdiValue.dereference();

			Literal xdiEndpointUriLiteral = xdiEndpointUriXdiValue == null ? null : xdiEndpointUriXdiValue.getContextNode().getLiteral();
			this.xdiEndpointUri = xdiEndpointUriLiteral == null ? null : xdiEndpointUriLiteral.getLiteralDataString();
		}

		// done

		this.registryMessageResult = registryMessageResult;
	}

	public void initFromAuthorityMessageResult(MessageResult authorityMessageResult) throws Xdi2ClientException {

		Graph authorityMessageResultGraph = authorityMessageResult.getGraph();

		// look into authority message result

		if (authorityMessageResult != null) {

			// find cloud number

			XdiRoot xdiRoot = XdiLocalRoot.findLocalRoot(authorityMessageResultGraph).getSelfPeerRoot();
			if (xdiRoot == null) return;

			if (xdiRoot instanceof XdiPeerRoot && XDI3Util.isCloudNumber(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

				this.cloudNumber = ((XdiPeerRoot) xdiRoot).getXriOfPeerRoot();
			}

			xdiRoot = xdiRoot == null ? null : xdiRoot.dereference();

			if (xdiRoot instanceof XdiPeerRoot && XDI3Util.isCloudNumber(((XdiPeerRoot) xdiRoot).getXriOfPeerRoot())) {

				this.cloudNumber = ((XdiPeerRoot) xdiRoot).getXriOfPeerRoot();
			}

			// find public key

			XdiAttribute publicKeyXdiAttribute = XdiAttributeSingleton.fromContextNode(xdiRoot.getContextNode().getDeepContextNode(XDIAuthenticationConstants.XRI_S_PUBLIC_KEY));
			publicKeyXdiAttribute = publicKeyXdiAttribute == null ? null : publicKeyXdiAttribute.dereference();

			XdiValue publicKeyXdiValue = publicKeyXdiAttribute == null ? null : publicKeyXdiAttribute.getXdiValue(false);
			publicKeyXdiValue = publicKeyXdiValue == null ? null : publicKeyXdiValue.dereference();

			Literal publicKeyLiteral = publicKeyXdiValue == null ? null : publicKeyXdiValue.getContextNode().getLiteral();
			this.publicKey = publicKeyLiteral == null ? null : publicKeyFromPublicKeyString(publicKeyLiteral.getLiteralDataString());
		}

		// done

		this.authorityMessageResult = authorityMessageResult;
	}

	/*
	 * Helper methods
	 */

	public static PublicKey publicKeyFromPublicKeyString(String publicKeyString) throws Xdi2ClientException {

		if (publicKeyString == null) return null;

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return keyFactory.generatePublic(keySpec);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Cannot parse public key: " + ex.getMessage(), ex, null);
		}
	}

	/*
	 * Getters
	 */

	public XDI3Segment getCloudNumber() {

		return this.cloudNumber;
	}

	public String getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}

	public PublicKey getPublicKey() {

		return this.publicKey;
	}

	public Map<String, String> getServices() {

		return this.services;
	}

	public MessageResult getRegistryMessageResult() {

		return this.registryMessageResult;
	}

	public MessageResult getAuthorityMessageResult() {

		return this.authorityMessageResult;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.cloudNumber + " (" + this.xdiEndpointUri + ")";
	}
}
