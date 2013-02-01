package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3InnerGraph;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3XRef;
import xdi2.core.xri3.parser.XDI3Parser;

public abstract class XDI3ParserTest extends TestCase {

	public void testXDI3Statement() throws Exception {

		XDI3Parser parser = this.getParser();

		XDI3Statement statement;

		statement = XDI3Statement.create(parser, "=markus+email/!/(data:,markus.sabadello@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=markus+email"));
		assertEquals(statement.getPredicate(), XDI3Segment.create(parser, "!"));
		assertEquals(statement.getObject(), XDI3Segment.create(parser, "(data:,markus.sabadello@gmail.com)"));
		assertEquals(XDIUtil.dataXriSegmentToString(statement.getObject()), "markus.sabadello@gmail.com");
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

		statement = XDI3Statement.create(parser, "=neustar*animesh+email/!/(data:,animesh@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create(parser, "=neustar*animesh+email"));
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
		assertEquals(XDI3InnerGraph.create(parser, "=markus/$add"), xref.getInnerGraph());

		xref = XDI3XRef.create(parser, "(=markus/+friend/=drummond)");
		assertEquals(XDI3Statement.create(parser, "=markus/+friend/=drummond"), xref.getStatement());

		xref = XDI3XRef.create(parser, "(data:,markus.sabadello@gmail.com)");
		assertEquals("data:,markus.sabadello@gmail.com", xref.getIRI());

		xref = XDI3XRef.create(parser, "(email)");
		assertEquals("email", xref.getLiteral());
	}

	public abstract XDI3Parser getParser();
}
