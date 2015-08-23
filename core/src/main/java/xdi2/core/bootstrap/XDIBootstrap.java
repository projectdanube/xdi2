package xdi2.core.bootstrap;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;

public class XDIBootstrap {

	public static final XDIAddress ALL_LINK_CONTRACT_ADDRESS = XDIAddress.create("$all{$do}");
	public static final XDIAddress GET_LINK_CONTRACT_ADDRESS = XDIAddress.create("$get{$do}");
	public static final XDIAddress PUSH_LINK_CONTRACT_ADDRESS = XDIAddress.create("$push{$do}");

	public static final Graph BOOTSTRAP_GRAPH;
	public static final LinkContractTemplate ALL_LINK_CONTRACT_TEMPLATE;
	public static final LinkContractTemplate GET_LINK_CONTRACT_TEMPLATE;
	public static final LinkContractTemplate PUSH_LINK_CONTRACT_TEMPLATE;

	static {

		String bootstrapGraphString = "" +

				// identity statements

				"($xdi)/$ref/\n" +
				"/$is$ref/($xdi)\n" +

				// public link contract

				"($xdi/$public)$do/$get/\n" +

				// standard link contract templates

				"$all{$do}/$all/\n" +
				"($all{$do}$if/$true){$_from}/$is/{$from}\n" +

				"$get{$do}/$get/{$target}\n" +
				"($get{$do}$if/$true){$_from}/$is/{$from}\n" +

				"$push{$do}/$push/{$target}\n" +
				"$push{$do}/$is()/{($from)}\n";

		try {

			BOOTSTRAP_GRAPH = MemoryGraphFactory.getInstance().parseGraph(bootstrapGraphString, "XDI DISPLAY", null);
			ALL_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(ALL_LINK_CONTRACT_ADDRESS)));
			GET_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(GET_LINK_CONTRACT_ADDRESS)));
			PUSH_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(PUSH_LINK_CONTRACT_ADDRESS)));
		} catch (Xdi2ParseException | IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private XDIBootstrap() {

	}
}
