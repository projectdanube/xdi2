package xdi2.webtools.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.xri3.impl.XRI3Authority;
import xdi2.xri3.impl.XRI3Path;
import xdi2.xri3.impl.XRI3Reference;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class Drawer1 implements Drawer {

	public static final int MARGIN = 20;
	public static final int CONTEXTNODE_WIDTH = 5;
	public static final int CONTEXTNODE_HEIGHT = 5;
	public static final int ARC_WIDTH = 50;
	public static final int ARC_HEIGHT = 50;
	public static final int LITERAL_WIDTH = 160;
	public static final int LITERAL_HEIGHT = 30;
	public static final int SPACING_HEIGHT = 20;
	public static final int GRAPH_SPACING_WIDTH = 20;
	public static final int GRAPH_SPACING_HEIGHT = 20;
	public static final Font FONT_SEGMENT = new Font("Dialog", Font.PLAIN, 12);
	public static final Font FONT_LITERAL = new Font("Dialog", Font.PLAIN, 12);
	public static final Color CONTEXTNODE_COLOR = Color.BLACK; 
	public static final Color RELATION_COLOR = Color.BLACK; 
	public static final Color LITERAL_COLOR = Color.BLACK; 
	public static final Color ARC_COLOR = Color.BLACK; 
	public static final Color FONT_COLOR = Color.BLACK; 
	public static final Stroke BOX_STROKE = new BasicStroke(4);
	public static final Stroke ARC_STROKE = new BasicStroke(2);

	private boolean legend;

	public Drawer1(boolean legend) {

		this.legend = legend;
	}

	public Point draw(Graphics2D graphics, Graph graph, Point size) {

		if (graphics != null) graphics.setBackground(Color.WHITE);

		Point offset = new Point(MARGIN, MARGIN);
		Point maxOffset = new Point(offset);
		Point totalMaxOffset = new Point(offset);

		if (this.legend) {

			maxOffset = drawLegend(graphics, offset);
			adjustTotalMaxOffset(totalMaxOffset, maxOffset);
			offset.translate(0, LITERAL_HEIGHT + MARGIN);
		}

		maxOffset = drawGraph(graphics, graph.getRootContextNode().getContextNodes(), offset);
		adjustTotalMaxOffset(totalMaxOffset, maxOffset);

		totalMaxOffset.translate(MARGIN, MARGIN);

		if (graphics != null) {

			graphics.setStroke(BOX_STROKE);
			graphics.setColor(Color.BLACK);
			graphics.drawRect(0, 0, (int) totalMaxOffset.getX(), (int) totalMaxOffset.getY());
		}

		return totalMaxOffset;
	}

	private static Point drawLegend(Graphics2D graphics, Point offset) {
/*
		Point currentOffset = new Point(offset);

		write(graphics, "Legend:", (int) offset.getX() + 20, (int) offset.getY() + BOX_HEIGHT / 2, true, true, FONT_SEGMENT);

		currentOffset.translate(60, 0);
		drawContextNode(graphics, "Context Node", currentOffset, CONTEXTNODE_COLOR, false, false);
		currentOffset.translate(BOX_WIDTH, 0);

		currentOffset.translate(10, 0);
		drawContextNode(graphics, "Relation", currentOffset, RELATION_COLOR, false, false);
		currentOffset.translate(BOX_WIDTH, 0);

		currentOffset.translate(10, 0);
		drawLiteral(graphics, "\"Literal\"", currentOffset, false);
		currentOffset.translate(LITERAL_WIDTH, 0);
*/
		Point currentOffset = offset;
		return currentOffset;
	}

	private static Point drawGraph(Graphics2D graphics, Iterator<ContextNode> contextNodes, Point offset) {

		Point firstOffset;
		Point secondOffset;
		Point maxOffset;
		Point totalMaxOffset;

		firstOffset = new Point(offset);
		maxOffset = new Point(offset);
		totalMaxOffset = new Point(offset);

		for (Iterator<ContextNode> i = contextNodes; i.hasNext(); ) {

			ContextNode contextNode = i.next();
			secondOffset = drawContextNode(graphics, contextNode, firstOffset, CONTEXTNODE_COLOR, true, true);
			maxOffset = new Point(secondOffset);
			adjustTotalMaxOffset(totalMaxOffset, maxOffset);

			if (contextNode.containsRelations()) {

/*				for (Iterator<Relation> ii=contextNode.getRelations(); ii.hasNext(); ) {

					Relation relation = ii.next();

					maxOffset = drawContextNode(graphics, relation.getArcXri().toString(), secondOffset, RELATION_COLOR, true, true);
					adjustTotalMaxOffset(totalMaxOffset, maxOffset);

					if (graphics != null) graphics.setStroke(ARC_STROKE);
					if (ii.hasNext())
						drawLine(graphics, (int) secondOffset.getX(), (int) secondOffset.getY(), (int) secondOffset.getX(), (int) maxOffset.getY());

					secondOffset.setLocation(secondOffset.getX(), maxOffset.getY());
				}*/
			} 

			if (contextNode.containsLiteral()) {

				Literal literal = contextNode.getLiteral();

				maxOffset = drawLiteral(graphics, "\"" + literal.getLiteralData() + "\"", secondOffset, true);
				adjustTotalMaxOffset(totalMaxOffset, maxOffset);

				secondOffset.setLocation(secondOffset.getX(), maxOffset.getY());
			} 
			
			if (contextNode.containsContextNodes()) {

				Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes();

				maxOffset = drawGraph(graphics, innerContextNodes, secondOffset);
				adjustTotalMaxOffset(totalMaxOffset, maxOffset);

				secondOffset.setLocation(secondOffset.getX(), maxOffset.getY());
			}

			if (graphics != null) graphics.setStroke(ARC_STROKE);
			if (i.hasNext())
				drawLine(graphics, (int) firstOffset.getX(), (int) firstOffset.getY(), (int) firstOffset.getX(), (int) maxOffset.getY());

			firstOffset.setLocation(firstOffset.getX(), maxOffset.getY());
		}

		totalMaxOffset.translate(GRAPH_SPACING_WIDTH, GRAPH_SPACING_HEIGHT);

		return totalMaxOffset;
	}

	private static Point drawContextNode(Graphics2D graphics, ContextNode contextNode, Point offset, Color color, boolean withArc, boolean upLine) {

		Point currentOffset = new Point(offset);

		if (withArc) {

			drawArc(graphics, currentOffset, upLine);
			currentOffset.translate(ARC_WIDTH, ARC_HEIGHT / 2);
		}

		drawContextNodeBox(graphics, currentOffset, contextNode, color);
		currentOffset.translate(CONTEXTNODE_WIDTH, CONTEXTNODE_HEIGHT);

		return currentOffset;
	}

	private static void drawContextNodeBox(Graphics2D graphics, Point offset, ContextNode contextNode, Color color) {

		if (graphics == null) return;

		Color currentColor = (graphics != null) ? graphics.getColor() : null;

		graphics.setColor(color);
		graphics.setStroke(BOX_STROKE);
		graphics.drawOval((int) offset.getX(), (int) offset.getY(), CONTEXTNODE_WIDTH, CONTEXTNODE_HEIGHT);
		graphics.setColor(currentColor);

		write(graphics, contextNode.getArcXri().toString(), (int) offset.getX() + CONTEXTNODE_WIDTH / 2, (int) offset.getY() + CONTEXTNODE_HEIGHT / 2, true, true, FONT_SEGMENT);
	}

	private static Point drawLiteral(Graphics2D graphics, String literal, Point offset, boolean withArc) {

		Point currentOffset = new Point(offset);

		if (withArc) {

			drawArc(graphics, currentOffset, true);
			currentOffset.translate(ARC_WIDTH, ARC_HEIGHT / 2);
		}

		drawLiteralBox(graphics, currentOffset, literal);
		currentOffset.translate(LITERAL_WIDTH, LITERAL_HEIGHT);

		return currentOffset;
	}

	private static void drawArc(Graphics2D graphics, Point offset, boolean upLine) {

		if (graphics == null) return;

		Color currentColor = (graphics != null) ? graphics.getColor() : null;

		graphics.setColor(ARC_COLOR);
		graphics.setStroke(ARC_STROKE);
		graphics.drawArc((int) offset.getX(), (int) offset.getY() - ARC_HEIGHT, ARC_WIDTH * 2, ARC_HEIGHT * 2, -180, 90);
		if (upLine) graphics.drawLine((int) offset.getX(), (int) offset.getY(), (int) offset.getX(), (int) offset.getY() - SPACING_HEIGHT / 2);
		graphics.setColor(currentColor);
	}

	private static void drawLiteralBox(Graphics2D graphics, Point offset, String text) {

		if (graphics == null) return;

		Color currentColor = (graphics != null) ? graphics.getColor() : null;

		graphics.setColor(LITERAL_COLOR);
		graphics.setStroke(BOX_STROKE);
		graphics.drawRect((int) offset.getX(), (int) offset.getY(), LITERAL_WIDTH, LITERAL_HEIGHT);
		write(graphics, text, (int) offset.getX() + 10, (int) offset.getY() + LITERAL_HEIGHT / 2, false, true, FONT_LITERAL);
		graphics.setColor(currentColor);
	}

	private static void drawLine(Graphics2D graphics, int x1, int y1, int x2, int y2) {

		if (graphics == null) return;

		graphics.drawLine(x1, y1, x2, y2);
	}

	private static void write(Graphics2D graphics, String text, int posX, int posY, boolean centerX, boolean centerY, Font font) {

		if (graphics == null) return;

		FontMetrics fontMetrics = graphics.getFontMetrics(FONT_SEGMENT);

		if (centerX) posX -= (int) (fontMetrics.getStringBounds(text, graphics).getWidth() / 2);
		if (centerY) posY -= (int) (fontMetrics.getStringBounds(text, graphics).getHeight() / 2);

		graphics.setFont(font);
		graphics.setColor(FONT_COLOR);
		graphics.drawString(text, posX, posY + fontMetrics.getAscent());
	}

	@SuppressWarnings("unused")
	private static List<String> getAllSubSegments(List<XRI3SubSegment> subSegments) {

		List<String> list = new ArrayList<String> ();

		for (XRI3SubSegment subSegment : subSegments) {

			if (subSegment.hasXRef()) {

				XRI3Reference xref = (XRI3Reference) subSegment.getXRef().getXRIReference();
				XRI3Authority xrefAuthority = ((XRI3Authority) xref.getAuthority());
				List<XRI3Segment> xrefSegments = ((XRI3Path) xref.getPath()).getSegments();

				list.addAll(getAllSubSegments(xrefAuthority.getSubSegments()));

				for (XRI3Segment xrefSegment : xrefSegments) {

					list.addAll(getAllSubSegments(xrefSegment.getSubSegments()));
				}
			} else {

				list.add(subSegment.toString());
			}
		}

		return list;
	}

	private static void adjustTotalMaxOffset(Point totalMaxOffset, Point maxOffset) {

		if (maxOffset.getX() > totalMaxOffset.getX()) totalMaxOffset.setLocation(maxOffset.getX(), totalMaxOffset.getY());
		if (maxOffset.getY() > totalMaxOffset.getY()) totalMaxOffset.setLocation(totalMaxOffset.getX(), maxOffset.getY());
	}
}
