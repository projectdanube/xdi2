package xdi2.webtools.grapher;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Authority;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Path;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Reference;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

public class Drawer1 implements Drawer {

	public static final int MARGIN = 20;
	public static final int STROKE_SIZE = 4;
	public static final int BOX_WIDTH = 50;
	public static final int BOX_HEIGHT = 50;
	public static final int LITERAL_HEIGHT = 50;
	public static final int SPACING_HEIGHT = BOX_HEIGHT;
	public static final Font FONT_SEGMENT = new Font("Dialog", Font.PLAIN, 12);
	public static final Font FONT_LITERAL = new Font("Dialog", Font.PLAIN, 12);

	private static final Graphics2D TEMP_GRAPHICS2D;

	static {

		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		TEMP_GRAPHICS2D = image.createGraphics();
	}

	public Point draw(Graphics2D graphics, Graph graph, Point size) {

		if (graphics != null && size != null) {

			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, (int) size.getX(), (int) size.getY());
			graphics.setColor(Color.BLACK);
			graphics.setStroke(new BasicStroke(STROKE_SIZE));
			graphics.drawRect(0, 0, (int) size.getX(), (int) size.getY());
		}

		Point offset = new Point(MARGIN, MARGIN);
		offset = drawGraph(graphics, graph, offset);
		offset.translate(MARGIN, MARGIN);

		return offset;
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
			secondOffset = drawSegment(graphics, subject.getSubjectXri(), firstOffset);
			maxOffset = new Point(secondOffset);
			adjustTotalMaxOffset(totalMaxOffset, maxOffset);

			for (Iterator<Predicate> ii=subject.getPredicates(); ii.hasNext(); ) {

				Predicate predicate = ii.next();
				thirdOffset = drawSegment(graphics, predicate.getPredicateXri(), secondOffset);
				maxOffset = new Point(thirdOffset);
				adjustTotalMaxOffset(totalMaxOffset, maxOffset);

				if (predicate.containsReferences()) {

					for (Iterator<Reference> iii=predicate.getReferences(); iii.hasNext(); ) {

						Reference reference = iii.next();
						maxOffset = drawSegment(graphics, reference.getReferenceXri(), thirdOffset);
						adjustTotalMaxOffset(totalMaxOffset, maxOffset);

						thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
					}
				} else if (predicate.containsLiteral()) {

					Literal literal = predicate.getLiteral();

					maxOffset = drawLiteral(graphics, literal.getData(), thirdOffset);
					adjustTotalMaxOffset(totalMaxOffset, maxOffset);

					thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
				} else if (predicate.containsInnerGraph()) {

					Graph innerGraph = predicate.getInnerGraph();

					maxOffset = drawInnerGraph(graphics, innerGraph, thirdOffset);
					adjustTotalMaxOffset(totalMaxOffset, maxOffset);

					thirdOffset.setLocation(thirdOffset.getX(), maxOffset.getY());
				}

				if (ii.hasNext())
					drawLine(graphics, (int) secondOffset.getX(), (int) secondOffset.getY(), (int) secondOffset.getX(), (int) maxOffset.getY() + SPACING_HEIGHT);

				secondOffset.setLocation(secondOffset.getX(), maxOffset.getY() + SPACING_HEIGHT);
			}

			if (i.hasNext())
				drawLine(graphics, (int) firstOffset.getX(), (int) firstOffset.getY(), (int) firstOffset.getX(), (int) maxOffset.getY() + SPACING_HEIGHT);

