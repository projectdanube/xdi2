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

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Authority;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Path;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Reference;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

public class Drawer2 implements Drawer {

	public static final int MARGIN = 20;
	public static final int BOX_WIDTH = 160;
	public static final int BOX_HEIGHT = 30;
	public static final int BOX_ARCWIDTH = 30;
	public static final int BOX_ARCHEIGHT = 30;
	public static final int ARC_WIDTH = 30;
	public static final int ARC_HEIGHT = 30;
	public static final int LITERAL_WIDTH = 160;
	public static final int LITERAL_HEIGHT = 30;
	public static final int SPACING_HEIGHT = 20;
	public static final int GRAPH_SPACING_WIDTH = 20;
	public static final int GRAPH_SPACING_HEIGHT = 20;
	public static final Font FONT_SEGMENT = new Font("Dialog", Font.PLAIN, 12);
	public static final Font FONT_LITERAL = new Font("Dialog", Font.PLAIN, 12);
	public static final Color SUBJECT_COLOR = Color.BLUE; 
	public static final Color PREDICATE_COLOR = Color.RED; 
	public static final Color REFERENCE_COLOR = Color.BLACK; 
	public static final Color LITERAL_COLOR = Color.BLACK; 
	public static final Color GRAPH_COLOR = Color.GRAY;
	public static final Color ARC_COLOR = Color.BLACK; 
	public static final Color FONT_COLOR = Color.BLACK; 
	public static final Stroke GRAPH_STROKE = new BasicStroke(4, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {7, 7}, 0);  
	public static final Stroke BOX_STROKE = new BasicStroke(4);
	public static final Stroke ARC_STROKE = new BasicStroke(2);

	private boolean legend;

	public Drawer2(boolean legend) {

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

		maxOffset = drawGraph(graphics, graph, offset);
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

		Point currentOffset = new Point(offset);

		write(graphics, "Legend:", (int) offset.getX() + 20, (int) offset.getY() + BOX_HEIGHT / 2, true, true, FONT_SEGMENT);

		currentOffset.translate(60, 0);
		drawSegment(graphics, "Subject", currentOffset, SUBJECT_COLOR, false, false);
		currentOffset.translate(BOX_WIDTH, 0);

		currentOffset.translate(10, 0);
		drawSegment(graphics, "Predicate", currentOffset, PREDICATE_COLOR, false, false);
		currentOffset.translate(BOX_WIDTH, 0);

		currentOffset.translate(10, 0);
		drawSegment(graphics, "Reference", currentOffset, REFERENCE_COLOR, false, false);
		currentOffset.translate(BOX_WIDTH, 0);

		currentOffset.translate(10, 0);
		drawLiteral(graphics, "\"Literal\"", currentOffset, false);
		currentOffset.translate(LITERAL_WIDTH, 0);

		currentOffset.translate(10, 0);
		if (graphics != null) graphics.setStroke(GRAPH_STROKE);
		if (graphics != null) graphics.setColor(GRAPH_COLOR);
		if (graphics != null) graphics.drawRect((int) currentOffset.getX(), (int) currentOffset.getY(), LITERAL_WIDTH, LITERAL_HEIGHT);
		if (graphics != null) write(graphics, "Graph/Context", (int) currentOffset.getX() + 10, (int) currentOffset.getY() + LITERAL_HEIGHT / 2, false, true, FONT_LITERAL);
		currentOffset.translate(LITERAL_WIDTH, LITERAL_HEIGHT);

		return currentOffset;
	}

