package xdi2.webtools.grapher;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.features.nodetypes.XdiAbstractRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.readers.AutoReader;

/**
 * Servlet implementation class for Servlet: XDIGrapher
 *
 */
public class XDIGrapher extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2578333401873629083L;

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();

		while (true) {

			InputStream inputStream = XDIGrapher.class.getResourceAsStream("graph" + (sampleInputs.size() + 1) + ".xdi");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			int i;

			try {

				while ((i = inputStream.read()) != -1) outputStream.write(i);
				sampleInputs.add(new String(outputStream.toByteArray()));
			} catch (Exception ex) {

				break;
			} finally {

				try {

					inputStream.close();
					outputStream.close();
				} catch (Exception ex) {

				}
			}
		}
	}

	public XDIGrapher() {

		super();
	}   	

	@Override
	public void destroy() {

		super.destroy();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));

		request.getRequestDispatcher("/XDIGrapher.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String input = request.getParameter("input");
		String type = request.getParameter("type");
		String graphId = null;
		String stats = "-1";
		String error = null;

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		Graph graph = graphFactory.openGraph();

		long start = System.currentTimeMillis();

		try {

			xdiReader.read(graph, new StringReader(input));

			graphId = UUID.randomUUID().toString();

			ImageCache.put(graphId, makeImage(graph, type));
			JSONCache.put(graphId, makeJSON(graph));
		} catch (Exception ex) {

			ex.printStackTrace(System.err);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		long stop = System.currentTimeMillis();

		stats = "";
		stats += Long.toString(stop - start) + " ms time. ";
		stats += Integer.toString(graph.getRootContextNode().getAllContextNodeCount()) + " context nodes. ";
		stats += Integer.toString(graph.getRootContextNode().getAllRelationCount()) + " relations. ";
		stats += Integer.toString(graph.getRootContextNode().getAllLiteralCount()) + " literals. ";
		stats += Integer.toString(graph.getRootContextNode().getAllStatementCount()) + " statements. ";
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + ((xdiReader instanceof AutoReader && ((AutoReader) xdiReader).getLastSuccessfulReader() != null) ? " (" + ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat() + ")": "")+ ". ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", input);
		request.setAttribute("type", type);
		request.setAttribute("graphId", graphId);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIGrapher.jsp").forward(request, response);
	}   	  	    

	private static byte[] makeImage(Graph graph, String type) throws IOException {

		Drawer drawer = null;
		if (type.equals("d2")) drawer = new Drawer1();
		if (type.equals("d3")) drawer = new Drawer2();
		if (type.equals("d4")) drawer = new Drawer3();
		if (drawer == null) drawer = new EmptyDrawer();

		BufferedImage image = drawer.draw(graph);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", buffer);

		return buffer.toByteArray();
	}

	private static void addContextNodeToBuffer(StringBuffer buffer, ContextNode contextNode) {

		buffer.append("{\n");
		buffer.append("type: \"context\",\n");
		buffer.append("name: \"" + contextNode.getXri() + "\",\n");
		buffer.append("arc: \"" + (contextNode.isRootContextNode() ? "()" : contextNode.getArcXri()) + "\",\n");
		buffer.append("root: " + XdiAbstractRoot.isValid(contextNode) + ",\n");
		buffer.append("contents: [\n");

		for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();
			addContextNodeToBuffer(buffer, innerContextNode);
			if (innerContextNodes.hasNext() || contextNode.containsLiteral()) buffer.append(",");
			buffer.append("\n");
		}

		if (contextNode.containsLiteral()) {

			buffer.append("{\n");
			buffer.append("type: \"literal\",\n");
			buffer.append("name: \"\\\"" + contextNode.getLiteral().getLiteralData() + "\\\"\",\n");
			buffer.append("arc: \"&\"\n");
			buffer.append("}\n");
		}

		buffer.append("],\n");

		buffer.append("rel: [");
		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();

			buffer.append("{\n");
			buffer.append("type: \"relation\",\n");
			buffer.append("arc: \"" + relation.getArcXri() + "\",\n");
			buffer.append("target: \"" + relation.getTargetContextNodeXri() + "\"\n");
			buffer.append("}");
			if (relations.hasNext()) buffer.append(",");
			buffer.append("\n");
		}
		buffer.append("]");

		buffer.append("}\n");
	}

	private static void addRelationsToBuffer(StringBuffer buffer, Iterator<Relation> relations) {

		buffer.append("[\n");

		while (relations.hasNext()) {

			Relation relation = relations.next();

			buffer.append("{\n");

			buffer.append("arc: \"" + relation.getArcXri() + "\",\n");
			buffer.append("source: \"" + relation.getContextNode().getXri() + "\",\n");
			buffer.append("target: \"" + relation.getTargetContextNodeXri() + "\"\n");

			if (relations.hasNext()) buffer.append("},\n"); else buffer.append("}\n"); 
		}

		buffer.append("]\n");
	}

	private static String makeJSON(Graph graph) throws IOException {

		StringBuffer buffer = new StringBuffer();

		buffer.append("var treeData = \n");
		addContextNodeToBuffer(buffer, graph.getRootContextNode());
		buffer.append(";\n");

		buffer.append("var relData = \n");
		addRelationsToBuffer(buffer, graph.getRootContextNode().getAllRelations());
		buffer.append(";\n");

		return buffer.toString();

		/*		StringWriter writer = new StringWriter();

		new XDIJSONWriter(null).write(graph, writer);

		return writer.getBuffer().toString();*/
	}
}
