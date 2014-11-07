package xdi2.tests.core.features.variables;

import java.util.Arrays;

import junit.framework.TestCase;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.VariableUtil;

public class VariablesTest extends TestCase {

	public void testVariables() throws Exception {

		XDIArc variables[] = new XDIArc[] {
				XDIArc.create("{}"),
				XDIArc.create("{{}}"),
				XDIArc.create("{1}"),
				XDIArc.create("{$msg}"),
				XDIArc.create("{(=)}"),
				XDIArc.create("{{(=@)}}"),
				XDIArc.create("{=}"),
				XDIArc.create("{!*}"),
				XDIArc.create("{{[+]}}"),
				XDIArc.create("{{<$>}}"),
				XDIArc.create("{{[+]<$>}}"),
				XDIArc.create("{[<+>][<$>]}"),
				XDIArc.create("{+}")
		};

		XDIArc variablesArcs[][] = new XDIArc[][] {
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { XDIArc.create("$msg") },
				new XDIArc[] { XDIArc.create("=") },
				new XDIArc[] { XDIArc.create("="), XDIArc.create("@") },
				new XDIArc[] { XDIArc.create("=") },
				new XDIArc[] { XDIArc.create("!"), XDIArc.create("*") },
				new XDIArc[] { XDIArc.create("[+]") },
				new XDIArc[] { XDIArc.create("<$>") },
				new XDIArc[] { XDIArc.create("[+]"), XDIArc.create("<$>") },
				new XDIArc[] { XDIArc.create("[<+>]"), XDIArc.create("[<$>]") },
				new XDIArc[] { XDIArc.create("+") }
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

		XDIArc variablesMatchesTrue[][] = new XDIArc[][] {
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { XDIArc.create("(=)"), XDIArc.create("(=markus)") },
				new XDIArc[] { XDIArc.create("(=)"), XDIArc.create("(=markus)"), XDIArc.create("(@)"), XDIArc.create("(@org)") },
				new XDIArc[] { XDIArc.create("="), XDIArc.create("=markus") },
				new XDIArc[] { XDIArc.create("!"), XDIArc.create("*"), XDIArc.create("!1234"), XDIArc.create("*work") },
				new XDIArc[] { XDIArc.create("[+]"), XDIArc.create("[+address]"), XDIArc.create("[<#email>]") },
				new XDIArc[] { XDIArc.create("<$>"), XDIArc.create("<$uri>"), XDIArc.create("[<$uri>]") },
				new XDIArc[] { XDIArc.create("[+]"), XDIArc.create("[+address]"), XDIArc.create("[<#email>]"), XDIArc.create("<$>"), XDIArc.create("<$uri>"), XDIArc.create("[<$uri>]") },
				new XDIArc[] { XDIArc.create("[<#email>]"), XDIArc.create("[<$uri>]") },
				new XDIArc[] { XDIArc.create("[<+(email)>]"), XDIArc.create("<+(email)>"), XDIArc.create("[<#email>]"), XDIArc.create("<#email>") }
		};

		XDIArc variablesMatchesFalse[][] = new XDIArc[][] {
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { },
				new XDIArc[] { XDIArc.create("(@)"), XDIArc.create("(@org)"), XDIArc.create("@org"), XDIArc.create("=markus") },
				new XDIArc[] { XDIArc.create("=markus"), XDIArc.create("@org") },
				new XDIArc[] { XDIArc.create("@"), XDIArc.create("@org"), XDIArc.create("(@org)"), XDIArc.create("(=markus)") },
				new XDIArc[] { XDIArc.create("@org"), XDIArc.create("=markus") },
				new XDIArc[] { XDIArc.create("+"), XDIArc.create("<+>"), XDIArc.create("+address"), XDIArc.create("<#email>"), XDIArc.create("[$v]"), XDIArc.create("[<$uri>]") },
				new XDIArc[] { XDIArc.create("$"), XDIArc.create("[$]"), XDIArc.create("$uri"), XDIArc.create("[$uri]"), XDIArc.create("<#email>"), XDIArc.create("[<#email>]") },
				new XDIArc[] { XDIArc.create("+"), XDIArc.create("<+>"), XDIArc.create("+address"), XDIArc.create("<#email>"), XDIArc.create("[$v]"), XDIArc.create("$"), XDIArc.create("[$]"), XDIArc.create("$uri"), XDIArc.create("[$uri]"), XDIArc.create("<#email>") },
				new XDIArc[] { XDIArc.create("+"), XDIArc.create("<+>"), XDIArc.create("+address"), XDIArc.create("<#email>"), XDIArc.create("<#email>"), XDIArc.create("$"), XDIArc.create("<$>"), XDIArc.create("$uri"), XDIArc.create("[$uri]"), XDIArc.create("<$uri>") },
				new XDIArc[] { XDIArc.create("(#name)") }
		};
		
		for (XDIArc variable : variables) assertTrue(VariableUtil.isVariable(variable));

		assertEquals(variables.length, variablesArcs.length);
		assertEquals(variables.length, variablesXs.length);
		assertEquals(variables.length, variablesMultiple.length);
		assertEquals(variables.length, variablesMatchesTrue.length);
		assertEquals(variables.length, variablesMatchesFalse.length);

		for (int i=0; i<variables.length; i++) {

			assertTrue(Arrays.deepEquals(variablesArcs[i], VariableUtil.getArcs(variables[i]).toArray()));
			assertEquals(variablesXs[i], VariableUtil.getXs(variables[i]));
			assertEquals(variablesMultiple[i], VariableUtil.isMultiple(variables[i]));

			for (int ii=0; ii<variablesMatchesTrue[i].length; ii++)
				assertTrue(VariableUtil.matches(variables[i], variablesMatchesTrue[i][ii]));

			for (int ii=0; ii<variablesMatchesFalse[i].length; ii++)
				assertFalse(VariableUtil.matches(variables[i], variablesMatchesFalse[i][ii]));
		}
	}
}
