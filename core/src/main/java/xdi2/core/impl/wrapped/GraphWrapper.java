package xdi2.core.impl.wrapped;

import xdi2.core.impl.memory.MemoryGraph;

public interface GraphWrapper {

	public void load(MemoryGraph memoryGraph);
	public void save(MemoryGraph memoryGraph);
}
