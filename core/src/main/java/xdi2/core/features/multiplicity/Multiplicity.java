package xdi2.core.features.multiplicity;

import java.util.Arrays;
import java.util.Iterator;

import xdi2.core.ContextNode;
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
public class Multiplicity {

	private Multiplicity() { }

	/*
	 * Methods for building arc XRIs.
	 */

	public static XDI3SubSegment entitySingletonArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	public static XDI3SubSegment attributeSingletonArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ATTRIBUTE[0] + arcXri + XDI3Constants.CF_ATTRIBUTE[1]);
	}

	public static XDI3SubSegment entityCollectionArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_COLLECTION[0] + arcXri + XDI3Constants.CF_COLLECTION[1]);
	}

	public static XDI3SubSegment attributeCollectionArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_COLLECTION[0] + XDI3Constants.CF_ATTRIBUTE[0] + arcXri + XDI3Constants.CF_ATTRIBUTE[1] + XDI3Constants.CF_COLLECTION[1]);
	}

	public static XDI3SubSegment memberArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_MEMBER[0] + arcXri + XDI3Constants.CF_MEMBER[1]);
	}

	public static XDI3SubSegment baseArcXri(XDI3SubSegment arcXri) {

		try {

			if (isEntityCollectionArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment();
			}

			if (isEntitySingletonArcXri(arcXri)) {

				return arcXri;
			}

			if (isAttributeSingletonArcXri(arcXri)) {

				return arcXri.getXRef().getSegment().getFirstSubSegment();
			}

			if (isAttributeCollectionArcXri(arcXri)) {

				return XDI3SubSegment.create("" + arcXri.getXRef().getSegment().toString());
			}

			if (isMemberArcXri(arcXri)) {

				return XDI3SubSegment.create("" + arcXri.getXRef().getSegment().toString());
			}
		} catch (ParserException ex) {

			return null;
		}

		throw new IllegalArgumentException("Invalid multiplicity subsegment: " + arcXri);
	}

	/*
	 * Methods for checking arc XRIs.
	 */

	public static boolean isEntitySingletonArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasXRef()) return false;

		return true;
	}

	public static boolean isAttributeSingletonArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasCs()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! Arrays.equals(arcXri.getXRef().getCf(), XDI3Constants.CF_ATTRIBUTE)) return false;

		return true;
	}

	public static boolean isEntityCollectionArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasCs()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! Arrays.equals(arcXri.getXRef().getCf(), XDI3Constants.CF_COLLECTION)) return false;

		return true;
	}

	public static boolean isAttributeCollectionArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasCs()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! Arrays.equals(arcXri.getXRef().getCf(), XDI3Constants.CF_COLLECTION)) return false;
		if (! arcXri.getXRef().hasSegment()) return false;
		if (arcXri.getXRef().getSegment().getNumSubSegments() != 1) return false;
		if (! arcXri.getXRef().getSegment().getFirstSubSegment().hasXRef()) return false;
		if (! Arrays.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getXRef().getCf(), XDI3Constants.CF_ATTRIBUTE)) return false;

		return true;
	}

	public static boolean isMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasCs()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! Arrays.equals(arcXri.getXRef().getCf(), XDI3Constants.CF_MEMBER)) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeEntitySingletonIterator extends NotNullIterator<XdiEntitySingleton> {

		public MappingContextNodeEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntitySingleton> (contextNodes) {

				@Override
				public XdiEntitySingleton map(ContextNode contextNode) {

					return XdiEntitySingleton.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeAttributeSingletonIterator extends NotNullIterator<XdiAttributeSingleton> {

		public MappingContextNodeAttributeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeSingleton> (contextNodes) {

				@Override
				public XdiAttributeSingleton map(ContextNode contextNode) {

					return XdiAttributeSingleton.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeEntityCollectionIterator extends NotNullIterator<XdiEntityCollection> {

		public MappingContextNodeEntityCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityCollection> (contextNodes) {

				@Override
				public XdiEntityCollection map(ContextNode contextNode) {

					return XdiEntityCollection.fromContextNode(contextNode);
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

	public static class MappingContextNodeEntityMemberIterator extends NotNullIterator<XdiEntityMember> {

		public MappingContextNodeEntityMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityMember> (contextNodes) {

				@Override
				public XdiEntityMember map(ContextNode contextNode) {

					return XdiEntityMember.fromContextNode(contextNode);
				}
			});
		}
	}

	public static class MappingContextNodeAttributeMemberIterator extends NotNullIterator<XdiAttributeMember> {

		public MappingContextNodeAttributeMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeMember> (contextNodes) {

				@Override
				public XdiAttributeMember map(ContextNode contextNode) {

					return XdiAttributeMember.fromContextNode(contextNode);
				}
			});
		}
	}
}
