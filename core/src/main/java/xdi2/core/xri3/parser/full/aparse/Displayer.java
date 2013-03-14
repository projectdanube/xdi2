/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Mar 14 16:34:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.full.aparse;

import java.util.ArrayList;

public class Displayer implements Visitor
{

  public Object visit(Rule_xdi_graph rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_contextual rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_direct rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_absolute rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_peer_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_collection_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_absolute_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_peer_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_collection_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_relational rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_direct_relation rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inverse_relation rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_ref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_peer rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_peer_relative_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_relative_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_collection_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_member_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inner_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iri_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subgraph rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_collection rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_member rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_entity_member rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_member rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_order_ref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_entity_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_type rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_instance rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_specific rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_generic rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_person rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_organization rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_mutable rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_immutable rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_scheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_uuid rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_time_low rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_time_mid rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_time_high_and_version rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_clock_seq_and_reserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_clock_seq_low rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_node rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_hexoctet rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipv6 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_data_xref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_data_iri rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iri_scheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iri_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_nonparen_delim rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iunreserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_pct_encoded rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ucschar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ALPHA rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DIGIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_HEXDIG rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CRLF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_LF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  private Object visitRules(ArrayList<Rule> rules)
  {
    for (Rule rule : rules)
      rule.accept(this);
    return null;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
