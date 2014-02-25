package xdi2.client.util;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.CloudNumber;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class XDIClientUtil {

	public static void authenticateSecretToken(CloudNumber cloudNumber, String xdiEndpoint, String secretToken) throws Xdi2ClientException {

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpoint);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXri());
		message.setToPeerRootXri(cloudNumber.getPeerRootXri());
		message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
		message.setSecretToken(secretToken);
		message.createGetOperation(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));

		xdiHttpClient.send(messageEnvelope, null);
	}

	public static PrivateKey retrieveSignaturePrivateKey(CloudNumber cloudNumber, String xdiEndpoint, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpoint, secretToken, XDIAuthenticationConstants.XRI_S_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	public static PrivateKey retrieveEncryptionPrivateKey(CloudNumber cloudNumber, String xdiEndpoint, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpoint, secretToken, XDIAuthenticationConstants.XRI_S_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	private static PrivateKey retrievePrivateKey(CloudNumber cloudNumber, String xdiEndpoint, String secretToken, XDI3Segment privateKeyRelativeAddress) throws Xdi2ClientException, GeneralSecurityException {

		// request the private key from the graph

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpoint);

		XDI3Segment privateKeyAddress = XDI3Util.concatXris(cloudNumber.getXri(), privateKeyRelativeAddress);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXri());
		message.setToPeerRootXri(cloudNumber.getPeerRootXri());
		message.setLinkContractXri(RootLinkContract.createRootLinkContractXri(cloudNumber.getXri()));
		message.setSecretToken(secretToken);
		message.createGetOperation(privateKeyAddress);

		MessageResult authorityMessageResult = xdiHttpClient.send(messageEnvelope, null);

		Graph authorityMessageResultGraph = authorityMessageResult.getGraph();

		// find authority

		XdiEntity authorityXdiEntity = XdiLocalRoot.findLocalRoot(authorityMessageResultGraph).getXdiEntity(cloudNumber.getXri(), false);
		if (authorityXdiEntity == null) return null;

		// find signature private key

		XdiAttribute signaturePrivateKeyXdiAttribute = authorityXdiEntity.getXdiAttribute(privateKeyRelativeAddress, false);
		signaturePrivateKeyXdiAttribute = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.dereference();

		XdiValue signaturePrivateKeyXdiValue = signaturePrivateKeyXdiAttribute == null ? null : signaturePrivateKeyXdiAttribute.getXdiValue(false);
		signaturePrivateKeyXdiValue = signaturePrivateKeyXdiValue == null ? null : signaturePrivateKeyXdiValue.dereference();

		Literal signaturePrivateKeyLiteral = signaturePrivateKeyXdiValue == null ? null : signaturePrivateKeyXdiValue.getContextNode().getLiteral();
		PrivateKey signaturePrivateKey = signaturePrivateKeyLiteral == null ? null : privateKeyFromPrivateKeyString(signaturePrivateKeyLiteral.getLiteralDataString());

		// done

		return signaturePrivateKey;
	}

	/*
	 * Helper methods
	 */

	public static PrivateKey privateKeyFromPrivateKeyString(String privateKeyString) throws Xdi2ClientException {

		if (privateKeyString == null) return null;

		try {

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyString));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return keyFactory.generatePrivate(keySpec);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Invalid RSA public key " + privateKeyString + ": " + ex.getMessage(), ex, null);
		}
	}

	public static PublicKey publicKeyFromPublicKeyString(String publicKeyString) throws Xdi2ClientException {

		if (publicKeyString == null) return null;

		try {

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyString));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			return keyFactory.generatePublic(keySpec);
		} catch (GeneralSecurityException ex) {

			throw new Xdi2ClientException("Invalid RSA public key " + publicKeyString + ": " + ex.getMessage(), ex, null);
		}
	}
}
