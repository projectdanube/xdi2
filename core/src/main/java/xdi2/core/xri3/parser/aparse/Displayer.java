/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Wed Feb 20 10:37:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

import java.util.ArrayList;

public class Displayer implements Visitor
{

  public Object visit(Rule_xdi_context rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_subject rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_predicate rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_object rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_segment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_global_subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_local_subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_gcs_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_lcs_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_empty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_IRI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_segment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_subject_predicate rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_statement rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xref_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_xdi_pchar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IRI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_scheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ihier_part rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iauthority rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iuserinfo rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ihost rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IP_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPvFuture rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPv6address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ls32 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_h16 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPv4address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_dec_octet rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ireg_name rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_port rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipath_abempty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipath_abs rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipath_rootless rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipath_empty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_isegment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_isegment_nz rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iquery rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_iprivate rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ifragment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ipchar rule)
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

  public Object visit(Rule_reserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_gen_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_sub_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unreserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ALPHA rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_BIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CRLF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_CTL rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DIGIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DQUOTE rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_HEXDIG rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_HTAB rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_LF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_LWSP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_OCTET rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_SP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_VCHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_WSP rule)
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
