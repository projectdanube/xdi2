package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3Statement;

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
				"=!1111$*(+tel)/+home+fax/=!1111$*(+tel)$!1",
				"=markus/!/($)",
		};

		String literalStatements[] = new String[] {
				"=markus+name/!/(data:,Markus%20Sabadello)",
				"=!1111$!(+tel)/!/(data:,+1-206-555-1212)",
				"=!1111$*(+tel)$!1/!/(data:,+1.206.555.1111)"
		};

		String invalidStatements[] = new String[] {
				"=markus/!/=markus",
				"=markus/()/()"
		};

		for (String contextNodeStatement : contextNodeStatements) assertTrue(new XDI3Statement(contextNodeStatement).isContextNodeStatement());
		for (String relationStatement : relationStatements) assertTrue(new XDI3Statement(relationStatement).isRelationStatement());
		for (String literalStatement : literalStatements) assertTrue(new XDI3Statement(literalStatement).isLiteralStatement());

		for (String invalidStatement : invalidStatements) {

			Graph graph = null;

			try {

				graph = MemoryGraphFactory.getInstance().openGraph();
				graph.createStatement(new XDI3Statement(invalidStatement));

				fail();
			} catch (Exception ex) {

				graph.close();
			}
		} 
	}

	public void testComponents() throws Exception {

		XDI3Statement contextNodeStatement = new XDI3Statement("=markus/()/+email");
		XDI3Statement contextNodeStatement2 = StatementUtil.fromComponents(new XDI3Segment("=markus"), XDIConstants.XRI_S_CONTEXT, new XDI3Segment("+email"));
		XDI3Statement contextNodeStatement3 = StatementUtil.fromContextNodeComponents(new XDI3Segment("=markus"), new XDI3Segment("+email"));

		assertTrue(contextNodeStatement.getSubject().equals(new XDI3Segment("=markus")));
		assertTrue(contextNodeStatement.getPredicate().equals(XDIConstants.XRI_S_CONTEXT));
		assertTrue(contextNodeStatement.getObject().equals(new XDI3Segment("+email")));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		XDI3Statement relationStatement = new XDI3Statement("=markus/+friend/=animesh");
		XDI3Statement relationStatement2 = StatementUtil.fromComponents(new XDI3Segment("=markus"), new XDI3Segment("+friend"), new XDI3Segment("=animesh"));
		XDI3Statement relationStatement3 = StatementUtil.fromRelationComponents(new XDI3Segment("=markus"), new XDI3Segment("+friend"), new XDI3Segment("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertTrue(relationStatement.getSubject().equals(new XDI3Segment("=markus")));
		assertTrue(relationStatement.getPredicate().equals(new XDI3Segment("+friend")));
		assertTrue(relationStatement.getObject().equals(new XDI3Segment("=animesh")));

		XDI3Statement literalStatement = new XDI3Statement("=markus+name/!/(data:,Markus%20Sabadello)");
		XDI3Statement literalStatement2 = StatementUtil.fromComponents(new XDI3Segment("=markus+name"), XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment("Markus Sabadello"));
		XDI3Statement literalStatement3 = StatementUtil.fromLiteralComponents(new XDI3Segment("=markus+name"), "Markus Sabadello");

		assertTrue(literalStatement.getSubject().equals(new XDI3Segment("=markus+name")));
		assertTrue(literalStatement.getPredicate().equals(XDIConstants.XRI_S_LITERAL));
		assertTrue(literalStatement.getObject().equals(new XDI3Segment("(data:,Markus%20Sabadello)")));

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public void testRelative() throws Exception {

		XDI3Statement literalStatement = new XDI3Statement("=markus+name/!/(data:,Markus%20Sabadello)");

		XDI3Statement relativeXDI3Statement = StatementUtil.relativeStatement(literalStatement, new XDI3Segment("=markus"));

		assertTrue(relativeXDI3Statement.getSubject().equals(new XDI3Segment("+name")));
		assertTrue(relativeXDI3Statement.getPredicate().equals(new XDI3Segment("!")));
		assertTrue(relativeXDI3Statement.getObject().equals(new XDI3Segment("(data:,Markus%20Sabadello)")));

		assertNull(StatementUtil.relativeStatement(relativeXDI3Statement, new XDI3Segment("($)"), false, true));
	}
}
