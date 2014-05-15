package xdi2.core.features.signatures;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Key;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.util.CopyUtil.CopyStrategy;

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
		contextNode = contextNode.getContextNode();

		return contextNode;
	}

	public String getDigestAlgorithm() {

		return Signatures.getDigestAlgorithm(this.getXdiAttribute());
	}

	public Integer getDigestLength() {

		return Signatures.getDigestLength(this.getXdiAttribute());
	}

	public String getKeyAlgorithm() {

		return Signatures.getKeyAlgorithm(this.getXdiAttribute());
	}

	public Integer getKeyLength() {

		return Signatures.getKeyLength(this.getXdiAttribute());
	}

	public abstract String getAlgorithm();

	/*
	 * Signing and validating
	 */

	/**
	 * Get the value
	 */
	public String getValue() {

		XdiValue xdiValue = this.getXdiAttribute().getXdiValue(false);
		Literal literal = xdiValue == null ? null : xdiValue.getContextNode().getLiteral();
		String value = literal == null ? null : literal.getLiteralDataString();

		return value;
	}

	/**
	 * Set the value
	 */
	public void setValue(String value) {

		XdiValue xdiValue = this.getXdiAttribute().getXdiValue(true);

		xdiValue.getContextNode().setLiteralString(value);
	}

	/**
	 * Create the signature value.
	 */
	public abstract void sign(SKEY key) throws GeneralSecurityException;

	/**
	 * Validate the signature value.
	 */
	public abstract boolean validate(VKEY key) throws GeneralSecurityException;

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
