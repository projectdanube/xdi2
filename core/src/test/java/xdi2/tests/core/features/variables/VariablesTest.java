package xdi2.tests.core.features.variables;

import junit.framework.TestCase;
import xdi2.core.features.variables.Variables;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		XRI3Segment xriSegments[] = new XRI3Segment [] {
				new XRI3Segment("($)"),
				new XRI3Segment("($1)"),
				new XRI3Segment("($34)"),
				new XRI3Segment("!($)"),
				new XRI3Segment("+($)"),
				new XRI3Segment("*($)"),
				new XRI3Segment("(!)"),
				new XRI3Segment("(!12)"),
				new XRI3Segment("(=abc)"),
				new XRI3Segment("($)$1"),
				new XRI3Segment("($)()"),
				new XRI3Segment("$1"),
				new XRI3Segment("$()"),
				new XRI3Segment("$()"),
				new XRI3Segment("($$)"),
				new XRI3Segment("($$!)"),
				new XRI3Segment("($1$!)"),
				new XRI3Segment("($34$!)"),
		};

		boolean isVariableSingle[] = new boolean [] {
				true,
				true,
				true,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false
		};

		boolean isVariableMultiple[] = new boolean [] {
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				true,
				false,
				false,
				false
		};

		boolean isVariableMultipleLocal[] = new boolean [] {
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				true,
				true,
				true
		};

		assertEquals(xriSegments.length, isVariableSingle.length);
		assertEquals(xriSegments.length, isVariableMultipleLocal.length);

		for (int i=0; i<xriSegments.length; i++) {

			if (isVariableSingle[i]) {

				assertTrue(Variables.isVariableSingle(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableSingle((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableSingle(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableSingle((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			}

			if (isVariableMultiple[i]) {

				assertTrue(Variables.isVariableMultiple(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableMultiple((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableMultiple(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableMultiple((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			}

			if (isVariableMultipleLocal[i]) {

				assertTrue(Variables.isVariableMultipleLocal(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableMultipleLocal((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableMultipleLocal(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableMultipleLocal((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			}
		}
	}
}
