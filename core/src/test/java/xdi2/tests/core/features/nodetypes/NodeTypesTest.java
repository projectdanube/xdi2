package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeInstanceOrdered;
import xdi2.core.features.nodetypes.XdiAttributeInstanceUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonDefinition;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiCommonVariable;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;

public class NodeTypesTest extends TestCase {

	public void testNodeTypes1() throws Exception {

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("")) instanceof XdiCommonRoot);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{}")) instanceof XdiCommonVariable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("||")) instanceof XdiCommonDefinition);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("()")) instanceof XdiPeerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|()|")) instanceof XdiPeerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|()|}")) instanceof XdiPeerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{()}")) instanceof XdiPeerRoot.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("(=a)")) instanceof XdiPeerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|(=a)|")) instanceof XdiPeerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|(=a)|}")) instanceof XdiPeerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{(=a)}")) instanceof XdiPeerRoot.Variable);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("(/)")) instanceof XdiInnerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|(/)|")) instanceof XdiInnerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|(/)|}")) instanceof XdiInnerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{(/)}")) instanceof XdiInnerRoot.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("(=a/#b)")) instanceof XdiInnerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|(=a/#b)|")) instanceof XdiInnerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|(=a/#b)|}")) instanceof XdiInnerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{(=a/#b)}")) instanceof XdiInnerRoot.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("(=a/)")) instanceof XdiInnerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|(=a/)|")) instanceof XdiInnerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|(=a/)|}")) instanceof XdiInnerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{(=a/)}")) instanceof XdiInnerRoot.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("(/#b)")) instanceof XdiInnerRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|(/#b)|")) instanceof XdiInnerRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|(/#b)|}")) instanceof XdiInnerRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{(/#b)}")) instanceof XdiInnerRoot.Variable);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("<#summary>")) instanceof XdiAttributeSingleton);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|<#summary>|")) instanceof XdiAttributeSingleton.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|<#summary>|}")) instanceof XdiAttributeSingleton.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{<#summary>}")) instanceof XdiAttributeSingleton.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]")) instanceof XdiAttributeCollection);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|[<#tel>]|")) instanceof XdiAttributeCollection.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|[<#tel>]|}")) instanceof XdiAttributeCollection.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{[<#tel>]}")) instanceof XdiAttributeCollection.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]<*!:uuid:1111>")) instanceof XdiAttributeInstanceUnordered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]|<*!:uuid:1111>|")) instanceof XdiAttributeInstanceUnordered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{|<*!:uuid:1111>|}")) instanceof XdiAttributeInstanceUnordered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{<*!:uuid:1111>}")) instanceof XdiAttributeInstanceUnordered.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]<@0>")) instanceof XdiAttributeInstanceOrdered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]|<@0>|")) instanceof XdiAttributeInstanceOrdered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{|<@0>|}")) instanceof XdiAttributeInstanceOrdered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{<@0>}")) instanceof XdiAttributeInstanceOrdered.Variable);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("#vehicle")) instanceof XdiEntitySingleton);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|#vehicle|")) instanceof XdiEntitySingleton.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|#vehicle|}")) instanceof XdiEntitySingleton.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{#vehicle}")) instanceof XdiEntitySingleton.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]")) instanceof XdiEntityCollection);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|[#passport]|")) instanceof XdiEntityCollection.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|[#passport]|}")) instanceof XdiEntityCollection.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{[#passport]}")) instanceof XdiEntityCollection.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]*!:uuid:1111")) instanceof XdiEntityInstanceUnordered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]|*!:uuid:1111|")) instanceof XdiEntityInstanceUnordered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{|*!:uuid:1111|}")) instanceof XdiEntityInstanceUnordered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{*!:uuid:1111}")) instanceof XdiEntityInstanceUnordered.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]@0")) instanceof XdiEntityInstanceOrdered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]|@0|")) instanceof XdiEntityInstanceOrdered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{|@0|}")) instanceof XdiEntityInstanceOrdered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{@0}")) instanceof XdiEntityInstanceOrdered.Variable);
	}

	public void testNodeTypes2() throws Exception {

		assertNotNull(XdiCommonRoot.fromXDIAddress(XDIAddress.create("")));

		assertNotNull(XdiCommonVariable.fromXDIAddress(XDIAddress.create("{}")));
		assertNotNull(XdiCommonDefinition.fromXDIAddress(XDIAddress.create("||")));

		assertNotNull(XdiPeerRoot.fromXDIAddress(XDIAddress.create("()")));
		assertNotNull(XdiPeerRoot.Definition.fromXDIAddress(XDIAddress.create("|()|")));
		assertNotNull(XdiPeerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|()|}")));
		assertNotNull(XdiPeerRoot.Variable.fromXDIAddress(XDIAddress.create("{()}")));
		assertNotNull(XdiPeerRoot.fromXDIAddress(XDIAddress.create("(=a)")));
		assertNotNull(XdiPeerRoot.Definition.fromXDIAddress(XDIAddress.create("|(=a)|")));
		assertNotNull(XdiPeerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|(=a)|}")));
		assertNotNull(XdiPeerRoot.Variable.fromXDIAddress(XDIAddress.create("{(=a)}")));

		assertNotNull(XdiInnerRoot.fromXDIAddress(XDIAddress.create("(/)")));
		assertNotNull(XdiInnerRoot.Definition.fromXDIAddress(XDIAddress.create("|(/)|")));
		assertNotNull(XdiInnerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|(/)|}")));
		assertNotNull(XdiInnerRoot.Variable.fromXDIAddress(XDIAddress.create("{(/)}")));
		assertNotNull(XdiInnerRoot.fromXDIAddress(XDIAddress.create("(=a/#b)")));
		assertNotNull(XdiInnerRoot.Definition.fromXDIAddress(XDIAddress.create("|(=a/#b)|")));
		assertNotNull(XdiInnerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|(=a/#b)|}")));
		assertNotNull(XdiInnerRoot.Variable.fromXDIAddress(XDIAddress.create("{(=a/#b)}")));
		assertNotNull(XdiInnerRoot.fromXDIAddress(XDIAddress.create("(=a/)")));
		assertNotNull(XdiInnerRoot.Definition.fromXDIAddress(XDIAddress.create("|(=a/)|")));
		assertNotNull(XdiInnerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|(=a/)|}")));
		assertNotNull(XdiInnerRoot.Variable.fromXDIAddress(XDIAddress.create("{(=a/)}")));
		assertNotNull(XdiInnerRoot.fromXDIAddress(XDIAddress.create("(/#b)")));
		assertNotNull(XdiInnerRoot.Definition.fromXDIAddress(XDIAddress.create("|(/#b)|")));
		assertNotNull(XdiInnerRoot.Definition.Variable.fromXDIAddress(XDIAddress.create("{|(/#b)|}")));
		assertNotNull(XdiInnerRoot.Variable.fromXDIAddress(XDIAddress.create("{(/#b)}")));

		assertNotNull(XdiAttributeSingleton.fromXDIAddress(XDIAddress.create("<#summary>")));
		assertNotNull(XdiAttributeSingleton.Definition.fromXDIAddress(XDIAddress.create("|<#summary>|")));
		assertNotNull(XdiAttributeSingleton.Definition.Variable.fromXDIAddress(XDIAddress.create("{|<#summary>|}")));
		assertNotNull(XdiAttributeSingleton.Variable.fromXDIAddress(XDIAddress.create("{<#summary>}")));
		assertNotNull(XdiAttributeCollection.fromXDIAddress(XDIAddress.create("[<#tel>]")));
		assertNotNull(XdiAttributeCollection.Definition.fromXDIAddress(XDIAddress.create("|[<#tel>]|")));
		assertNotNull(XdiAttributeCollection.Definition.Variable.fromXDIAddress(XDIAddress.create("{|[<#tel>]|}")));
		assertNotNull(XdiAttributeCollection.Variable.fromXDIAddress(XDIAddress.create("{[<#tel>]}")));
		assertNotNull(XdiAttributeInstanceUnordered.fromXDIAddress(XDIAddress.create("[<#tel>]<*!:uuid:1111>")));
		assertNotNull(XdiAttributeInstanceUnordered.Definition.fromXDIAddress(XDIAddress.create("[<#tel>]|<*!:uuid:1111>|")));
		assertNotNull(XdiAttributeInstanceUnordered.Definition.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{|<*!:uuid:1111>|}")));
		assertNotNull(XdiAttributeInstanceUnordered.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{<*!:uuid:1111>}")));
		assertNotNull(XdiAttributeInstanceOrdered.fromXDIAddress(XDIAddress.create("[<#tel>]<@0>")));
		assertNotNull(XdiAttributeInstanceOrdered.Definition.fromXDIAddress(XDIAddress.create("[<#tel>]|<@0>|")));
		assertNotNull(XdiAttributeInstanceOrdered.Definition.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{|<@0>|}")));
		assertNotNull(XdiAttributeInstanceOrdered.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{<@0>}")));

		assertNotNull(XdiEntitySingleton.fromXDIAddress(XDIAddress.create("#vehicle")));
		assertNotNull(XdiEntitySingleton.Definition.fromXDIAddress(XDIAddress.create("|#vehicle|")));
		assertNotNull(XdiEntitySingleton.Definition.Variable.fromXDIAddress(XDIAddress.create("{|#vehicle|}")));
		assertNotNull(XdiEntitySingleton.Variable.fromXDIAddress(XDIAddress.create("{#vehicle}")));
		assertNotNull(XdiEntityCollection.fromXDIAddress(XDIAddress.create("[#passport]")));
		assertNotNull(XdiEntityCollection.Definition.fromXDIAddress(XDIAddress.create("|[#passport]|")));
		assertNotNull(XdiEntityCollection.Definition.Variable.fromXDIAddress(XDIAddress.create("{|[#passport]|}")));
		assertNotNull(XdiEntityCollection.Variable.fromXDIAddress(XDIAddress.create("{[#passport]}")));
		assertNotNull(XdiEntityInstanceUnordered.fromXDIAddress(XDIAddress.create("[#passport]*!:uuid:1111")));
		assertNotNull(XdiEntityInstanceUnordered.Definition.fromXDIAddress(XDIAddress.create("[#passport]|*!:uuid:1111|")));
		assertNotNull(XdiEntityInstanceUnordered.Definition.Variable.fromXDIAddress(XDIAddress.create("[#passport]{|*!:uuid:1111|}")));
		assertNotNull(XdiEntityInstanceUnordered.Variable.fromXDIAddress(XDIAddress.create("[#passport]{*!:uuid:1111}")));
		assertNotNull(XdiEntityInstanceOrdered.fromXDIAddress(XDIAddress.create("[#passport]@0")));
		assertNotNull(XdiEntityInstanceOrdered.Definition.fromXDIAddress(XDIAddress.create("[#passport]|@0|")));
		assertNotNull(XdiEntityInstanceOrdered.Definition.Variable.fromXDIAddress(XDIAddress.create("[#passport]{|@0|}")));
		assertNotNull(XdiEntityInstanceOrdered.Variable.fromXDIAddress(XDIAddress.create("[#passport]{@0}")));
	}
}
