/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Fri Oct 19 08:29:48 CEST 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser;

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  private boolean terminal = true;

  public Object visit(Rule$xri rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_reference rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-reference>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-reference>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$relative_xri_ref rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<relative-xri-ref>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</relative-xri-ref>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$relative_xri_part rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<relative-xri-part>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</relative-xri-part>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_hier_part rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-hier-part>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-hier-part>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_authority rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-authority>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-authority>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$subseg rule)
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

  public Object visit(Rule$global_subseg rule)
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

  public Object visit(Rule$local_subseg rule)
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

  public Object visit(Rule$gcs_char rule)
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

  public Object visit(Rule$lcs_char rule)
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

  public Object visit(Rule$literal rule)
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

  public Object visit(Rule$literal_nc rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<literal-nc>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</literal-nc>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xref rule)
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

  public Object visit(Rule$xref_empty rule)
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

  public Object visit(Rule$xref_xri_reference rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xref-xri-reference>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xref-xri-reference>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xref_IRI rule)
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

  public Object visit(Rule$xri_path rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-path>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-path>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_path_abempty rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-path-abempty>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-path-abempty>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_path_abs rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-path-abs>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-path-abs>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_path_noscheme rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-path-noscheme>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-path-noscheme>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_segment rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-segment>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-segment>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_segment_nz rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-segment-nz>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-segment-nz>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_segment_nc rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-segment-nc>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-segment-nc>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_pchar rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-pchar>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-pchar>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_pchar_nc rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-pchar-nc>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-pchar-nc>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_reserved rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-reserved>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-reserved>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_gen_delims rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-gen-delims>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-gen-delims>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$xri_sub_delims rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<xri-sub-delims>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</xri-sub-delims>");
    terminal = false;
    return null;
  }

  public Object visit(Rule$IRI rule)
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

  public Object visit(Rule$scheme rule)
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

  public Object visit(Rule$ihier_part rule)
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

  public Object visit(Rule$iauthority rule)
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

  public Object visit(Rule$iuserinfo rule)
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

  public Object visit(Rule$ihost rule)
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

  public Object visit(Rule$IP_literal rule)
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

  public Object visit(Rule$IPvFuture rule)
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

  public Object visit(Rule$IPv6address rule)
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

  public Object visit(Rule$ls32 rule)
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

  public Object visit(Rule$h16 rule)
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

  public Object visit(Rule$IPv4address rule)
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

  public Object visit(Rule$dec_octet rule)
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

  public Object visit(Rule$ireg_name rule)
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

  public Object visit(Rule$port rule)
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

  public Object visit(Rule$ipath_abempty rule)
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

  public Object visit(Rule$ipath_abs rule)
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

  public Object visit(Rule$ipath_rootless rule)
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

  public Object visit(Rule$ipath_empty rule)
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

  public Object visit(Rule$isegment rule)
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

  public Object visit(Rule$isegment_nz rule)
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

  public Object visit(Rule$iquery rule)
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

  public Object visit(Rule$iprivate rule)
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

  public Object visit(Rule$ifragment rule)
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

  public Object visit(Rule$ipchar rule)
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

  public Object visit(Rule$iunreserved rule)
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

  public Object visit(Rule$pct_encoded rule)
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

  public Object visit(Rule$ucschar rule)
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

  public Object visit(Rule$reserved rule)
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

  public Object visit(Rule$gen_delims rule)
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

  public Object visit(Rule$sub_delims rule)
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

  public Object visit(Rule$unreserved rule)
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

  public Object visit(Rule$ALPHA rule)
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

  public Object visit(Rule$BIT rule)
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

  public Object visit(Rule$CHAR rule)
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

  public Object visit(Rule$CR rule)
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

  public Object visit(Rule$CRLF rule)
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

  public Object visit(Rule$CTL rule)
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

  public Object visit(Rule$DIGIT rule)
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

  public Object visit(Rule$DQUOTE rule)
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

  public Object visit(Rule$HEXDIG rule)
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

  public Object visit(Rule$HTAB rule)
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

  public Object visit(Rule$LF rule)
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

  public Object visit(Rule$LWSP rule)
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

  public Object visit(Rule$OCTET rule)
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

  public Object visit(Rule$SP rule)
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

  public Object visit(Rule$VCHAR rule)
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

  public Object visit(Rule$WSP rule)
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

  public Object visit(Terminal$StringValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  public Object visit(Terminal$NumericValue value)
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
