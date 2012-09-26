package xdi2.tests.core.features.datatypes;

import java.util.List;

import org.junit.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import xdi2.core.Graph;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.impl.XRI3Segment;

public class DataTypesTest extends TestCase {

	@Test
	public void testDatatypes() throws Exception {
		StringBuilder sbXDI = new StringBuilder();
		sbXDI.append("=markus$!(+age)/!/(data:,33)\n");
		sbXDI.append("=markus$!(+age)/$is+/+$xsd$int!\n");
		sbXDI.append("=markus$!(+age)/$is+/+$json$number!\n");
		String xdiString = sbXDI.toString();

		try {
			// Create graph object from xdi string
			Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);

			// Set a new datatype to a literal in graph
			DataTypes.setLiteralDataType(
					graph.findLiteral(new XRI3Segment("=markus$!(+age)")),
					new XRI3Segment("+$mime$image$png!"));

			// Get datatype list for a literal
			List<XRI3Segment> lst = DataTypes.getLiteralDataType(graph
					.findLiteral(new XRI3Segment("=markus$!(+age)")));

			assertNotNull(lst);

			for (XRI3Segment xriSeg : lst) {

				if (xriSeg.toString().contains("json")) {

					// Get datatype from xri datatype list and assert for a
					// valid json type
					assertEquals("number",
							DataTypes.jsonTypeFromDataTypeXri(xriSeg)
									.toString());

				} else if (xriSeg.toString().contains("xsd")) {

					// Get datatype from xri datatype list and assert for a
					// valid xsd type
					assertEquals("xsd:int",
							DataTypes.xsdTypeFromDataTypeXri(xriSeg).toString());

				} else if (xriSeg.toString().contains("mime")) {

					// Get datatype from xri datatype list and assert for a
					// valid mime type
					assertEquals("image/png", DataTypes
							.mimeTypeFromDataTypeXri(xriSeg).toString());

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			Assert.fail();

		}

	}

	@Test
	public void testDuplicateDatatypes() throws Exception {
		StringBuilder sbXDI = new StringBuilder();
		sbXDI.append("=markus$!(+age)/!/(data:,33)\n");
		sbXDI.append("=markus$!(+age)/$is+/+$xsd$int!\n");
		sbXDI.append("=markus$!(+age)/$is+/+$json$number!\n");
		String xdiString = sbXDI.toString();

		try {
			// Create graph object from xdi string
			Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);

			// Set a new datatype to a literal in graph
			DataTypes.setLiteralDataType(
					graph.findLiteral(new XRI3Segment("=markus$!(+age)")),
					new XRI3Segment("+$json$number!"));
			Assert.fail();

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}

	@Test
	public void testBinaryDatatypes() throws Exception {
		StringBuilder sbXDI = new StringBuilder();
		sbXDI.append("=markus$!(+age)/!/(data:,33)\n");
		sbXDI.append("=markus$!(+age)/$is+/+$xsd$int!\n");
		sbXDI.append("=markus$!(+age)/$is+/+$json$number!\n");
		String xdiString = sbXDI.toString();

		try {
			// Create graph object from xdi string
			Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);

			// Set a new datatype to a literal in graph
			DataTypes.setLiteralDataType(
					graph.findLiteral(new XRI3Segment("=markus$!(+age)")),
					new XRI3Segment("+$binary!"));

			// Get datatype from xri datatype list and assert for a binary type
			assertEquals(true, DataTypes.isLiteralBinary(graph
					.findLiteral(new XRI3Segment("=markus$!(+age)"))));

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			Assert.fail();
		}

	}

}
