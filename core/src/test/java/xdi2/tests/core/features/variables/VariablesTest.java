package xdi2.tests.core.features.variables;

import junit.framework.TestCase;
import xdi2.core.util.VariableUtil;
import xdi2.core.xri3.XDI3SubSegment;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		XDI3SubSegment variables[] = new XDI3SubSegment[] {
				XDI3SubSegment.create("{}"),
				XDI3SubSegment.create("{1}"),
				XDI3SubSegment.create("{$msg}"),
				XDI3SubSegment.create("{$from}"),
				XDI3SubSegment.create("{()}"),
				XDI3SubSegment.create("{{}}"),
				XDI3SubSegment.create("{[]}"),
				XDI3SubSegment.create("{<>}"),
				XDI3SubSegment.create("{=}"),
				XDI3SubSegment.create("{(+)}"),
				XDI3SubSegment.create("{[!]}"),
				XDI3SubSegment.create("{{=@}}"),
				XDI3SubSegment.create("{<>}"),
				XDI3SubSegment.create("{(){}}"),
				XDI3SubSegment.create("{{+*!4}{}}")
		};

		String variablesCf[] = new String[] {
				null,
				null,
				null,
				null,
				"()",
				"{}",
				"[]",
				"<>",
				null,
				"()",
				"[]",
				"{}",
				"<>",
				"()",
				"{}"
		};

		String variablesCss[] = new String[] {
				"",
				"",
				"$",
				"$",
				"",
				"",
				"",
				"",
				"=",
				"+",
				"!",
				"=@",
				"",
				"",
				"+*!"
		};

		boolean variablesMultiple[] = new boolean[] {
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
				true
		};

		for (XDI3SubSegment variable : variables) assertTrue(VariableUtil.isVariable(variable));

		assertEquals(variables.length, variablesCf.length);
		assertEquals(variables.length, variablesCss.length);
		assertEquals(variables.length, variablesMultiple.length);
		
		for (int i=0; i<variables.length; i++) {
System.err.println("" + i + ": " + VariableUtil.getCss(variables[i]));
			assertEquals(variablesCf[i], VariableUtil.getCf(variables[i]));
			assertEquals(variablesCss[i], VariableUtil.getCss(variables[i]));
			assertEquals(Boolean.valueOf(variablesMultiple[i]), Boolean.valueOf(VariableUtil.isMultiple(variables[i])));
		}
	}
}
