package xdi2.tests.core.impl.wrapper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.wrapped.file.FileWrapperGraphFactory;
import xdi2.tests.core.impl.AbstractGraphTest;

public class FileWrapperGraphTest extends AbstractGraphTest {

	private static FileWrapperGraphFactory graphFactory = new FileWrapperGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith("xdi2-graph.") && name.endsWith(".xdi");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}

	@Override
	protected Graph reopenGraph(Graph graph, String identifier) throws IOException {

		graph.close();

		return graphFactory.openGraph(identifier);
	}
}
