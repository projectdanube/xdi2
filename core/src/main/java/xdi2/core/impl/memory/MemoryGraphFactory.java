package xdi2.core.impl.memory;

import java.util.HashMap;
import java.util.Map;

import xdi2.core.GraphFactory;
import xdi2.core.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates in-memory graphs.
 * 
 * @author markus
 */
public class MemoryGraphFactory extends AbstractGraphFactory implements GraphFactory {

	public static final int SORTMODE_NONE = 0;
	public static final int SORTMODE_ORDER = 1;
	public static final int SORTMODE_ALPHA = 2;

	private static MemoryGraphFactory instance = null;

	private int sortmode;

	private Map<String, MemoryGraph> graphs;

	public MemoryGraphFactory() { 

		this.sortmode = SORTMODE_NONE;

		this.graphs = new HashMap<String, MemoryGraph> ();
	}

	public static MemoryGraphFactory getInstance() {

		if (instance == null) instance = new MemoryGraphFactory();

		return instance;
	}

	@Override
	public MemoryGraph openGraph() {

		// create new graph

		return new MemoryGraph(this.sortmode);
	}

	@Override
	public MemoryGraph openGraph(String identifier) {

		MemoryGraph graph = this.graphs.get(identifier);
		
		if (graph == null) {
			
			graph = this.openGraph();
			this.graphs.put(identifier, graph);
		}
		
		return graph;
	}

	public int getSortmode() {

		return this.sortmode;
	}

	public void setSortmode(int sortmode) {

		this.sortmode = sortmode;
	}
}
