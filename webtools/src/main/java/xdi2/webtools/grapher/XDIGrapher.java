package xdi2.webtools.grapher;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import xdi2.Graph;
import xdi2.impl.memory.MemoryGraphFactory;
import xdi2.io.AutoReader;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;

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
		String imageId = null;
		String stats = "-1";
		String error = null;

		XDIReader xdiReader = XDIReaderRegistry.getAuto();
		Graph graph = graphFactory.openGraph();

		long start = System.currentTimeMillis();

		try {

			Drawer drawer = null;
			if (type.equals("stdl")) drawer = new Drawer1(true);
			if (type.equals("std")) drawer = new Drawer1(false);
			if (drawer == null) return;
			
			xdiReader.read(graph, input, null);

			Point size = drawer.draw(null, graph, null);

			BufferedImage image = new BufferedImage((int) size.getX(), (int) size.getY(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = image.createGraphics();
			drawer.draw(graphics, graph, size);

			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			imageId = UUID.randomUUID().toString();
			ImageIO.write(image, "PNG", buffer);
			ImageCache.put(imageId, buffer.toByteArray());
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
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + (xdiReader instanceof AutoReader ? " (" + ((AutoReader) xdiReader).getLastSuccessfulReader().getFormat() + ")": "")+ ". ";

		// display results

		request.setAttribute("input", input);
		request.setAttribute("type", type);
		request.setAttribute("imageId", imageId);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIGrapher.jsp").forward(request, response);
	}   	  	    
}
