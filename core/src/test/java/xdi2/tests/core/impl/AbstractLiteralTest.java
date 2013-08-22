package xdi2.tests.core.impl;

import junit.framework.TestCase;
import xdi2.core.impl.AbstractLiteral;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class AbstractLiteralTest extends TestCase {

	private static String s = new String("Markus Sabadello");
	private static Double d = new Double(34);
	private static Boolean b = new Boolean(false);
	private static JsonArray a = new JsonArray();
	private static JsonObject o = new JsonObject();

	static {

		a.add(new JsonPrimitive("test"));
		a.add(new JsonPrimitive(5));
		a.add(new JsonPrimitive(false));

		o.add("one", new JsonPrimitive("Markus Sabadello"));
		o.add("two", new JsonPrimitive(34));
		o.add("three", new JsonPrimitive(false));
	}

	public void testIsValidLiteralData() {

		assertTrue(AbstractLiteral.isValidLiteralData(s));
		assertTrue(AbstractLiteral.isValidLiteralData(d));
		assertTrue(AbstractLiteral.isValidLiteralData(b));
		assertTrue(AbstractLiteral.isValidLiteralData(a));
		assertTrue(AbstractLiteral.isValidLiteralData(o));
	}

	public void testJsonElement() {

		assertEquals(AbstractLiteral.literalDataToJsonElement(s), new JsonPrimitive(s));
		assertEquals(AbstractLiteral.literalDataToJsonElement(d), new JsonPrimitive(d));
		assertEquals(AbstractLiteral.literalDataToJsonElement(b), new JsonPrimitive(b));
		assertEquals(AbstractLiteral.literalDataToJsonElement(a), a);
		assertEquals(AbstractLiteral.literalDataToJsonElement(o), o);

		assertEquals(AbstractLiteral.jsonElementToLiteralData(new JsonPrimitive(s)), s);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(new JsonPrimitive(d)), d);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(new JsonPrimitive(b)), b);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(a), a);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(o), o);

		assertEquals(AbstractLiteral.jsonElementToLiteralData(AbstractLiteral.literalDataToJsonElement(s)), s);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(AbstractLiteral.literalDataToJsonElement(d)), d);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(AbstractLiteral.literalDataToJsonElement(b)), b);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(AbstractLiteral.literalDataToJsonElement(a)), a);
		assertEquals(AbstractLiteral.jsonElementToLiteralData(AbstractLiteral.literalDataToJsonElement(o)), o);
	}

	public void testString() {

		assertEquals(AbstractLiteral.literalDataToString(s), "\"Markus Sabadello\"");
		assertEquals(AbstractLiteral.literalDataToString(d), "34.0");
		assertEquals(AbstractLiteral.literalDataToString(b), "false");
		assertEquals(AbstractLiteral.literalDataToString(a), "[\"test\",5,false]");
		assertEquals(AbstractLiteral.literalDataToString(o), "{\"one\":\"Markus Sabadello\",\"two\":34,\"three\":false}");

		assertEquals(AbstractLiteral.stringToLiteralData("\"Markus Sabadello\""), s);
		assertEquals(AbstractLiteral.stringToLiteralData("34"), d);
		assertEquals(AbstractLiteral.stringToLiteralData("false"), b);
		assertEquals(AbstractLiteral.stringToLiteralData("[\"test\",5,false]"), a);
		assertEquals(AbstractLiteral.stringToLiteralData("{\"one\":\"Markus Sabadello\",\"two\":34,\"three\":false}"), o);

		assertEquals(AbstractLiteral.stringToLiteralData(AbstractLiteral.literalDataToString(s)), s);
		assertEquals(AbstractLiteral.stringToLiteralData(AbstractLiteral.literalDataToString(d)), d);
		assertEquals(AbstractLiteral.stringToLiteralData(AbstractLiteral.literalDataToString(b)), b);
		assertEquals(AbstractLiteral.stringToLiteralData(AbstractLiteral.literalDataToString(a)), a);
		assertEquals(AbstractLiteral.stringToLiteralData(AbstractLiteral.literalDataToString(o)), o);
	}
}
