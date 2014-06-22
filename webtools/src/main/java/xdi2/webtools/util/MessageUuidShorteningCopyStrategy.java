package xdi2.webtools.util;

import xdi2.core.ContextNode;
import xdi2.core.impl.BasicContextNode;
import xdi2.core.util.CopyUtil.CopyStrategy;
import xdi2.core.xri3.XDI3SubSegment;

public class MessageUuidShorteningCopyStrategy extends CopyStrategy {

	@Override
	public ContextNode replaceContextNode(ContextNode contextNode) {

		if (contextNode != null && 
				contextNode.getContextNode() != null && 
				contextNode.getContextNode().getArcXri() != null && 
				contextNode.getContextNode().getArcXri().equals("[$msg]")) {

			return new BasicContextNode(
					contextNode.getGraph(),
					contextNode.getContextNode(),
					XDI3SubSegment.create("!:uuid:1234"),
					contextNode.getContextNodes(),
					contextNode.getRelations(),
					contextNode.getLiteral()
					);
		}

		return contextNode;
	}
}
