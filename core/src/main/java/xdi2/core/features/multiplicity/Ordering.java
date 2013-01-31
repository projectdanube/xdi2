package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.TerminatingOnNullIterator;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.XRI3Constants;

public class Ordering {

	private Ordering() { }

	/*
	 * Methods for arc XRIs.
	 */

	public static XDI3SubSegment indexArcXri(int index) {

		return XDI3SubSegment.create("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_STAR + Integer.toString(index));
	}

	public static int arcXriIndex(XDI3SubSegment arcXri) {

		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return -1;
		if (! XRI3Constants.LCS_STAR.equals(arcXri.getLCS())) return -1;
		if (! arcXri.hasLiteral()) return -1;

		return Integer.parseInt(arcXri.getLiteral());
	}

	/*
	 * Methods for ordering context statements.
	 */

	public static ContextNode getOrderedContextNodeByIndex(final ContextNode contextNode, int index) {

		XDI3SubSegment indexArcXri = indexArcXri(index);
		ContextNode indexContextNode = contextNode.getContextNode(indexArcXri);
		Relation indexRelation = indexContextNode == null ? null : indexContextNode.getRelation(XDIDictionaryConstants.XRI_S_IS);
		ContextNode orderedContextNode = indexRelation == null ? null : indexRelation.follow();

		return orderedContextNode;
	}

	public static int getHighestIndex(final ContextNode contextNode) {

		return -1;
	}

	public static ReadOnlyIterator<ContextNode> getOrderedContextNodes(final ContextNode contextNode) {

		return new TerminatingOnNullIterator<ContextNode> (new ReadOnlyIterator<ContextNode> () {

			private int index = 0;
			private ContextNode nextOrderedContextNode = null;
			private boolean triedNextOrderedContextNode = false;

			@Override
			public boolean hasNext() {

				this.tryNextIndexContextNode();

				return this.nextOrderedContextNode != null;
			}

			@Override
			public ContextNode next() {

				this.tryNextIndexContextNode();

				this.index++;
				this.triedNextOrderedContextNode = false;

				return this.nextOrderedContextNode;
			}

			private void tryNextIndexContextNode() {

				if (this.triedNextOrderedContextNode) return;

				this.nextOrderedContextNode = getOrderedContextNodeByIndex(contextNode, this.index + 1);

				this.triedNextOrderedContextNode = true;
			}
		});
	}
}
