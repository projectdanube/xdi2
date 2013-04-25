package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.constants.XDIConstants;
import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XDI3XRef;

public abstract class XDI3ParserTest extends TestCase {
	
	public void testBasic() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement = parser.parseXDI3Statement("=markus[<+email>]!1&/&/\"xxx\"");

		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=markus[<+email>]!1&"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("&"));
		assertEquals(statement.getObject(), "xxx");

		assertEquals(statement.getContextNodeXri(), parser.parseXDI3Segment("=markus[<+email>]!1&"));
		assertNull(statement.getArcXri());
		assertNull(statement.getTargetContextNodeXri());
		assertEquals(statement.getLiteralData(), "xxx");
		assertNull(statement.getInnerRootStatement());

		assertEquals(statement.getSubject().getNumSubSegments(), 4);
		assertEquals(statement.getSubject().getSubSegment(0), statement.getSubject().getFirstSubSegment());
		assertEquals(statement.getSubject().getSubSegment(3), statement.getSubject().getLastSubSegment());
		assertEquals(statement.getSubject().getSubSegment(0), parser.parseXDI3SubSegment("=markus"));
		assertEquals(statement.getSubject().getSubSegment(0).getCs(), XDIConstants.CS_EQUALS);
		assertEquals(statement.getSubject().getSubSegment(0).getLiteral(), "markus");
		assertNull(statement.getSubject().getSubSegment(0).getXRef());
		assertEquals(statement.getSubject().getSubSegment(1), parser.parseXDI3SubSegment("[<+email>]"));
		assertEquals(statement.getSubject().getSubSegment(1).getCs(), XDIConstants.CS_PLUS);
		assertTrue(statement.getSubject().getSubSegment(1).isClassXs());
		assertTrue(statement.getSubject().getSubSegment(1).isAttributeXs());
		assertEquals(statement.getSubject().getSubSegment(1).getLiteral(), "email");
		assertNull(statement.getSubject().getSubSegment(1).getXRef());
		assertEquals(statement.getSubject().getSubSegment(2), parser.parseXDI3SubSegment("!1"));
		assertEquals(statement.getSubject().getSubSegment(2).getCs(), XDIConstants.CS_BANG);
		assertFalse(statement.getSubject().getSubSegment(2).isClassXs());
		assertFalse(statement.getSubject().getSubSegment(2).isAttributeXs());
		assertEquals(statement.getSubject().getSubSegment(2).getLiteral(), "1");
		assertNull(statement.getSubject().getSubSegment(2).getXRef());
		assertEquals(statement.getSubject().getSubSegment(3), parser.parseXDI3SubSegment("&"));
		assertEquals(statement.getSubject().getSubSegment(3).getCs(), XDIConstants.CS_VALUE);
		assertFalse(statement.getSubject().getSubSegment(3).isClassXs());
		assertFalse(statement.getSubject().getSubSegment(3).isAttributeXs());
		assertNull(statement.getSubject().getSubSegment(3).getLiteral());
		assertNull(statement.getSubject().getSubSegment(3).getXRef());

		assertEquals(statement.getPredicate().getNumSubSegments(), 1);
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getFirstSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getLastSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), parser.parseXDI3SubSegment("&"));
	}

	public void testBasicXRef() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Segment segment = parser.parseXDI3Segment("+(user)<+(first_name)>");

		assertEquals(segment.getNumSubSegments(), 2);
		assertEquals(segment.getSubSegment(0), parser.parseXDI3SubSegment("+(user)"));
		assertEquals(segment.getSubSegment(1), parser.parseXDI3SubSegment("<+(first_name)>"));
	}

	public void testXDI3Statement() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement;

		statement = parser.parseXDI3Statement("=markus<+email>&/&/\"markus.sabadello@gmail.com\"");
		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=markus<+email>&"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("&"));
		assertTrue(statement.getObject() instanceof String);
		assertEquals(statement.getObject(), "markus.sabadello@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDI3Statement("=markus/+friend/=neustar*animesh");
		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=markus"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("+friend"));
		assertEquals(statement.getObject(), parser.parseXDI3Segment("=neustar*animesh"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());

		statement = parser.parseXDI3Statement("=neustar*animesh<+email>&/&/\"animesh@gmail.com\"");
		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=neustar*animesh<+email>&"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("&"));
		assertEquals(statement.getObject(), "animesh@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = parser.parseXDI3Statement("=neustar*animesh/+friend/=markus");
		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=neustar*animesh"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("+friend"));
		assertEquals(statement.getObject(), parser.parseXDI3Segment("=markus"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());
	}

	public void testXDI3XRef() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3XRef xref;

		xref = parser.parseXDI3XRef("()");
		assertTrue(xref.isEmpty());

		xref = parser.parseXDI3XRef("(=markus)");
		assertFalse(xref.isEmpty());
		assertEquals(parser.parseXDI3Segment("=markus"), xref.getSegment());

		xref = parser.parseXDI3XRef("(=markus/$add)");
		assertFalse(xref.isEmpty());
		assertEquals(parser.parseXDI3Segment("=markus"), xref.getPartialSubject());
		assertEquals(parser.parseXDI3Segment("$add"), xref.getPartialPredicate());

		xref = parser.parseXDI3XRef("(=markus/+friend/=drummond)");
		assertFalse(xref.isEmpty());
		assertEquals(parser.parseXDI3Statement("=markus/+friend/=drummond"), xref.getStatement());

		xref = parser.parseXDI3XRef("(data:,markus.sabadello@gmail.com)");
		assertFalse(xref.isEmpty());
		assertEquals("data:,markus.sabadello@gmail.com", xref.getIri());

		xref = parser.parseXDI3XRef("(email)");
		assertFalse(xref.isEmpty());
		assertEquals("email", xref.getLiteral());
	}

	public void testLiteralXRef() {

		XDI3Parser parser = this.getParser();

		XDI3SubSegment s;
		
		s = parser.parseXDI3SubSegment("{[<+(name)>]}");
		assertTrue(s.hasXRef());
		assertEquals(s.getXRef(), parser.parseXDI3XRef("{[<+(name)>]}"));
		assertEquals(s.getXRef().getXs(), XDIConstants.XS_VARIABLE);
		assertTrue(s.getXRef().hasSegment());
		assertEquals(s.getXRef().getSegment(), parser.parseXDI3Segment("[<+(name)>]"));
		assertEquals(s.getXRef().getSegment().getNumSubSegments(), 1);
		assertEquals(s.getXRef().getSegment().getFirstSubSegment(), parser.parseXDI3SubSegment("[<+(name)>]"));
		assertTrue(s.getXRef().getSegment().getFirstSubSegment().isClassXs());
		assertTrue(s.getXRef().getSegment().getFirstSubSegment().isAttributeXs());
		assertTrue(s.getXRef().getSegment().getFirstSubSegment().hasXRef());
		assertEquals(s.getXRef().getSegment().getFirstSubSegment().getXRef(), parser.parseXDI3XRef("(name)"));
		assertEquals(s.getXRef().getSegment().getFirstSubSegment().getXRef().getXs(), XDIConstants.XS_ROOT);
		assertTrue(s.getXRef().getSegment().getFirstSubSegment().getXRef().hasLiteral());
		assertEquals(s.getXRef().getSegment().getFirstSubSegment().getXRef().getLiteral(), "name");
	}

	public abstract XDI3Parser getParser();
}
