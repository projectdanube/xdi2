package xdi2.messaging.target.contributor.impl;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.features.keys.Keys;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiLocalRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.signatures.KeyPairSignature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.features.signatures.SymmetricKeySignature;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.VariableUtil;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can add metadata to a message result, e.g. a timestamp, 
 * a TO peer root XRI, and a signature.
 */
@ContributorMount(contributorAddresss={""})
public class MessageResultContributor extends AbstractContributor implements Prototype<MessageResultContributor> {

	private static final Logger log = LoggerFactory.getLogger(MessageResultContributor.class);

	private Graph keyGraph;

	public MessageResultContributor(Graph keyGraph) {

		this.keyGraph = keyGraph;

		this.getContributors().addContributor(new TimestampContributor());
		this.getContributors().addContributor(new ToPeerRootAddressContributor());
		this.getContributors().addContributor(new SignatureContributor());
	}

	public MessageResultContributor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public MessageResultContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		MessageResultContributor contributor = new MessageResultContributor();

		// set the private key graph

		contributor.setKeyGraph(this.getKeyGraph());

		// done

		return contributor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getKeyGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setKeyGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getKeyGraph() == null) throw new Xdi2MessagingException("No private key graph.", null, null);
	}

	/*
	 * Sub-Contributors
	 */

	@ContributorMount(contributorAddresss={"<$t>"})
	private class TimestampContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnAddress(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			if (relativeTargetAddress == null) return ContributorResult.DEFAULT;

			// create timestamp

			Date timestamp = new Date();

			// add it to the message result

			Timestamps.setContextNodeTimestamp(messageResult.getGraph().getRootContextNode(), timestamp);

			// done

			return ContributorResult.DEFAULT;
		}
	}

	@ContributorMount(contributorAddresss={""})
	private class ToPeerRootAddressContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			XDIAddress arc = relativeTargetStatement.getRelationAddress();
			XDIAddress targetContextNodeAddress = relativeTargetStatement.getTargetContextNodeAddress();

			// check if applicable

			if (! arc.equals(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC)) return ContributorResult.DEFAULT;
			if (! VariableUtil.isVariable(targetContextNodeAddress)) return ContributorResult.DEFAULT;

			// determine TO peer root XRI

			XDIAddress toPeerRootAddress = XDIAddress.fromComponent(XdiPeerRoot.createPeerRootArc(operation.getSenderAddress()));

			// add it to the message result

			messageResult.getGraph().getRootContextNode().setRelation(XDIMessagingConstants.XDI_ADD_TO_PEER_ROOT_ARC, toPeerRootAddress);

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET;
		}
	}

	@ContributorMount(contributorAddresss={"<$sig>"})
	private class SignatureContributor extends AbstractContributor {

		@Override
		public ContributorResult executeGetOnRelationStatement(XDIAddress[] contributorAddresss, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

			XDIAddress arc = relativeTargetStatement.getRelationAddress();
			XDIAddress targetContextNodeAddress = relativeTargetStatement.getTargetContextNodeAddress();

			// check if applicable

			if (! arc.equals(XDIDictionaryConstants.XDI_ADD_IS_TYPE)) return ContributorResult.DEFAULT;

			// check parameters

			XDIAddress dataTypeAddress = targetContextNodeAddress;

			String digestAlgorithm;
			Integer digestLength;
			String keyAlgorithm;
			Integer keyLength;

			digestAlgorithm = Signatures.getDigestAlgorithm(dataTypeAddress);
			if (digestAlgorithm == null) throw new Xdi2MessagingException("Invalid digest algorithm: " + dataTypeAddress, null, executionContext);

			digestLength = Signatures.getDigestLength(dataTypeAddress);
			if (digestLength == null) throw new Xdi2MessagingException("Invalid digest length: " + dataTypeAddress, null, executionContext);

			keyAlgorithm = Signatures.getKeyAlgorithm(dataTypeAddress);
			if (keyAlgorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + dataTypeAddress, null, executionContext);

			keyLength = Signatures.getKeyLength(dataTypeAddress);
			if (keyLength == null) throw new Xdi2MessagingException("Invalid key length: " + dataTypeAddress, null, executionContext);

			if (log.isDebugEnabled()) log.debug("digestAlgorithm: " + digestAlgorithm + ", digestLength: " + digestLength + ", keyAlgorithm: " + keyAlgorithm + ", keyLength: " + keyLength);

			// key pair or symmetric key?

			if (KeyPairSignature.DIGEST_ALGORITHM_SHA.equals(digestAlgorithm) &&
					KeyPairSignature.KEY_ALGORITHM_RSA.equals(keyAlgorithm) || KeyPairSignature.KEY_ALGORITHM_DSA.equals(keyAlgorithm)) {

				// recipient

				XDIAddress recipientAddress = XdiPeerRoot.getAddressOfPeerRootArc(operation.getMessage().getToPeerRootArc());
				if (recipientAddress == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// recipient entity

				XdiEntity recipientXdiEntity = XdiLocalRoot.findLocalRoot(MessageResultContributor.this.getKeyGraph()).getXdiEntity(recipientAddress, false);
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
					DataTypes.setDataType(signature.getContextNode(), dataTypeAddress);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Cannot sign using private key: " + ex.getMessage(), ex, null);
				}
			} else if (SymmetricKeySignature.DIGEST_ALGORITHM_SHA.equals(digestAlgorithm) &&
					SymmetricKeySignature.KEY_ALGORITHM_AES.equals(keyAlgorithm)) {

				// recipient

				XDIAddress recipientAddress = XdiPeerRoot.getAddressOfPeerRootArc(operation.getMessage().getToPeerRootArc());
				if (recipientAddress == null) return ContributorResult.SKIP_MESSAGING_TARGET;

				// recipient entity

				XdiEntity recipientXdiEntity = XdiLocalRoot.findLocalRoot(MessageResultContributor.this.getKeyGraph()).getXdiEntity(recipientAddress, false);
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
					DataTypes.setDataType(signature.getContextNode(), dataTypeAddress);
				} catch (GeneralSecurityException ex) {

					throw new Xdi2MessagingException("Cannot sign using secret key: " + ex.getMessage(), ex, null);
				}
			}

			// done

			return ContributorResult.SKIP_MESSAGING_TARGET;
		}
	}

	/*
	 * Getters and setters
	 */

	public Graph getKeyGraph() {

		return this.keyGraph;
	}

	public void setKeyGraph(Graph keyGraph) {

		this.keyGraph = keyGraph;
	}
}
