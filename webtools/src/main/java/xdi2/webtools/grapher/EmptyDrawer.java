package xdi2.webtools.grapher;

import java.awt.image.BufferedImage;

import xdi2.core.Graph;

public class EmptyDrawer implements Drawer {

	@Override
	public BufferedImage draw(Graph graph) {

		return new BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_RGB);
	}
}
