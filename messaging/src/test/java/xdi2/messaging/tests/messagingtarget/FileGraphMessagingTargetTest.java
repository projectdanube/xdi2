package xdi2.messaging.tests.messagingtarget;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.wrapped.file.FileGraphFactory;

public class FileGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static FileGraphFactory graphFactory = new FileGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {

				return name.startsWith("xdi2-test-graph.") && name.endsWith(".xdi");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String id) throws IOException {

		String path = "xdi2-test-graph." + id + ".xdi";

		File file = new File(path);
		if (file.exists()) file.delete();

		graphFactory.setPath(path);

		return graphFactory.openGraph();
	}
}
