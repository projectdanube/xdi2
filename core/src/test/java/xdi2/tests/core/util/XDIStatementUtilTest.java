package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIStatementUtil;

public class XDIStatementUtilTest extends TestCase {

	public void testStatementUtil() throws Exception {

		String contextNodeStatements[] = new String[] {
				"=markus//[<#email>]",
				"=markus//",
				"//=markus"
		};

		String relationStatements[] = new String[] {
				"=markus/#friend/=animesh",
				"=markus/$ref/[=]!1111",
				"()/$is$ref/[=]!1111",
				"[=]!1111/$ref/",
				"[=]!1111#tel/#home#fax/[=]!1111#tel!1"
		};

		String literalStatements[] = new String[] {
				"=markus<#name>&/&/\"Markus Sabadello\"",
				"[=]!1111<#tel>&/&/\"+1-206-555-1212\"",
				"[=]!1111<#tel>[1]&/&/\"+1.206.555.1111\""
		};

		String invalidStatements[] = new String[] {
				"=markus&/&/=markus",
				"=markus&/&/{}"
		};

		for (String contextNodeStatement : contextNodeStatements) {

			assertTrue(XDIStatement.create(contextNodeStatement).isContextNodeStatement());
			assertFalse(XDIStatement.create(contextNodeStatement).isRelationStatement());
			assertFalse(XDIStatement.create(contextNodeStatement).isLiteralStatement());
		}

		for (String relationStatement : relationStatements) {

			assertFalse(XDIStatement.create(relationStatement).isContextNodeStatement());
			assertTrue(XDIStatement.create(relationStatement).isRelationStatement());
			assertFalse(XDIStatement.create(relationStatement).isLiteralStatement());
		}

		for (String literalStatement : literalStatements) {

			assertFalse(XDIStatement.create(literalStatement).isContextNodeStatement());
			assertFalse(XDIStatement.create(literalStatement).isRelationStatement());
			assertTrue(XDIStatement.create(literalStatement).isLiteralStatement());
		}

		for (String invalidStatement : invalidStatements) {

			Graph graph = null;

			try {

				graph = MemoryGraphFactory.getInstance().openGraph();
				graph.setStatement(XDIStatement.create(invalidStatement));

				fail();

				graph.close();
			} catch (Exception ex) {

				if (graph != null) graph.close();
			}
		} 
	}

	public void testremoveStartAddressStatement() throws Exception {

		XDIStatement contextStatement = XDIStatement.create("=markus<#full>//<#name>");

		XDIStatement reducedContextStatement = XDIStatementUtil.removeStartXDIStatement(contextStatement, XDIAddress.create("=markus"));

		assertEquals(reducedContextStatement, XDIStatement.create("<#full>//<#name>"));
		assertEquals(reducedContextStatement.getSubject(), XDIAddress.create("<#full>"));
		assertEquals(reducedContextStatement.getPredicate(), XDIAddress.create(""));
		assertEquals(reducedContextStatement.getObject(), "<#name>");

		assertEquals(XDIStatementUtil.removeStartXDIStatement(reducedContextStatement, XDIAddress.create("{}"), false, true), XDIStatement.create("//<#name>"));

		XDIStatement literalStatement = XDIStatement.create("=markus<#name>&/&/\"Markus Sabadello\"");

		XDIStatement reducedLiteralStatement = XDIStatementUtil.removeStartXDIStatement(literalStatement, XDIAddress.create("=markus"));

		assertEquals(reducedLiteralStatement, XDIStatement.create("<#name>&/&/\"Markus Sabadello\""));
		assertEquals(reducedLiteralStatement.getSubject(), XDIAddress.create("<#name>&"));
		assertEquals(reducedLiteralStatement.getPredicate(), XDIAddress.create("&"));
		assertEquals(reducedLiteralStatement.getObject(), "Markus Sabadello");

		assertEquals(XDIStatementUtil.removeStartXDIStatement(reducedLiteralStatement, XDIAddress.create("{}"), false, true), XDIStatement.create("&/&/\"Markus Sabadello\""));
		assertEquals(XDIStatementUtil.removeStartXDIStatement(reducedLiteralStatement, XDIAddress.create("{}{}"), false, true), XDIStatement.create("/&/\"Markus Sabadello\""));

		XDIStatement relationStatement = XDIStatement.create("=markus<#name>/$ref/=markus<#full><#name>");

		XDIStatement reducedRelationStatement1 = XDIStatementUtil.removeStartXDIStatement(relationStatement, XDIAddress.create("=markus"));

		assertEquals(reducedRelationStatement1, XDIStatement.create("<#name>/$ref/=markus<#full><#name>"));
		assertEquals(reducedRelationStatement1.getSubject(), XDIAddress.create("<#name>"));
		assertEquals(reducedRelationStatement1.getPredicate(), XDIAddress.create("$ref"));
		assertEquals(reducedRelationStatement1.getObject(), "=markus<#full><#name>");

		assertEquals(XDIStatementUtil.removeStartXDIStatement(reducedRelationStatement1, XDIAddress.create("{}"), false, true), XDIStatement.create("/$ref/=markus<#full><#name>"));

		XDIStatement reducedRelationStatement2 = XDIStatementUtil.removeStartXDIStatement(relationStatement, XDIAddress.create("=markus"));

		assertEquals(reducedRelationStatement2, XDIStatement.create("<#name>/$ref/=markus<#full><#name>"));
		assertEquals(reducedRelationStatement2.getSubject(), XDIAddress.create("<#name>"));
		assertEquals(reducedRelationStatement2.getPredicate(), XDIAddress.create("$ref"));
		assertEquals(reducedRelationStatement2.getObject(), "=markus<#full><#name>");

		assertEquals(XDIStatementUtil.removeStartXDIStatement(reducedRelationStatement2, XDIAddress.create("{}"), false, true), XDIStatement.create("/$ref/=markus<#full><#name>"));
	}
}
