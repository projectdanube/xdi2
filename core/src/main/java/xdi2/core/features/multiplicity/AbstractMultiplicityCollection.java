package xdi2.core.features.multiplicity;

import xdi2.core.ContextNode;
import xdi2.core.xri3.impl.XRI3SubSegment;

public abstract class AbstractMultiplicityCollection<T> extends AbstractMultiplicityContextNode implements Iterable<T> {

	private static final long serialVersionUID = 284615577959792946L;

	protected AbstractMultiplicityCollection(ContextNode contextNode) {

		super(contextNode);
	}

	/**
	 * Gets or returns an attribute singleton under a context node.
	 * @param contextNode A context node.
	 * @param arcXri The "base" arc XRI of the attribute singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The attribute singleton.
	 */
	public AttributeSingleton getAttributeSingleton(String arcXri, boolean create) {

		XRI3SubSegment attributeSingletonArcXri = Multiplicity.attributeSingletonArcXri(arcXri);
		ContextNode attributeSingletonContextNode = this.getContextNode().getContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null) attributeSingletonContextNode = this.getContextNode().createContextNode(attributeSingletonArcXri);

		return new AttributeSingleton(attributeSingletonContextNode);
	}

	/**
	 * Gets or returns an attribute collection under a context node.
	 * @param arcXri The "base" arc XRI of the attribute collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The attribute collection.
	 */
	public AttributeCollection getAttributeCollection(String arcXri, boolean create) {

		XRI3SubSegment attributeCollectionArcXri = Multiplicity.attributeCollectionArcXri(arcXri);
		ContextNode attributeCollectionContextNode = this.getContextNode().getContextNode(attributeCollectionArcXri);
		if (attributeCollectionContextNode == null) attributeCollectionContextNode = this.getContextNode().createContextNode(attributeCollectionArcXri);

		return new AttributeCollection(attributeCollectionContextNode);
	}

	/**
	 * Gets or returns an entity singleton under a context node.
	 * @param arcXri The "base" arc XRI of the entity singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The entity singleton.
	 */
	public EntitySingleton getEntitySingleton(String arcXri, boolean create) {

		XRI3SubSegment entitySingletonArcXri = Multiplicity.entitySingletonArcXri(arcXri);
		ContextNode entitySingletonContextNode = this.getContextNode().getContextNode(entitySingletonArcXri);
		if (entitySingletonContextNode == null) entitySingletonContextNode = this.getContextNode().createContextNode(entitySingletonArcXri);

		return new EntitySingleton(entitySingletonContextNode);
	}

	/**
	 * Gets or returns an entity collection under a context node.
	 * @param arcXri The "base" arc XRI of the entity collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The entity collection.
	 */
	public EntityCollection getEntityCollection(String arcXri, boolean create) {

		XRI3SubSegment entityCollectionArcXri = Multiplicity.entityCollectionArcXri(arcXri);
		ContextNode entityCollectionContextNode = this.getContextNode().getContextNode(entityCollectionArcXri);
		if (entityCollectionContextNode == null) entityCollectionContextNode = this.getContextNode().createContextNode(entityCollectionArcXri);

		return new EntityCollection(entityCollectionContextNode);
	}
}
