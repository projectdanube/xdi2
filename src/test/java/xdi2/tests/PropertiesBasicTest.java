package xdi2.tests;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.Graph;
import xdi2.impl.keyvalue.properties.PropertiesGraphFactory;

public class PropertiesBasicTest extends BasicTest {

	private PropertiesGraphFactory graphFactory = new PropertiesGraphFactory();

	public static void deleteFiles() throws IOException {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith("xdi2-graph.") && name.endsWith(".properties");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		File file = new File(".", "xdi2-graph." + id + ".properties");
		if (file.exists()) file.delete();

		this.graphFactory.setFile(file);
		this.graphFactory.setAutoSave(true);

		return this.graphFactory.openGraph();
	}
}
