package xdi2.webtools.util;

import xdi2.core.ContextNode;
import xdi2.core.impl.BasicContextNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil.CopyStrategy;

public class MessageUuidShorteningCopyStrategy extends CopyStrategy {

	@Override
	public ContextNode replaceContextNode(ContextNode contextNode) {

		if (contextNode != null && 
				contextNode.getContextNode() != null && 
				contextNode.getContextNode().getArc() != null && 
				contextNode.getContextNode().getArc().equals("[$msg]")) {

			return new BasicContextNode(
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
