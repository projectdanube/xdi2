package xdi2.tests.core.xri3;

import junit.framework.TestCase;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;

public class XDI3ParserTest extends TestCase {

	public void testCopyUtil() throws Exception {

		XDI3Statement statement;

		statement = XDI3Statement.create("=markus+email/!/(data:,markus.sabadello@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create("=markus+email"));
		assertEquals(statement.getPredicate(), XDI3Segment.create("!"));
		assertEquals(statement.getObject(), XDI3Segment.create("(data:,markus.sabadello@gmail.com)"));
		assertEquals(XDIUtil.dataXriSegmentToString(statement.getObject()), "markus.sabadello@gmail.com");
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = XDI3Statement.create("=markus/+friend/=neustar*animesh");
		assertEquals(statement.getSubject(), XDI3Segment.create("=markus"));
		assertEquals(statement.getPredicate(), XDI3Segment.create("+friend"));
		assertEquals(statement.getObject(), XDI3Segment.create("=neustar*animesh"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());

		statement = XDI3Statement.create("=neustar*animesh+email/!/(data:,animesh@gmail.com)");
		assertEquals(statement.getSubject(), XDI3Segment.create("=neustar*animesh+email"));
		assertEquals(statement.getPredicate(), XDI3Segment.create("!"));
		assertEquals(statement.getObject(), XDI3Segment.create("(data:,animesh@gmail.com)"));
		assertFalse(statement.isContextNodeStatement());
		assertTrue(statement.isLiteralStatement());
		assertFalse(statement.isRelationStatement());

		statement = XDI3Statement.create("=neustar*animesh/+friend/=markus");
		assertEquals(statement.getSubject(), XDI3Segment.create("=neustar*animesh"));
		assertEquals(statement.getPredicate(), XDI3Segment.create("+friend"));
		assertEquals(statement.getObject(), XDI3Segment.create("=markus"));
		assertFalse(statement.isContextNodeStatement());
		assertFalse(statement.isLiteralStatement());
		assertTrue(statement.isRelationStatement());
	}
}
