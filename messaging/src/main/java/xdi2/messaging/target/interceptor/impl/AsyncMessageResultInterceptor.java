package xdi2.messaging.target.interceptor.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.signatures.Signature;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.request.RequestMessage;
import xdi2.messaging.response.ResponseMessage;
import xdi2.messaging.response.ResponseMessageEnvelope;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractMessageInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;

/**
 * This contributor can add metadata to a message result, e.g. a timestamp, 
 * a TO peer root XRI, and a signature.
 */
@ContributorMount(contributorAddresses={""})
public class AsyncMessageResultInterceptor extends AbstractMessageInterceptor implements Prototype<AsyncMessageResultInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(AsyncMessageResultInterceptor.class);

	private Graph privateKeyGraph;

	public AsyncMessageResultInterceptor(Graph privateKeyGraph) {

		this.privateKeyGraph = privateKeyGraph;
	}

	public AsyncMessageResultInterceptor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public AsyncMessageResultInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		AsyncMessageResultInterceptor contributor = new AsyncMessageResultInterceptor();

		// set the private key graph

		contributor.setPrivateKeyGraph(this.getPrivateKeyGraph());

		// done

		return contributor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getPrivateKeyGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setPrivateKeyGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getPrivateKeyGraph() == null) throw new Xdi2MessagingException("No private key graph.", null, null);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult after(RequestMessage message, Graph resultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress senderXDIAddress = message.getSenderXDIAddress();
		XDIAddress toXDIAddress = message.getToXDIAddress();

		if (toXDIAddress == null) {

			if (log.isDebugEnabled()) log.debug("No TO peer root found, cannot construct response message.");
			return InterceptorResult.DEFAULT;
		}

		ResponseMessageEnvelope responseMessageEnvelope = new ResponseMessageEnvelope();
		ResponseMessage responseMessage = responseMessageEnvelope.createMessage(toXDIAddress);
		responseMessage.setToXDIAddress(senderXDIAddress);

		// sign response message?

		Signature<?, ?> signature = null;

		Iterator<Signature<?, ?>> signatures = message.getSignatures();
		if (signatures != null && signatures.hasNext()) signature = signatures.next();

		if (signature != null) {

			String digestAlgorithm = signature.getDigestAlgorithm();
			Integer digestLength = signature.getDigestLength();
			String keyAlgorithm = signature.getKeyAlgorithm();
			Integer keyLength = signature.getKeyLength();

			Signature<?, ?> responseSignature = responseMessage.createSignature(digestAlgorithm, digestLength.intValue(), keyAlgorithm, keyLength.intValue(), true);
		}

		// done

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Sub-Contributors
	 */

	/*	@ContributorMount(contributorAddresses={"<$t>"})
	private class TimestampContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			if (relativeTargetAddress == null) return ContributorResult.DEFAULT;

			// create timestamp

			Date timestamp = new Date();

			// add it to the message result

			Timestamps.setContextNodeTimestamp(messageResult.getGraph().getRootContextNode(), timestamp);

			// done

			return ContributorResult.DEFAULT;
		}
	}

	@ContributorMount(contributorAddresses={""})
	private class ToPeerRootAddressContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			XDIAddress XDIaddress = relativeTargetStatement.getRelationXDIAddress();
			XDIAddress targetContextNodeXDIAddress = relativeTargetStatement.getTargetContextNodeXDIAddress();

			// check if applicable

			if (! XDIaddress.equals(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC)) return ContributorResult.DEFAULT;
			if (! VariableUtil.isVariable(targetContextNodeXDIAddress)) return ContributorResult.DEFAULT;

			// determine TO peer root XRI

			XDIAddress toPeerRootAddress = XDIAddress.fromComponent(XdiPeerRoot.createPeerRootXDIArc(operation.getSenderXDIAddress()));

			// add it to the message result

			messageResult.getGraph().getRootContextNode().setRelation(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC, toPeerRootAddress);

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET;
		}
	}

	@ContributorMount(contributorAddresses={"<$sig>"})
	private class SignatureContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			XDIAddress XDIaddress = relativeTargetStatement.getRelationXDIAddress();
			XDIAddress targetContextNodeXDIAddress = relativeTargetStatement.getTargetContextNodeXDIAddress();

			// check if applicable

			if (! XDIaddress.equals(XDIDictionaryConstants.XDI_ADD_IS_TYPE)) return ContributorResult.DEFAULT;

			// check parameters

			XDIAddress dataTypeXDIAddress = targetContextNodeXDIAddress;

			String digestAlgorithm;
			Integer digestLength;
			String keyAlgorithm;
			Integer keyLength;

			digestAlgorithm = Signatures.getDigestAlgorithm(dataTypeXDIAddress);
			if (digestAlgorithm == null) throw new Xdi2MessagingException("Invalid digest algorithm: " + dataTypeXDIAddress, null, executionContext);

			digestLength = Signatures.getDigestLength(dataTypeXDIAddress);
			if (digestLength == null) throw new Xdi2MessagingException("Invalid digest length: " + dataTypeXDIAddress, null, executionContext);

			keyAlgorithm = Signatures.getKeyAlgorithm(dataTypeXDIAddress);
			if (keyAlgorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + dataTypeXDIAddress, null, executionContext);

			keyLength = Signatures.getKeyLength(dataTypeXDIAddress);
			if (keyLength == null) throw new Xdi2MessagingException("Invalid key length: " + dataTypeXDIAddress, null, executionContext);

			if (log.isDebugEnabled()) log.debug("digestAlgorithm: " + digestAlgorithm + ", digestLength: " + digestLength + ", keyAlgorithm: " + keyAlgorithm + ", keyLength: " + keyLength);

			// key pair or symmetric key?

			if (KeyPairSignature.DIGEST_ALGORITHM_SHA.equals(digestAlgorithm) &&
					KeyPairSignature.KEY_ALGORITHM_RSA.equals(keyAlgorithm) || KeyPairSignature.KEY_ALGORITHM_DSA.equals(keyAlgorithm)) {

				// recipient

				XDIAddress recipientAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());
				if (recipientAddress == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// recipient entity

				XdiEntity recipientXdiEntity = XdiCommonRoot.findCommonRoot(AsyncMessageResultInterceptor.this.getPrivateKeyGraph()).getXdiEntity(recipientAddress, false);
				recipientXdiEntity = recipientXdiEntity == null ? null : recipientXdiEntity.dereference();

				if (log.isDebugEnabled()) log.debug("Recipient entity: " + recipientXdiEntity);

				if (recipientXdiEntity == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// find signature private key

				PrivateKey privateKey;

				try {

					privateKey = Keys.getSignaturePrivateKey(recipientXdiEntity);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Invalid signature private key: " + ex.getMessage(), ex, null);
				}

				// sign the graph

				KeyPairSignature signature = (KeyPairSignature) Signatures.createSignature(
						messageResult.getGraph().getRootContextNode(), 
						digestAlgorithm, 
						digestLength.intValue(), 
						keyAlgorithm, 
						keyLength.intValue(), 
						true);

				try {

					signature.sign(privateKey);
					DataTypes.setDataType(signature.getContextNode(), dataTypeXDIAddress);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Cannot sign using private key: " + ex.getMessage(), ex, null);
				}
			} else if (SymmetricKeySignature.DIGEST_ALGORITHM_SHA.equals(digestAlgorithm) &&
					SymmetricKeySignature.KEY_ALGORITHM_AES.equals(keyAlgorithm)) {

				// recipient

				XDIAddress recipientAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(operation.getMessage().getToPeerRootXDIArc());
				if (recipientAddress == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// recipient entity

				XdiEntity recipientXdiEntity = XdiCommonRoot.findCommonRoot(AsyncMessageResultInterceptor.this.getPrivateKeyGraph()).getXdiEntity(recipientAddress, false);
				recipientXdiEntity = recipientXdiEntity == null ? null : recipientXdiEntity.dereference();

				if (log.isDebugEnabled()) log.debug("Recipient entity: " + recipientXdiEntity);

				if (recipientXdiEntity == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// find signature secret key

				SecretKey secretKey;

				try {

					secretKey = Keys.getSignatureSecretKey(recipientXdiEntity);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Invalid signature secret key: " + ex.getMessage(), ex, null);
				}

				// sign the graph

				SymmetricKeySignature signature = (SymmetricKeySignature) Signatures.createSignature(
						messageResult.getGraph().getRootContextNode(), 
						digestAlgorithm, 
						digestLength.intValue(), 
						keyAlgorithm, 
						keyLength.intValue(), 
						true);

				try {

					signature.sign(secretKey);
					DataTypes.setDataType(signature.getContextNode(), dataTypeXDIAddress);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Cannot sign using secret key: " + ex.getMessage(), ex, null);
				}
			}

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET;
		}
	}*/

	/*
	 * Getters and setters
	 */

	public Graph getPrivateKeyGraph() {

		return this.privateKeyGraph;
	}

	public void setPrivateKeyGraph(Graph privateKeyGraph) {

		this.privateKeyGraph = privateKeyGraph;
	}
}
