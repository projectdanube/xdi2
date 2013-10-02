package xdi2.messaging.target.contributor.impl.keygen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;

/**
 * This contributor can generate secret tokens in digest form in a target graph.
 */
@ContributorXri(addresses={"{{=@+*!}}{$}{$}<$key>", "{{(=@+*!)}}{$}{$}<$key>", "{$}{$}<$key>"})
public class GenerateKeyContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(GenerateKeyContributor.class);

	public static final XDI3Segment XRI_S_DO_KEY = XDI3Segment.create("$do<$key>");

	public static final String ALGORITHM_RSA = "RSA";
	public static final String ALGORITHM_DSA = "DSA";
	public static final String ALGORITHM_AES = "AES";

	public GenerateKeyContributor() {

	}

	@Override
	public boolean executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment keyXri = contributorXris[contributorXris.length - 1];

		if (log.isDebugEnabled()) log.debug("keyXri: " + keyXri);

		if (keyXri.equals("{{}}{$}{$}<$key>")) return false;

		// check operation

		if (! XRI_S_DO_KEY.equals(operation.getOperationXri())) return false;

		// check parameters

		String algorithm;
		Integer length;

		XDI3SubSegment algorithmXri = keyXri.getSubSegment(keyXri.getNumSubSegments() - 3);
		algorithm = algorithmFromAlgorithmXri(algorithmXri);
		if (algorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + algorithmXri, null, executionContext);

		XDI3SubSegment lengthXri = keyXri.getSubSegment(keyXri.getNumSubSegments() - 2);
		length = lengthFromLengthXri(lengthXri);
		if (length == null) throw new Xdi2MessagingException("Invalid key length: " + lengthXri, null, executionContext);

		if (log.isDebugEnabled()) log.debug("algorithm: " + algorithm + ", length: " + length);

		// key pair or symmetric key?

		if (ALGORITHM_RSA.equalsIgnoreCase(algorithm) || ALGORITHM_DSA.equalsIgnoreCase(algorithm)) {

			// generate key pair

			KeyPair keyPair;

			try {

				KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
				keyPairGen.initialize(length.intValue());
				keyPair = keyPairGen.generateKeyPair();
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while creating key pair: " + ex.getMessage(), ex, executionContext);
			}

			// add it to the response

			XdiAttributeSingleton keyXdiAttribute = XdiAttributeSingleton.fromContextNode(messageResult.getGraph().setDeepContextNode(contributorsXri));
			XdiAttributeSingleton publicKeyXdiAttribute = keyXdiAttribute.getXdiAttributeSingleton(XDI3SubSegment.create("$public"), true);
			XdiAttributeSingleton privateKeyXdiAttribute = keyXdiAttribute.getXdiAttributeSingleton(XDI3SubSegment.create("$private"), true);
			publicKeyXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));
			privateKeyXdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(keyPair.getPrivate().getEncoded()));
		} else if (ALGORITHM_AES.equalsIgnoreCase(algorithm)) {

			// generate symmetric key

			SecretKey secretKey;

			try {

				KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
				keyGen.init(length.intValue());
				secretKey = keyGen.generateKey(); 
			} catch (Exception ex) {

				throw new Xdi2MessagingException("Problem while creating symmetric key: " + ex.getMessage(), ex, executionContext);
			}

			// add it to the response

			XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(messageResult.getGraph().setDeepContextNode(contributorsXri));
			xdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(secretKey.getEncoded()));
		}

		// done

		return false;
	}

	private static String algorithmFromAlgorithmXri(XDI3SubSegment algorithmXri) {

		return algorithmXri == null ? null : algorithmXri.getLiteral();
	}

	private static Integer lengthFromLengthXri(XDI3SubSegment lengthXri) {

		return lengthXri == null ? null : Integer.valueOf(lengthXri.getLiteral());
	}
}
