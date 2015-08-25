package xdi2.tests.core.impl;


import com.google.gson.JsonArray;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import junit.framework.TestCase;
import xdi2.core.impl.AbstractLiteralNode;

public class AbstractLiteralTest extends TestCase {

	private static String s = new String("Markus Sabadello");
	private static Double d = new Double(34);
	private static Boolean b = new Boolean(false);
	private static JsonArray a = new JsonArray();
	private static JsonObject o = new JsonObject();
	private static Object n = null;

	static {

		a.add(new JsonPrimitive("test"));
		a.add(new JsonPrimitive(Integer.valueOf(5)));
		a.add(new JsonPrimitive(Boolean.FALSE));

		o.add("one", new JsonPrimitive("Markus Sabadello"));
		o.add("two", new JsonPrimitive(Integer.valueOf(34)));
		o.add("three", new JsonPrimitive(Boolean.FALSE));
	}

	public void testIsValidLiteralData() {

		assertTrue(AbstractLiteralNode.isValidLiteralData(s));
		assertTrue(AbstractLiteralNode.isValidLiteralData(d));
		assertTrue(AbstractLiteralNode.isValidLiteralData(b));
		assertTrue(AbstractLiteralNode.isValidLiteralData(a));
		assertTrue(AbstractLiteralNode.isValidLiteralData(o));
		assertTrue(AbstractLiteralNode.isValidLiteralData(n));
	}

	public void testJsonElement() {

		assertEquals(AbstractLiteralNode.literalDataToJsonElement(s), new JsonPrimitive(s));
		assertEquals(AbstractLiteralNode.literalDataToJsonElement(d), new JsonPrimitive(d));
		assertEquals(AbstractLiteralNode.literalDataToJsonElement(b), new JsonPrimitive(b));
		assertEquals(AbstractLiteralNode.literalDataToJsonElement(a), a);
		assertEquals(AbstractLiteralNode.literalDataToJsonElement(o), o);
		assertEquals(AbstractLiteralNode.literalDataToJsonElement(n), JsonNull.INSTANCE);

		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(new JsonPrimitive(s)), s);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(new JsonPrimitive(d)), d);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(new JsonPrimitive(b)), b);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(a), a);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(o), o);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(JsonNull.INSTANCE), n);

		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(s)), s);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(d)), d);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(b)), b);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(a)), a);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(o)), o);
		assertEquals(AbstractLiteralNode.jsonElementToLiteralData(AbstractLiteralNode.literalDataToJsonElement(n)), n);
	}

	public void testString() {

		assertEquals(AbstractLiteralNode.literalDataToString(s), "\"Markus Sabadello\"");
		assertEquals(AbstractLiteralNode.literalDataToString(d), "34.0");
		assertEquals(AbstractLiteralNode.literalDataToString(b), "false");
		assertEquals(AbstractLiteralNode.literalDataToString(a), "[\"test\",5,false]");
		assertEquals(AbstractLiteralNode.literalDataToString(o), "{\"one\":\"Markus Sabadello\",\"two\":34,\"three\":false}");
		assertEquals(AbstractLiteralNode.literalDataToString(n), "null");

		assertEquals(AbstractLiteralNode.stringToLiteralData("\"Markus Sabadello\""), s);
		assertEquals(AbstractLiteralNode.stringToLiteralData("34"), d);
		assertEquals(AbstractLiteralNode.stringToLiteralData("false"), b);
		assertEquals(AbstractLiteralNode.stringToLiteralData("[\"test\",5,false]"), a);
		assertEquals(AbstractLiteralNode.stringToLiteralData("{\"one\":\"Markus Sabadello\",\"two\":34,\"three\":false}"), o);
		assertEquals(AbstractLiteralNode.stringToLiteralData("null"), n);

		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(s)), s);
		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(d)), d);
		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(b)), b);
		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(a)), a);
		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(o)), o);
		assertEquals(AbstractLiteralNode.stringToLiteralData(AbstractLiteralNode.literalDataToString(n)), n);
	}
}
