package xdi2.client.util;

import java.net.URI;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.constants.XDISecurityConstants;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class XDIClientUtil {

	public static void authenticateSecretToken(CloudNumber cloudNumber, URI xdiEndpointUri, String secretToken) throws Xdi2ClientException {

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpointUri);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXDIAddress());
		message.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
		message.setLinkContractClass(RootLinkContract.class);
		message.setSecretToken(secretToken);
		message.createGetOperation(RootLinkContract.createRootLinkContractXDIAddress(cloudNumber.getXDIAddress()));

		xdiHttpClient.send(messageEnvelope);
	}

	public static PrivateKey retrieveSignaturePrivateKey(CloudNumber cloudNumber, URI xdiEndpointUri, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpointUri, secretToken, XDISecurityConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	public static PrivateKey retrieveEncryptionPrivateKey(CloudNumber cloudNumber, URI xdiEndpointUri, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpointUri, secretToken, XDISecurityConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	// TODO: deprecate this, or at least use XdiAgent?
	private static PrivateKey retrievePrivateKey(CloudNumber cloudNumber, URI xdiEndpointUri, String secretToken, XDIAddress privateKeyRelativeAddress) throws Xdi2ClientException, GeneralSecurityException {

		// request the private key from the graph

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpointUri);

		XDIAddress privateKeyAddress = XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), privateKeyRelativeAddress);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXDIAddress());
		message.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
		message.setLinkContractClass(RootLinkContract.class);
		message.setSecretToken(secretToken);
		message.createGetOperation(privateKeyAddress);

		Graph authorityResultGraph = xdiHttpClient.send(messageEnvelope).getResultGraph();

		// find authority

		XdiEntity authorityXdiEntity = XdiCommonRoot.findCommonRoot(authorityResultGraph).getXdiEntity(cloudNumber.getXDIAddress(), false);
		if (authorityXdiEntity == null) return null;

		// find private key

		PrivateKey privateKey = Keys.getPrivateKey(authorityXdiEntity, privateKeyRelativeAddress);

		// done

		return privateKey;
	}
}
