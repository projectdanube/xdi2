package xdi2.tests;

import junit.framework.TestCase;

public abstract class AbstractTestCase extends TestCase {

	public static void assertNotEquals(Object o1, Object o2) throws Exception {

		assertFalse(o1.equals(o2));
	}

	public static void assertNotEquals(int i1, int i2) throws Exception {

		assertFalse(i1 == i2);
	}
}
