package xdi2.tests.core.util;

import junit.framework.TestCase;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.util.StatementUtil;
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

		assertTrue(contextNodeStatement.getSubject().equals(new XRI3Segment("=markus")));
		assertTrue(contextNodeStatement.getPredicate().equals(new XRI3Segment("()")));
		assertTrue(contextNodeStatement.getObject().equals(new XRI3Segment("+email")));

		RelationStatement relationStatement = (RelationStatement) StatementUtil.fromString("=markus/+friend/=animesh");

		assertTrue(relationStatement.getSubject().equals(new XRI3Segment("=markus")));
		assertTrue(relationStatement.getPredicate().equals(new XRI3Segment("+friend")));
		assertTrue(relationStatement.getObject().equals(new XRI3Segment("=animesh")));

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");

		assertTrue(literalStatement.getSubject().equals(new XRI3Segment("=markus+name")));
		assertTrue(literalStatement.getPredicate().equals(new XRI3Segment("!")));
		assertTrue(literalStatement.getObject().equals(new XRI3Segment("(data:,Markus%20Sabadello)")));
	}

	public void testRelative() throws Exception {

		LiteralStatement literalStatement = (LiteralStatement) StatementUtil.fromString("=markus+name/!/(data:,Markus%20Sabadello)");

		LiteralStatement relativeLiteralStatement = (LiteralStatement) StatementUtil.relativeStatement(literalStatement, new XRI3Segment("=markus"));

		assertTrue(relativeLiteralStatement.getSubject().equals(new XRI3Segment("+name")));
		assertTrue(relativeLiteralStatement.getPredicate().equals(new XRI3Segment("!")));
		assertTrue(relativeLiteralStatement.getObject().equals(new XRI3Segment("(data:,Markus%20Sabadello)")));
	}
}
