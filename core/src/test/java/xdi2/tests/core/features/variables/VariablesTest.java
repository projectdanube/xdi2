package xdi2.tests.core.features.variables;

import junit.framework.TestCase;
import xdi2.core.features.variables.Variables;
import xdi2.core.xri3.XDI3Segment;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		XDI3Segment xriSegments[] = new XDI3Segment[] {
				XDI3Segment.create("($)"),
				XDI3Segment.create("($1)"),
				XDI3Segment.create("($34)"),
				XDI3Segment.create("!($)"),
				XDI3Segment.create("+($)"),
				XDI3Segment.create("*($)"),
				XDI3Segment.create("(!)"),
				XDI3Segment.create("(!12)"),
				XDI3Segment.create("(=abc)"),
				XDI3Segment.create("($)$1"),
				XDI3Segment.create("($)()"),
				XDI3Segment.create("$1"),
				XDI3Segment.create("$()"),
				XDI3Segment.create("$()"),
				XDI3Segment.create("($$)"),
				XDI3Segment.create("($$!)"),
				XDI3Segment.create("($1$!)"),
				XDI3Segment.create("($34$!)"),
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
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableSingle(xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableSingle(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableSingle(xriSegments[i].getFirstSubSegment()));
			}

			if (isVariableMultiple[i]) {

				assertTrue(Variables.isVariableMultiple(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableMultiple(xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableMultiple(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableMultiple(xriSegments[i].getFirstSubSegment()));
			}

			if (isVariableMultipleLocal[i]) {

				assertTrue(Variables.isVariableMultipleLocal(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariableMultipleLocal(xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariableMultipleLocal(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariableMultipleLocal(xriSegments[i].getFirstSubSegment()));
			}
		}
	}
}
