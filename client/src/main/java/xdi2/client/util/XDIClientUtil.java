package xdi2.client.util;

import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.linkcontracts.instance.RootLinkContract;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class XDIClientUtil {

	public static void authenticateSecretToken(CloudNumber cloudNumber, URL xdiEndpointUrl, String secretToken) throws Xdi2ClientException {

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpointUrl);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXDIAddress());
		message.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
		message.setLinkContract(RootLinkContract.class);
		message.setSecretToken(secretToken);
		message.createGetOperation(RootLinkContract.createRootLinkContractXDIAddress(cloudNumber.getXDIAddress()));

		xdiHttpClient.send(messageEnvelope, null);
	}

	public static PrivateKey retrieveSignaturePrivateKey(CloudNumber cloudNumber, URL xdiEndpointUrl, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpointUrl, secretToken, XDIAuthenticationConstants.XDI_ADD_MSG_SIG_KEYPAIR_PRIVATE_KEY);
	}

	public static PrivateKey retrieveEncryptionPrivateKey(CloudNumber cloudNumber, URL xdiEndpointUrl, String secretToken) throws Xdi2ClientException, GeneralSecurityException {

		return retrievePrivateKey(cloudNumber, xdiEndpointUrl, secretToken, XDIAuthenticationConstants.XDI_ADD_MSG_ENCRYPT_KEYPAIR_PRIVATE_KEY);
	}

	private static PrivateKey retrievePrivateKey(CloudNumber cloudNumber, URL xdiEndpointUrl, String secretToken, XDIAddress privateKeyRelativeAddress) throws Xdi2ClientException, GeneralSecurityException {

		// request the private key from the graph

		XDIHttpClient xdiHttpClient = new XDIHttpClient(xdiEndpointUrl);

		XDIAddress privateKeyAddress = XDIAddressUtil.concatXDIAddresses(cloudNumber.getXDIAddress(), privateKeyRelativeAddress);

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(cloudNumber.getXDIAddress());
		message.setToPeerRootXDIArc(cloudNumber.getPeerRootXDIArc());
		message.setLinkContract(RootLinkContract.class);
		message.setSecretToken(secretToken);
		message.createGetOperation(privateKeyAddress);

		MessageResult authorityMessageResult = xdiHttpClient.send(messageEnvelope, null);

		Graph authorityMessageResultGraph = authorityMessageResult.getGraph();

		// find authority

		XdiEntity authorityXdiEntity = XdiCommonRoot.findCommonRoot(authorityMessageResultGraph).getXdiEntity(cloudNumber.getXDIAddress(), false);
		if (authorityXdiEntity == null) return null;

		// find private key

		PrivateKey privateKey = Keys.getPrivateKey(authorityXdiEntity, privateKeyRelativeAddress);

		// done

		return privateKey;
	}
}
