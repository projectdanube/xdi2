package xdi2.messaging.tests.target.impl.graph;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.impl.json.AbstractJSONGraphFactory;
import xdi2.core.impl.json.file.FileJSONStore;

public class JSONFileGraphMessagingTargetTest extends AbstractGraphMessagingTargetTest {

	private static AbstractJSONGraphFactory graphFactory = new AbstractJSONGraphFactory();

	static {

		cleanup();

		graphFactory.setJsonStore(new FileJSONStore());
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
