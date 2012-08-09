package xdi2.core.impl.file;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.impl.memory.MemoryRelation;
import xdi2.core.xri3.impl.XRI3Segment;

public class FileRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -3710590809580009142L;

	private MemoryRelation memoryRelation;

	FileRelation(FileGraph graph, FileContextNode contextNode, MemoryRelation memoryRelation) {

		super(graph, contextNode);

		this.memoryRelation = memoryRelation;
	}

	@Override
	public XRI3Segment getArcXri() {
		
		return this.memoryRelation.getArcXri();
	}

	@Override
	public XRI3Segment getTargetContextNodeXri() {
		
		return this.memoryRelation.getTargetContextNodeXri();
	}
}
