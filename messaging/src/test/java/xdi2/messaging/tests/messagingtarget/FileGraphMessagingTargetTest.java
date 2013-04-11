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

				return name.startsWith("xdi2-graph.") && name.endsWith(".xdi");
			}
		});

		for (File file : files) file.delete();
	}

	@Override
	protected Graph openNewGraph(String identifier) throws IOException {

		return graphFactory.openGraph(identifier);
	}
}
