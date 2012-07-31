package xdi2.tests.core.impl;

import junit.framework.TestCase;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.impl.AbstractStatement;
import xdi2.core.xri3.impl.XRI3Segment;

public class AbstractStatementTest extends TestCase {

	public void testAbstractStatement() throws Exception {

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

			assertTrue(AbstractStatement.fromString(contextNodeStatement) instanceof ContextNodeStatement);
		} 

		for (String relationStatement : relationStatements) {

			assertTrue(AbstractStatement.fromString(relationStatement) instanceof RelationStatement);
		} 

		for (String literalStatement : literalStatements) {

			assertTrue(AbstractStatement.fromString(literalStatement) instanceof LiteralStatement);
		} 
	}

	public void testComponents() throws Exception {

		ContextNodeStatement contextNodeStatement = (ContextNodeStatement) AbstractStatement.fromString("=markus/()/+email");

		contextNodeStatement.getSubject().equals(new XRI3Segment("=markus"));
		contextNodeStatement.getPredicate().equals(new XRI3Segment("()"));
		contextNodeStatement.getObject().equals(new XRI3Segment("+email"));

		RelationStatement relationStatement = (RelationStatement) AbstractStatement.fromString("=markus/+friend/=animesh");

		relationStatement.getSubject().equals(new XRI3Segment("=markus"));
		relationStatement.getPredicate().equals(new XRI3Segment("+friend"));
		relationStatement.getObject().equals(new XRI3Segment("=animesh"));

		LiteralStatement literalStatement = (LiteralStatement) AbstractStatement.fromString("=markus+name/!/(data:,Markus%20Sabadello)");

		literalStatement.getSubject().equals(new XRI3Segment("=markus+name"));
		literalStatement.getPredicate().equals(new XRI3Segment("!"));
		literalStatement.getObject().equals(new XRI3Segment("(data:,Markus%20Sabadello)"));
	}
}
