package xdi2.webtools.validator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.webtools.converter.XDIConverter;

/**
 * Servlet implementation class for Servlet: XDIValidator
 *
 */
public class XDIValidator extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2578333401873629083L;

	private static Log log = LogFactory.getLog(XDIValidator.class);

	private static MemoryGraphFactory graphFactory;
	private static String sampleInput;

	static {

		graphFactory = MemoryGraphFactory.getInstance();
		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);

		InputStream inputStream = XDIConverter.class.getResourceAsStream("test.json");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int i;

		try {

			while ((i = inputStream.read()) != -1) outputStream.write(i);
			sampleInput = new String(outputStream.toByteArray());
		} catch (Exception ex) {

			sampleInput = "[Error: Can't read sample data: " + ex.getMessage();
		} finally {

			try {

				inputStream.close();
				outputStream.close();
			} catch (Exception ex) {

			}
		}
	}

	public XDIValidator() {

		super();
	}   	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setAttribute("input", sampleInput);
		request.getRequestDispatcher("/XDIValidator.jsp").forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String from = request.getParameter("from");
		String input = request.getParameter("input");
		String output = "";
		String stats = "-1";
		String error = null;

		XDIReader xdiReader = XDIReaderRegistry.forFormat(from);
		Graph graph = graphFactory.openGraph();

		try {

			xdiReader.read(graph, input, null);

			output = "Success!\n\n";
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
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + ". ";

		// display results

		request.setAttribute("from", from);
		request.setAttribute("input", input);
		request.setAttribute("output", output);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIValidator.jsp").forward(request, response);
	}   	  	    
}
