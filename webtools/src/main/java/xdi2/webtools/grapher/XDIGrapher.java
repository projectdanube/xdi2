package xdi2.webtools.grapher;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.io.XDIReader;
import org.eclipse.higgins.xdi4j.io.XDIReaderRegistry;

/**
 * Servlet implementation class for Servlet: XDIGrapher
 *
 */
public class XDIGrapher extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 2578333401873629083L;

	private static MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	static {

		graphFactory.setSortmode(MemoryGraphFactory.SORTMODE_ORDER);
	}

	private String sampleInput;

	public XDIGrapher() {

		super();

		InputStream inputStream = this.getClass().getResourceAsStream("test.graph");
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

		ImageCache.init();
	}   	

	@Override
	public void destroy() {

		super.destroy();

		ImageCache.shutdown();
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setAttribute("input", sampleInput);
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
			if (type.equals("box")) drawer = new Drawer1();
			if (type.equals("spol")) drawer = new Drawer2(true);
			if (type.equals("spo")) drawer = new Drawer2(false);
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
		stats += Integer.toString(graph.getSubjectCount()) + " subjects. ";
		stats += Integer.toString(graph.getPredicateCount()) + " predicates. ";
		stats += Integer.toString(graph.getReferenceCount()) + " references. ";
		stats += Integer.toString(graph.getLiteralCount()) + " literals. ";
		stats += Integer.toString(graph.getInnerGraphCount()) + " inner graphs. ";
		stats += Integer.toString(graph.getStatementCount()) + " statements. ";
		stats += Integer.toString(graph.getGraphComponentCount(false)) + " nodes. ";
		stats += Integer.toString(graph.getGraphComponentCount(true)) + " nodes (deep). ";
		stats += Integer.toString(graph.getCommentCount()) + " comments. ";
		if (xdiReader != null) stats += "Input format: " + xdiReader.getFormat() + ". ";

		// display results

		request.setAttribute("input", input);
		request.setAttribute("type", type);
		request.setAttribute("imageId", imageId);
		request.setAttribute("stats", stats);
		request.setAttribute("error", error);

		request.getRequestDispatcher("/XDIGrapher.jsp").forward(request, response);
	}   	  	    
}
