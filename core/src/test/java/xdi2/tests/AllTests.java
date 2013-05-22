package xdi2.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import xdi2.tests.core.features.datatypes.DataTypesTest;
import xdi2.tests.core.features.dictionary.DictionaryTest;
import xdi2.tests.core.features.equivalence.EquivalenceTest;
import xdi2.tests.core.features.nodetypes.ContextFunctionsTest;
import xdi2.tests.core.features.nodetypes.InnerRootsTest;
import xdi2.tests.core.features.nodetypes.LocalRootsTest;
import xdi2.tests.core.features.nodetypes.PeerRootsTest;
import xdi2.tests.core.features.nodetypes.RootsTest;
import xdi2.tests.core.features.timestamps.TimestampsTest;
import xdi2.tests.core.features.variables.VariablesTest;
import xdi2.tests.core.graph.BDBGraphTest;
import xdi2.tests.core.graph.FileGraphTest;
import xdi2.tests.core.graph.MapGraphTest;
import xdi2.tests.core.graph.MemoryGraphTest;
import xdi2.tests.core.graph.PropertiesGraphTest;
import xdi2.tests.core.impl.keyvalue.BDBKeyValueTest;
import xdi2.tests.core.impl.keyvalue.MapKeyValueTest;
import xdi2.tests.core.impl.keyvalue.PropertiesKeyValueTest;
import xdi2.tests.core.io.ReaderWriterRegistryTest;
import xdi2.tests.core.io.ReaderWriterTest;
import xdi2.tests.core.util.CopyUtilTest;
import xdi2.tests.core.util.StatementUtilTest;
import xdi2.tests.core.util.XDI3UtilTest;
import xdi2.tests.core.util.XRI2UtilTest;
import xdi2.tests.core.util.iterators.IteratorTest;
import xdi2.tests.core.xri3.XDI3ParserAPGTest;
import xdi2.tests.core.xri3.XDI3ParserAParseTest;
import xdi2.tests.core.xri3.XDI3ParserManualTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(XDI3ParserAParseTest.class);
		suite.addTestSuite(XDI3ParserAPGTest.class);
		suite.addTestSuite(XDI3ParserManualTest.class);
		suite.addTestSuite(MemoryGraphTest.class);
		suite.addTestSuite(MapGraphTest.class);
		suite.addTestSuite(PropertiesGraphTest.class);
		suite.addTestSuite(BDBGraphTest.class);
		suite.addTestSuite(FileGraphTest.class);
		suite.addTestSuite(MapKeyValueTest.class);
		suite.addTestSuite(PropertiesKeyValueTest.class);
		suite.addTestSuite(BDBKeyValueTest.class);
		suite.addTestSuite(DataTypesTest.class);
		suite.addTestSuite(DictionaryTest.class);
		suite.addTestSuite(EquivalenceTest.class);
		suite.addTestSuite(ContextFunctionsTest.class);
		suite.addTestSuite(RootsTest.class);
		suite.addTestSuite(LocalRootsTest.class);
		suite.addTestSuite(PeerRootsTest.class);
		suite.addTestSuite(InnerRootsTest.class);
		suite.addTestSuite(TimestampsTest.class);
		suite.addTestSuite(VariablesTest.class);
		suite.addTestSuite(ReaderWriterRegistryTest.class);
		suite.addTestSuite(ReaderWriterTest.class);
		suite.addTestSuite(XRI2UtilTest.class);
		suite.addTestSuite(XDI3UtilTest.class);
		suite.addTestSuite(CopyUtilTest.class);
		suite.addTestSuite(StatementUtilTest.class);
		suite.addTestSuite(IteratorTest.class);
		//$JUnit-END$
		return suite;
	}
}
