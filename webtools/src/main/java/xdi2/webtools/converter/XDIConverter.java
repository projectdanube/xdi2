package xdi2.webtools.converter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;

/**
 * Servlet implementation class for Servlet: XDIConverter
 *
 */
public class XDIConverter extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2578333401873629083L;

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();

		while (true) {

			InputStream inputStream = XDIConverter.class.getResourceAsStream("test" + (sampleInputs.size() + 1) + ".graph");
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

	public XDIConverter() {

		super();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));
		request.getRequestDispatcher("/XDIConverter.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String from = request.getParameter("from");
		String to = request.getParameter("to");
		String input = request.getParameter("input");
		String output = "";
		String stats = "-1";
		String error = null;

		XDIReader xdiReader = XDIReaderRegistry.forFormat(from);
		XDIWriter xdiWriter = XDIWriterRegistry.forFormat(to);
		Graph graph = graphFactory.openGraph();

		try {

			StringWriter writer = new StringWriter();

			xdiReader.read(graph, input, null);
			xdiWriter.write(graph, writer, null);

			output = StringEscapeUtils.escapeHtml(writer.getBuffer().toString());
		} catch (Exception ex) {

			ex.printStackTrace(System.err);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		stats = "";
		/*		stats += Integer.toString(graph.getSubjectCount()) + " subjects. ";
		stats += Integer.toString(graph.getPredicateCount()) + " predicates. ";
		stats += Integer.toString(graph.getReferenceCount()) + " references. ";
		stats += Integer.toString(graph.getLiteralCount()) + " literals. ";
		stats += Integer.toString(graph.getInnerGraphCount()) + " inner graphs. ";
		stats += Integer.toString(graph.getStatementCount()) + " statements. ";
		stats += Integer.toString(graph.getCommentCount()) + " comments. ";*/
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + " (" + Arrays.asList(xdiReader.getMimeTypes()) + "). ";

		graph.close();

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("from", from);
		request.setAttribute("to", to);
		request.setAttribute("input", input);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIConverter.jsp").forward(request, response);
	}   	  	    
}
