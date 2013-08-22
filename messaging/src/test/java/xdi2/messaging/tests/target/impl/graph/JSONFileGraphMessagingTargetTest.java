package xdi2.messaging.tests.target.impl.graph;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.file.FileJSONGraphFactory;

public class JSONFileGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static FileJSONGraphFactory graphFactory = new FileJSONGraphFactory();

	static {

		cleanup();
	}

	public static void cleanup() {

		File[] files = new File(".").listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File file, String filename) {

				return filename.endsWith(".json");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