			firstOffset.setLocation(firstOffset.getX(), maxOffset.getY() + SPACING_HEIGHT);
		}

		return totalMaxOffset;
	}

	private static Point drawSegment(Graphics2D graphics, XRI3Segment segment, Point offset) {

		Point currentOffset = new Point(offset);

		if (segment.getNumSubSegments() == 1 && 
				segment.getSubSegment(0).hasXRef() && 
				segment.getSubSegment(0).getXRef().hasXRIReference() &&
				(
						(
								segment.getSubSegment(0).getXRef().getXRIReference().hasAuthority() &&
								! segment.getSubSegment(0).getXRef().getXRIReference().hasPath()
						) ||
						(
								! segment.getSubSegment(0).getXRef().getXRIReference().hasAuthority() &&
								segment.getSubSegment(0).getXRef().getXRIReference().getPath().getNumSegments() == 1
						)
				)
		) {

			currentOffset.translate(BOX_WIDTH, BOX_HEIGHT);
			XRI3Reference xref = ((XRI3Reference) segment.getSubSegment(0).getXRef().getXRIReference());
			Graph xrefGraph = Addressing.convertAddressToGraph(xref.toXRI3());
			currentOffset = drawGraph(graphics, xrefGraph, currentOffset);
		} else {

			for (XRI3SubSegment subSegment : (List<XRI3SubSegment>) segment.getSubSegments()) {

				if (subSegment.hasXRef()) {

					if (subSegment.getXRef().hasIRI()) {

						drawSegmentBox(graphics, currentOffset, subSegment.getXRef().toString(), 1);
						currentOffset.translate(BOX_WIDTH, BOX_HEIGHT);
						drawLine(graphics, currentOffset.x, currentOffset.y, currentOffset.x, offset.y);
						drawLine(graphics, currentOffset.x, currentOffset.y, offset.x, currentOffset.y);
					} else {

						XRI3Reference xref = ((XRI3Reference) subSegment.getXRef().getXRIReference());
						Graph xrefGraph = Addressing.convertAddressToGraph(xref.toXRI3());
						currentOffset = drawGraph(graphics, xrefGraph, currentOffset);
					}
				} else {

					drawSegmentBox(graphics, currentOffset, subSegment.toString(), 1);
					currentOffset.translate(BOX_WIDTH, BOX_HEIGHT);

					for (int x=offset.x; x<=currentOffset.x; x+=BOX_WIDTH)
						drawLine(graphics, x, currentOffset.y, x, offset.y);

					for (int y=offset.y; y<=currentOffset.y; y+=BOX_HEIGHT)
						drawLine(graphics, currentOffset.x, y, offset.x, y);
				}
			}
		}

		if (graphics != null)
			graphics.drawRect((int) offset.getX(), (int) offset.getY(), (int) currentOffset.getX() - (int) offset.getX(), (int) currentOffset.getY() - (int) offset.getY());

		return currentOffset;
	}

	private static Point drawLiteral(Graphics2D graphics, String literal, Point offset) {

		Point currentOffset = new Point(offset);

		int literalWidth = drawLiteralBox(graphics, offset, "\"" + literal + "\"");

		currentOffset.translate(literalWidth, LITERAL_HEIGHT);

		return currentOffset;
	}

	private static Point drawInnerGraph(Graphics2D graphics, Graph innerGraph, Point offset) {

		Point currentOffset = new Point(offset);

		drawSegmentBox(graphics, offset, "", 1);

		currentOffset.translate(BOX_WIDTH, BOX_HEIGHT);

		currentOffset = drawGraph(graphics, innerGraph, currentOffset);

		return currentOffset;
	}

	private static void drawSegmentBox(Graphics2D graphics, Point offset, String text, int size) {

		if (graphics == null) return;

		graphics.drawRect((int) offset.getX(), (int) offset.getY(), size * BOX_WIDTH, size * BOX_HEIGHT);
		write(graphics, text, (int) offset.getX() + size * BOX_WIDTH / 2, (int) offset.getY() + size * BOX_HEIGHT / 2, true, true, FONT_SEGMENT);
	}

	private static int drawLiteralBox(Graphics2D graphics, Point offset, String text) {

		Graphics2D g = graphics;
		if (g == null) g = TEMP_GRAPHICS2D;

		FontMetrics fontMetrics = g.getFontMetrics(FONT_SEGMENT);
		int literalWidth = (int) fontMetrics.getStringBounds(text, graphics).getWidth() + 20;

		if (graphics == null) return literalWidth;

		graphics.fillRect((int) offset.getX(), (int) offset.getY(), literalWidth, LITERAL_HEIGHT);
		graphics.setColor(Color.WHITE);
		write(graphics, text, (int) offset.getX() + 10, (int) offset.getY() + LITERAL_HEIGHT / 2, false, true, FONT_LITERAL);
		graphics.setColor(Color.BLACK);

		return literalWidth;
	}

	private static void drawLine(Graphics2D graphics, int x1, int y1, int x2, int y2) {

		if (graphics == null) return;

		graphics.drawLine(x1, y1, x2, y2);
	}

	private static void write(Graphics2D graphics, String text, int posX, int posY, boolean centerX, boolean centerY, Font font) {

		FontMetrics fontMetrics = graphics.getFontMetrics(FONT_SEGMENT);

		if (centerX) posX -= (int) (fontMetrics.getStringBounds(text, graphics).getWidth() / 2);
		if (centerY) posY -= (int) (fontMetrics.getStringBounds(text, graphics).getHeight() / 2);

		graphics.setFont(font);
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
