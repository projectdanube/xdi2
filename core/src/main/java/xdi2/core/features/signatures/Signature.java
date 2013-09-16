package xdi2.core.features.signatures;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeMember;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.io.writers.XDIJSONWriter;
import xdi2.core.util.CopyUtil;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class Signature implements Serializable, Comparable<Signature> {

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

		if (xdiAttribute instanceof XdiAttributeSingleton) {

			return ((XdiAttributeSingleton) xdiAttribute).getBaseArcXri().equals(XDIAuthenticationConstants.XRI_S_SIGNATURE);
		} else if (xdiAttribute instanceof XdiAttributeMember) {

			return ((XdiAttributeMember) xdiAttribute).getXdiCollection().getBaseArcXri().equals(XDIAuthenticationConstants.XRI_S_SIGNATURE);
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

	public void generate(PrivateKey privateKey) {

		
	}

	public boolean validate(PublicKey publicKey) {

		return false;
	}

	public static String generateNormalizedSerialization(Graph graph) {

		Properties parameters = new Properties();
		parameters.setProperty(XDIWriterRegistry.PARAMETER_IMPLIED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_ORDERED, "1");
		parameters.setProperty(XDIWriterRegistry.PARAMETER_INNER, null);
		parameters.setProperty(XDIWriterRegistry.PARAMETER_PRETTY, null);

		XDIJSONWriter writer = new XDIJSONWriter(parameters);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(graph, buffer);
		} catch (IOException ex) {

			throw new Xdi2RuntimeException("Cannot serialize graph: " + ex.getMessage(), ex);
		}

		return buffer.toString();
	}

	public static String generateNormalizedSerialization(ContextNode contextNode) {

		Graph graph;

		if (contextNode.isRootContextNode()) {

			graph = contextNode.getGraph();
		} else {

			graph = MemoryGraphFactory.getInstance().openGraph();
			CopyUtil.copyContextNode(contextNode, graph, null);
		}

		return generateNormalizedSerialization(graph);
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
}
