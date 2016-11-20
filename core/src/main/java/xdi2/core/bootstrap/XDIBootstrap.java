package xdi2.core.bootstrap;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;

public class XDIBootstrap {

	public static final XDIAddress GET_MESSAGE_TEMPLATE_ADDRESS = XDIAddress.create("$get{$msg}");

	public static final XDIAddress ALL_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$all{$contract}");
	public static final XDIAddress GET_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$get{$contract}");
	public static final XDIAddress SET_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$set{$contract}");
	public static final XDIAddress PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$push{$contract}");
	public static final XDIAddress DEFER_PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$defer$push{$contract}");
	public static final XDIAddress MSG_DIGEST_LINK_CONTRACT_TEMPLATE_ADDRESS = XDIAddress.create("$msg$digest{$contract}");

	public static final Graph BOOTSTRAP_GRAPH;

	public static final XdiEntitySingleton.Variable GET_MESSAGE_TEMPLATE;

	public static final XdiEntitySingleton.Variable ALL_LINK_CONTRACT_TEMPLATE;
	public static final XdiEntitySingleton.Variable GET_LINK_CONTRACT_TEMPLATE;
	public static final XdiEntitySingleton.Variable SET_LINK_CONTRACT_TEMPLATE;
	public static final XdiEntitySingleton.Variable PUSH_LINK_CONTRACT_TEMPLATE;
	public static final XdiEntitySingleton.Variable DEFER_PUSH_LINK_CONTRACT_TEMPLATE;
	public static final XdiEntitySingleton.Variable MSG_DIGEST_LINK_CONTRACT_TEMPLATE;


	static {

		String bootstrapGraphString = "" +

				// identity statements

				"($xdi)/$ref/\n" +
				"/$is$ref/($xdi)\n" +

				// public link contract on the bootstrap graph

				"($xdi/$public)$contract$do/$get/\n" +

				// standard message templates

				"$get{$msg}$do/$get/{$get}\n" + 

				"(#register{$msg}$do/$set){(#name)}/$ref/{(#number)}\n" + 
				"(#register{$msg}$do/$set){(#number)}/$is$ref/{(#name)}\n" + 
				"(#register{$msg}$do/$set){#name}/$ref/{#number}\n" + 
				"(#register{$msg}$do/$set){(#number)}<$digest><$secret><$token>/{&}/{#password}\n" + 

				// standard link contract templates

				"$all{$contract}$do/$all/\n" +
				"($all{$contract}$if$and/$true){$~from}/$is/{$from}\n" +
				"($all{$contract}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$get{$contract}$do/$get/{$get}\n" +
				"($get{$contract}$if$and/$true){$~from}/$is/{$from}\n" +
				"($get{$contract}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$set{$contract}$do/$set/{$set}\n" +
				"($set{$contract}$if$and/$true){$~from}/$is/{$from}\n" +
				"($set{$contract}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$push{$contract}$do/$push/{$push}\n" +
				"$push{$contract}/$to/{($from)}\n" +
				"($push{$contract}$if$and/$true){$~from}/$is/{$to}\n" +
				"($push{$contract}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +

				"$defer$push{$contract}$do/$push/{$push}\n" +
				"$defer$push{$contract}/$to/{($from)}\n" +
				"($defer$push{$contract}$if$and/$true){$~from}/$is/{$to}\n" +
				"($defer$push{$contract}$if$and/$true){$~msg}<$sig><$valid>/&/true\n" +
				"($defer$push{$contract}$if$and/$true){$~msg}/$is$msg/{$msg}\n" + 

				"$msg$digest{$contract}$do/$all/\n" +
				"($msg$digest{$contract}$if/$true){$~msg}<$digest>/{&}/{<$digest>}\n";

		try {

			BOOTSTRAP_GRAPH = MemoryGraphFactory.getInstance().parseGraph(bootstrapGraphString, "XDI DISPLAY", null);

			GET_MESSAGE_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(GET_MESSAGE_TEMPLATE_ADDRESS));

			ALL_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(ALL_LINK_CONTRACT_TEMPLATE_ADDRESS));
			GET_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(GET_LINK_CONTRACT_TEMPLATE_ADDRESS));
			SET_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(SET_LINK_CONTRACT_TEMPLATE_ADDRESS));
			PUSH_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS));
			DEFER_PUSH_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(DEFER_PUSH_LINK_CONTRACT_TEMPLATE_ADDRESS));
			MSG_DIGEST_LINK_CONTRACT_TEMPLATE = XdiEntitySingleton.Variable.fromContextNode(BOOTSTRAP_GRAPH.getDeepContextNode(MSG_DIGEST_LINK_CONTRACT_TEMPLATE_ADDRESS));
		} catch (Exception ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}
	}

	private XDIBootstrap() {

	}
}
