package xdi2.tests.core.util.iterators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.TestCase;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.InsertableIterator;
import xdi2.core.util.iterators.IteratorArrayMaker;
import xdi2.core.util.iterators.IteratorContains;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.util.iterators.TerminatingIterator;

public class IteratorTest extends TestCase {

	public void testIteratorUsers() throws Exception {

		assertTrue(new IteratorContains(myObjectIterator(), myObject()).contains());
		assertEquals(new IteratorCounter(myObjectIterator()).count(), 4);
		assertEquals(new IteratorFirstItem<MyObject> (myObjectIterator()).item(), myObject());
		assertEquals(new IteratorArrayMaker<MyObject> (myObjectIterator()).array(new MyObject[0]).length, 4);
		assertEquals(new IteratorListMaker<MyObject> (myObjectIterator()).list().size(), 4);
	}

	public void testEmptyIterator() throws Exception {

		Iterator<MyObject> i = new EmptyIterator<MyObject> ();

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testSingleItemIterator() throws Exception {

		Iterator<MyObject> i = new SingleItemIterator<MyObject> (myObject());

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testReadOnlyIterator() throws Exception {

		Iterator<MyObject> i = new ReadOnlyIterator<MyObject> (myObjectIterator());

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertNull(i.next());
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 10);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testNotNullIterator() throws Exception {

		Iterator<MyObject> i = new NotNullIterator<MyObject> (myObjectIterator());

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 10);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testNoDuplicatesIterator() throws Exception {

		Iterator<MyObject> i = new NoDuplicatesIterator<MyObject> (myObjectIterator());

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 20);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertNull(i.next());
		assertRemoveUnsupportedOperationException(i);

		assertHasNextTrue(i);
		assertRemoveUnsupportedOperationException(i);
		assertEquals(i.next().value, 10);
		assertRemoveUnsupportedOperationException(i);

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testSelectingIterator() throws Exception {

		Iterator<MyObject> i1 = new SelectingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean select(MyObject item) {

				return item != null && item.value == 20;
			}
		};

		assertHasNextTrue(i1);
		assertRemoveUnsupportedOperationException(i1);
		assertEquals(i1.next().value, 20);
		assertRemoveUnsupportedOperationException(i1);

		assertHasNextTrue(i1);
		assertRemoveUnsupportedOperationException(i1);
		assertEquals(i1.next().value, 20);
		assertRemoveUnsupportedOperationException(i1);

		assertHasNextFalse(i1);
		assertRemoveUnsupportedOperationException(i1);
		assertNextNoSuchElementException(i1);
		assertRemoveUnsupportedOperationException(i1);

		Iterator<MyObject> i2 = new SelectingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean select(MyObject item) {

				return item != null && item.value == 10;
			}
		};

		assertHasNextTrue(i2);
		assertRemoveUnsupportedOperationException(i2);
		assertEquals(i2.next().value, 10);
		assertRemoveUnsupportedOperationException(i2);

		assertHasNextFalse(i2);
		assertRemoveUnsupportedOperationException(i2);
		assertNextNoSuchElementException(i2);
		assertRemoveUnsupportedOperationException(i2);

		Iterator<MyObject> i3 = new SelectingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean select(MyObject item) {

				return item == null;
			}
		};

		assertHasNextTrue(i3);
		assertRemoveUnsupportedOperationException(i3);
		assertNull(i3.next());
		assertRemoveUnsupportedOperationException(i3);

		assertHasNextFalse(i3);
		assertRemoveUnsupportedOperationException(i3);
		assertNextNoSuchElementException(i3);
		assertRemoveUnsupportedOperationException(i3);

