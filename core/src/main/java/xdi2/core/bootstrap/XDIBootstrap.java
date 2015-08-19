package xdi2.core.bootstrap;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;

public class XDIBootstrap {

	public static XDIAddress ALL_LINK_CONTRACT_ADDRESS = XDIAddress.create("$all{$do}");
	public static XDIAddress GET_LINK_CONTRACT_ADDRESS = XDIAddress.create("$get{$do}");
	public static XDIAddress PUSH_LINK_CONTRACT_ADDRESS = XDIAddress.create("$push{$do}");

	private static Graph BOOTSTRAP_GRAPH;
	private static LinkContractTemplate ALL_LINK_CONTRACT_TEMPLATE;
	private static LinkContractTemplate GET_LINK_CONTRACT_TEMPLATE;
	private static LinkContractTemplate PUSH_LINK_CONTRACT_TEMPLATE;

	static {

		String bootstrapGraphString = "" +
				"($)/$ref/\n" +
				"/$is$ref/($)\n" +
				"$all{$do}/$all/\n" +
				"$get{$do}/$get/{$target}\n" +
				"$push{$do}/$push/{$target}\n";

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

	public Graph getBootstrapGraph() {

		return BOOTSTRAP_GRAPH;
	}

	public LinkContractTemplate getAllLinkContractTemplate() {

		return ALL_LINK_CONTRACT_TEMPLATE;
	}

	public LinkContractTemplate getGetLinkContractTemplate() {

		return GET_LINK_CONTRACT_TEMPLATE;
	}

	public LinkContractTemplate getPushLinkContractTemplate() {

		return PUSH_LINK_CONTRACT_TEMPLATE;
	}
}