	private static Point drawGraph(Graphics2D graphics, Graph graph, Point offset) {

		Point firstOffset;
		Point secondOffset;
		Point thirdOffset;
		Point maxOffset;
		Point totalMaxOffset;

		firstOffset = new Point(offset);
		maxOffset = new Point(offset);
		totalMaxOffset = new Point(offset);

		for (Iterator<Subject> i=graph.getSubjects(); i.hasNext(); ) {

			Subject subject = i.next();
			secondOffset = drawSegment(graphics, subject.getSubjectXri().toString(), firstOffset, SUBJECT_COLOR, true, false);
			maxOffset = new Point(secondOffset);
			adjustTotalMaxOffset(totalMaxOffset, maxOffset);

			for (Iterator<Predicate> ii=subject.getPredicates(); ii.hasNext(); ) {

				Predicate predicate = ii.next();
				thirdOffset = drawSegment(graphics, predicate.getPredicateXri().toString(), secondOffset, PREDICATE_COLOR, true, true);
				maxOffset = new Point(thirdOffset);
				adjustTotalMaxOffset(totalMaxOffset, maxOffset);

				if (predicate.containsReferences()) {

					for (Iterator<Reference> iii=predicate.getReferences(); iii.hasNext(); ) {

						Reference reference = iii.next();

						maxOffset = drawSegment(graphics, reference.getReferenceXri().toString(), thirdOffset, REFERENCE_COLOR, true, true);
						adjustTotalMaxOffset(totalMaxOffset, maxOffset);

						if (graphics != null) graphics.setStroke(ARC_STROKE);
						if (iii.hasNext())
							drawLine(graphics, (int) thirdOffset.getX(), (int) thirdOffset.getY(), (int) thirdOffset.getX(), (int) maxOffset.getY());

						thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
					}
				} else if (predicate.containsLiteral()) {

					Literal literal = predicate.getLiteral();

					maxOffset = drawLiteral(graphics, "\"" + literal.getData() + "\"", thirdOffset, true);
					adjustTotalMaxOffset(totalMaxOffset, maxOffset);

					thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
				} else if (predicate.containsInnerGraph()) {

					Graph innerGraph = predicate.getInnerGraph();

					maxOffset = drawInnerGraph(graphics, innerGraph, thirdOffset, true);
					adjustTotalMaxOffset(totalMaxOffset, maxOffset);

					thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
				}

				if (graphics != null) graphics.setStroke(ARC_STROKE);
				if (ii.hasNext())
					drawLine(graphics, (int) secondOffset.getX(), (int) secondOffset.getY(), (int) secondOffset.getX(), (int) maxOffset.getY());

				secondOffset.setLocation(secondOffset.getX(), maxOffset.getY());
			}

			if (graphics != null) graphics.setStroke(ARC_STROKE);

			firstOffset.setLocation(firstOffset.getX(), maxOffset.getY());
		}

		if (graphics != null) {

			Color currentColor = (graphics != null) ? graphics.getColor() : null;

			graphics.setStroke(GRAPH_STROKE);
			graphics.setColor(GRAPH_COLOR);
			graphics.drawRect((int) offset.getX(), (int) offset.getY(), (int) totalMaxOffset.getX() - (int) offset.getX() + GRAPH_SPACING_WIDTH, (int) totalMaxOffset.getY() - (int) offset.getY() + GRAPH_SPACING_HEIGHT);
			graphics.setColor(currentColor);
		}

		totalMaxOffset.translate(GRAPH_SPACING_WIDTH, GRAPH_SPACING_HEIGHT);

		return totalMaxOffset;
	}

	private static Point drawSegment(Graphics2D graphics, String segment, Point offset, Color color, boolean withArc, boolean upLine) {

		Point currentOffset = new Point(offset);

		if (withArc) {

			drawArc(graphics, currentOffset, upLine);
			currentOffset.translate(ARC_WIDTH, ARC_HEIGHT / 2);
		}

		drawSegmentBox(graphics, currentOffset, segment, color);
		currentOffset.translate(BOX_WIDTH, BOX_HEIGHT);

		return currentOffset;
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

	private static Point drawInnerGraph(Graphics2D graphics, Graph innerGraph, Point offset, boolean withArc) {

		Point currentOffset = new Point(offset);

		if (withArc) {

			drawArc(graphics, currentOffset, false);
			currentOffset.translate(ARC_WIDTH, ARC_HEIGHT);
		}

		currentOffset = drawGraph(graphics, innerGraph, currentOffset);

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

	private static void drawSegmentBox(Graphics2D graphics, Point offset, String text, Color color) {

		if (graphics == null) return;

		Color currentColor = (graphics != null) ? graphics.getColor() : null;

		graphics.setColor(color);
		graphics.setStroke(BOX_STROKE);
		graphics.drawRoundRect((int) offset.getX(), (int) offset.getY(), BOX_WIDTH, BOX_HEIGHT, BOX_ARCWIDTH, BOX_ARCHEIGHT);
		graphics.setColor(currentColor);

		write(graphics, text, (int) offset.getX() + BOX_WIDTH / 2, (int) offset.getY() + BOX_HEIGHT / 2, true, true, FONT_SEGMENT);
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
