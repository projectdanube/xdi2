package xdi2.core.features.variables;

import xdi2.core.xri3.impl.XRI3Authority;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3Reference;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.core.xri3.impl.XRI3XRef;

public class Variables {

	private Variables() { }

	public static boolean isVariableSingle(XRI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XRI3XRef xref = (XRI3XRef) var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasXRIReference()) return false;

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference.hasPath()) return false;
		if (reference.hasQuery()) return false;
		if (reference.hasFragment()) return false;
		if (! reference.hasAuthority()) return false;

		XRI3Authority referenceAuthority = (XRI3Authority) reference.getAuthority();
		if (referenceAuthority.getNumSubSegments() != 1) return false;

		XRI3SubSegment firstSubSegment = (XRI3SubSegment) referenceAuthority.getSubSegment(0);
		if (! XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS())) return false;

		return true;
	}

	public static boolean isVariableSingle(XRI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableSingle((XRI3SubSegment) var.getFirstSubSegment()) : false;
	}

	public static boolean isVariableMultiple(XRI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XRI3XRef xref = (XRI3XRef) var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasXRIReference()) return false;

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference.hasPath()) return false;
		if (reference.hasQuery()) return false;
		if (reference.hasFragment()) return false;
		if (! reference.hasAuthority()) return false;

		XRI3Authority referenceAuthority = (XRI3Authority) reference.getAuthority();
		if (referenceAuthority.getNumSubSegments() != 2) return false;

		XRI3SubSegment firstSubSegment = (XRI3SubSegment) referenceAuthority.getSubSegment(0);
		if (! (XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS()))) return false;

		XRI3SubSegment secondSubSegment = (XRI3SubSegment) referenceAuthority.getSubSegment(1);
		if (! XRI3Constants.GCS_DOLLAR.equals(secondSubSegment.getGCS())) return false;
		if (secondSubSegment.hasLCS()) return false;
		if (secondSubSegment.hasLiteral()) return false;
		if (secondSubSegment.hasXRef()) return false;

		return true;
	}

	public static boolean isVariableMultiple(XRI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableMultiple((XRI3SubSegment) var.getFirstSubSegment()) : false;
	}

	public static boolean isVariableMultipleLocal(XRI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XRI3XRef xref = (XRI3XRef) var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasXRIReference()) return false;

		XRI3Reference reference = (XRI3Reference) xref.getXRIReference();
		if (reference.hasPath()) return false;
		if (reference.hasQuery()) return false;
		if (reference.hasFragment()) return false;
		if (! reference.hasAuthority()) return false;

		XRI3Authority referenceAuthority = (XRI3Authority) reference.getAuthority();
		if (referenceAuthority.getNumSubSegments() != 2) return false;

		XRI3SubSegment firstSubSegment = (XRI3SubSegment) referenceAuthority.getSubSegment(0);
		if (! (XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS()))) return false;

		XRI3SubSegment secondSubSegment = (XRI3SubSegment) referenceAuthority.getSubSegment(1);
		if (! XRI3Constants.GCS_DOLLAR.equals(secondSubSegment.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(secondSubSegment.getLCS())) return false;
		if (secondSubSegment.hasLiteral()) return false;
		if (secondSubSegment.hasXRef()) return false;

		return true;
	}

	public static boolean isVariableMultipleLocal(XRI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableMultipleLocal((XRI3SubSegment) var.getFirstSubSegment()) : false;
	}
}
