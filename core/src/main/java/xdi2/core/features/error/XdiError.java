package xdi2.core.features.error;

import java.io.Serializable;
import java.util.Date;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeInstanceOrdered;
import xdi2.core.features.nodetypes.XdiAttributeInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.timestamps.Timestamps;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * An XDI error, represented as an XDI attribute.
 * 
 * @author markus
 */
public class XdiError implements Serializable {

	private static final long serialVersionUID = 5732150498065911411L;

	public static final XDIArc XDI_ARC_ERROR = XDIArc.create("$error");
	public static final XDIAddress XDI_ADD_ERROR = XDIAddress.fromComponent(XDI_ARC_ERROR);

	public static final XDIArc XDI_ARC_AS_ERROR = XdiAttributeSingleton.createXDIArc(XDI_ARC_ERROR);
	public static final XDIAddress XDI_ADD_AS_ERROR = XDIAddress.fromComponent(XDI_ARC_AS_ERROR);

	public static final XDIArc XDI_ARC_AC_ERROR = XdiAttributeCollection.createXDIArc(XDI_ARC_ERROR);
	public static final XDIAddress XDI_ADD_AC_ERROR = XDIAddress.fromComponent(XDI_ARC_AC_ERROR);

	private XdiAttribute xdiAttribute;

	protected XdiError(XdiAttribute xdiAttribute) {

		if (xdiAttribute == null) throw new NullPointerException();

		this.xdiAttribute = xdiAttribute;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI attribute is a valid XDI error.
	 * @param xdiAttribute The XDI attribute to check.
	 * @return True if the XDI attribute is a valid XDI error.
	 */
	public static boolean isValid(XdiAttribute xdiAttribute) {

		if (xdiAttribute instanceof XdiAttributeSingleton)
			return ((XdiAttributeSingleton) xdiAttribute).getXDIArc().equals(XDI_ARC_AS_ERROR);
		else if (xdiAttribute instanceof XdiAttributeInstanceUnordered)
			return ((XdiAttributeInstanceUnordered) xdiAttribute).getXdiCollection().getXDIArc().equals(XDI_ARC_AC_ERROR);
		else if (xdiAttribute instanceof XdiAttributeInstanceOrdered)
			return ((XdiAttributeInstanceOrdered) xdiAttribute).getXdiCollection().getXDIArc().equals(XDI_ARC_AC_ERROR);

		return false;
	}

	/**
	 * Factory method that creates an XDI error bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI error.
	 * @return The XDI error.
	 */
	public static XdiError fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new XdiError(xdiAttribute);
	}

	/**
	 * Factory method that finds or creates an XDI error for a context.
	 * @return The XDI error.
	 */
	public static XdiError findXdiError(XdiContext<?> xdiContext, boolean create) {

		XdiAttribute xdiAttribute = xdiContext.getXdiAttributeSingleton(XDI_ARC_AS_ERROR, create);
		if (xdiAttribute == null) return null;

		return new XdiError(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI attribute to which this XDI error is bound.
	 * @return An XDI attribute that represents the XDI error.
	 */
	public XdiAttribute getXdiAttribute() {

		return this.xdiAttribute;
	}

	/**
	 * Returns the underlying context node to which this XDI error is bound.
	 * @return A context node that represents the XDI error.
	 */
	public ContextNode getContextNode() {

		return this.getXdiAttribute().getContextNode();
	}

	public Date getErrorTimestamp() {

		return Timestamps.getTimestamp(XdiAbstractContext.fromContextNode(this.getContextNode()));
	}

	public void setErrorTimestamp(Date errorTimestamp) {

		Timestamps.setTimestamp(XdiAbstractContext.fromContextNode(this.getContextNode()), errorTimestamp);
	}

	public String getErrorString() {

		LiteralNode errorStringLiteral = this.getXdiAttribute().getLiteralNode();
		if (errorStringLiteral == null) return null;

		return errorStringLiteral.getLiteralDataString();
	}

	public void setErrorString(String errorString) {

		this.getXdiAttribute().setLiteralDataString(errorString);
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

		if (object == null || ! (object instanceof XdiContext)) return false;
		if (object == this) return true;

		XdiError other = (XdiError) object;

		// two XDi errors are equal if their context nodes are equal

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}
}
