package xdi2.tests.core.variables;

import junit.framework.TestCase;
import xdi2.core.features.variables.Variables;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class VariablesUtilTest extends TestCase {

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
				new XRI3Segment("$()")
		};

		boolean isVariable[] = new boolean [] {
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
				false
		};

		assertEquals(xriSegments.length, isVariable.length);

		for (int i=0; i<xriSegments.length; i++) {

			if (isVariable[i]) {

				assertTrue(Variables.isVariable(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertTrue(Variables.isVariable((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			} else {

				assertFalse(Variables.isVariable(xriSegments[i]));
				if (xriSegments[i].getNumSubSegments() == 1) assertFalse(Variables.isVariable((XRI3SubSegment) xriSegments[i].getFirstSubSegment()));
			}
		}
	}
}
