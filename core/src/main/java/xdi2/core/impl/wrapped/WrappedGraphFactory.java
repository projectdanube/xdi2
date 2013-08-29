package xdi2.core.impl.wrapped;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;
import xdi2.core.impl.memory.MemoryGraph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIWriterRegistry;

/**
 * GraphFactory that creates wrapped memory graphs.
 * 
 * @author markus
 */
public abstract class WrappedGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final String DEFAULT_PATH = "xdi2-graph.xdi";
	public static final String DEFAULT_MIMETYPE = XDIWriterRegistry.getDefault().getMimeType().toString();

	private WrapperStore wrapper;
	private MemoryGraphFactory memoryGraphFactory;

	public WrappedGraphFactory() { 

		super();

		this.wrapper = null;
		this.memoryGraphFactory = MemoryGraphFactory.getInstance();
	}

	@Override
	public Graph openGraph(String identifier) throws IOException {

		// get wrapper

		WrapperStore wrapper = this.openWrapper(identifier);

		// initialize graph

		MemoryGraph memoryGraph = this.memoryGraphFactory.openGraph();

		return new WrappedGraph(this, identifier, wrapper, memoryGraph);
	}

	/**
	 * This must be overridden by subclasses to instantiate the wrapper
	 * @param identifier An optional identifier to distinguish wrappers from one another.
	 */
	public abstract WrapperStore openWrapper(String identifier) throws IOException;

	public WrapperStore getWrapper() {

		return this.wrapper;
	}

	public void setWrapper(WrapperStore wrapper) {

		this.wrapper = wrapper;
	}

	public MemoryGraphFactory getMemoryGraphFactory() {

		return this.memoryGraphFactory;
	}

	public void setMemoryGraphFactory(MemoryGraphFactory memoryGraphFactory) {

		this.memoryGraphFactory = memoryGraphFactory;
	}
}
