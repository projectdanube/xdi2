package xdi2.tests.core.features.variables;

import java.util.Arrays;

import junit.framework.TestCase;
import xdi2.core.util.VariableUtil;
import xdi2.core.xri3.XDI3SubSegment;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		XDI3SubSegment variables[] = new XDI3SubSegment[] {
				XDI3SubSegment.create("{}"),
				XDI3SubSegment.create("{{}}"),
				XDI3SubSegment.create("{1}"),
				XDI3SubSegment.create("{$msg}"),
				XDI3SubSegment.create("{(=)}"),
				XDI3SubSegment.create("{{(=@)}}"),
				XDI3SubSegment.create("{=}"),
				XDI3SubSegment.create("{!*}"),
				XDI3SubSegment.create("{{[+]}}"),
				XDI3SubSegment.create("{{<$>}}"),
				XDI3SubSegment.create("{{[+]<$>}}"),
				XDI3SubSegment.create("{[<+>][<$>]}"),
				XDI3SubSegment.create("{+}")
		};

		XDI3SubSegment variablesSubSegments[][] = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("$msg") },
				new XDI3SubSegment[] { XDI3SubSegment.create("=") },
				new XDI3SubSegment[] { XDI3SubSegment.create("="), XDI3SubSegment.create("@") },
				new XDI3SubSegment[] { XDI3SubSegment.create("=") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!"), XDI3SubSegment.create("*") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("<$>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+]"), XDI3SubSegment.create("<$>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[<+>]"), XDI3SubSegment.create("[<$>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+") }
		};

		String variablesXs[] = new String[] {
				null,
				null,
				null,
				null,
				"()",
				"()",
				null,
				null,
				null,
				null,
				null,
				null,
				null
		};

		boolean variablesMultiple[] = new boolean[] {
				false,
				true,
				false,
				false,
				false,
				true,
				false,
				false,
				true,
				true,
				true,
				false,
				false
		};

		XDI3SubSegment variablesMatchesTrue[][] = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("(=)"), XDI3SubSegment.create("(=markus)") },
				new XDI3SubSegment[] { XDI3SubSegment.create("(=)"), XDI3SubSegment.create("(=markus)"), XDI3SubSegment.create("(@)"), XDI3SubSegment.create("(@org)") },
				new XDI3SubSegment[] { XDI3SubSegment.create("="), XDI3SubSegment.create("=markus") },
				new XDI3SubSegment[] { XDI3SubSegment.create("!"), XDI3SubSegment.create("*"), XDI3SubSegment.create("!1234"), XDI3SubSegment.create("*work") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+]"), XDI3SubSegment.create("[+address]"), XDI3SubSegment.create("[<+email>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("<$>"), XDI3SubSegment.create("<$uri>"), XDI3SubSegment.create("[<$uri>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[+]"), XDI3SubSegment.create("[+address]"), XDI3SubSegment.create("[<+email>]"), XDI3SubSegment.create("<$>"), XDI3SubSegment.create("<$uri>"), XDI3SubSegment.create("[<$uri>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[<+email>]"), XDI3SubSegment.create("[<$uri>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("[<+(email)>]"), XDI3SubSegment.create("<+(email)>"), XDI3SubSegment.create("[<+email>]"), XDI3SubSegment.create("<+email>") }
		};

		XDI3SubSegment variablesMatchesFalse[][] = new XDI3SubSegment[][] {
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { },
				new XDI3SubSegment[] { XDI3SubSegment.create("(@)"), XDI3SubSegment.create("(@org)"), XDI3SubSegment.create("@org"), XDI3SubSegment.create("=markus") },
				new XDI3SubSegment[] { XDI3SubSegment.create("=markus"), XDI3SubSegment.create("@org") },
				new XDI3SubSegment[] { XDI3SubSegment.create("@"), XDI3SubSegment.create("@org"), XDI3SubSegment.create("(@org)"), XDI3SubSegment.create("(=markus)") },
				new XDI3SubSegment[] { XDI3SubSegment.create("@org"), XDI3SubSegment.create("=markus") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+"), XDI3SubSegment.create("<+>"), XDI3SubSegment.create("+address"), XDI3SubSegment.create("<+email>"), XDI3SubSegment.create("[$v]"), XDI3SubSegment.create("[<$uri>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("$"), XDI3SubSegment.create("[$]"), XDI3SubSegment.create("$uri"), XDI3SubSegment.create("[$uri]"), XDI3SubSegment.create("<+email>"), XDI3SubSegment.create("[<+email>]") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+"), XDI3SubSegment.create("<+>"), XDI3SubSegment.create("+address"), XDI3SubSegment.create("<+email>"), XDI3SubSegment.create("[$v]"), XDI3SubSegment.create("$"), XDI3SubSegment.create("[$]"), XDI3SubSegment.create("$uri"), XDI3SubSegment.create("[$uri]"), XDI3SubSegment.create("<+email>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("+"), XDI3SubSegment.create("<+>"), XDI3SubSegment.create("+address"), XDI3SubSegment.create("<+email>"), XDI3SubSegment.create("<+email>"), XDI3SubSegment.create("$"), XDI3SubSegment.create("<$>"), XDI3SubSegment.create("$uri"), XDI3SubSegment.create("[$uri]"), XDI3SubSegment.create("<$uri>") },
				new XDI3SubSegment[] { XDI3SubSegment.create("(+name)") }
		};
		
		for (XDI3SubSegment variable : variables) assertTrue(VariableUtil.isVariable(variable));

		assertEquals(variables.length, variablesSubSegments.length);
		assertEquals(variables.length, variablesXs.length);
		assertEquals(variables.length, variablesMultiple.length);
		assertEquals(variables.length, variablesMatchesTrue.length);
		assertEquals(variables.length, variablesMatchesFalse.length);

		for (int i=0; i<variables.length; i++) {

			assertTrue(Arrays.deepEquals(variablesSubSegments[i], VariableUtil.getSubSegments(variables[i]).toArray()));
			assertEquals(variablesXs[i], VariableUtil.getXs(variables[i]));
			assertEquals(variablesMultiple[i], VariableUtil.isMultiple(variables[i]));

			for (int ii=0; ii<variablesMatchesTrue[i].length; ii++)
				assertTrue(VariableUtil.matches(variables[i], variablesMatchesTrue[i][ii]));

			for (int ii=0; ii<variablesMatchesFalse[i].length; ii++)
				assertFalse(VariableUtil.matches(variables[i], variablesMatchesFalse[i][ii]));
		}
	}
}
