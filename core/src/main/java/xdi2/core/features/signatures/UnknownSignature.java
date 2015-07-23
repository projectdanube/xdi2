package xdi2.core.features.signatures;

import java.security.GeneralSecurityException;

import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeInstance;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;

/**
 * An XDI signature, represented as an XDI attribute.
 * 
 * @author markus
 */
public final class UnknownSignature extends Signature<Object, Object> {

	private static final long serialVersionUID = -8222252937720290576L;

	protected UnknownSignature(XdiAttribute xdiAttribute) {

		super(xdiAttribute);
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

			if (! ((XdiAttributeSingleton) xdiAttribute).getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else if (xdiAttribute instanceof XdiAttributeInstance) {

			if (! ((XdiAttributeInstance) xdiAttribute).getXdiCollection().getBaseXDIArc().equals(XdiAbstractContext.getBaseXDIArc(XDIAuthenticationConstants.XDI_ARC_SIGNATURE))) return false;
		} else {

			return false;
		}

		return true;
	}

	/**
	 * Factory method that creates an XDI signature bound to a given XDI attribute.
	 * @param xdiAttribute The XDI attribute that is an XDI signature.
	 * @return The XDI signature.
	 */
	public static UnknownSignature fromXdiAttribute(XdiAttribute xdiAttribute) {

		if (! isValid(xdiAttribute)) return null;

		return new UnknownSignature(xdiAttribute);
	}

	/*
	 * Instance methods
	 */

	@Override
	public String getAlgorithm() {

		return "unknown";
	}

	@Override
	public void sign(Object privateKey) throws GeneralSecurityException {

		throw new Xdi2RuntimeException("Not suppoerted.");
	}

	@Override
	public boolean validate(Object publicKey) throws GeneralSecurityException {

		throw new Xdi2RuntimeException("Not supported.");
	}
}