		Iterator<MyObject> i4 = new SelectingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean select(MyObject item) {

				return false;
			}
		};

		assertHasNextFalse(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertNextNoSuchElementException(i4);
		assertRemoveUnsupportedOperationException(i4);
	}

	public void testMappingIterator() throws Exception {

		Iterator<Integer> i = new MappingIterator<MyObject, Integer> (myObjectIterator()) {

			@Override
			public Integer map(MyObject item) {

				if (item == null) return Integer.valueOf(-1);

				return Integer.valueOf(item.value + 1);
			}
		};

		assertHasNextTrue(i);
		assertRemoveIllegalStateException(i);
		assertEquals(i.next().intValue(), 21);
		i.remove();

		assertHasNextTrue(i);
		assertRemoveIllegalStateException(i);
		assertEquals(i.next().intValue(), -1);
		i.remove();

		assertHasNextTrue(i);
		assertRemoveIllegalStateException(i);
		assertEquals(i.next().intValue(), 11);
		i.remove();

		assertHasNextTrue(i);
		assertRemoveIllegalStateException(i);
		assertEquals(i.next().intValue(), 21);
		i.remove();

		assertHasNextFalse(i);
		assertRemoveIllegalStateException(i);
		assertNextNoSuchElementException(i);
		assertRemoveIllegalStateException(i);
	}

	public void testTerminatingIterator() throws Exception {

		Iterator<MyObject> i1 = new TerminatingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean terminate(MyObject item) {

				return item != null && item.value == 20;
			}
		};

		assertHasNextFalse(i1);
		assertRemoveUnsupportedOperationException(i1);
		assertNextNoSuchElementException(i1);
		assertRemoveUnsupportedOperationException(i1);

		Iterator<MyObject> i2 = new TerminatingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean terminate(MyObject item) {

				return item != null && item.value == 10;
			}
		};

		assertHasNextTrue(i2);
		assertRemoveUnsupportedOperationException(i2);
		assertEquals(i2.next().value, 20);
		assertRemoveUnsupportedOperationException(i2);

		assertHasNextTrue(i2);
		assertRemoveUnsupportedOperationException(i2);
		assertNull(i2.next());
		assertRemoveUnsupportedOperationException(i2);

		assertHasNextFalse(i2);
		assertRemoveUnsupportedOperationException(i2);
		assertNextNoSuchElementException(i2);
		assertRemoveUnsupportedOperationException(i2);

		Iterator<MyObject> i3 = new TerminatingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean terminate(MyObject item) {

				return item == null;
			}
		};

		assertHasNextTrue(i3);
		assertRemoveUnsupportedOperationException(i3);
		assertEquals(i3.next().value, 20);
		assertRemoveUnsupportedOperationException(i3);

		assertHasNextFalse(i3);
		assertRemoveUnsupportedOperationException(i3);
		assertNextNoSuchElementException(i3);
		assertRemoveUnsupportedOperationException(i3);

		Iterator<MyObject> i4 = new TerminatingIterator<MyObject> (myObjectIterator()) {

			@Override
			public boolean terminate(MyObject item) {

				return false;
			}
		};

		assertHasNextTrue(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertEquals(i4.next().value, 20);
		assertRemoveUnsupportedOperationException(i4);

		assertHasNextTrue(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertNull(i4.next());
		assertRemoveUnsupportedOperationException(i4);

		assertHasNextTrue(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertEquals(i4.next().value, 10);
		assertRemoveUnsupportedOperationException(i4);

		assertHasNextTrue(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertEquals(i4.next().value, 20);
		assertRemoveUnsupportedOperationException(i4);

		assertHasNextFalse(i4);
		assertRemoveUnsupportedOperationException(i4);
		assertNextNoSuchElementException(i4);
		assertRemoveUnsupportedOperationException(i4);
	}

	public void testDescendingIterator() throws Exception {

		String[] strings = new String[] { "a", "b", "c", "d", "e" };

		Iterator<MyObject> i = new DescendingIterator<String, MyObject> (Arrays.asList(strings).iterator()) {

			@Override
			public Iterator<MyObject> descend(String item) {

				if (item.equals("b")) return new EmptyIterator<MyObject> ();
				if (item.equals("c")) return new SingleItemIterator<MyObject> (myObject());
				if (item.equals("e")) return null;

				return myObjectIterator();
			}
		};

		String[] items = new String[] { "20", "null", "10", "20", "20", "20", "null", "10", "20" };

		for (int n=0; n<9; n++) {

			assertHasNextTrue(i);
			assertRemoveUnsupportedOperationException(i);
			assertEquals("" + i.next(), items[n]);
			assertRemoveUnsupportedOperationException(i);
		}

		assertHasNextFalse(i);
		assertRemoveUnsupportedOperationException(i);
		assertNextNoSuchElementException(i);
		assertRemoveUnsupportedOperationException(i);
	}

	public void testInsertableIterator() throws Exception {

		List<String> strings1 = Arrays.asList("a", "b", "c");
		List<String> strings2 = Arrays.asList("x", "y", "z");
		List<String> strings3 = Arrays.asList("1", "2", "3");

		InsertableIterator<String> iterator1 = new InsertableIterator<String> (strings1.iterator(), false);
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "a");
		iterator1.insert(strings2.iterator());
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "x");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "y");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "z");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "b");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "c");
		iterator1.insert(strings3.iterator());
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "1");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "2");
		assertTrue(iterator1.hasNext()); assertEquals(iterator1.next(), "3");
		assertFalse(iterator1.hasNext());

		InsertableIterator<String> iterator2 = new InsertableIterator<String> (strings1.iterator(), true);
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "a");
		iterator2.insert(strings2.iterator());
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "b");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "c");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "x");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "y");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "z");
		iterator2.insert(strings3.iterator());
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "1");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "2");
		assertTrue(iterator2.hasNext()); assertEquals(iterator2.next(), "3");
		assertFalse(iterator2.hasNext());
	}

	/*
	 * Helper object and iterator
	 */

	private static class MyObject {

		private int value;

		private MyObject(int value) {

			this.value = value;
		}

		@Override
		public boolean equals(Object object) {

			return this.value == ((MyObject) object).value;
		}

		@Override
		public int hashCode() {

			return this.value;
		}

		@Override
		public String toString() {

			return Integer.toString(this.value);
		}
	}

	private static MyObject myObject() {

		return new MyObject(20);
	}

	private static Iterator<MyObject> myObjectIterator() {

		List<MyObject> list = new ArrayList<MyObject> ();
		list.add(new MyObject(20));
		list.add(null);
		list.add(new MyObject(10));
		list.add(new MyObject(20));
		return list.iterator();
	}

	/*
	 * Helper assert methods
	 */

	private static void assertHasNextTrue(Iterator<?> i) {

		assertTrue(i.hasNext());
	}

	private static void assertHasNextFalse(Iterator<?> i) {

		assertFalse(i.hasNext());
	}

	private static void assertNextNoSuchElementException(Iterator<?> i) {

		try { Object o = i.next(); fail(o.toString()); } catch (NoSuchElementException ex) { }
	}

	private static void assertRemoveIllegalStateException(Iterator<?> i) {

		try { i.remove(); fail(); } catch (IllegalStateException ex) { }
	}

	private static void assertRemoveUnsupportedOperationException(Iterator<?> i) {

		try { i.remove(); fail(); } catch (UnsupportedOperationException ex) { }
	}
}
