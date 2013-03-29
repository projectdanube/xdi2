package xdi2.core.features.multiplicity;

import java.util.Arrays;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.roots.XdiLocalRoot;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.core.xri3.parser.aparse.ParserException;

/**
 * Multiplicity supports constructs to express XDI collections, entities and attributes.
 * All these constructs are context nodes whose arc XRI follow certain rules.
 * 
 * Examples:
 * 
 * Entity Singleton: =markus, =!1111
 * Attribute Singleton: <+email>
 * Entity Collection: {+printer}
 * Attribute Collection: {<+email>}
 * Member: [!1]
 * 
 * @author markus
 */
public class ContextFunctions {

	private ContextFunctions() { }
	
	/*
	 * Methods for building arc XRIs.
	 */

	public static XDI3SubSegment baseArcXri(XDI3SubSegment arcXri) {

		try {

			if (isEntityCollectionArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment();
			}

			if (isMemberArcXri(arcXri)) {

				return arcXri;
			}

			if (isAttributeSingletonArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment();
			}

			if (isAttributeCollectionArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment().getXRef().getSegment().getFirstSubSegment();
			}

			if (isMemberArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment();
			}
		} catch (ParserException ex) {

			return null;
		}

		throw new IllegalArgumentException("Invalid multiplicity subsegment: " + arcXri);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeEntitySingletonIterator extends NotNullIterator<XdiMember> {

		public MappingContextNodeEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMember> (contextNodes) {

				@Override
				public XdiMember map(ContextNode contextNode) {

					return XdiMember.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeAttributeSingletonIterator extends NotNullIterator<XdiValue> {

		public MappingContextNodeAttributeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiValue> (contextNodes) {

				@Override
				public XdiValue map(ContextNode contextNode) {

					return XdiValue.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeMemberIterator extends NotNullIterator<XdiMember> {

		public MappingContextNodeMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMember> (contextNodes) {

				@Override
				public XdiMember map(ContextNode contextNode) {

					return XdiMember.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeAttributeCollectionIterator extends NotNullIterator<XdiAttributeCollection> {

		public MappingContextNodeAttributeCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeCollection> (contextNodes) {

				@Override
				public XdiAttributeCollection map(ContextNode contextNode) {

					return XdiAttributeCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
