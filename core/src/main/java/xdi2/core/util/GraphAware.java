package xdi2.core.util;

import xdi2.core.Graph;

/*
 * This interface can be used by classes that need to be aware of a graph they need to operate on.
 */
public interface GraphAware {

	public void setGraph(Graph graph);
}
