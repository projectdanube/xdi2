package xdi2.core.features.digests;

import java.io.Serializable;
import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Base64;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;

/**
 * An XDI digest, represented as an XDI attribute.
 * 
 * @author markus
 */
public abstract class Digest implements Serializable, Comparable<Digest> {

	private static final long serialVersionUID = -5014834282335704784L;

	private XdiAttribute xdiAttribute;

	protected Digest(XdiAttribute xdiAttribute) {

		this.xdiAttribute = xdiAttribute;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI digest.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI digest.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute == null) return false;

		return
				SHADigest.isValid(xdiAttribute) ||
				MDDigest.isValid(xdiAttribute);
	}

	/**
	 * Factory method that creates an XDI digest bound to a given XDI attribute.
	 * @param xdiAttribute The XDI digest that is an XDI digest.
	 * @return The XDI digest.
	 */
	public static Digest fromXdiAttribute(XdiAttribute xdiAttribute) {

		Digest digest;

		if ((digest = MDDigest.fromXdiAttribute(xdiAttribute)) != null) return digest;
		if ((digest = SHADigest.fromXdiAttribute(xdiAttribute)) != null) return digest;

		return null;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI attribute to which this XDI digest is bound.
	 * @return An XDI attribute that represents the XDI digest.
	 */
	public XdiAttribute getXdiAttribute() {

		return this.xdiAttribute;
	}

	/**
	 * Returns the underlying context node to which this XDI digest is bound.
	 * @return A context node that represents the XDI digest.
	 */
	public ContextNode getContextNode() {

		return this.getXdiAttribute().getContextNode();
	}

	public ContextNode getBaseContextNode() {

		ContextNode contextNode = (this.getXdiAttribute() instanceof XdiAttributeInstance) ? this.getXdiAttribute().getContextNode().getContextNode() : this.getXdiAttribute().getContextNode();
		contextNode = contextNode.getContextNode();

		return contextNode;
	}

	public String getDigestAlgorithm() {

		return Digests.getDigestAlgorithm(this.getXdiAttribute());
	}

	public Integer getDigestVersion() {

		return Digests.getDigestVersion(this.getXdiAttribute());
	}

	/*
	 * Digest value
	 */

	/**
	 * Get the value
	 */
	public byte[] getDigestValue() {

		LiteralNode literalNode = this.getXdiAttribute().getLiteralNode();
		String literalDataString = literalNode == null ? null : literalNode.getLiteralDataString();
		if (literalDataString == null) return null;

		byte[] digestValue = Base64.decodeBase64(literalDataString.getBytes(Charset.forName("UTF-8")));

		return digestValue;
	}

	/**
	 * Set the value
	 */
	public void setDigestValue(byte[] digestValue) {

		this.getXdiAttribute().setLiteralString(new String(Base64.encodeBase64(digestValue), Charset.forName("UTF-8")));
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

		if (object == null || !(object instanceof Digest)) return false;
		if (object == this) return true;

		Digest other = (Digest) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Digest other) {

		if (other == this || other == null) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
