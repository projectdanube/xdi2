/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.util;

import java.util.Iterator;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.util.CopyUtil;

/**
 * Standard set operations for XDI graphs.
 * 
 * @author msabadello at parityinc dot net
 */
public final class SetUtil {

	private SetUtil() { }

	/**
	 * Performs the union set operation on multiple graphs and returns a new one.
	 * The original graphs are not modified. This is a commutative operation, i.e. the order of the 
	 * graphs does not matter.
	 * @param graphs An array of graphs on which the union operation should be performed.
	 * @param resultGraph A graph that will contain all nodes and arcs that are in any input graph. This
	 * should be an empty graph.
	 */
	public static void union(Graph[] graphs, Graph resultGraph) {

		for (Graph graph : graphs) {

			CopyUtil.copyStatements(graph, resultGraph, CopyUtil.SAFECOPYSTATEMENTSTRATEGY);
		}
	}

	/**
	 * Performs the intersection set operation on multiple graphs and returns a new one.
	 * The original graphs are not modified. This is a commutative operation, i.e. the order of the 
	 * graphs in the array does not matter.
	 * @param graphs An array of graphs on which the intersection operation should be performed.
	 * @param resultGraph A graph that will contain all nodes and arcs that are in all input graphs. This
	 * should be an empty graph.
	 */
	public static void intersection(Graph[] graphs, Graph resultGraph) {

		if (graphs.length < 1) return;

		for (Iterator<Statement> i = graphs[0].getStatements(); i.hasNext(); ) {

			Statement statement = i.next();
			boolean missing = false;

			for (int ii=1; ii<graphs.length; ii++) {

				if (! graphs[ii].containsStatement(statement)) {

					missing = true;
					break;
				}
			}

			if (! missing) CopyUtil.copyStatement(statement, resultGraph, null);
		}
	}

	/**
	 * Performs the set theoretic difference operation on multiple graphs and returns a new one.
	 * The original graphs are not modified. This is a non-commutative operation, i.e. the order of the 
	 * graphs does matter. 
	 * @param graphs An array of graphs on which the difference operation should be performed.
	 * @param resultGraph A graph that will contain all nodes and arcs that are in the first graph, but not
	 * in any of the others. This should be an empty graph.
	 */
	public static void setTheoreticDifference(Graph[] graphs, Graph resultGraph) {

		if (graphs.length < 1) return;

		for (Iterator<Statement> i = graphs[0].getStatements(); i.hasNext(); ) {

			Statement statement = i.next();
			boolean found = false;

			for (int ii=1; ii<graphs.length; ii++) {

				if (graphs[ii].containsStatement(statement)) {

					found = true;
					break;
				}
			}

			if (! found) CopyUtil.copyStatement(statement, resultGraph, null);
		}
	}

	/**
	 * Performs the symmetric difference set operation on multiple graphs and returns a new one.
	 * The original graphs are not modified. This is a non-commutative operation, i.e. the order of the 
	 * graphs does matter. 
	 * @param graphs An array of graphs on which the difference operation should be performed.
	 * @param resultGraph A graph that will contain all nodes and arcs that are in the first graph, but not
	 * in any of the others. This should be an empty graph.
	 */
	public static void symmetricDifference(Graph[] graphs, Graph resultGraph) {

		if (graphs.length < 1) return;

		for (Graph graph : graphs) {

			for (Iterator<Statement> i = graph.getStatements(); i.hasNext(); ) {

				Statement statement = i.next();
				int found = 1;

				for (Graph graph2 : graphs) {

					if (graph == graph2) continue;

					if (graph2.containsStatement(statement)) found++;
				}

				if (found % 2 == 1) CopyUtil.copyStatement(statement, resultGraph, CopyUtil.SAFECOPYSTATEMENTSTRATEGY);
			}
		}
	}
}
