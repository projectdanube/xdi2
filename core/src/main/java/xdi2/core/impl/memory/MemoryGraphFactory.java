package xdi2.core.impl.memory;

import xdi2.core.Graph;
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

	public MemoryGraphFactory() { 

		this.sortmode = SORTMODE_NONE;
	}

	public static MemoryGraphFactory getInstance() {

		if (instance == null) instance = new MemoryGraphFactory();

		return instance;
	}

	@Override
	public Graph openGraph() {

		// create new graph

		return new MemoryGraph(this.sortmode);
	}

	@Override
	public Graph openGraph(String identifier) {

		return this.openGraph();
	}

	public int getSortmode() {

		return this.sortmode;
	}

	public void setSortmode(int sortmode) {

		this.sortmode = sortmode;
	}
}
