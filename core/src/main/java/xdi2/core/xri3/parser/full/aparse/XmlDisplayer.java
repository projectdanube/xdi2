/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Mar 14 16:34:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.full.aparse;

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  private boolean terminal = true;

  public Object visit(Rule_xdi_graph rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-graph>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-graph>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_statement rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-statement>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-statement>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_contextual rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<contextual>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</contextual>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_direct rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<direct>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</direct>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_inverse rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<inverse>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</inverse>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_absolute rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<absolute>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</absolute>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_peer_relative rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<peer-relative>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</peer-relative>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_context_relative rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<context-relative>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</context-relative>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_collection_relative rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<collection-relative>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</collection-relative>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_absolute_inverse rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<absolute-inverse>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</absolute-inverse>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_peer_inverse rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<peer-inverse>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</peer-inverse>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_context_inverse rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<context-inverse>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</context-inverse>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_collection_inverse rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<collection-inverse>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</collection-inverse>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_relational rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<relational>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</relational>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_direct_relation rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<direct-relation>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</direct-relation>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_inverse_relation rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<inverse-relation>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</inverse-relation>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_literal rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<literal>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</literal>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_literal_value rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<literal-value>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</literal-value>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_literal_ref rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<literal-ref>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</literal-ref>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_peer rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<peer>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</peer>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_peer_relative_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<peer-relative-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</peer-relative-context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_relative_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<relative-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</relative-context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_collection_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<collection-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</collection-context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_member_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<member-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</member-context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_literal_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<literal-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</literal-context>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_inner_root rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<inner-root>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</inner-root>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iri_literal rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iri-literal>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iri-literal>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_literal rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-literal>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-literal>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_subgraph rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<subgraph>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</subgraph>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_collection rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<collection>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</collection>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_member rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<member>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</member>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_entity_member rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<entity-member>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</entity-member>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attribute_member rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attribute-member>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attribute-member>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_order_ref rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<order-ref>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</order-ref>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_singleton rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<singleton>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</singleton>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attribute_singleton rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attribute-singleton>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attribute-singleton>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_entity_singleton rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<entity-singleton>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</entity-singleton>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_type rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<type>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</type>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_instance rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<instance>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</instance>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_specific rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<specific>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</specific>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_generic rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<generic>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</generic>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_person rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<person>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</person>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_organization rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<organization>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</organization>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_mutable rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<mutable>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</mutable>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_immutable rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<immutable>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</immutable>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_scheme rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-scheme>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-scheme>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_uuid rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<uuid>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</uuid>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_time_low rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<time-low>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</time-low>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_time_mid rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<time-mid>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</time-mid>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_time_high_and_version rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<time-high-and-version>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</time-high-and-version>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_clock_seq_and_reserved rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<clock-seq-and-reserved>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</clock-seq-and-reserved>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_clock_seq_low rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<clock-seq-low>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</clock-seq-low>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_node rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<node>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</node>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_hexoctet rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<hexoctet>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</hexoctet>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipv6 rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipv6>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipv6>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_data_xref rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<data-xref>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</data-xref>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_data_iri rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<data-iri>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</data-iri>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iri_scheme rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iri-scheme>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iri-scheme>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iri_char rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iri-char>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iri-char>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_nonparen_delim rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<nonparen-delim>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</nonparen-delim>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_char rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-char>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-char>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iunreserved rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iunreserved>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iunreserved>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_pct_encoded rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<pct-encoded>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</pct-encoded>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ucschar rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ucschar>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ucschar>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ALPHA rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ALPHA>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ALPHA>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_DIGIT rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<DIGIT>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</DIGIT>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_HEXDIG rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<HEXDIG>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</HEXDIG>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CRLF rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CRLF>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CRLF>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CR>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_LF rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<LF>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</LF>");
    terminal = false;
    return null;
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  private Boolean visitRules(ArrayList<Rule> rules)
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
