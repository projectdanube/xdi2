package xdi2.tests.core.features.signatures;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import junit.framework.TestCase;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.signatures.KeyPairSignature;
import xdi2.core.features.signatures.Signatures;
import xdi2.core.features.signatures.SymmetricKeySignature;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;

public class SignaturesTest extends TestCase {

	public void testAlgorithmAndLengthXris() throws Exception {

		XDIAddress xriKeyPair = XDIAddress.create("$sha$256$rsa$1024");
		XDIAddress xriSymmetricKey = XDIAddress.create("$sha$384$aes$256");

		assertEquals(Signatures.getDigestAlgorithm(xriKeyPair), "sha");
		assertEquals(Signatures.getDigestLength(xriKeyPair), Integer.valueOf(256));
		assertEquals(Signatures.getKeyAlgorithm(xriKeyPair), "rsa");
		assertEquals(Signatures.getKeyLength(xriKeyPair), Integer.valueOf(1024));

		assertEquals(Signatures.getDigestAlgorithm(xriSymmetricKey), "sha");
		assertEquals(Signatures.getDigestLength(xriSymmetricKey), Integer.valueOf(384));
		assertEquals(Signatures.getKeyAlgorithm(xriSymmetricKey), "aes");
		assertEquals(Signatures.getKeyLength(xriSymmetricKey), Integer.valueOf(256));

		assertEquals(Signatures.getDataTypeXri("sha", 256, "rsa", 1024), xriKeyPair);
		assertEquals(Signatures.getDataTypeXri("sha", 384, "aes", 256), xriSymmetricKey);
	}

	public void testSignAndValidateKeyPair() throws Exception {

		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
		keyPairGen.initialize(1024);
		KeyPair keyPair = keyPairGen.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>&/&/\"markus.sabadello@gmail.com\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		KeyPairSignature signature = (KeyPairSignature) Signatures.createSignature(contextNode, KeyPairSignature.DIGEST_ALGORITHM_SHA, 256, KeyPairSignature.KEY_ALGORITHM_RSA, 1024, false);
		signature.sign(privateKey);

		signature = (KeyPairSignature) Signatures.getSignatures(contextNode).next();

		assertEquals(signature.getDigestAlgorithm(), KeyPairSignature.DIGEST_ALGORITHM_SHA);
		assertEquals(signature.getDigestLength(), Integer.valueOf(256));
		assertEquals(signature.getKeyAlgorithm(), KeyPairSignature.KEY_ALGORITHM_RSA);
		assertEquals(signature.getKeyLength(), Integer.valueOf(1024));

		assertEquals(signature.getAlgorithm(), "SHA256withRSA");

		assertEquals(signature.getBaseContextNode(), contextNode);

		assertTrue(signature.validate(publicKey));

		contextNode.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=joseph"));

		assertFalse(signature.validate(publicKey));

		graph.close();
	}

	public void testSignAndValidateSymmetricKey() throws Exception {

		KeyGenerator secretKeyGen = KeyGenerator.getInstance("AES");
		secretKeyGen.init(256);
		SecretKey secretKey = secretKeyGen.generateKey(); 

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>&/&/\"markus.sabadello@gmail.com\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		SymmetricKeySignature signature = (SymmetricKeySignature) Signatures.createSignature(contextNode, SymmetricKeySignature.DIGEST_ALGORITHM_SHA, 384, SymmetricKeySignature.KEY_ALGORITHM_AES, 256, false);
		signature.sign(secretKey);

		signature = (SymmetricKeySignature) Signatures.getSignatures(contextNode).next();

		assertEquals(signature.getDigestAlgorithm(), SymmetricKeySignature.DIGEST_ALGORITHM_SHA);
		assertEquals(signature.getDigestLength(), Integer.valueOf(384));
		assertEquals(signature.getKeyAlgorithm(), SymmetricKeySignature.KEY_ALGORITHM_AES);
		assertEquals(signature.getKeyLength(), Integer.valueOf(256));

		assertEquals(signature.getAlgorithm(), "HmacSHA384");

		assertEquals(signature.getBaseContextNode(), contextNode);

		assertTrue(signature.validate(secretKey));

		contextNode.setRelation(XDIAddress.create("#friend"), XDIAddress.create("=joseph"));

		assertFalse(signature.validate(secretKey));

		graph.close();
	}

	public void testNormalizedSerialization() throws Exception {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		graph.setStatement(XDIStatement.create("=markus<#email>&/&/\"markus.sabadello@gmail.com\""));
		graph.setStatement(XDIStatement.create("=markus/#friend/=animesh"));

		ContextNode contextNode = graph.getDeepContextNode(XDIAddress.create("=markus"));

		String normalizedSerialization = "{\"/\":[\"=animesh\",\"=markus\"],\"=markus/\":[\"<#email>\"],\"=markus<#email>/\":[\"&\"],\"=markus/#friend\":[\"=animesh\"],\"=markus<#email>&/&\":\"markus.sabadello@gmail.com\"}";

		assertEquals(Signatures.getNormalizedSerialization(contextNode), normalizedSerialization);

		graph.close();
	}
}
