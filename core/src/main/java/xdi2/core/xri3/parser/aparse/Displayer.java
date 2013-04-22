/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Mon Apr 22 13:14:58 CEST 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

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

  public Object visit(Rule_contextual_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_direct_contextual rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inverse_contextual rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_absolute rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_root_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_class_relative rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_absolute_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_root_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_class_inverse rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_relational_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_direct_relational rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inverse_relational rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_relative_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_class_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_class_path rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_instance_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal_path rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_path rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_outer_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_relative_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_peer_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_inner_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_statement_root rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subpath rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subsegment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_entity_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_authority_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_type_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_person_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_group_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_reserved_type rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unreserved_type rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_singleton rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_meta_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_reserved_meta_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unreserved_meta_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_concrete_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_entity_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_authority_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_type_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_instance_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_reserved_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unreserved_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_person_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_group_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_mutable_id_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_immutable_id_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_class rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_instance rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ordered_instance rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unordered_instance rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_mutable_id rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_immutable_id rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_definition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_authority_definition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_authority_path rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_type_definition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_entity_definition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attribute_definition rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_variable rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_value rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_string rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_number rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_boolean rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_array rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_json_object rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipv6_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_uuid_literal rule)
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

  public Object visit(Rule_time_high rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_clock_seq rule)
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

  public Object visit(Rule_iri_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_nonparen_delim rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_context_symbol rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_char rule)
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

  public Object visit(Rule_DQUOTE rule)
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
