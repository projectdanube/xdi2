package xdi2.core.features.signatures;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public abstract class Signature <SKEY extends Key, VKEY extends Key> implements Serializable, Comparable<Signature<SKEY, VKEY>> {

	private static final long serialVersionUID = -6984622275903043863L;

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

		if (xdiAttribute == null) return false;

		return
				KeyPairSignature.isValid(xdiAttribute) ||
				SymmetricKeySignature.isValid(xdiAttribute);
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI signature that is an XDI signature.
	 * @return The XDI signature.
	 */
	public static Signature<? extends Key, ? extends Key> fromXdiAttribute(XdiAttribute xdiAttribute) {

		Signature<? extends Key, ? extends Key> signature;

		if ((signature = KeyPairSignature.fromXdiAttribute(xdiAttribute)) != null) return signature;
		if ((signature = SymmetricKeySignature.fromXdiAttribute(xdiAttribute)) != null) return signature;

		return null;
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

	public ContextNode getBaseContextNode() {

		ContextNode contextNode = (this.getXdiAttribute() instanceof XdiAttributeMember) ? this.getXdiAttribute().getContextNode().getContextNode() : this.getXdiAttribute().getContextNode();
		contextNode = contextNode.getContextNode(5);

		return contextNode;
	}

	public String getDigestAlgorithm() {

		return getDigestAlgorithm(this.getXdiAttribute());
	}

	public Integer getDigestLength() {

		return getDigestLength(this.getXdiAttribute());
	}

	public String getKeyAlgorithm() {

		return getKeyAlgorithm(this.getXdiAttribute());
	}

	public Integer getKeyLength() {

		return getKeyLength(this.getXdiAttribute());
	}

	public abstract String getAlgorithm();

	/*
	 * Signing and validating
	 */

	/**
	 * Create the signature value.
	 */
	public abstract void sign(SKEY key) throws GeneralSecurityException;

	/**
	 * Validate the signature value.
	 */
	public abstract boolean validate(VKEY key) throws GeneralSecurityException;

	/*
	 * Helper methods
	 */

	/**
	 * Returns the normalized serialization string of a context node, to be used
	 * for creating and validating signatures.
	 */
	public static String getNormalizedSerialization(ContextNode contextNode) {

		Graph graph;

		graph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(contextNode, graph, new NoSignaturesCopyStrategy());

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

	public static String getDigestAlgorithm(XdiAttribute xdiAttribute) {

		ContextNode contextNode = (xdiAttribute instanceof XdiAttributeMember) ? xdiAttribute.getContextNode().getContextNode() : xdiAttribute.getContextNode();
		contextNode = contextNode.getContextNode(4);
		if (contextNode == null) return null;

		XDI3SubSegment xri = contextNode.getArcXri();
		if (! XDIConstants.CS_DOLLAR.equals(xri.getCs())) return null;
		if (xri.hasXRef()) return null;
		if (! xri.hasLiteral()) return null;

		return xri.getLiteral();
	}

	public static Integer getDigestLength(XdiAttribute xdiAttribute) {

		ContextNode contextNode = (xdiAttribute instanceof XdiAttributeMember) ? xdiAttribute.getContextNode().getContextNode() : xdiAttribute.getContextNode();
		contextNode = contextNode.getContextNode(3);
		if (contextNode == null) return null;

		XDI3SubSegment xri = contextNode.getArcXri();
		if (! XDIConstants.CS_DOLLAR.equals(xri.getCs())) return null;
		if (xri.hasXRef()) return null;
		if (! xri.hasLiteral()) return null;

		return Integer.valueOf(xri.getLiteral());
	}

	public static String getKeyAlgorithm(XdiAttribute xdiAttribute) {

		ContextNode contextNode = (xdiAttribute instanceof XdiAttributeMember) ? xdiAttribute.getContextNode().getContextNode() : xdiAttribute.getContextNode();
		contextNode = contextNode.getContextNode(2);
		if (contextNode == null) return null;

		XDI3SubSegment xri = contextNode.getArcXri();
		if (! XDIConstants.CS_DOLLAR.equals(xri.getCs())) return null;
		if (xri.hasXRef()) return null;
		if (! xri.hasLiteral()) return null;

		return xri.getLiteral();
	}

	public static Integer getKeyLength(XdiAttribute xdiAttribute) {

		ContextNode contextNode = (xdiAttribute instanceof XdiAttributeMember) ? xdiAttribute.getContextNode().getContextNode() : xdiAttribute.getContextNode();
		contextNode = contextNode.getContextNode(1);
		if (contextNode == null) return null;

		XDI3SubSegment xri = contextNode.getArcXri();
		if (! XDIConstants.CS_DOLLAR.equals(xri.getCs())) return null;
		if (xri.hasXRef()) return null;
		if (! xri.hasLiteral()) return null;

		return Integer.valueOf(xri.getLiteral());
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

		Signature<?, ?> other = (Signature<?, ?>) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Signature<SKEY, VKEY> other) {

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

			Signature<?, ?> signature = Signature.fromXdiAttribute(xdiAttribute);
			if (signature == null) return contextNode;

			return null;
		}
	}
}
