package xdi2.webtools.util;

import xdi2.core.ContextNode;
import xdi2.core.impl.DummyContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class MessageUuidShorteningCopyStrategy extends CopyStrategy {

	@Override
	public ContextNode replaceContextNode(ContextNode contextNode) {

		if (contextNode != null && 
				contextNode.getContextNode() != null && 
				contextNode.getContextNode().getXDIArc() != null && 
				contextNode.getContextNode().getXDIArc().equals("[$msg]")) {

			return new DummyContextNode(
					contextNode.getGraph(),
					contextNode.getContextNode(),
					XDIArc.create("!:uuid:1234"),
					contextNode.getContextNodes(),
					contextNode.getRelations(),
					contextNode.getLiteral()
					);
		}

		return contextNode;
	}
}
