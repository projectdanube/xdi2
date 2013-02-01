package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public class StatementUtilTest extends TestCase {

	public void testStatementUtil() throws Exception {

		String contextNodeStatements[] = new String[] {
				"=markus/()/+email",
				"()/()/=markus"
		};

		String relationStatements[] = new String[] {
				"=markus/+friend/=animesh",
				"=markus/$is/=!1111",
				"()/$is$is/=!1111",
				"=!1111/$is/()",
				"=!1111$*(+tel)/+home+fax/=!1111$*(+tel)$!1"
		};

		String literalStatements[] = new String[] {
				"=markus+name/!/(data:,Markus%20Sabadello)",
				"=!1111$!(+tel)/!/(data:,+1-206-555-1212)",
				"=!1111$*(+tel)$!1/!/(data:,+1.206.555.1111)"
		};

		String invalidStatements[] = new String[] {
				"=markus/!/=markus",
				"=markus/()/()",
				"=markus/!/($)"
		};

		for (String contextNodeStatement : contextNodeStatements) assertTrue(XDI3Statement.create(contextNodeStatement).isContextNodeStatement());
		for (String relationStatement : relationStatements) assertTrue(XDI3Statement.create(relationStatement).isRelationStatement());
		for (String literalStatement : literalStatements) assertTrue(XDI3Statement.create(literalStatement).isLiteralStatement());

		for (String invalidStatement : invalidStatements) {

			Graph graph = null;

			try {

				graph = MemoryGraphFactory.getInstance().openGraph();
				graph.createStatement(XDI3Statement.create(invalidStatement));

				fail();
			} catch (Exception ex) {

				graph.close();
			}
		} 
	}

	public void testComponents() throws Exception {

		XDI3Statement contextNodeStatement = XDI3Statement.create("=markus/()/+email");
		XDI3Statement contextNodeStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus"), XDIConstants.XRI_S_CONTEXT, XDI3Segment.create("+email"));
		XDI3Statement contextNodeStatement3 = StatementUtil.fromContextNodeComponents(XDI3Segment.create("=markus"), XDI3Segment.create("+email"));

		assertTrue(contextNodeStatement.getSubject().equals(XDI3Segment.create("=markus")));
		assertTrue(contextNodeStatement.getPredicate().equals(XDIConstants.XRI_S_CONTEXT));
		assertTrue(contextNodeStatement.getObject().equals(XDI3Segment.create("+email")));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		XDI3Statement relationStatement = XDI3Statement.create("=markus/+friend/=animesh");
		XDI3Statement relationStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));
		XDI3Statement relationStatement3 = StatementUtil.fromRelationComponents(XDI3Segment.create("=markus"), XDI3Segment.create("+friend"), XDI3Segment.create("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertTrue(relationStatement.getSubject().equals(XDI3Segment.create("=markus")));
		assertTrue(relationStatement.getPredicate().equals(XDI3Segment.create("+friend")));
		assertTrue(relationStatement.getObject().equals(XDI3Segment.create("=animesh")));

		XDI3Statement literalStatement = XDI3Statement.create("=markus+name/!/(data:,Markus%20Sabadello)");
		XDI3Statement literalStatement2 = StatementUtil.fromComponents(XDI3Segment.create("=markus+name"), XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment("Markus Sabadello"));
		XDI3Statement literalStatement3 = StatementUtil.fromLiteralComponents(XDI3Segment.create("=markus+name"), "Markus Sabadello");

		assertTrue(literalStatement.getSubject().equals(XDI3Segment.create("=markus+name")));
		assertTrue(literalStatement.getPredicate().equals(XDIConstants.XRI_S_LITERAL));
		assertTrue(literalStatement.getObject().equals(XDI3Segment.create("(data:,Markus%20Sabadello)")));

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public void testRelative() throws Exception {

		XDI3Statement literalStatement = XDI3Statement.create("=markus+name/!/(data:,Markus%20Sabadello)");

		XDI3Statement relativeXDI3Statement = StatementUtil.relativeStatement(literalStatement, XDI3Segment.create("=markus"));

		assertTrue(relativeXDI3Statement.getSubject().equals(XDI3Segment.create("+name")));
		assertTrue(relativeXDI3Statement.getPredicate().equals(XDI3Segment.create("!")));
		assertTrue(relativeXDI3Statement.getObject().equals(XDI3Segment.create("(data:,Markus%20Sabadello)")));

		assertNull(StatementUtil.relativeStatement(relativeXDI3Statement, XDI3Segment.create("($)"), false, true));
	}
}
