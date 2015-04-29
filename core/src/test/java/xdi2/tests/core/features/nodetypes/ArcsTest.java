package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAbstractInstanceOrdered;
import xdi2.core.features.nodetypes.XdiAbstractInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIArc;

public class ArcsTest extends TestCase {

	public void testXDIArcs() throws Exception {

		assertEquals(XdiEntitySingleton.createXDIArc(XDIArc.create("#address")), XDIArc.create("#address"));
		assertEquals(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address")), XDIArc.create("<#address>"));
		assertEquals(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address")), XDIArc.create("[#address]"));
		assertEquals(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address")), XDIArc.create("[<#address>]"));
		assertEquals(XdiAbstractInstanceUnordered.createXDIArc(true, true, false, "1"), XDIArc.create("<*!1>"));
		assertEquals(XdiAbstractInstanceOrdered.createXDIArc(true, false, false, "1"), XDIArc.create("<@1>"));
		assertEquals(XdiAbstractInstanceUnordered.createXDIArc(false, true, false, "1"), XDIArc.create("*!1"));
		assertEquals(XdiAbstractInstanceOrdered.createXDIArc(false, false, false, "1"), XDIArc.create("@1"));

		assertTrue(XdiEntitySingleton.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeSingleton.isValidXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiEntityCollection.isValidXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isValidXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isValidXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isValidXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isValidXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeCollection.isValidXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));

		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiEntitySingleton.createXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
	}
}
