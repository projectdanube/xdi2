package xdi2.tests.core.syntax;


import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.Parser;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.syntax.parser.ParserException;

public abstract class ParserAbstractTest extends TestCase {

	public void testBasic() throws Exception {

		Parser parser = this.getParser();

		XDIStatement statement = parser.parseXDIStatement("=markus[<#email>]!1&/&/\"xxx\"");

		assertEquals(statement.getSubject(), parser.parseXDIAddress("=markus[<#email>]!1&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertEquals(statement.getObject(), "xxx");

		assertEquals(statement.getContextNodeAddress(), parser.parseXDIAddress("=markus[<#email>]!1&"));
		assertNull(statement.getContextNodeArc());
		assertNull(statement.getTargetContextNodeAddress());
		assertEquals(statement.getLiteralData(), "xxx");

		assertEquals(statement.getSubject().getNumArcs(), 4);
		assertEquals(statement.getSubject().getArc(0), statement.getSubject().getFirstArc());
		assertEquals(statement.getSubject().getArc(3), statement.getSubject().getLastArc());
		assertEquals(statement.getSubject().getArc(0), parser.parseXDIArc("=markus"));
		assertEquals(statement.getSubject().getArc(0).getCs(), XDIConstants.CS_AUTHORITY_PERSONAL);
		assertEquals(statement.getSubject().getArc(0).getLiteral(), "markus");
		assertNull(statement.getSubject().getArc(0).getXRef());
		assertEquals(statement.getSubject().getArc(1), parser.parseXDIArc("[<#email>]"));
		assertEquals(statement.getSubject().getArc(1).getCs(), XDIConstants.CS_CLASS_UNRESERVED);
		assertTrue(statement.getSubject().getArc(1).isClassXs());
		assertTrue(statement.getSubject().getArc(1).isAttributeXs());
		assertEquals(statement.getSubject().getArc(1).getLiteral(), "email");
		assertNull(statement.getSubject().getArc(1).getXRef());
		assertEquals(statement.getSubject().getArc(2), parser.parseXDIArc("!1"));
		assertEquals(statement.getSubject().getArc(2).getCs(), XDIConstants.CS_MEMBER_UNORDERED);
		assertFalse(statement.getSubject().getArc(2).isClassXs());
		assertFalse(statement.getSubject().getArc(2).isAttributeXs());
		assertEquals(statement.getSubject().getArc(2).getLiteral(), "1");
		assertNull(statement.getSubject().getArc(2).getXRef());
		assertEquals(statement.getSubject().getArc(3), parser.parseXDIArc("&"));
		assertEquals(statement.getSubject().getArc(3).getCs(), XDIConstants.CS_VALUE);
		assertFalse(statement.getSubject().getArc(3).isClassXs());
		assertFalse(statement.getSubject().getArc(3).isAttributeXs());
		assertNull(statement.getSubject().getArc(3).getLiteral());
		assertNull(statement.getSubject().getArc(3).getXRef());

		assertEquals(statement.getPredicate().getNumArcs(), 1);
		assertEquals(statement.getPredicate().getArc(0), statement.getPredicate().getFirstArc());
		assertEquals(statement.getPredicate().getArc(0), statement.getPredicate().getLastArc());
		assertEquals(statement.getPredicate().getArc(0), parser.parseXDIArc("&"));
	}

	public void testBasicXRef() throws Exception {

		Parser parser = this.getParser();

		XDIAddress address = parser.parseXDIAddress("+(user)<#(first_name)>");

		assertEquals(address.getNumArcs(), 2);
		assertEquals(address.getArc(0), parser.parseXDIArc("+(user)"));
		assertEquals(address.getArc(1), parser.parseXDIArc("<#(first_name)>"));
	}

	public void testXDI3Statement() throws Exception {

		Parser parser = this.getParser();

		XDIStatement statement;

		statement = parser.parseXDIStatement("=markus<#email>&/&/\"markus.sabadello@gmail.com\"");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=markus<#email>&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertTrue(statement.getObject() instanceof String);
		assertEquals(statement.getObject(), "markus.sabadello@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDIStatement("=markus/+friend/=neustar*animesh");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=markus"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("+friend"));
		assertEquals(statement.getObject(), parser.parseXDIAddress("=neustar*animesh"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());

		statement = parser.parseXDIStatement("=neustar*animesh<#email>&/&/\"animesh@gmail.com\"");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=neustar*animesh<#email>&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertEquals(statement.getObject(), "animesh@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDIStatement("=neustar*animesh<#age>&/&/99");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=neustar*animesh<#age>&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertEquals(statement.getObject(), Double.valueOf(99));
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDIStatement("=neustar*animesh<#smoker>&/&/false");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=neustar*animesh<#smoker>&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertEquals(statement.getObject(), Boolean.valueOf(false));
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDIStatement("=neustar*animesh<#color>&/&/null");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=neustar*animesh<#color>&"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("&"));
		assertNull(statement.getObject());
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		try {

			statement = parser.parseXDIStatement("=neustar*animesh<#err>&/&/test");
			fail();
		} catch (ParserException ex) {

		}

		statement = parser.parseXDIStatement("=neustar*animesh/+friend/=markus");
		assertEquals(statement.getSubject(), parser.parseXDIAddress("=neustar*animesh"));
		assertEquals(statement.getPredicate(), parser.parseXDIAddress("+friend"));
		assertEquals(statement.getObject(), parser.parseXDIAddress("=markus"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());
	}

	public void testXDI3XRef() throws Exception {

		Parser parser = this.getParser();

		XDIXRef xref;

		xref = parser.parseXDIXRef("()");
		assertTrue(xref.isEmpty());

		xref = parser.parseXDIXRef("(=markus)");
		assertFalse(xref.isEmpty());
		assertEquals(parser.parseXDIAddress("=markus"), xref.getAddress());

		xref = parser.parseXDIXRef("(=markus/$add)");
		assertFalse(xref.isEmpty());
		assertEquals(parser.parseXDIAddress("=markus"), xref.getPartialSubject());
		assertEquals(parser.parseXDIAddress("$add"), xref.getPartialPredicate());

		xref = parser.parseXDIXRef("(data:,markus.sabadello@gmail.com)");
		assertFalse(xref.isEmpty());
		assertEquals("data:,markus.sabadello@gmail.com", xref.getIri());

		xref = parser.parseXDIXRef("(email)");
		assertFalse(xref.isEmpty());
		assertEquals("email", xref.getLiteral());
	}

	public void testLiteralXRef() {

		Parser parser = this.getParser();

		XDIArc s;

		s = parser.parseXDIArc("{[<#(name)>]}");
		assertTrue(s.hasXRef());
		assertEquals(s.getXRef(), parser.parseXDIXRef("{[<#(name)>]}"));
		assertEquals(s.getXRef().getXs(), XDIConstants.XS_VARIABLE);
		assertTrue(s.getXRef().hasAddress());
		assertEquals(s.getXRef().getAddress(), parser.parseXDIAddress("[<#(name)>]"));
		assertEquals(s.getXRef().getAddress().getNumArcs(), 1);
		assertEquals(s.getXRef().getAddress().getFirstArc(), parser.parseXDIArc("[<#(name)>]"));
		assertTrue(s.getXRef().getAddress().getFirstArc().isClassXs());
		assertTrue(s.getXRef().getAddress().getFirstArc().isAttributeXs());
		assertTrue(s.getXRef().getAddress().getFirstArc().hasXRef());
		assertEquals(s.getXRef().getAddress().getFirstArc().getXRef(), parser.parseXDIXRef("(name)"));
		assertEquals(s.getXRef().getAddress().getFirstArc().getXRef().getXs(), XDIConstants.XS_ROOT);
		assertTrue(s.getXRef().getAddress().getFirstArc().getXRef().hasLiteral());
		assertEquals(s.getXRef().getAddress().getFirstArc().getXRef().getLiteral(), "name");
	}

	public void testComponents() throws Exception {

		XDIStatement contextNodeStatement = XDIStatement.create("=markus//[<#email>]");
		XDIStatement contextNodeStatement2 = XDIStatement.fromComponents(XDIAddress.create("=markus"), XDIConstants.XDI_ADD_CONTEXT, XDIArc.create("[<#email>]"));
		XDIStatement contextNodeStatement3 = XDIStatement.fromContextNodeComponents(XDIAddress.create("=markus"), XDIArc.create("[<#email>]"));

		assertEquals(contextNodeStatement.getSubject(), XDIAddress.create("=markus"));
		assertEquals(contextNodeStatement.getPredicate(), XDIConstants.XDI_ADD_CONTEXT);
		assertEquals(contextNodeStatement.getObject(), XDIArc.create("[<#email>]"));

		assertEquals(contextNodeStatement, contextNodeStatement2);
		assertEquals(contextNodeStatement, contextNodeStatement3);

		XDIStatement relationStatement = XDIStatement.create("=markus/+friend/=animesh");
		XDIStatement relationStatement2 = XDIStatement.fromComponents(XDIAddress.create("=markus"), XDIAddress.create("+friend"), XDIAddress.create("=animesh"));
		XDIStatement relationStatement3 = XDIStatement.fromRelationComponents(XDIAddress.create("=markus"), XDIAddress.create("+friend"), XDIAddress.create("=animesh"));

		assertEquals(relationStatement, relationStatement2);
		assertEquals(relationStatement, relationStatement3);

		assertEquals(relationStatement.getSubject(), XDIAddress.create("=markus"));
		assertEquals(relationStatement.getPredicate(), XDIAddress.create("+friend"));
		assertEquals(relationStatement.getObject(), XDIAddress.create("=animesh"));

		XDIStatement literalStatement = XDIStatement.create("=markus<#name>&/&/\"Markus Sabadello\"");
		XDIStatement literalStatement2 = XDIStatement.fromComponents(XDIAddress.create("=markus<#name>&"), XDIConstants.XDI_ADD_LITERAL, "Markus Sabadello");
		XDIStatement literalStatement3 = XDIStatement.fromLiteralComponents(XDIAddress.create("=markus<#name>&"), "Markus Sabadello");

		assertEquals(literalStatement.getSubject(), XDIAddress.create("=markus<#name>&"));
		assertEquals(literalStatement.getPredicate(), XDIConstants.XDI_ADD_LITERAL);
		assertEquals(literalStatement.getObject(), "Markus Sabadello");

		assertEquals(literalStatement, literalStatement2);
		assertEquals(literalStatement, literalStatement3);
	}

	public abstract Parser getParser();
}
