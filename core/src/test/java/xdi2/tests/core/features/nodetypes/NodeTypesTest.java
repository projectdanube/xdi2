package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAbstractMemberOrdered;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIArc;

public class NodeTypesTest extends TestCase {

	public void testXDIArcs() throws Exception {

		assertEquals(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address")), XDIArc.create("#address"));
		assertEquals(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address")), XDIArc.create("<#address>"));
		assertEquals(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address")), XDIArc.create("[#address]"));
		assertEquals(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address")), XDIArc.create("[<#address>]"));
		assertEquals(XdiAbstractMemberUnordered.createXDIArc("1", XdiAttributeCollection.class), XDIArc.create("<!1>"));
		assertEquals(XdiAbstractMemberOrdered.createXDIArc("1", XdiAttributeCollection.class), XDIArc.create("<@1>"));
		assertEquals(XdiAbstractMemberUnordered.createXDIArc("1", XdiEntityCollection.class), XDIArc.create("!1"));
		assertEquals(XdiAbstractMemberOrdered.createXDIArc("1", XdiEntityCollection.class), XDIArc.create("@1"));

		assertTrue(XdiEntitySingleton.isEntitySingletonXDIArc(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isAttributeSingletonXDIArc(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isEntityCollectionXDIArc(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isAttributeCollectionXDIArc(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isEntitySingletonXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeSingleton.isAttributeSingletonXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isEntityCollectionXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isAttributeCollectionXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isEntitySingletonXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isAttributeSingletonXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiEntityCollection.isEntityCollectionXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeCollection.isAttributeCollectionXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))));

		assertFalse(XdiEntitySingleton.isEntitySingletonXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiAttributeSingleton.isAttributeSingletonXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertFalse(XdiEntityCollection.isEntityCollectionXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));
		assertTrue(XdiAttributeCollection.isAttributeCollectionXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))));

		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiEntitySingleton.createEntitySingletonXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiAttributeSingleton.createAttributeSingletonXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiEntityCollection.createEntityCollectionXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
		assertEquals(XdiAbstractContext.getBaseXDIArc(XdiAttributeCollection.createAttributeCollectionXDIArc(XDIArc.create("#address"))), XDIArc.create("#address"));
	}
}
