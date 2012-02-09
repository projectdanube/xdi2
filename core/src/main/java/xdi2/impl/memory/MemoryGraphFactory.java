package xdi2.impl.memory;

import xdi2.Graph;
import xdi2.GraphFactory;
import xdi2.impl.AbstractGraphFactory;

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

	public Graph openGraph() {

		// create new graph

		return new MemoryGraph(this.sortmode);
	}

	public int getSortmode() {

		return this.sortmode;
	}

	public void setSortmode(int sortmode) {

		this.sortmode = sortmode;
	}
}
