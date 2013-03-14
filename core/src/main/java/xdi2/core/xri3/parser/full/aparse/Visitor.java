/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Mar 14 16:34:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.full.aparse;

public interface Visitor
{
  public Object visit(Rule_xdi_graph rule);
  public Object visit(Rule_xdi_statement rule);
  public Object visit(Rule_contextual rule);
  public Object visit(Rule_direct rule);
  public Object visit(Rule_inverse rule);
  public Object visit(Rule_absolute rule);
  public Object visit(Rule_peer_relative rule);
  public Object visit(Rule_context_relative rule);
  public Object visit(Rule_collection_relative rule);
  public Object visit(Rule_absolute_inverse rule);
  public Object visit(Rule_peer_inverse rule);
  public Object visit(Rule_context_inverse rule);
  public Object visit(Rule_collection_inverse rule);
  public Object visit(Rule_relational rule);
  public Object visit(Rule_direct_relation rule);
  public Object visit(Rule_inverse_relation rule);
  public Object visit(Rule_literal rule);
  public Object visit(Rule_literal_value rule);
  public Object visit(Rule_literal_ref rule);
  public Object visit(Rule_context rule);
  public Object visit(Rule_peer rule);
  public Object visit(Rule_peer_relative_context rule);
  public Object visit(Rule_relative_context rule);
  public Object visit(Rule_collection_context rule);
  public Object visit(Rule_member_context rule);
  public Object visit(Rule_literal_context rule);
  public Object visit(Rule_xref rule);
  public Object visit(Rule_inner_root rule);
  public Object visit(Rule_iri_literal rule);
  public Object visit(Rule_xdi_literal rule);
  public Object visit(Rule_subgraph rule);
  public Object visit(Rule_collection rule);
  public Object visit(Rule_member rule);
  public Object visit(Rule_entity_member rule);
  public Object visit(Rule_attribute_member rule);
  public Object visit(Rule_order_ref rule);
  public Object visit(Rule_singleton rule);
  public Object visit(Rule_attribute_singleton rule);
  public Object visit(Rule_entity_singleton rule);
  public Object visit(Rule_type rule);
  public Object visit(Rule_instance rule);
  public Object visit(Rule_specific rule);
  public Object visit(Rule_generic rule);
  public Object visit(Rule_person rule);
  public Object visit(Rule_organization rule);
  public Object visit(Rule_mutable rule);
  public Object visit(Rule_immutable rule);
  public Object visit(Rule_xdi_scheme rule);
  public Object visit(Rule_uuid rule);
  public Object visit(Rule_time_low rule);
  public Object visit(Rule_time_mid rule);
  public Object visit(Rule_time_high_and_version rule);
  public Object visit(Rule_clock_seq_and_reserved rule);
  public Object visit(Rule_clock_seq_low rule);
  public Object visit(Rule_node rule);
  public Object visit(Rule_hexoctet rule);
  public Object visit(Rule_ipv6 rule);
  public Object visit(Rule_data_xref rule);
  public Object visit(Rule_data_iri rule);
  public Object visit(Rule_iri_scheme rule);
  public Object visit(Rule_iri_char rule);
  public Object visit(Rule_nonparen_delim rule);
  public Object visit(Rule_xdi_char rule);
  public Object visit(Rule_iunreserved rule);
  public Object visit(Rule_pct_encoded rule);
  public Object visit(Rule_ucschar rule);
  public Object visit(Rule_ALPHA rule);
  public Object visit(Rule_DIGIT rule);
  public Object visit(Rule_HEXDIG rule);
  public Object visit(Rule_CRLF rule);
  public Object visit(Rule_CR rule);
  public Object visit(Rule_LF rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
