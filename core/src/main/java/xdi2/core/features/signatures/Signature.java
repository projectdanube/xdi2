package xdi2.core.features.signatures;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class Signature implements Serializable, Comparable<Signature> {

	private static final long serialVersionUID = -6984622275903043863L;

	public static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256withRSA";
	public static final String DEFAULT_HMAC_ALGORITHM = "HmacSHA256";

	private XdiAttribute xdiAttribute;

	protected Signature(XdiAttribute xdiAttribute) {

		this.xdiAttribute = xdiAttribute;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI signature.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI signature.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute instanceof XdiAttributeSingleton) {

			return ((XdiAttributeSingleton) xdiAttribute).getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE));
		} else if (xdiAttribute instanceof XdiAttributeMember) {

			return ((XdiAttributeMember) xdiAttribute).getXdiCollection().getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDIAuthenticationConstants.XRI_SS_SIGNATURE));
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI signature that is an XDI signature.
	 * @return The XDI signature.
	 */
	public static Signature fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new Signature(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI attribute to which this XDI signature is bound.
	 * @return An XDI attribute that represents the XDI signature.
	 */
	public XdiAttribute getXdiAttribute() {

		return this.xdiAttribute;
	}

	/**
	 * Returns the underlying context node to which this XDI signature is bound.
	 * @return A context node that represents the XDI signature.
	 */
	public ContextNode getContextNode() {

		return this.getXdiAttribute().getContextNode();
	}

	public void createSignature(PrivateKey privateKey) throws Exception {

		this.createSignature(privateKey, DEFAULT_SIGNATURE_ALGORITHM);
	}

	public void createSignature(PrivateKey privateKey, String algorithm) throws Exception {

		String normalizedSerialization = this.getNormalizedSerialization();

		java.security.Signature signature = java.security.Signature.getInstance(algorithm);
		signature.initSign(privateKey);
		signature.update(normalizedSerialization.getBytes("UTF-8"));

		byte[] bytes = signature.sign();

		this.getXdiAttribute().getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(bytes));
	}

	public boolean validateSignature(PublicKey publicKey) throws Exception {

		return this.validateSignature(publicKey, DEFAULT_SIGNATURE_ALGORITHM);
	}

	public boolean validateSignature(PublicKey publicKey, String algorithm) throws Exception {

		XdiValue xdiValue = this.getXdiAttribute().getXdiValue(false);
		if (xdiValue == null) return false;

		Literal literal = xdiValue.getContextNode().getLiteral();
		if (literal == null) return false;

		String literalString = literal.getLiteralDataString();
		if (literalString == null) return false;

		byte[] bytes = Base64.decodeBase64(literalString);

		String normalizedSerialization = this.getNormalizedSerialization();

		java.security.Signature signature = java.security.Signature.getInstance(algorithm);
		signature.initVerify(publicKey);
		signature.update(normalizedSerialization.getBytes("UTF-8"));

		boolean verify = signature.verify(bytes);

		return verify;
	}

	public void createHMAC(SecretKey secretKey) throws Exception {

		this.createHMAC(secretKey, DEFAULT_HMAC_ALGORITHM);
	}

	public void createHMAC(SecretKey secretKey, String algorithm) throws Exception {

		String normalizedSerialization = this.getNormalizedSerialization();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization.getBytes("UTF-8"));

		byte[] bytes = mac.doFinal();

		this.getXdiAttribute().getXdiValue(true).getContextNode().setLiteralString(Base64.encodeBase64String(bytes));
	}

	public boolean validateHMAC(SecretKey publicKey) throws Exception {

		return this.validateHMAC(publicKey, DEFAULT_HMAC_ALGORITHM);
	}

	public boolean validateHMAC(SecretKey secretKey, String algorithm) throws Exception {

		XdiValue xdiValue = this.getXdiAttribute().getXdiValue(false);
		if (xdiValue == null) return false;

		Literal literal = xdiValue.getContextNode().getLiteral();
		if (literal == null) return false;

		String literalString = literal.getLiteralDataString();
		if (literalString == null) return false;

		byte[] bytes = Base64.decodeBase64(literalString);

		String normalizedSerialization = this.getNormalizedSerialization();

		Mac mac = Mac.getInstance(algorithm);
		mac.init(secretKey);
		mac.update(normalizedSerialization.getBytes("UTF-8"));

		boolean verify = Arrays.equals(bytes, mac.doFinal());

		return verify;
	}

	public String getNormalizedSerialization() {

		Graph graph;

		graph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(this.getContextNode().getContextNode(), graph, new NoSignaturesCopyStrategy());

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, "0");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, "0");

		XDIJSONWriter writer = new XDIJSONWriter(parameters);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(graph, buffer);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot serialize graph: " + ex.getMessage(), ex);
		}

		return buffer.toString();
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || !(object instanceof Signature)) return false;
		if (object == this) return true;

		Signature other = (Signature) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Signature other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class NoSignaturesCopyStrategy extends CopyStrategy {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (xdiAttribute == null) return contextNode;

			Signature signature = Signature.fromXdiAttribute(xdiAttribute);
			if (signature == null) return contextNode;

			return null;
		}
	}
}
