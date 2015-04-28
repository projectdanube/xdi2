package xdi2.core.features.encryption;

import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.security.Key;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;

/**
 * An XDI encryption, represented as an XDI attribute.
 * 
 * @author markus
 */
public abstract class Encryption <EKEY extends Key, DKEY extends Key> implements Serializable, Comparable<Encryption<EKEY, DKEY>> {

	private static final long serialVersionUID = 556316259037177674L;

	private XdiAttribute xdiAttribute;

	protected Encryption(XdiAttribute xdiAttribute) {

		this.xdiAttribute = xdiAttribute;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI encryption.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI encryption.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute == null) return false;

		return
				KeyPairEncryption.isValid(xdiAttribute) ||
				SymmetricKeyEncryption.isValid(xdiAttribute);
	}

	/**
	 * Factory method that creates an XDI encryption bound to a given XDI attribute.
	 * @param xdiAttribute The XDI encryption that is an XDI encryption.
	 * @return The XDI encryption.
	 */
	public static Encryption<? extends Key, ? extends Key> fromXdiAttribute(XdiAttribute xdiAttribute) {

		Encryption<? extends Key, ? extends Key> encryption;

		if ((encryption = KeyPairEncryption.fromXdiAttribute(xdiAttribute)) != null) return encryption;
		if ((encryption = SymmetricKeyEncryption.fromXdiAttribute(xdiAttribute)) != null) return encryption;

		return null;
	}

	/*
	 * Instance methods
	 */

	public void clearAfterEncrypt() {

		for (ContextNode contextNode : this.getBaseContextNode().getContextNodes()) {

			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (Encryption.isValid(xdiAttribute)) continue;

			contextNode.delete();
		}

		this.getBaseContextNode().delRelations();
		this.getBaseContextNode().delLiteralNode();
	}

	public void clearAfterDecrypt() {

		for (ContextNode contextNode : this.getBaseContextNode().getContextNodes()) {

			XdiAttribute xdiAttribute = XdiAbstractAttribute.fromContextNode(contextNode);
			if (! Encryption.isValid(xdiAttribute)) continue;

			contextNode.delete();
		}
	}

	/**
	 * Returns the underlying XDI attribute to which this XDI encryption is bound.
	 * @return An XDI attribute that represents the XDI encryption.
	 */
	public XdiAttribute getXdiAttribute() {

		return this.xdiAttribute;
	}

	/**
	 * Returns the underlying context node to which this XDI encryption is bound.
	 * @return A context node that represents the XDI encryption.
	 */
	public ContextNode getContextNode() {

		return this.getXdiAttribute().getContextNode();
	}

	public ContextNode getBaseContextNode() {

		ContextNode contextNode = (this.getXdiAttribute() instanceof XdiAttributeInstance) ? this.getXdiAttribute().getContextNode().getContextNode() : this.getXdiAttribute().getContextNode();
		contextNode = contextNode.getContextNode();

		return contextNode;
	}

	public String getKeyAlgorithm() {

		return Encryptions.getKeyAlgorithm(this.getXdiAttribute());
	}

	public Integer getKeyLength() {

		return Encryptions.getKeyLength(this.getXdiAttribute());
	}

	public abstract String getTransformation();

	/*
	 * Encrypting and decrypting
	 */

	/**
	 * Get the value
	 */
	public String getValue() {

		LiteralNode literalNode = this.getXdiAttribute().getLiteralNode();
		String value = literalNode == null ? null : literalNode.getLiteralDataString();

		return value;
	}

	/**
	 * Set the value
	 */
	public void setValue(String value) {

		this.getXdiAttribute().setLiteralDataString(value);
	}

	/**
	 * Create the encryption value.
	 */
	public abstract void encrypt(EKEY key) throws GeneralSecurityException;

	/**
	 * Decrypt the encryption value.
	 */
	public abstract void decrypt(DKEY key) throws GeneralSecurityException;

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || !(object instanceof Encryption)) return false;
		if (object == this) return true;

		Encryption<?, ?> other = (Encryption<?, ?>) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Encryption<EKEY, DKEY> other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
