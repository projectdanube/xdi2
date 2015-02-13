package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeMemberOrdered;
import xdi2.core.features.nodetypes.XdiAttributeMemberUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonDefinition;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiCommonVariable;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.features.nodetypes.XdiEntityMemberUnordered;
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
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]<!:uuid:1111>")) instanceof XdiAttributeMemberUnordered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]|<!:uuid:1111>|")) instanceof XdiAttributeMemberUnordered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{|<!:uuid:1111>|}")) instanceof XdiAttributeMemberUnordered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{<!:uuid:1111>}")) instanceof XdiAttributeMemberUnordered.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]<@0>")) instanceof XdiAttributeMemberOrdered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]|<@0>|")) instanceof XdiAttributeMemberOrdered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{|<@0>|}")) instanceof XdiAttributeMemberOrdered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[<#tel>]{<@0>}")) instanceof XdiAttributeMemberOrdered.Variable);

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("#vehicle")) instanceof XdiEntitySingleton);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|#vehicle|")) instanceof XdiEntitySingleton.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|#vehicle|}")) instanceof XdiEntitySingleton.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{#vehicle}")) instanceof XdiEntitySingleton.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]")) instanceof XdiEntityCollection);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("|[#passport]|")) instanceof XdiEntityCollection.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{|[#passport]|}")) instanceof XdiEntityCollection.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{[#passport]}")) instanceof XdiEntityCollection.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]!:uuid:1111")) instanceof XdiEntityMemberUnordered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]|!:uuid:1111|")) instanceof XdiEntityMemberUnordered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{|!:uuid:1111|}")) instanceof XdiEntityMemberUnordered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{!:uuid:1111}")) instanceof XdiEntityMemberUnordered.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]@0")) instanceof XdiEntityMemberOrdered);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]|@0|")) instanceof XdiEntityMemberOrdered.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{|@0|}")) instanceof XdiEntityMemberOrdered.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("[#passport]{@0}")) instanceof XdiEntityMemberOrdered.Variable);
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
		assertNotNull(XdiAttributeMemberUnordered.fromXDIAddress(XDIAddress.create("[<#tel>]<!:uuid:1111>")));
		assertNotNull(XdiAttributeMemberUnordered.Definition.fromXDIAddress(XDIAddress.create("[<#tel>]|<!:uuid:1111>|")));
		assertNotNull(XdiAttributeMemberUnordered.Definition.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{|<!:uuid:1111>|}")));
		assertNotNull(XdiAttributeMemberUnordered.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{<!:uuid:1111>}")));
		assertNotNull(XdiAttributeMemberOrdered.fromXDIAddress(XDIAddress.create("[<#tel>]<@0>")));
		assertNotNull(XdiAttributeMemberOrdered.Definition.fromXDIAddress(XDIAddress.create("[<#tel>]|<@0>|")));
		assertNotNull(XdiAttributeMemberOrdered.Definition.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{|<@0>|}")));
		assertNotNull(XdiAttributeMemberOrdered.Variable.fromXDIAddress(XDIAddress.create("[<#tel>]{<@0>}")));

		assertNotNull(XdiEntitySingleton.fromXDIAddress(XDIAddress.create("#vehicle")));
		assertNotNull(XdiEntitySingleton.Definition.fromXDIAddress(XDIAddress.create("|#vehicle|")));
		assertNotNull(XdiEntitySingleton.Definition.Variable.fromXDIAddress(XDIAddress.create("{|#vehicle|}")));
		assertNotNull(XdiEntitySingleton.Variable.fromXDIAddress(XDIAddress.create("{#vehicle}")));
		assertNotNull(XdiEntityCollection.fromXDIAddress(XDIAddress.create("[#passport]")));
		assertNotNull(XdiEntityCollection.Definition.fromXDIAddress(XDIAddress.create("|[#passport]|")));
		assertNotNull(XdiEntityCollection.Definition.Variable.fromXDIAddress(XDIAddress.create("{|[#passport]|}")));
		assertNotNull(XdiEntityCollection.Variable.fromXDIAddress(XDIAddress.create("{[#passport]}")));
		assertNotNull(XdiEntityMemberUnordered.fromXDIAddress(XDIAddress.create("[#passport]!:uuid:1111")));
		assertNotNull(XdiEntityMemberUnordered.Definition.fromXDIAddress(XDIAddress.create("[#passport]|!:uuid:1111|")));
		assertNotNull(XdiEntityMemberUnordered.Definition.Variable.fromXDIAddress(XDIAddress.create("[#passport]{|!:uuid:1111|}")));
		assertNotNull(XdiEntityMemberUnordered.Variable.fromXDIAddress(XDIAddress.create("[#passport]{!:uuid:1111}")));
		assertNotNull(XdiEntityMemberOrdered.fromXDIAddress(XDIAddress.create("[#passport]@0")));
		assertNotNull(XdiEntityMemberOrdered.Definition.fromXDIAddress(XDIAddress.create("[#passport]|@0|")));
		assertNotNull(XdiEntityMemberOrdered.Definition.Variable.fromXDIAddress(XDIAddress.create("[#passport]{|@0|}")));
		assertNotNull(XdiEntityMemberOrdered.Variable.fromXDIAddress(XDIAddress.create("[#passport]{@0}")));
	}
}
