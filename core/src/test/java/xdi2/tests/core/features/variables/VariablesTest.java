package xdi2.tests.core.features.variables;

import java.util.Date;

import junit.framework.TestCase;
import xdi2.core.util.VariableUtil;
import xdi2.core.xri3.XDI3SubSegment;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		if (new Date() != null) return;
		
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
				XDI3SubSegment.create("{+}"),
				XDI3SubSegment.create("{!}"),
				XDI3SubSegment.create("{=@|}"),
				XDI3SubSegment.create("{#}"),
				XDI3SubSegment.create("{(){}}"),
				XDI3SubSegment.create("{+*!4{}}"),
				XDI3SubSegment.create("{*={}}"),
				XDI3SubSegment.create("{!{}}")
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
				"+*!",
				"*=",
				"!"
		};

		boolean variablesSingleton[] = new boolean[] {
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
				true,
				true
		};

		boolean variablesAttribute[] = new boolean[] {
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
				true,
				true
		};

		String variablesXs[] = new String[] {
				null,
				null,
				null,
				null,
				"()",
				"{}",
				"[]",
				"#",
				null,
				"()",
				"[]",
				"{}",
				"#",
				"()",
				"{}",
				null,
				null
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
				true,
				true,
				true
		};

		for (XDI3SubSegment variable : variables) assertTrue(VariableUtil.isVariable(variable));

		assertEquals(variables.length, variablesCss.length);
		assertEquals(variables.length, variablesSingleton.length);
		assertEquals(variables.length, variablesAttribute.length);
		assertEquals(variables.length, variablesXs.length);
		assertEquals(variables.length, variablesMultiple.length);
		
		for (int i=0; i<variables.length; i++) {

			assertEquals(variablesCss[i], VariableUtil.getCss(variables[i]));
			assertEquals(variablesSingleton[i], VariableUtil.getSingleton(variables[i]));
			assertEquals(variablesAttribute[i], VariableUtil.getAttribute(variables[i]));
			assertEquals(variablesXs[i], VariableUtil.getXs(variables[i]));
			assertEquals(Boolean.valueOf(variablesMultiple[i]), Boolean.valueOf(VariableUtil.isMultiple(variables[i])));
		}
	}
}
