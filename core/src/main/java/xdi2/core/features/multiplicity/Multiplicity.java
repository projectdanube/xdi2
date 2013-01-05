package xdi2.core.features.multiplicity;

import java.util.Iterator;
import java.util.UUID;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.parser.ParserException;

/**
 * Multiplicity supports constructs to express XDI collections, entities and attributes.
 * All these constructs are context nodes whose arc XRI follow certain rules.
 * 
 * Examples:
 * 
 * Collection: $(+tel), $(+passport)
 * Entity Singleton: =!1111, +passport
 * Attribute Singleton: $!(+tel)
 * Entity Member: $(!1)
 * Attribute Member: $!(!1)
 * 
 * @author markus
 */
public class Multiplicity {

	private Multiplicity() { }

	/*
	 * Methods for building arc XRIs.
	 */

	public static XDI3SubSegment collectionArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + arcXri + ")");
	}

	public static XDI3SubSegment collectionArcXri(String identifier) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + identifier + ")");
	}

	public static XDI3SubSegment entitySingletonArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	public static XDI3SubSegment entityMemberArcXri(String identifier) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + "(" + XRI3Constants.LCS_BANG + identifier + ")");
	}

	public static XDI3SubSegment entityMemberArcXriRandom() {

		return entityMemberArcXri(UUID.randomUUID().toString());
	}

	public static XDI3SubSegment attributeSingletonArcXri(XDI3SubSegment arcXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + arcXri + ")");
	}

	public static XDI3SubSegment attributeMemberArcXri(String identifier) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_DOLLAR + XRI3Constants.LCS_BANG + "(" + XRI3Constants.LCS_BANG + identifier + ")");
	}

	public static XDI3SubSegment attributeMemberArcXriRandom() {

		return attributeMemberArcXri(UUID.randomUUID().toString());
	}

	public static XDI3SubSegment baseArcXri(XDI3SubSegment arcXri) {

		try {

			if (isCollectionArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isEntitySingletonArcXri(arcXri)) {

				return arcXri;
			}

			if (isAttributeSingletonArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isEntityMemberArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}

			if (isAttributeMemberArcXri(arcXri)) {

				return new XDI3SubSegment("" + arcXri.getXRef().getNode().toString());
			}
		} catch (ParserException ex) {

			return null;
		}

		throw new IllegalArgumentException("Invalid multiplicity subsegment: " + arcXri);
	}

	/*
	 * Methods for checking arc XRIs.
	 */

	public static boolean isCollectionArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;

		return true;
	}

	public static boolean isEntitySingletonArcXri(XDI3SubSegment arcXri) {

		if (isCollectionArcXri(arcXri)) return false;
		if (isAttributeSingletonArcXri(arcXri)) return false;
		if (isEntityMemberArcXri(arcXri)) return false;
		if (isAttributeMemberArcXri(arcXri)) return false;

		return true;
	}

	public static boolean isAttributeSingletonArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;

		return true;
	}

	public static boolean isEntityMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (arcXri.hasLCS()) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;
		if (arcXri.getXRef().getNode().getNumSubSegments() <= 0) return false;
		if (arcXri.getXRef().getNode().getFirstSubSegment().hasGCS()) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getXRef().getNode().getFirstSubSegment().getLCS())) return false;

		return true;
	}

	public static boolean isAttributeMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;
		if (! XRI3Constants.GCS_DOLLAR.equals(arcXri.getGCS())) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getLCS())) return false;
		if (! arcXri.hasXRef()) return false;
		if (! arcXri.getXRef().hasNode()) return false;
		if (arcXri.getXRef().getNode().getNumSubSegments() <= 0) return false;
		if (arcXri.getXRef().getNode().getFirstSubSegment().hasGCS()) return false;
		if (! XRI3Constants.LCS_BANG.equals(arcXri.getXRef().getNode().getFirstSubSegment().getLCS())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeCollectionIterator extends NotNullIterator<XdiCollection> {

		public MappingContextNodeCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiCollection> (contextNodes) {

				@Override
				public XdiCollection map(ContextNode contextNode) {

					return XdiCollection.fromContextNode(contextNode);
				}
			});
		}
	}

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
