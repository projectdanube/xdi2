package xdi2.core.variables;

import xdi2.core.xri3.impl.XRI3Authority;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public class VariablesUtil {

	private VariablesUtil() { }

	public static boolean isVariable(XRI3Segment xri) {

		if (xri.getNumSubSegments() != 1) return false;

		return isVariable((XRI3SubSegment) xri.getFirstSubSegment());
	}

	public static boolean isVariable(XRI3SubSegment xri) {

		if (xri.hasGCS()) return false;
		if (xri.hasLCS()) return false;
		if (xri.hasLiteral()) return false;
		if (! xri.hasXRef()) return false;

		XRI3XRef xref = (XRI3XRef) xri.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasXRIReference()) return false;

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference.hasPath()) return false;
		if (reference.hasQuery()) return false;
		if (reference.hasFragment()) return false;
		if (! reference.hasAuthority()) return false;

		XRI3Authority referenceAuthority = (XRI3Authority) reference.getAuthority();
		if (referenceAuthority.getNumSubSegments() != 1) return false;

		XRI3SubSegment subSegment = (XRI3SubSegment) referenceAuthority.getFirstSubSegment();
		return Character.valueOf('$').equals(subSegment.getGCS());
	}
}
