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

@ContributorXri(addresses={"{{=@+*!}}{$}{$}<$key>"})
public class GenerateKeyContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(GenerateKeyContributor.class);

	public static final XDI3Segment XRI_S_DO_KEY = XDI3Segment.create("$do<$key>");

	public static final String ALGORITHM_RSA = "RSA";
	public static final String ALGORITHM_AES = "AES";

	public static final Integer LENGTH_256 = Integer.valueOf(256);
	public static final Integer LENGTH_512 = Integer.valueOf(512);
	public static final Integer LENGTH_1024 = Integer.valueOf(1024);
	public static final Integer LENGTH_2048 = Integer.valueOf(2048);
	public static final Integer LENGTH_4096 = Integer.valueOf(4096);

	@Override
	public boolean executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment keyXri = contributorXris[contributorXris.length - 1];

		log.debug("keyXri: " + keyXri);

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

		// key pair or symmetric key?

		if (ALGORITHM_RSA.equals(algorithmXri)) {

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
		} else if (ALGORITHM_AES.equals(algorithmXri)) {

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

		if (ALGORITHM_RSA.equalsIgnoreCase(algorithmXri.getLiteral())) return ALGORITHM_RSA;
		if (ALGORITHM_AES.equalsIgnoreCase(algorithmXri.getLiteral())) return ALGORITHM_AES;

		return null;
	}

	private static Integer lengthFromLengthXri(XDI3SubSegment lengthXri) {

		try {

			if (LENGTH_256.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_256;
			if (LENGTH_512.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_512;
			if (LENGTH_1024.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_1024;
			if (LENGTH_2048.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_2048;
			if (LENGTH_4096.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_4096;
		} catch (Exception ex) {

			return null;
		}

		return null;
	}
}
