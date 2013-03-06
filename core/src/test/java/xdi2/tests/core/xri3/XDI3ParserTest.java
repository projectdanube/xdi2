package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3Parser;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3XRef;
import xdi2.core.xri3.XRI3Constants;

public abstract class XDI3ParserTest extends TestCase {

	public void testBasic() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement = parser.parseXDI3Statement("=markus$!(+(+email/+test)+(email))/!/(data:,xxx)");

		assertEquals(statement.getSubject(), parser.parseXDI3Segment("=markus$!(+(+email/+test)+(email))"));
		assertEquals(statement.getPredicate(), parser.parseXDI3Segment("!"));
		assertEquals(statement.getObject(), parser.parseXDI3Segment("(data:,xxx)"));

		assertEquals(statement.getContextNodeXri(), parser.parseXDI3Segment("=markus$!(+(+email/+test)+(email))"));
		assertNull(statement.getArcXri());
		assertNull(statement.getTargetContextNodeXri());
		assertEquals(statement.getLiteralData(), "xxx");
		assertNull(statement.getInnerRootStatement());

		assertEquals(statement.getSubject().getNumSubSegments(), 2);
		assertEquals(statement.getSubject().getSubSegment(0), statement.getSubject().getFirstSubSegment());
		assertEquals(statement.getSubject().getSubSegment(1), statement.getSubject().getLastSubSegment());
		assertEquals(statement.getSubject().getSubSegment(0), parser.parseXDI3SubSegment("=markus"));
		assertEquals(statement.getSubject().getSubSegment(0).getGCS(), XRI3Constants.GCS_EQUALS);
		assertNull(statement.getSubject().getSubSegment(0).getLCS());
		assertEquals(statement.getSubject().getSubSegment(0).getLiteral(), "markus");
		assertNull(statement.getSubject().getSubSegment(0).getXRef());
		assertEquals(statement.getSubject().getSubSegment(1), parser.parseXDI3SubSegment("$!(+(+email/+test)+(email))"));
		assertEquals(statement.getSubject().getSubSegment(1).getGCS(), XRI3Constants.GCS_DOLLAR);
		assertEquals(statement.getSubject().getSubSegment(1).getLCS(), XRI3Constants.LCS_BANG);
		assertNull(statement.getSubject().getSubSegment(1).getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef(), parser.parseXDI3XRef("(+(+email/+test)+(email))"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment(), parser.parseXDI3Segment("+(+email/+test)+(email)"));
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getStatement());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getPartialSubject());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getPartialPredicate());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getIRI());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getNumSubSegments(), 2);
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0), statement.getSubject().getSubSegment(1).getXRef().getSegment().getFirstSubSegment());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1), statement.getSubject().getSubSegment(1).getXRef().getSegment().getLastSubSegment());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0), parser.parseXDI3SubSegment("+(+email/+test)"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getGCS(), XRI3Constants.GCS_PLUS);
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getLCS());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef(), parser.parseXDI3XRef("(+email/+test)"));
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getSegment());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getStatement());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialSubject(), parser.parseXDI3Segment("+email"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialSubject().getNumSubSegments(), 1);
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialSubject().getSubSegment(0), parser.parseXDI3SubSegment("+email"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialPredicate(), parser.parseXDI3Segment("+test"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialPredicate().getNumSubSegments(), 1);
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getPartialPredicate().getSubSegment(0), parser.parseXDI3SubSegment("+test"));
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getIRI());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(0).getXRef().getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1), parser.parseXDI3SubSegment("+(email)"));
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getGCS(), XRI3Constants.GCS_PLUS);
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getLCS());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getLiteral());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef(), parser.parseXDI3XRef("(email)"));
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getSegment());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getStatement());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getPartialSubject());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getPartialPredicate());
		assertNull(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getIRI());
		assertEquals(statement.getSubject().getSubSegment(1).getXRef().getSegment().getSubSegment(1).getXRef().getLiteral(), "email");

		assertEquals(statement.getPredicate().getNumSubSegments(), 1);
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getFirstSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), statement.getPredicate().getLastSubSegment());
		assertEquals(statement.getPredicate().getSubSegment(0), parser.parseXDI3SubSegment("!"));

		assertEquals(statement.getObject().getNumSubSegments(), 1);
		assertEquals(statement.getObject().getSubSegment(0), statement.getObject().getFirstSubSegment());
		assertEquals(statement.getObject().getSubSegment(0), statement.getObject().getLastSubSegment());
		assertEquals(statement.getObject().getSubSegment(0), parser.parseXDI3SubSegment("(data:,xxx)"));
		assertNull(statement.getObject().getSubSegment(0).getXRef().getSegment());
		assertNull(statement.getObject().getSubSegment(0).getXRef().getStatement());
		assertNull(statement.getObject().getSubSegment(0).getXRef().getPartialSubject());
		assertNull(statement.getObject().getSubSegment(0).getXRef().getPartialPredicate());
		assertEquals(statement.getObject().getSubSegment(0).getXRef().getIRI(), "data:,xxx");
		assertNull(statement.getObject().getSubSegment(0).getXRef().getLiteral());
	}

	public void testXDI3Statement() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement;

		statement = XDI3Statement.create(parser, "=markus$!(+email)/!/(data:,markus.sabadello@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=markus$!(+email)"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "!"));
		assertEquals(statement.getObject(), XDI3Segment.create(parser, "(data:,markus.sabadello@gmail.com)"));
		assertEquals(XDIUtil.literalSegmentToString(statement.getObject()), "markus.sabadello@gmail.com");
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

		statement = XDI3Statement.create(parser, "=neustar*animesh$!(+email)/!/(data:,animesh@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=neustar*animesh$!(+email)"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "!"));
		assertEquals(statement.getObject(), XDI3Segment.create(parser, "(data:,animesh@gmail.com)"));
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

		xref = XDI3XRef.create(parser, "(=markus)");
		assertEquals(XDI3Segment.create(parser, "=markus"), xref.getSegment());

		xref = XDI3XRef.create(parser, "(=markus/$add)");
		assertEquals(XDI3Segment.create(parser, "=markus"), xref.getPartialSubject());
		assertEquals(XDI3Segment.create(parser, "$add"), xref.getPartialPredicate());

		xref = XDI3XRef.create(parser, "(=markus/+friend/=drummond)");
		assertEquals(XDI3Statement.create(parser, "=markus/+friend/=drummond"), xref.getStatement());

		xref = XDI3XRef.create(parser, "(data:,markus.sabadello@gmail.com)");
		assertEquals("data:,markus.sabadello@gmail.com", xref.getIRI());

		xref = XDI3XRef.create(parser, "(email)");
		assertEquals("email", xref.getLiteral());
	}

	public abstract XDI3Parser getParser();
}
