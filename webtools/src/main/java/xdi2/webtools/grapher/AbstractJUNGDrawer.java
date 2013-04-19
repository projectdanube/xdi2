package xdi2.webtools.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import org.apache.commons.collections15.Transformer;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.webtools.util.WebtoolsUtil;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;

public abstract class AbstractJUNGDrawer implements Drawer {

	@Override
	public BufferedImage draw(xdi2.core.Graph graph) {

		DirectedGraph<Object, Statement> directedGraph = WebtoolsUtil.JUNGDirectedGraphFromGraph(graph);

		Layout<Object, Statement> layout = this.getLayout(directedGraph);
		VisualizationViewer<Object, Statement> vv = new VisualizationViewer<Object, Statement> (layout);
		Dimension size = layout.getSize();
		
		size.width *= 2;
		size.height *= 2;

		layout.setSize(size);
	    vv.setSize(size);

		vv.getRenderContext().setVertexFillPaintTransformer(new MyVertexFillPaintTransformer());
		vv.getRenderContext().setVertexLabelTransformer(new MyVertexLabelTransformer());
		vv.getRenderContext().setEdgeStrokeTransformer(new MyEdgeStrokeTransformer());
		vv.getRenderContext().setEdgeLabelClosenessTransformer(new MyEdgeLabelClosenessTransformer());
		vv.getRenderContext().setEdgeLabelTransformer(new MyEdgeLabelTransformer());
		vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.GRAY, false));

		vv.getRenderer().getVertexLabelRenderer().setPosition(Position.E);
		vv.getRenderer().getEdgeLabelRenderer();

		BufferedImage bufferedImage = new BufferedImage((int) size.getWidth(), (int) size.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics = bufferedImage.createGraphics();
		Container container = new Container();
		container.addNotify();
        container.add(vv);
        container.setVisible(true);
        container.paintComponents(graphics);
		
		return bufferedImage;
	}

	public abstract Layout<Object, Statement> getLayout(DirectedGraph<Object, Statement> directedGraph);

	static class MyVertexFillPaintTransformer implements Transformer<Object, Paint> {

		@Override
		public Paint transform(Object object) {

			if (object instanceof ContextNode) return Color.GREEN;
			if (object instanceof Literal) return Color.BLUE;

			return null;
		}
	}

	static class MyEdgeStrokeTransformer implements Transformer<Statement, Stroke> {

		private final Stroke contextNodeStatementStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);
		private final Stroke relationStatementStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 3.0f, 3.0f }, 0.0f);
		private final Stroke literalStatementStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f);

		@Override
		public Stroke transform(Statement statement) { 

			if (statement instanceof ContextNodeStatement) return this.contextNodeStatementStroke;
			if (statement instanceof RelationStatement) return this.relationStatementStroke;
			if (statement instanceof LiteralStatement) return this.literalStatementStroke;
			
			return null;
		}
	}

	static class MyVertexLabelTransformer implements Transformer<Object, String> {

		@Override
		public String transform(Object object) {

			if (object instanceof ContextNode) return ((ContextNode) object).getXri().getLastSubSegment().toString();
			if (object instanceof Literal) return ((Literal) object).getLiteralData();

			return null;
		}
	}

	static class MyEdgeLabelTransformer implements Transformer<Statement, String> {

		@Override
		public String transform(Statement statement) {

			if (statement instanceof ContextNodeStatement) return ((ContextNodeStatement) statement).getObject().toString();
			if (statement instanceof RelationStatement) return ((RelationStatement) statement).getPredicate().toString();
			if (statement instanceof LiteralStatement) return ((LiteralStatement) statement).getPredicate().toString();

			return null;
		}
	}

	static class MyEdgeLabelClosenessTransformer implements Transformer<Context<Graph<Object, Statement>, Statement>, Number> {

		@Override
		public Number transform(Context<Graph<Object, Statement>, Statement> context) {

			return Float.valueOf(0.5f);
		}
	}
}
