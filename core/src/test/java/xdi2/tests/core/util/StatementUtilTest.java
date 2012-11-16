package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
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

		for (String contextNodeStatement : contextNodeStatements) {

			assertTrue(StatementUtil.fromString(contextNodeStatement) instanceof ContextNodeStatement);
			assertEquals(StatementUtil.fromString(contextNodeStatement).toXdiStatement(), new XDI3Statement(contextNodeStatement));
		} 

		for (String relationStatement : relationStatements) {

			assertTrue(StatementUtil.fromString(relationStatement) instanceof RelationStatement);
			assertEquals(StatementUtil.fromString(relationStatement).toXdiStatement(), new XDI3Statement(relationStatement));
		} 

		for (String literalStatement : literalStatements) {

			assertTrue(StatementUtil.fromString(literalStatement) instanceof LiteralStatement);
			assertEquals(StatementUtil.fromString(literalStatement).toXdiStatement(), new XDI3Statement(literalStatement));
		} 

		for (String invalidStatement : invalidStatements) {

			try {
				
				StatementUtil.fromString(invalidStatement);
				
				fail();
			} catch (Exception ex) {
				
			}
		} 
	}

	public void testComponents() throws Exception {

		ContextNodeStatement contextNodeStatement = (ContextNodeStatement) StatementUtil.fromString("=markus/()/+email");
		ContextNodeStatement contextNodeStatement2 = (ContextNodeStatement) StatementUtil.fromComponents(new XDI3Segment("=markus"), XDIConstants.XRI_S_CONTEXT, new XDI3Segment("+email"));
		ContextNodeStatement contextNodeStatement3 = StatementUtil.fromContextNodeComponents(new XDI3Segment("=markus"), new XDI3Segment("+email"));

		assertTrue(contextNodeStatement.getSubject().equals(new XDI3Segment("=markus")));
		assertTrue(contextNodeStatement.getPredicate().equals(XDIConstants.XRI_S_CONTEXT));
		assertTrue(contextNodeStatement.getObject().equals(new XDI3Segment("+email")));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		RelationStatement relationStatement = (RelationStatement) StatementUtil.fromString("=markus/+friend/=animesh");
		RelationStatement relationStatement2 = (RelationStatement) StatementUtil.fromComponents(new XDI3Segment("=markus"), new XDI3Segment("+friend"), new XDI3Segment("=animesh"));
		RelationStatement relationStatement3 = StatementUtil.fromRelationComponents(new XDI3Segment("=markus"), new XDI3Segment("+friend"), new XDI3Segment("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertTrue(relationStatement.getSubject().equals(new XDI3Segment("=markus")));
		assertTrue(relationStatement.getPredicate().equals(new XDI3Segment("+friend")));
		assertTrue(relationStatement.getObject().equals(new XDI3Segment("=animesh")));

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");
		LiteralStatement literalStatement2 = (LiteralStatement) StatementUtil.fromComponents(new XDI3Segment("=markus+name"), XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment("Markus Sabadello"));
		LiteralStatement literalStatement3 = StatementUtil.fromLiteralComponents(new XDI3Segment("=markus+name"), "Markus Sabadello");

		assertTrue(literalStatement.getSubject().equals(new XDI3Segment("=markus+name")));
		assertTrue(literalStatement.getPredicate().equals(XDIConstants.XRI_S_LITERAL));
		assertTrue(literalStatement.getObject().equals(new XDI3Segment("(data:,Markus%20Sabadello)")));

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public void testRelative() throws Exception {

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");

		LiteralStatement relativeLiteralStatement = (LiteralStatement) StatementUtil.relativeStatement(literalStatement, new XDI3Segment("=markus"));

		assertTrue(relativeLiteralStatement.getSubject().equals(new XDI3Segment("+name")));
		assertTrue(relativeLiteralStatement.getPredicate().equals(new XDI3Segment("!")));
		assertTrue(relativeLiteralStatement.getObject().equals(new XDI3Segment("(data:,Markus%20Sabadello)")));

		assertNull(StatementUtil.relativeStatement(relativeLiteralStatement, new XDI3Segment("($)"), false, true));
	}
}
