package xdi2.core.xri3.parser.aparse;

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
	public Object visit(Rule_contextual_statement rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_direct_contextual rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inverse_contextual rule) {

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
	public Object visit(Rule_class_relative rule) {

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
	public Object visit(Rule_class_inverse rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_relational_statement rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_direct_relational rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inverse_relational rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inner_relational rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inner_statement rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_statement rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_relative_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_class_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_class_path rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_instance_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_literal_path rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_attribute_pair rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_value_context rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_root rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_local_root rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_peer_root rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_inner_root rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xref rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_subpath rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_subsegment rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_entity_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_attribute_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_person_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_organization_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_relative_singleton rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_class rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_entity_class rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_type_class rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_instance_class rule) {

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
	public Object visit(Rule_attribute_class rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_instance rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_element rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_value rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_string rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_number rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_boolean rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_array rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_json_object rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_ipv6_literal rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_uuid_literal rule) {

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
	public Object visit(Rule_time_high rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_clock_seq rule) {

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
	public Object visit(Rule_xdi_chars rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_iri_chars rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_nonparen_delim rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_context_symbol rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_xdi_char rule) {

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
	public Object visit(Rule_CRLF rule) {

		return visitRule(rule);
	}

	@Override
	public Object visit(Rule_DQUOTE rule) {

		return visitRule(rule);
	}
}
