/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Wed Feb 20 10:37:39 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  private boolean terminal = true;

  public Object visit(Rule_xdi_context rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-context>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-context>");
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

  public Object visit(Rule_xdi_subject rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-subject>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-subject>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_predicate rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-predicate>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-predicate>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_object rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-object>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-object>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xdi_segment rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-segment>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-segment>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_subseg rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<subseg>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</subseg>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_global_subseg rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<global-subseg>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</global-subseg>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_local_subseg rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<local-subseg>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</local-subseg>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_gcs_char rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<gcs-char>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</gcs-char>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_lcs_char rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<lcs-char>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</lcs-char>");
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

  public Object visit(Rule_xref_empty rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-empty>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-empty>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref_IRI rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-IRI>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-IRI>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref_segment rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-segment>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-segment>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref_subject_predicate rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-subject-predicate>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-subject-predicate>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref_statement rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-statement>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-statement>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_xref_literal rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-literal>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-literal>");
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

  public Object visit(Rule_xdi_pchar rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xdi-pchar>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xdi-pchar>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_IRI rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<IRI>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</IRI>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_scheme rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<scheme>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</scheme>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ihier_part rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ihier-part>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ihier-part>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iauthority rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iauthority>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iauthority>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iuserinfo rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iuserinfo>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iuserinfo>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ihost rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ihost>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ihost>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_IP_literal rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<IP-literal>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</IP-literal>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_IPvFuture rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<IPvFuture>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</IPvFuture>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_IPv6address rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<IPv6address>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</IPv6address>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ls32 rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ls32>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ls32>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_h16 rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<h16>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</h16>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_IPv4address rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<IPv4address>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</IPv4address>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_dec_octet rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<dec-octet>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</dec-octet>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ireg_name rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ireg-name>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ireg-name>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_port rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<port>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</port>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipath_abempty rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipath-abempty>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipath-abempty>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipath_abs rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipath-abs>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipath-abs>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipath_rootless rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipath-rootless>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipath-rootless>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipath_empty rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipath-empty>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipath-empty>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_isegment rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<isegment>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</isegment>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_isegment_nz rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<isegment-nz>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</isegment-nz>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iquery rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iquery>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iquery>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_iprivate rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<iprivate>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</iprivate>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ifragment rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ifragment>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ifragment>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ipchar rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ipchar>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ipchar>");
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

  public Object visit(Rule_reserved rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<reserved>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</reserved>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_gen_delims rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<gen-delims>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</gen-delims>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_sub_delims rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<sub-delims>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</sub-delims>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unreserved rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unreserved>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unreserved>");
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

  public Object visit(Rule_BIT rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<BIT>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</BIT>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_CHAR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CHAR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CHAR>");
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

  public Object visit(Rule_CTL rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<CTL>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</CTL>");
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

  public Object visit(Rule_DQUOTE rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<DQUOTE>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</DQUOTE>");
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

  public Object visit(Rule_HTAB rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<HTAB>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</HTAB>");
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

  public Object visit(Rule_LWSP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<LWSP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</LWSP>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_OCTET rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<OCTET>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</OCTET>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_SP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<SP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</SP>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_VCHAR rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<VCHAR>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</VCHAR>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_WSP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<WSP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</WSP>");
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
