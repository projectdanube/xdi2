package xdi2.discovery;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.MessageResult;

public class XDIDiscoveryResult implements Serializable {

	private static final long serialVersionUID = -1141807747864855392L;

	private XDI3Segment query;
	private XDI3Segment cloudNumber;
	private String xdiEndpointUri;
	private PublicKey publicKey;
	private Map<String, String> services;

	private MessageResult registryMessageResult;
	private MessageResult authorityMessageResult;

	public XDIDiscoveryResult(XDI3Segment query) {

		this.query = query;
		this.cloudNumber = null;
		this.xdiEndpointUri = null;
		this.publicKey = null;
		this.services = null;

		this.registryMessageResult = null;
		this.authorityMessageResult = null;
	}

	public void initFromRegistryMessageResult(MessageResult registryMessageResult) throws Xdi2ClientException {

		Graph registryMessageResultGraph = registryMessageResult.getGraph();

		// look into registry message result

		if (registryMessageResult != null) {

			// find cloud number

			XDI3SubSegment peerRootArcXri = XdiPeerRoot.createPeerRootArcXri(this.query);

			ContextNode peerRootContextNode = registryMessageResultGraph.getDeepContextNode(XDI3Segment.fromComponent(peerRootArcXri));
			ContextNode peerRootReferenceContextNode = peerRootContextNode == null ? null : Equivalence.getReferenceContextNode(peerRootContextNode);

			XDI3SubSegment cloudNumberPeerRootArcXri = peerRootReferenceContextNode == null ? null : peerRootReferenceContextNode.getXri().getFirstSubSegment();

			this.cloudNumber = cloudNumberPeerRootArcXri == null ? null : XdiPeerRoot.getXriOfPeerRootArcXri(cloudNumberPeerRootArcXri);
			if (this.cloudNumber == null && XDI3Util.isCloudNumber(this.query)) cloudNumber = this.query;

			// find XDI endpoint uri

			ContextNode xdiEndpointUriContextNode;

			if (cloudNumberPeerRootArcXri != null)
				xdiEndpointUriContextNode = registryMessageResultGraph.getDeepContextNode(XDI3Segment.create(cloudNumberPeerRootArcXri + "$xdi<$uri>"));
			else
				xdiEndpointUriContextNode = registryMessageResultGraph.getDeepContextNode(XDI3Segment.create(peerRootArcXri + "$xdi<$uri>"));

			ContextNode xdiEndpointUriReferenceContextNode = xdiEndpointUriContextNode == null ? null : Equivalence.getReferenceContextNode(xdiEndpointUriContextNode);
			XdiValue xdiEndpointUriXdiValue = null;

			if (xdiEndpointUriReferenceContextNode != null) 
				xdiEndpointUriXdiValue = XdiAbstractAttribute.fromContextNode(xdiEndpointUriReferenceContextNode).getXdiValue(false);
			else if (xdiEndpointUriContextNode != null) 
				xdiEndpointUriXdiValue = XdiAbstractAttribute.fromContextNode(xdiEndpointUriContextNode).getXdiValue(false);

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

			XDI3Segment xri = authorityMessageResult.getGraph().getDeepRelation(XDIConstants.XRI_S_ROOT, XDIDictionaryConstants.XRI_S_IS_REF).getTargetContextNodeXri();

			// find public key

			ContextNode publicKeyContextNode = authorityMessageResultGraph.getDeepContextNode(XDI3Segment.create("$public<$key>"));

			ContextNode publicKeyReferenceContextNode = publicKeyContextNode == null ? null : Equivalence.getReferenceContextNode(publicKeyContextNode);
			XdiValue publicKeyXdiValue = null;

			if (publicKeyReferenceContextNode != null) 
				publicKeyXdiValue = XdiAbstractAttribute.fromContextNode(publicKeyReferenceContextNode).getXdiValue(false);
			else if (publicKeyContextNode != null) 
				publicKeyXdiValue = XdiAbstractAttribute.fromContextNode(publicKeyContextNode).getXdiValue(false);

			Literal publicKeyLiteral = publicKeyXdiValue == null ? null : publicKeyXdiValue.getContextNode().getLiteral();
			String publicKeyString = publicKeyLiteral == null ? null : publicKeyLiteral.getLiteralDataString();
			this.publicKey = publicKeyFromPublicKeyString(publicKeyString);
		}

		// done

		this.authorityMessageResult = authorityMessageResult;
	}

	/*
	 * Helper methods
	 */

	public static PublicKey publicKeyFromPublicKeyString(String publicKeyString) throws Xdi2ClientException {

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return (PublicKey) keyFactory.generatePublic(keySpec);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Cannot parse public key: " + ex.getMessage(), ex, null);
		}
	}

	/*
	 * Getters
	 */

	public XDI3Segment getQuery() {

		return this.query;
	}

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
