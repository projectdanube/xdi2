/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Mon Apr 22 13:14:58 CEST 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

public interface Visitor
{
  public Object visit(Rule_xdi_graph rule);
  public Object visit(Rule_xdi_statement rule);
  public Object visit(Rule_contextual_statement rule);
  public Object visit(Rule_direct_contextual rule);
  public Object visit(Rule_inverse_contextual rule);
  public Object visit(Rule_absolute rule);
  public Object visit(Rule_root_relative rule);
  public Object visit(Rule_context_relative rule);
  public Object visit(Rule_class_relative rule);
  public Object visit(Rule_absolute_inverse rule);
  public Object visit(Rule_root_inverse rule);
  public Object visit(Rule_context_inverse rule);
  public Object visit(Rule_class_inverse rule);
  public Object visit(Rule_relational_statement rule);
  public Object visit(Rule_direct_relational rule);
  public Object visit(Rule_inverse_relational rule);
  public Object visit(Rule_literal_statement rule);
  public Object visit(Rule_context rule);
  public Object visit(Rule_relative_context rule);
  public Object visit(Rule_class_context rule);
  public Object visit(Rule_class_path rule);
  public Object visit(Rule_instance_context rule);
  public Object visit(Rule_literal_context rule);
  public Object visit(Rule_literal_path rule);
  public Object visit(Rule_attribute_path rule);
  public Object visit(Rule_root rule);
  public Object visit(Rule_outer_root rule);
  public Object visit(Rule_relative_root rule);
  public Object visit(Rule_peer_root rule);
  public Object visit(Rule_inner_root rule);
  public Object visit(Rule_statement_root rule);
  public Object visit(Rule_xref rule);
  public Object visit(Rule_subpath rule);
  public Object visit(Rule_subsegment rule);
  public Object visit(Rule_singleton rule);
  public Object visit(Rule_entity_singleton rule);
  public Object visit(Rule_authority_singleton rule);
  public Object visit(Rule_type_singleton rule);
  public Object visit(Rule_person_singleton rule);
  public Object visit(Rule_group_singleton rule);
  public Object visit(Rule_reserved_type rule);
  public Object visit(Rule_unreserved_type rule);
  public Object visit(Rule_attribute_singleton rule);
  public Object visit(Rule_class rule);
  public Object visit(Rule_meta_class rule);
  public Object visit(Rule_reserved_meta_class rule);
  public Object visit(Rule_unreserved_meta_class rule);
  public Object visit(Rule_concrete_class rule);
  public Object visit(Rule_entity_class rule);
  public Object visit(Rule_authority_class rule);
  public Object visit(Rule_type_class rule);
  public Object visit(Rule_instance_class rule);
  public Object visit(Rule_reserved_class rule);
  public Object visit(Rule_unreserved_class rule);
  public Object visit(Rule_person_class rule);
  public Object visit(Rule_group_class rule);
  public Object visit(Rule_mutable_id_class rule);
  public Object visit(Rule_immutable_id_class rule);
  public Object visit(Rule_attribute_class rule);
  public Object visit(Rule_instance rule);
  public Object visit(Rule_ordered_instance rule);
  public Object visit(Rule_unordered_instance rule);
  public Object visit(Rule_mutable_id rule);
  public Object visit(Rule_immutable_id rule);
  public Object visit(Rule_definition rule);
  public Object visit(Rule_authority_definition rule);
  public Object visit(Rule_authority_path rule);
  public Object visit(Rule_type_definition rule);
  public Object visit(Rule_entity_definition rule);
  public Object visit(Rule_attribute_definition rule);
  public Object visit(Rule_variable rule);
  public Object visit(Rule_json_value rule);
  public Object visit(Rule_json_string rule);
  public Object visit(Rule_json_number rule);
  public Object visit(Rule_json_boolean rule);
  public Object visit(Rule_json_array rule);
  public Object visit(Rule_json_object rule);
  public Object visit(Rule_ipv6_literal rule);
  public Object visit(Rule_uuid_literal rule);
  public Object visit(Rule_time_low rule);
  public Object visit(Rule_time_mid rule);
  public Object visit(Rule_time_high rule);
  public Object visit(Rule_clock_seq rule);
  public Object visit(Rule_clock_seq_low rule);
  public Object visit(Rule_node rule);
  public Object visit(Rule_iri_char rule);
  public Object visit(Rule_nonparen_delim rule);
  public Object visit(Rule_context_symbol rule);
  public Object visit(Rule_xdi_char rule);
  public Object visit(Rule_ALPHA rule);
  public Object visit(Rule_DIGIT rule);
  public Object visit(Rule_HEXDIG rule);
  public Object visit(Rule_CRLF rule);
  public Object visit(Rule_DQUOTE rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
