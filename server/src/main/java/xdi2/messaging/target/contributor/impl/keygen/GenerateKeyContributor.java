package xdi2.messaging.target.contributor.impl.keygen;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.DoOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorXri;

/**
 * This contributor can generate key pairs and symmetric keys in a target graph.
 */
@ContributorXri(addresses={"{{=@+*!}}$keypair", "{{(=@+*!)}}$keypair", "$keypair", "{{=@+*!}}<$key>", "{{(=@+*!)}}<$key>", "<$key>"})
public class GenerateKeyContributor extends AbstractContributor {

	private static final Logger log = LoggerFactory.getLogger(GenerateKeyContributor.class);

	public static final XDI3Segment XRI_S_DO_KEYPAIR = XDI3Segment.create("$do$keypair");
	public static final XDI3Segment XRI_S_DO_KEY = XDI3Segment.create("$do<$key>");

	public static final String ALGORITHM_RSA = "RSA";
	public static final String ALGORITHM_DSA = "DSA";
	public static final String ALGORITHM_AES = "AES";

	public GenerateKeyContributor() {

	}

	@Override
	public boolean executeDoOnRelationStatement(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Statement relativeTargetStatement, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contributorXri = contributorXris[contributorXris.length - 1];

		if (log.isDebugEnabled()) log.debug("contributorXri: " + contributorXri);

		if (this.containsAddress(contributorXri.toString())) return false;

		// check operation

		if (! XRI_S_DO_KEYPAIR.equals(operation.getOperationXri()) && ! XRI_S_DO_KEY.equals(operation.getOperationXri())) return false;

		// check parameters

		XDI3Segment arcXri = relativeTargetStatement.getRelationArcXri();
		if (! XDIDictionaryConstants.XRI_S_IS_TYPE.equals(arcXri)) return false;

		XDI3Segment dataType = relativeTargetStatement.getTargetContextNodeXri();

		String keyAlgorithm;
		Integer keyLength;

		keyAlgorithm = getKeyAlgorithm(dataType);
		if (keyAlgorithm == null) throw new Xdi2MessagingException("Invalid key algorithm: " + dataType, null, executionContext);

		keyLength = getKeyLength(dataType);
		if (keyLength == null) throw new Xdi2MessagingException("Invalid key length: " + dataType, null, executionContext);

		if (log.isDebugEnabled()) log.debug("keyAlgorithm: " + keyAlgorithm + ", keyLength: " + keyLength);

		// key pair or symmetric key?

		if (XRI_S_DO_KEYPAIR.equals(operation.getOperationXri())) {

			// generate key pair

			KeyPair keyPair;

			try {

				KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(keyAlgorithm);
				keyPairGen.initialize(keyLength.intValue());
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
		} else if (XRI_S_DO_KEY.equals(operation.getOperationXri())) {

			// generate symmetric key

			SecretKey secretKey;

			try {

				KeyGenerator keyGen = KeyGenerator.getInstance(keyAlgorithm);
				keyGen.init(keyLength.intValue());
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

	public static String getKeyAlgorithm(XDI3Segment dataType) {

		XDI3SubSegment keyAlgorithmXri = dataType.getNumSubSegments() > 0 ? dataType.getSubSegment(0) : null;
		if (keyAlgorithmXri == null) return null;

		if (! XDIConstants.CS_DOLLAR.equals(keyAlgorithmXri.getCs())) return null;
		if (keyAlgorithmXri.hasXRef()) return null;
		if (! keyAlgorithmXri.hasLiteral()) return null;

		return keyAlgorithmXri.getLiteral();
	}

	public static Integer getKeyLength(XDI3Segment dataType) {

		XDI3SubSegment keyLengthXri = dataType.getNumSubSegments() > 1 ? dataType.getSubSegment(1) : null;
		if (keyLengthXri == null) return null;

		if (! XDIConstants.CS_DOLLAR.equals(keyLengthXri.getCs())) return null;
		if (keyLengthXri.hasXRef()) return null;
		if (! keyLengthXri.hasLiteral()) return null;

		return Integer.valueOf(keyLengthXri.getLiteral());
	}
}
