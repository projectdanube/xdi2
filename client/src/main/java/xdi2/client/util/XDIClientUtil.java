package xdi2.client.util;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.linkcontracts.RootLinkContract;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiLocalRoot;
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

		// find private key

		PrivateKey privateKey = Keys.getPrivateKey(authorityXdiEntity, privateKeyRelativeAddress);

		// done

		return privateKey;
	}
}
