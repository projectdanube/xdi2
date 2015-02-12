package xdi2.tests.core.features.nodetypes;

import junit.framework.TestCase;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiAttributeMemberOrdered;
import xdi2.core.features.nodetypes.XdiAttributeMemberUnordered;
import xdi2.core.features.nodetypes.XdiAttributeSingleton;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;

public class NodeTypesTest extends TestCase {

	public void testNodeTypes() throws Exception {

		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("")) instanceof XdiCommonRoot);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("||")) instanceof XdiCommonRoot.Definition);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{||}")) instanceof XdiCommonRoot.Definition.Variable);
		assertTrue(XdiAbstractContext.fromXDIAddress(XDIAddress.create("{}")) instanceof XdiCommonRoot.Variable);

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
	}
}
