package xdi2.webtools.validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.readers.AutoReader;

/**
 * Servlet implementation class for Servlet: XDIValidator
 *
 */
public class XDIValidator extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2578333401873629083L;

	private static Logger log = LoggerFactory.getLogger(XDIValidator.class);

	private static MemoryGraphFactory graphFactory;
	private static List<String> sampleInputs;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		sampleInputs = new ArrayList<String> ();

		while (true) {

			InputStream inputStream = XDIValidator.class.getResourceAsStream("graph" + (sampleInputs.size() + 1) + ".xdi");
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

	public XDIValidator() {

		super();
	}   	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sample = request.getParameter("sample");
		if (sample == null) sample = "1";

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("input", sampleInputs.get(Integer.parseInt(sample) - 1));

		request.getRequestDispatcher("/XDIValidator.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String from = request.getParameter("from");
		String input = request.getParameter("input");
		String output = "";
		String stats = "-1";
		String error = null;

		XDIReader xdiReader = XDIReaderRegistry.forFormat(from, null);
		Graph graph = graphFactory.openGraph();

		try {

			xdiReader.read(graph, new StringReader(input));

			output = "Success!\n";
/*			output += Integer.toString(Constraints.getAllConstraintCount(graph)) + " constraints found.\n";
			output += Integer.toString(Versioning.getAllVersionListCount(graph)) + " version lists, ";
			output += Integer.toString(Versioning.getAllVersionSnapshotCount(graph)) + " version snapshots and ";
			output += Integer.toString(Versioning.getAllVersionLogCount(graph)) + " version logs found.\n";
			output += Integer.toString(LinkContracts.getAllLinkContractRootCount(graph)) + " link contract roots found. ";
			output += Integer.toString(LinkContracts.getAllLinkContractCount(graph)) + " link contracts found.\n";
			output += Integer.toString(Signatures.getAllSignatureCount(graph)) + " signatures found.";*/
		} catch (Exception ex) {

			log.error(ex.getMessage(), ex);
			error = ex.getMessage();
			if (error == null) error = ex.getClass().getName();
		}

		stats = "";
		stats += Integer.toString(graph.getRootContextNode().getAllContextNodeCount()) + " context nodes. ";
		stats += Integer.toString(graph.getRootContextNode().getAllRelationCount()) + " relations. ";
		stats += Integer.toString(graph.getRootContextNode().getAllLiteralCount()) + " literals. ";
		stats += Integer.toString(graph.getRootContextNode().getAllStatementCount()) + " statements. ";
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + ((xdiReader instanceof AutoReader && ((AutoReader) xdiReader).getLastSuccessfulReader() != null) ? " (" + ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat() + ")": "")+ ". ";

		// display results

		request.setAttribute("sampleInputs", Integer.valueOf(sampleInputs.size()));
		request.setAttribute("from", from);
		request.setAttribute("input", input);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIValidator.jsp").forward(request, response);
	}   	  	    
}
