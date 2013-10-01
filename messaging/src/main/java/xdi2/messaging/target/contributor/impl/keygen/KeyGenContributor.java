package xdi2.messaging.target.contributor.impl.keygen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;

@ContributorXri(addresses={"{{}}{$}{$}<$key>"})
public class KeyGenContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(KeyGenContributor.class);

	public static final String ALGORITHM_RSA = "RSA";
	public static final String ALGORITHM_AES = "AES";

	public static final Integer LENGTH_512 = Integer.valueOf(512);
	public static final Integer LENGTH_1024 = Integer.valueOf(1024);
	public static final Integer LENGTH_2048 = Integer.valueOf(2048);

	@Override
	public boolean executeGetOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment keyXri = contributorXris[contributorXris.length - 1];

		log.debug("keyXri: " + keyXri);

		if (keyXri.equals("{}{$}{$}<$key>")) return false;

		// check parameters

		String algorithm;
		Integer length;

		XDI3SubSegment algorithmXri = keyXri.getSubSegment(keyXri.getNumSubSegments() - 3);
		algorithm = algorithmFromAlgorithmXri(algorithmXri);
		if (algorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + algorithmXri, null, executionContext);

		XDI3SubSegment lengthXri = keyXri.getSubSegment(keyXri.getNumSubSegments() - 2);
		length = lengthFromLengthXri(lengthXri);
		if (length == null) throw new Xdi2MessagingException("Invalid key length: " + lengthXri, null, executionContext);

		// generate key

		KeyPair keyPair;

		try {

			KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(algorithm);
			keyPairGen.initialize(length.intValue());
			keyPair = keyPairGen.generateKeyPair();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Problem while creating key pair: " + ex.getMessage(), ex, executionContext);
		}

		// add it to the response

		XdiAttributeSingleton xdiAttribute = XdiAttributeSingleton.fromContextNode(messageResult.getGraph().setDeepContextNode(contributorsXri));
		xdiAttribute.getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(keyPair.getPublic().getEncoded()));

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

			if (LENGTH_512.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_512;
			if (LENGTH_1024.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_1024;
			if (LENGTH_2048.equals(Integer.valueOf(lengthXri.getLiteral()))) return LENGTH_2048;
		} catch (Exception ex) {

			return null;
		}

		return null;
	}
}
