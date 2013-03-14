package xdi2.core.xri3.parser.full.aparse;

public abstract class AbstractVisitor implements Visitor {

	public abstract Object visitRule(Rule rule);

	@Override
	public Object visit(Rule_xdi_graph rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xdi_statement rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_contextual rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_direct rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_absolute rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_peer_relative rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_context_relative rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_collection_relative rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_absolute_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_peer_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_context_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_collection_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_relational rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_direct_relation rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inverse_relation rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_value rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_ref rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_peer rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_peer_relative_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_relative_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_collection_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_member_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xref rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inner_root rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_iri_literal rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xdi_literal rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_subgraph rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_collection rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_member rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_entity_member rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_attribute_member rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_order_ref rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_attribute_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_entity_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_type rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_instance rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_specific rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_generic rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_person rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_organization rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_immutable rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_mutable rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_data_xref rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_data_iri rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_iri_scheme rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_iri_char rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_nonparen_delim rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xdi_char rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_iunreserved rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_pct_encoded rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_ucschar rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_ALPHA rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_DIGIT rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_HEXDIG rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xdi_scheme rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_uuid rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_time_low rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_time_mid rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_time_high_and_version rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_clock_seq_and_reserved rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_clock_seq_low rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_node rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_hexoctet rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_ipv6 rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_CRLF rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_CR rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_LF rule) {

		return visitRule(rule);
	}
}
