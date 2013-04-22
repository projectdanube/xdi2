package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.StatementUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public class StatementUtilTest extends TestCase {

	public void testStatementUtil() throws Exception {

		String contextNodeStatements[] = new String[] {
				"=markus/()/[<+email>]",
				"=markus/()/()",
				"()/()/=markus"
		};

		String relationStatements[] = new String[] {
				"=markus/+friend/=animesh",
				"=markus/$ref/[=]!1111",
				"()/$is$ref/[=]!1111",
				"[=]!1111/$ref/()",
				"[=]!1111+tel/+home+fax/[=]!1111+tel!1"
		};

		String literalStatements[] = new String[] {
				"=markus<+name>&/&/\"Markus Sabadello\"",
				"[=]!1111<+tel>&/&/\"+1-206-555-1212\"",
				"[=]!1111<+tel>[1]&/&/\"+1.206.555.1111\""
		};

		String invalidStatements[] = new String[] {
				"=markus&/&/=markus",
				"=markus&/&/{}"
		};

		for (String contextNodeStatement : contextNodeStatements) {

			assertTrue(XDI3Statement.create(contextNodeStatement).isContextNodeStatement());
			assertFalse(XDI3Statement.create(contextNodeStatement).isRelationStatement());
			assertFalse(XDI3Statement.create(contextNodeStatement).isLiteralStatement());
		}

		for (String relationStatement : relationStatements) {

			assertFalse(XDI3Statement.create(relationStatement).isContextNodeStatement());
			assertTrue(XDI3Statement.create(relationStatement).isRelationStatement());
			assertFalse(XDI3Statement.create(relationStatement).isLiteralStatement());
		}

		for (String literalStatement : literalStatements) {

			assertFalse(XDI3Statement.create(literalStatement).isContextNodeStatement());
			assertFalse(XDI3Statement.create(literalStatement).isRelationStatement());
			assertTrue(XDI3Statement.create(literalStatement).isLiteralStatement());
		}

		for (String invalidStatement : invalidStatements) {

			Graph graph = null;

			try {

				graph = MemoryGraphFactory.getInstance().openGraph();
				graph.createStatement(XDI3Statement.create(invalidStatement));

				fail();
			} catch (Exception ex) {

				if (graph != null) graph.close();
			}
		} 
	}

	public void testComponents() throws Exception {

		XDI3Statement contextNodeStatement = XDI3Statement.create("=markus/()/[<+email>]");
		XDI3Statement contextNodeStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus"), XDIConstants.XRI_S_CONTEXT, XDI3Segment.create("[<+email>]"));
		XDI3Statement contextNodeStatement3 = StatementUtil.fromContextNodeComponents(XDI3Segment.create("=markus"), XDI3Segment.create("[<+email>]"));

		assertEquals(contextNodeStatement.getSubject(), XDI3Segment.create("=markus"));
		assertEquals(contextNodeStatement.getPredicate(), XDIConstants.XRI_S_CONTEXT);
		assertEquals(contextNodeStatement.getObject(), XDI3Segment.create("[<+email>]"));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		XDI3Statement relationStatement = XDI3Statement.create("=markus/+friend/=animesh");
		XDI3Statement relationStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
		XDI3Statement relationStatement3 = StatementUtil.fromRelationComponents(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertEquals(relationStatement.getSubject(), XDI3Segment.create("=markus"));
		assertEquals(relationStatement.getPredicate(), XDI3Segment.create("+friend"));
		assertEquals(relationStatement.getObject(), XDI3Segment.create("=animesh"));

		XDI3Statement literalStatement = XDI3Statement.create("=markus<+name>&/&/\"Markus Sabadello\"");
		XDI3Statement literalStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus<+name>&"), XDIConstants.XRI_S_LITERAL, "Markus Sabadello");
		XDI3Statement literalStatement3 = StatementUtil.fromLiteralComponents(XDI3Segment.create("=markus<+name>&"), "Markus Sabadello");

		assertEquals(literalStatement.getSubject(), XDI3Segment.create("=markus<+name>&"));
		assertEquals(literalStatement.getPredicate(), XDIConstants.XRI_S_LITERAL);
		assertEquals(literalStatement.getObject(), "Markus Sabadello");

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public void testReduceStatement() throws Exception {

		XDI3Statement contextStatement = XDI3Statement.create("=markus+full/()/<+name>");

		XDI3Statement reducedContextStatement = StatementUtil.reduceStatement(contextStatement, XDI3Segment.create("=markus"));

		assertEquals(reducedContextStatement, XDI3Statement.create("+full/()/<+name>"));
		assertEquals(reducedContextStatement.getSubject(), XDI3Segment.create("+full"));
		assertEquals(reducedContextStatement.getPredicate(), XDI3Segment.create("()"));
		assertEquals(reducedContextStatement.getObject(), "<+name>");

		assertNull(StatementUtil.reduceStatement(reducedContextStatement, XDI3Segment.create("{}"), false, true));

		XDI3Statement literalStatement = XDI3Statement.create("=markus<+name>&/&/\"Markus Sabadello\"");

		XDI3Statement reducedLiteralStatement = StatementUtil.reduceStatement(literalStatement, XDI3Segment.create("=markus"));

		assertEquals(reducedLiteralStatement, XDI3Statement.create("<+name>&/&/\"Markus Sabadello\""));
		assertEquals(reducedLiteralStatement.getSubject(), XDI3Segment.create("<+name>&"));
		assertEquals(reducedLiteralStatement.getPredicate(), XDI3Segment.create("&"));
		assertEquals(reducedLiteralStatement.getObject(), "Markus Sabadello");

		assertEquals(StatementUtil.reduceStatement(reducedLiteralStatement, XDI3Segment.create("{}"), false, true), XDI3Statement.create("&/&/\"Markus Sabadello\""));
		assertNull(StatementUtil.reduceStatement(reducedLiteralStatement, XDI3Segment.create("{}{}"), false, true));

		XDI3Statement relationStatement = XDI3Statement.create("=markus<+name>/$ref/=markus+full<+name>");

		XDI3Statement reducedRelationStatement = StatementUtil.reduceStatement(relationStatement, XDI3Segment.create("=markus"));

		assertEquals(reducedRelationStatement, XDI3Statement.create("<+name>/$ref/+full<+name>"));
		assertEquals(reducedRelationStatement.getSubject(), XDI3Segment.create("<+name>"));
		assertEquals(reducedRelationStatement.getPredicate(), XDI3Segment.create("$ref"));
		assertEquals(reducedRelationStatement.getObject(), "+full<+name>");

		assertNull(StatementUtil.reduceStatement(reducedRelationStatement, XDI3Segment.create("{}"), false, true));
	}
}
