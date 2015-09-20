package xdi2.core.bootstrap;

import java.io.IOException;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;

public class XDIBootstrap {

	public static final XDIAddress ALL_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$all{$do}");
	public static final XDIAddress GET_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$get{$do}");
	public static final XDIAddress PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$push{$do}");
	public static final XDIAddress MSG_PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$msg$push{$do}");

	public static final Graph BOOTSTRAP_GRAPH;
	public static final LinkContractTemplate ALL_LINK_CONTRACT_TEMPLATE;
	public static final LinkContractTemplate GET_LINK_CONTRACT_TEMPLATE;
	public static final LinkContractTemplate PUSH_LINK_CONTRACT_TEMPLATE;
	public static final LinkContractTemplate MSG_PUSH_LINK_CONTRACT_TEMPLATE;

	static {

		String bootstrapGraphString = "" +

				// identity statements

				"($xdi)/$ref/\n" +
				"/$is$ref/($xdi)\n" +

				// public link contract on the bootstrap graph

				"($xdi/$public)$do/$get/\n" +

				// standard link contract templates

				"$all{$do}/$all/\n" +
				"($all{$do}$if$and/$true){$~from}/$is/{$from}\n" +
				"($all{$do}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$get{$do}/$get/{$get}\n" +
				"($get{$do}$if$and/$true){$~from}/$is/{$from}\n" +
				"($get{$do}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$push{$do}/$push/{$push}\n" +
				"$push{$do}/$is()/{($from)}\n" +
				"($push{$do}$if$and/$true){$~from}/$is/{$to}\n" +
				"($push{$do}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$msg$push{$do}/$push/{$push}\n" +
				"$msg$push{$do}/$is()/{($from)}\n" +
				"($msg$push{$do}$if$and/$true){$~from}/$is/{$to}\n" +
				"($msg$push{$do}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +
				"($msg$push{$do}$if$and/$true){$~msg}/$is$msg/{$msg}\n";

		try {

			BOOTSTRAP_GRAPH = MemoryGraphFactory.getInstance().parseGraph(bootstrapGraphString, "XDI DISPLAY", null);
			ALL_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(ALL_LINK_CONTRACT_TEMPLATE_ADDRESS)));
			GET_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(GET_LINK_CONTRACT_TEMPLATE_ADDRESS)));
			PUSH_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS)));
			MSG_PUSH_LINK_CONTRACT_TEMPLATE = LinkContractTemplate.fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(MSG_PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS)));
		} catch (Xdi2ParseException | IOException ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	private XDIBootstrap() {

	}
}
