package xdi2.tests.core.features.datatypes;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import xdi2.core.Graph;
import xdi2.core.features.datatypes.DataTypes;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.xri3.XDI3Segment;

public class DataTypesTest extends TestCase {

	public void testDatatypes() throws Exception {

		StringBuilder buffer = new StringBuilder();

		buffer.append("=markus[<+age>]:/:/\"33\"\n");
		buffer.append("=markus[<+age>]/$is+/+$xsd$int\n");
		buffer.append("=markus[<+age>]/$is+/+$json$number\n");
		String xdiString = buffer.toString();

		Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString, "XDI DISPLAY", null);

		DataTypes.setLiteralDataType(graph.findLiteral(XDI3Segment.create("=markus[<+age>]:")), XDI3Segment.create("+$mime$image$png"));

		List<XDI3Segment> dataTypes = DataTypes.getLiteralDataType(graph.findLiteral(XDI3Segment.create("=markus[<+age>]:")));

		assertNotNull(dataTypes);

		for (XDI3Segment xriSeg : dataTypes) {

			if (xriSeg.toString().contains("json")) {

				assertEquals("number", DataTypes.jsonTypeFromDataTypeXri(xriSeg).toString());
			} else if (xriSeg.toString().contains("xsd")) {

				assertEquals("xsd:int", DataTypes.xsdTypeFromDataTypeXri(xriSeg).toString());
			} else if (xriSeg.toString().contains("mime")) {

				assertEquals("image/png", DataTypes.mimeTypeFromDataTypeXri(xriSeg).toString());
			}
		}
	}

	@Test
	public void testDuplicateDatatypes() throws Exception {

		StringBuilder buffer = new StringBuilder();

		buffer.append("=markus$!(+age)/!/(data:,33)\n");
		buffer.append("=markus$!(+age)/$is+/+$xsd$int!\n");
		buffer.append("=markus$!(+age)/$is+/+$json$number!\n");
		String xdiString = buffer.toString();

		try {

			Graph graph = (new MemoryGraphFactory()).parseGraph(xdiString);
			DataTypes.setLiteralDataType(graph.findLiteral(XDI3Segment.create("=markus$!(+age)")), XDI3Segment.create("+$json$number!"));

			fail();
		} catch (Exception ex) { }
	}
}
