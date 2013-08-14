package xdi2.tests.core.impl.keyvalue;

import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;
import xdi2.core.impl.keyvalue.KeyValueStore;
import xdi2.core.util.iterators.IteratorCounter;

public abstract class AbstractKeyValueTest extends TestCase {

	protected abstract KeyValueStore getKeyValueStore(String id) throws IOException;

	public void testBasic() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-1");
		keyValueStore.clear();

		keyValueStore.set("a", "b");
		keyValueStore.set("c", "d");

		assertEquals(keyValueStore.getOne("a"), "b");
		assertEquals(keyValueStore.getOne("c"), "d");
		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("c", "d"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(keyValueStore.count("c"), 1);
		assertEquals(keyValueStore.getAll("a").next(), "b");
		assertEquals(keyValueStore.getAll("c").next(), "d");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 1);

		keyValueStore.delete("a");
		keyValueStore.delete("a");
		keyValueStore.delete("c", "d");
		keyValueStore.delete("c", "d");

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);

		keyValueStore.close();
	}

	public void testMulti() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-2");
		keyValueStore.clear();

		String buf;

		keyValueStore.set("a", "b");
		keyValueStore.set("a", "bb");
		keyValueStore.set("a", "bbbb");
		keyValueStore.set("c", "d");
		keyValueStore.set("c", "ddd");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "bb"));
		assertTrue(keyValueStore.contains("a", "bbbb"));
		assertTrue(keyValueStore.contains("c", "d"));
		assertTrue(keyValueStore.contains("c", "ddd"));
		assertEquals(keyValueStore.count("a"), 3);
		assertEquals(keyValueStore.count("c"), 2);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "bbbbbbb");
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("c"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "dddd");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 3);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 2);

		keyValueStore.delete("a", "bb");
		keyValueStore.delete("c");
		keyValueStore.set("c", "x");

		assertEquals(keyValueStore.getOne("c"), "x");
		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("c"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "bb"));
		assertTrue(keyValueStore.contains("a", "bbbb"));
		assertFalse(keyValueStore.contains("c", "d"));
		assertFalse(keyValueStore.contains("c", "ddd"));
		assertTrue(keyValueStore.contains("c", "x"));
		assertEquals(keyValueStore.count("a"), 2);
		assertEquals(keyValueStore.count("c"), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "bbbbb");
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("c"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "x");
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 2);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 1);

		keyValueStore.delete("a");
		keyValueStore.delete("c", "x");

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);

		keyValueStore.close();
	}

	public void testClear() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-3");
		keyValueStore.clear();

		keyValueStore.set("a", "b");
		keyValueStore.set("a", "bb");
		keyValueStore.set("a", "bbbb");
		keyValueStore.set("c", "d");
		keyValueStore.set("c", "ddd");

		keyValueStore.clear();

		assertNull(keyValueStore.getOne("a"));
		assertNull(keyValueStore.getOne("c"));
		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertFalse(keyValueStore.contains("b", "d"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(keyValueStore.count("c"), 0);
		assertFalse(keyValueStore.getAll("a").hasNext());
		assertFalse(keyValueStore.getAll("c").hasNext());
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("c")).count(), 0);

		keyValueStore.close();
	}

	public void testReplace() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-4");
		keyValueStore.clear();

		keyValueStore.set("a", "b");

		assertEquals(keyValueStore.getOne("a"), "b");

		keyValueStore.replace("a", "c");

		assertEquals(keyValueStore.getOne("a"), "c");

		keyValueStore.close();
	}

	public void testDuplicate() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-5");
		keyValueStore.clear();

		String buf;

		keyValueStore.set("a", "b");
		keyValueStore.set("a", "c");
		keyValueStore.set("a", "b");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 2);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 2);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertTrue(buf.equals("bc") || buf.equals("cb"));

		keyValueStore.delete("a", "c");

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "b");

		keyValueStore.delete("a", "b");

		assertFalse(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("a", "b"));
		assertFalse(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 0);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 0);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "");

		keyValueStore.set("a", "c");

		assertTrue(keyValueStore.contains("a"));
		assertFalse(keyValueStore.contains("a", "b"));
		assertTrue(keyValueStore.contains("a", "c"));
		assertEquals(keyValueStore.count("a"), 1);
		assertEquals(new IteratorCounter(keyValueStore.getAll("a")).count(), 1);
		buf = ""; for (Iterator<String> i = keyValueStore.getAll("a"); i.hasNext(); ) buf += i.next(); assertEquals(buf, "c");

		keyValueStore.close();
	}

	public void testTransactions() throws Exception {

		KeyValueStore keyValueStore = this.getKeyValueStore(this.getClass().getName() + "-keyvalue-6");
		keyValueStore.clear();

		keyValueStore.beginTransaction();
		keyValueStore.set("a", "b");
		keyValueStore.rollbackTransaction();

		if (keyValueStore.supportsTransactions()) {

			assertFalse(keyValueStore.contains("a"));
			assertFalse(keyValueStore.contains("a", "b"));
			assertNull(keyValueStore.getOne("a"));
		} else {

			assertTrue(keyValueStore.contains("a"));
			assertTrue(keyValueStore.contains("a", "b"));
			assertEquals(keyValueStore.getOne("a"), "b");
		}

		keyValueStore.beginTransaction();
		keyValueStore.set("a", "b");
		keyValueStore.commitTransaction();

		assertTrue(keyValueStore.contains("a"));
		assertTrue(keyValueStore.contains("a", "b"));
		assertEquals(keyValueStore.getOne("a"), "b");

		keyValueStore.beginTransaction();
		keyValueStore.delete("a");
		keyValueStore.rollbackTransaction();

		if (keyValueStore.supportsTransactions()) {

			assertTrue(keyValueStore.contains("a"));
			assertTrue(keyValueStore.contains("a", "b"));
			assertEquals(keyValueStore.getOne("a"), "b");
		} else {

			assertFalse(keyValueStore.contains("a"));
			assertFalse(keyValueStore.contains("a", "b"));
			assertNull(keyValueStore.getOne("a"));
		}

		keyValueStore.beginTransaction();
		keyValueStore.set("x", "y");
		keyValueStore.commitTransaction();

		keyValueStore.beginTransaction();
		keyValueStore.clear();
		keyValueStore.rollbackTransaction();

		if (keyValueStore.supportsTransactions()) {

			assertTrue(keyValueStore.contains("x"));
			assertTrue(keyValueStore.contains("x", "y"));
			assertEquals(keyValueStore.getOne("x"), "y");
		} else {

			assertFalse(keyValueStore.contains("x"));
			assertFalse(keyValueStore.contains("x", "y"));
			assertNull(keyValueStore.getOne("x"));
		}

		keyValueStore.beginTransaction();
		keyValueStore.set("x", "y");
		keyValueStore.commitTransaction();

		keyValueStore.beginTransaction();
		keyValueStore.clear();
		keyValueStore.commitTransaction();

		assertFalse(keyValueStore.contains("x"));
		assertFalse(keyValueStore.contains("x", "y"));
		assertNull(keyValueStore.getOne("x"));

		keyValueStore.close();
	}
}
