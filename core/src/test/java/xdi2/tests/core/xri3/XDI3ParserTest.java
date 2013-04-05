package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3XRef;

public abstract class XDI3ParserTest extends TestCase {

	public void testBasic() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement = parser.parseXDI3Statement("=markus<+email>!1<<$string>>/<>/\"xxx\"");

		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=markus<+email>!1<<$string>>"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("<>"));
		assertEquals(statement.getObject(), "xxx");

		assertEquals(statement.getContextNodeXri(), parser.parseXDI3Segment("=markus<+email>!1<<$string>>"));
		assertNull(statement.getArcXri());
		assertNull(statement.getTargetContextNodeXri());
		assertEquals(statement.getLiteralData(), "xxx");
		assertNull(statement.getInnerRootStatement());

		assertEquals(statement.getSubject().getNumSubSegments(), 4);
		assertEquals(statement.getSubject().getSubSegment(0), statement.getSubject().getFirstSubSegment());
		assertEquals(statement.getSubject().getSubSegment(3), statement.getSubject().getLastSubSegment());
		assertEquals(statement.getSubject().getSubSegment(0), parser.parseXDI3SubSegment("=markus"));
		assertEquals(statement.getSubject().getSubSegment(0).getCs(), XDI3Constants.CS_EQUALS);
		assertEquals(statement.getSubject().getSubSegment(0).getLiteral(), "markus");
		assertNull(statement.getSubject().getSubSegment(0).getXRef());
		assertEquals(statement.getSubject().getSubSegment(1), parser.parseXDI3SubSegment("<+email>"));
		assertNull(statement.getSubject().getSubSegment(1).getCs());
		assertNull(statement.getSubject().getSubSegment(1).getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef(), parser.parseXDI3XRef("<+email>"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getCf(), XDI3Constants.CF_ATTRIBUTE_CLASS);
		assertFalse(statement.getSubject().getSubSegment(1).getXRef().isEmpty());
		assertTrue(statement.getSubject().getSubSegment(1).getXRef().hasSegment());
		assertFalse(statement.getSubject().getSubSegment(1).getXRef().hasStatement());
		assertFalse(statement.getSubject().getSubSegment(1).getXRef().hasPartialSubjectAndPredicate());
		assertFalse(statement.getSubject().getSubSegment(1).getXRef().hasIri());
		assertFalse(statement.getSubject().getSubSegment(1).getXRef().hasLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment(), parser.parseXDI3Segment("+email"));
		assertEquals(statement.getSubject().getSubSegment(2), parser.parseXDI3SubSegment("!1"));
		assertEquals(statement.getSubject().getSubSegment(2).getCs(), XDI3Constants.CS_BANG);
		assertEquals(statement.getSubject().getSubSegment(2).getLiteral(), "1");
		assertNull(statement.getSubject().getSubSegment(2).getXRef());
		assertEquals(statement.getSubject().getSubSegment(3), parser.parseXDI3SubSegment("<<$string>>"));
		assertNull(statement.getSubject().getSubSegment(3).getCs());
		assertNull(statement.getSubject().getSubSegment(3).getLiteral());
		assertEquals(statement.getSubject().getSubSegment(3).getXRef(), parser.parseXDI3XRef("<<$string>>"));
		assertEquals(statement.getSubject().getSubSegment(3).getXRef().getCf(), XDI3Constants.CF_VALUE);
		assertFalse(statement.getSubject().getSubSegment(3).getXRef().isEmpty());
		assertTrue(statement.getSubject().getSubSegment(3).getXRef().hasSegment());
		assertFalse(statement.getSubject().getSubSegment(3).getXRef().hasStatement());
		assertFalse(statement.getSubject().getSubSegment(3).getXRef().hasPartialSubjectAndPredicate());
		assertFalse(statement.getSubject().getSubSegment(3).getXRef().hasIri());
		assertFalse(statement.getSubject().getSubSegment(3).getXRef().hasLiteral());
		assertEquals(statement.getSubject().getSubSegment(3).getXRef().getSegment(), "<$string>");

		assertEquals(statement.getPredicate().getNumSubSegments(), 1);
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getFirstSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getLastSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), parser.parseXDI3SubSegment("<>"));
	}

	public void testXDI3Statement() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement;

		statement = XDI3Statement.create(parser, "=markus!<+email><<$string>>/<>/\"markus.sabadello@gmail.com\"");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=markus!<+email><<$string>>"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("<>"));
		assertTrue(statement.getObject() instanceof String);
		assertEquals(statement.getObject(), "markus.sabadello@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = XDI3Statement.create(parser, "=markus/+friend/=neustar*animesh");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=markus"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "+friend"));
		assertEquals(statement.getObject(), XDI3Segment.create(parser, "=neustar*animesh"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());

		statement = XDI3Statement.create(parser, "=neustar*animesh!<+email><<$string>>/<>/\"animesh@gmail.com\"");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=neustar*animesh!<+email><<$string>>"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "<>"));
		assertEquals(statement.getObject(), "animesh@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = XDI3Statement.create(parser, "=neustar*animesh/+friend/=markus");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=neustar*animesh"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "+friend"));
		assertEquals(statement.getObject(), XDI3Segment.create(parser, "=markus"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());
	}

	public void testXDI3XRef() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3XRef xref;

		xref = XDI3XRef.create(parser, "()");
		assertTrue(xref.isEmpty());

		xref = XDI3XRef.create(parser, "(=markus)");
		assertFalse(xref.isEmpty());
		assertEquals(XDI3Segment.create(parser, "=markus"), xref.getSegment());

		xref = XDI3XRef.create(parser, "(=markus/$add)");
		assertFalse(xref.isEmpty());
		assertEquals(XDI3Segment.create(parser, "=markus"), xref.getPartialSubject());
		assertEquals(XDI3Segment.create(parser, "$add"), xref.getPartialPredicate());

		xref = XDI3XRef.create(parser, "(=markus/+friend/=drummond)");
		assertFalse(xref.isEmpty());
		assertEquals(XDI3Statement.create(parser, "=markus/+friend/=drummond"), xref.getStatement());

		xref = XDI3XRef.create(parser, "(data:,markus.sabadello@gmail.com)");
		assertFalse(xref.isEmpty());
		assertEquals("data:,markus.sabadello@gmail.com", xref.getIri());

		xref = XDI3XRef.create(parser, "(email)");
		assertFalse(xref.isEmpty());
		assertEquals("email", xref.getLiteral());
	}

	public abstract XDI3Parser getParser();
}
