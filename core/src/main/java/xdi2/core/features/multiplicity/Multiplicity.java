package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class Multiplicity {

	private Multiplicity() { }

	public static XRI3SubSegment makeValueIndexSubSegment(int i) {

		return new XRI3SubSegment("$!" + i);
	}

	public static int lastValueIndex(ContextNode contextNode) {

		int valueIndex = 1; //contextNode.getAllContextNodeCount();

		while (true) {

			XRI3SubSegment valueIndexSubSegment = makeValueIndexSubSegment(valueIndex);
			
			if (! contextNode.containsContextNode(valueIndexSubSegment)) return valueIndex;

			valueIndex++;
		}
	}

	public static ContextNode createMultiValueContextNode(ContextNode contextNode) {

		int lastValueIndex = lastValueIndex(contextNode);

		return contextNode.createContextNode(makeValueIndexSubSegment(lastValueIndex));
	}
}