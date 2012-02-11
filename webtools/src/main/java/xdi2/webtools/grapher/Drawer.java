package xdi2.webtools.grapher;

import java.awt.Graphics2D;
import java.awt.Point;

import org.eclipse.higgins.xdi4j.Graph;

public interface Drawer {

	public Point draw(Graphics2D graphics, Graph graph, Point size);
}
