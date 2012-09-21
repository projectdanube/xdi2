package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.StatementUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;

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
				"=markus/!/($)"
		};

		String literalStatements[] = new String[] {
				"=markus+name/!/(data:,Markus%20Sabadello)",
				"=!1111$!(+tel)/!/(data:,+1-206-555-1212)",
				"=!1111$*(+tel)$!1/!/(data:,+1.206.555.1111)"
		};

		for (String contextNodeStatement : contextNodeStatements) {

			assertTrue(StatementUtil.fromString(contextNodeStatement) instanceof ContextNodeStatement);
		} 

		for (String relationStatement : relationStatements) {

			assertTrue(StatementUtil.fromString(relationStatement) instanceof RelationStatement);
		} 

		for (String literalStatement : literalStatements) {

			assertTrue(StatementUtil.fromString(literalStatement) instanceof LiteralStatement);
		} 
	}

	public void testComponents() throws Exception {

		ContextNodeStatement contextNodeStatement = (ContextNodeStatement) StatementUtil.fromString("=markus/()/+email");
		ContextNodeStatement contextNodeStatement2 = (ContextNodeStatement) StatementUtil.fromComponents(new XRI3Segment("=markus"), XDIConstants.XRI_S_CONTEXT, new XRI3Segment("+email"));
		ContextNodeStatement contextNodeStatement3 = StatementUtil.fromContextNodeComponents(new XRI3Segment("=markus"), new XRI3Segment("+email"));

		assertTrue(contextNodeStatement.getSubject().equals(new XRI3Segment("=markus")));
		assertTrue(contextNodeStatement.getPredicate().equals(XDIConstants.XRI_S_CONTEXT));
		assertTrue(contextNodeStatement.getObject().equals(new XRI3Segment("+email")));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		RelationStatement relationStatement = (RelationStatement) StatementUtil.fromString("=markus/+friend/=animesh");
		RelationStatement relationStatement2 = (RelationStatement) StatementUtil.fromComponents(new XRI3Segment("=markus"), new XRI3Segment("+friend"), new XRI3Segment("=animesh"));
		RelationStatement relationStatement3 = StatementUtil.fromRelationComponents(new XRI3Segment("=markus"), new XRI3Segment("+friend"), new XRI3Segment("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertTrue(relationStatement.getSubject().equals(new XRI3Segment("=markus")));
		assertTrue(relationStatement.getPredicate().equals(new XRI3Segment("+friend")));
		assertTrue(relationStatement.getObject().equals(new XRI3Segment("=animesh")));

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");
		LiteralStatement literalStatement2 = (LiteralStatement) StatementUtil.fromComponents(new XRI3Segment("=markus+name"), XDIConstants.XRI_S_LITERAL, XDIUtil.stringToDataXriSegment("Markus Sabadello"));
		LiteralStatement literalStatement3 = StatementUtil.fromLiteralComponents(new XRI3Segment("=markus+name"), "Markus Sabadello");

		assertTrue(literalStatement.getSubject().equals(new XRI3Segment("=markus+name")));
		assertTrue(literalStatement.getPredicate().equals(XDIConstants.XRI_S_LITERAL));
		assertTrue(literalStatement.getObject().equals(new XRI3Segment("(data:,Markus%20Sabadello)")));

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public void testRelative() throws Exception {

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");

		LiteralStatement relativeLiteralStatement = (LiteralStatement) StatementUtil.relativeStatement(literalStatement, new XRI3Segment("=markus"));

		assertTrue(relativeLiteralStatement.getSubject().equals(new XRI3Segment("+name")));
		assertTrue(relativeLiteralStatement.getPredicate().equals(new XRI3Segment("!")));
		assertTrue(relativeLiteralStatement.getObject().equals(new XRI3Segment("(data:,Markus%20Sabadello)")));
	}
}
