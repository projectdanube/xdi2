package xdi2.core.features.variables;

import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;
import xdi2.core.xri3.XRI3Constants;

public class Variables {

	private Variables() { }

	public static boolean isVariable(XDI3SubSegment var) {

		return isVariableSingle(var) || isVariableMultiple(var) | isVariableMultipleLocal(var);
	}

	public static boolean isVariable(XDI3Segment var) {

		return isVariableSingle(var) || isVariableMultiple(var) | isVariableMultipleLocal(var);
	}

	public static boolean isVariableSingle(XDI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XDI3XRef xref = var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasSegment()) return false;

		XDI3Segment node = xref.getSegment();
		if (node.getNumSubSegments() != 1) return false;

		XDI3SubSegment firstSubSegment = node.getSubSegment(0);
		if (! XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS())) return false;

		return true;
	}

	public static boolean isVariableSingle(XDI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableSingle(var.getFirstSubSegment()) : false;
	}

	public static boolean isVariableMultiple(XDI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XDI3XRef xref = var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasSegment()) return false;

		XDI3Segment node = xref.getSegment();
		if (node.getNumSubSegments() != 2) return false;

		XDI3SubSegment firstSubSegment = node.getSubSegment(0);
		if (! XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS())) return false;

		XDI3SubSegment secondSubSegment = node.getSubSegment(1);
		if (! XRI3Constants.GCS_DOLLAR.equals(secondSubSegment.getGCS())) return false;
		if (secondSubSegment.hasLCS()) return false;
		if (secondSubSegment.hasLiteral()) return false;
		if (secondSubSegment.hasXRef()) return false;

		return true;
	}

	public static boolean isVariableMultiple(XDI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableMultiple(var.getFirstSubSegment()) : false;
	}

	public static boolean isVariableMultipleLocal(XDI3SubSegment var) {

		if (var.hasGCS()) return false;
		if (var.hasLCS()) return false;

		if (var.hasLiteral()) return false;
		if (! var.hasXRef()) return false;

		XDI3XRef xref = var.getXRef();
		if (xref.hasIRI()) return false;
		if (! xref.hasSegment()) return false;

		XDI3Segment node = xref.getSegment();
		if (node.getNumSubSegments() != 2) return false;

		XDI3SubSegment firstSubSegment = node.getSubSegment(0);
		if (! (XRI3Constants.GCS_DOLLAR.equals(firstSubSegment.getGCS()))) return false;

		XDI3SubSegment secondSubSegment = node.getSubSegment(1);
		if (! XRI3Constants.GCS_DOLLAR.equals(secondSubSegment.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(secondSubSegment.getLCS())) return false;
		if (secondSubSegment.hasLiteral()) return false;
		if (secondSubSegment.hasXRef()) return false;

		return true;
	}

	public static boolean isVariableMultipleLocal(XDI3Segment var) {

		return var.getNumSubSegments() == 1 ? isVariableMultipleLocal(var.getFirstSubSegment()) : false;
	}
}
