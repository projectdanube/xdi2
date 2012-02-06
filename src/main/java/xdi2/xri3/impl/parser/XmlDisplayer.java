package xdi2.xri3.impl.parser;

/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 0.5
 * Produced : Sat Dec 20 01:35:48 CET 2008
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  public void visit(Rule rule)
  {
    rule.visit(this);
  }

  public Object visit_xri(Parser.xri rule)
  {
    System.out.println("<xri>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri>");

    return Boolean.FALSE;
  }

  public Object visit_xri_reference(Parser.xri_reference rule)
  {
    System.out.println("<xri-reference>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-reference>");

    return Boolean.FALSE;
  }

  public Object visit_relative_xri_ref(Parser.relative_xri_ref rule)
  {
    System.out.println("<relative-xri-ref>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</relative-xri-ref>");

    return Boolean.FALSE;
  }

  public Object visit_relative_xri_part(Parser.relative_xri_part rule)
  {
    System.out.println("<relative-xri-part>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</relative-xri-part>");

    return Boolean.FALSE;
  }

  public Object visit_xri_hier_part(Parser.xri_hier_part rule)
  {
    System.out.println("<xri-hier-part>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-hier-part>");

    return Boolean.FALSE;
  }

  public Object visit_xri_authority(Parser.xri_authority rule)
  {
    System.out.println("<xri-authority>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-authority>");

    return Boolean.FALSE;
  }

  public Object visit_subseg(Parser.subseg rule)
  {
    System.out.println("<subseg>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</subseg>");

    return Boolean.FALSE;
  }

  public Object visit_global_subseg(Parser.global_subseg rule)
  {
    System.out.println("<global-subseg>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</global-subseg>");

    return Boolean.FALSE;
  }

  public Object visit_local_subseg(Parser.local_subseg rule)
  {
    System.out.println("<local-subseg>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</local-subseg>");

    return Boolean.FALSE;
  }

  public Object visit_gcs_char(Parser.gcs_char rule)
  {
    System.out.println("<gcs-char>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</gcs-char>");

    return Boolean.FALSE;
  }

  public Object visit_lcs_char(Parser.lcs_char rule)
  {
    System.out.println("<lcs-char>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</lcs-char>");

    return Boolean.FALSE;
  }

  public Object visit_literal(Parser.literal rule)
  {
    System.out.println("<literal>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</literal>");

    return Boolean.FALSE;
  }

  public Object visit_literal_nc(Parser.literal_nc rule)
  {
    System.out.println("<literal-nc>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</literal-nc>");

    return Boolean.FALSE;
  }

  public Object visit_xref(Parser.xref rule)
  {
    System.out.println("<xref>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xref>");

    return Boolean.FALSE;
  }

  public Object visit_xref_empty(Parser.xref_empty rule)
  {
    System.out.println("<xref-empty>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xref-empty>");

    return Boolean.FALSE;
  }

  public Object visit_xref_xri_reference(Parser.xref_xri_reference rule)
  {
    System.out.println("<xref-xri-reference>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xref-xri-reference>");

    return Boolean.FALSE;
  }

  public Object visit_xref_IRI(Parser.xref_IRI rule)
  {
    System.out.println("<xref-IRI>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xref-IRI>");

    return Boolean.FALSE;
  }

  public Object visit_xri_path(Parser.xri_path rule)
  {
    System.out.println("<xri-path>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-path>");

    return Boolean.FALSE;
  }

  public Object visit_xri_path_abempty(Parser.xri_path_abempty rule)
  {
    System.out.println("<xri-path-abempty>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-path-abempty>");

    return Boolean.FALSE;
  }

  public Object visit_xri_path_abs(Parser.xri_path_abs rule)
  {
    System.out.println("<xri-path-abs>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-path-abs>");

    return Boolean.FALSE;
  }

  public Object visit_xri_path_noscheme(Parser.xri_path_noscheme rule)
  {
    System.out.println("<xri-path-noscheme>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-path-noscheme>");

    return Boolean.FALSE;
  }

  public Object visit_xri_segment(Parser.xri_segment rule)
  {
    System.out.println("<xri-segment>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-segment>");

    return Boolean.FALSE;
  }

  public Object visit_xri_segment_nz(Parser.xri_segment_nz rule)
  {
    System.out.println("<xri-segment-nz>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-segment-nz>");

    return Boolean.FALSE;
  }

  public Object visit_xri_segment_nc(Parser.xri_segment_nc rule)
  {
    System.out.println("<xri-segment-nc>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-segment-nc>");

    return Boolean.FALSE;
  }

  public Object visit_xri_pchar(Parser.xri_pchar rule)
  {
    System.out.println("<xri-pchar>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-pchar>");

    return Boolean.FALSE;
  }

  public Object visit_xri_pchar_nc(Parser.xri_pchar_nc rule)
  {
    System.out.println("<xri-pchar-nc>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-pchar-nc>");

    return Boolean.FALSE;
  }

  public Object visit_xri_reserved(Parser.xri_reserved rule)
  {
    System.out.println("<xri-reserved>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-reserved>");

    return Boolean.FALSE;
  }

  public Object visit_xri_gen_delims(Parser.xri_gen_delims rule)
  {
    System.out.println("<xri-gen-delims>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-gen-delims>");

    return Boolean.FALSE;
  }

  public Object visit_xri_sub_delims(Parser.xri_sub_delims rule)
  {
    System.out.println("<xri-sub-delims>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</xri-sub-delims>");

    return Boolean.FALSE;
  }

  public Object visit_IRI(Parser.IRI rule)
  {
    System.out.println("<IRI>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</IRI>");

    return Boolean.FALSE;
  }

  public Object visit_scheme(Parser.scheme rule)
  {
    System.out.println("<scheme>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</scheme>");

    return Boolean.FALSE;
  }

  public Object visit_ihier_part(Parser.ihier_part rule)
  {
    System.out.println("<ihier-part>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ihier-part>");

    return Boolean.FALSE;
  }

  public Object visit_iauthority(Parser.iauthority rule)
  {
    System.out.println("<iauthority>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</iauthority>");

    return Boolean.FALSE;
  }

  public Object visit_iuserinfo(Parser.iuserinfo rule)
  {
    System.out.println("<iuserinfo>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</iuserinfo>");

    return Boolean.FALSE;
  }

  public Object visit_ihost(Parser.ihost rule)
  {
    System.out.println("<ihost>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ihost>");

    return Boolean.FALSE;
  }

  public Object visit_IP_literal(Parser.IP_literal rule)
  {
    System.out.println("<IP-literal>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</IP-literal>");

    return Boolean.FALSE;
  }

  public Object visit_IPvFuture(Parser.IPvFuture rule)
  {
    System.out.println("<IPvFuture>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</IPvFuture>");

    return Boolean.FALSE;
  }

  public Object visit_IPv6address(Parser.IPv6address rule)
  {
    System.out.println("<IPv6address>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</IPv6address>");

    return Boolean.FALSE;
  }

  public Object visit_ls32(Parser.ls32 rule)
  {
    System.out.println("<ls32>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ls32>");

    return Boolean.FALSE;
  }

  public Object visit_h16(Parser.h16 rule)
  {
    System.out.println("<h16>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</h16>");

    return Boolean.FALSE;
  }

  public Object visit_IPv4address(Parser.IPv4address rule)
  {
    System.out.println("<IPv4address>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</IPv4address>");

    return Boolean.FALSE;
  }

  public Object visit_dec_octet(Parser.dec_octet rule)
  {
    System.out.println("<dec-octet>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</dec-octet>");

    return Boolean.FALSE;
  }

  public Object visit_ireg_name(Parser.ireg_name rule)
  {
    System.out.println("<ireg-name>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ireg-name>");

    return Boolean.FALSE;
  }

  public Object visit_port(Parser.port rule)
  {
    System.out.println("<port>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</port>");

    return Boolean.FALSE;
  }

  public Object visit_ipath_abempty(Parser.ipath_abempty rule)
  {
    System.out.println("<ipath-abempty>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ipath-abempty>");

    return Boolean.FALSE;
  }

  public Object visit_ipath_abs(Parser.ipath_abs rule)
  {
    System.out.println("<ipath-abs>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ipath-abs>");

    return Boolean.FALSE;
  }

  public Object visit_ipath_rootless(Parser.ipath_rootless rule)
  {
    System.out.println("<ipath-rootless>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ipath-rootless>");

    return Boolean.FALSE;
  }

  public Object visit_ipath_empty(Parser.ipath_empty rule)
  {
    System.out.println("<ipath-empty>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ipath-empty>");

    return Boolean.FALSE;
  }

  public Object visit_isegment(Parser.isegment rule)
  {
    System.out.println("<isegment>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</isegment>");

    return Boolean.FALSE;
  }

  public Object visit_isegment_nz(Parser.isegment_nz rule)
  {
    System.out.println("<isegment-nz>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</isegment-nz>");

    return Boolean.FALSE;
  }

  public Object visit_iquery(Parser.iquery rule)
  {
    System.out.println("<iquery>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</iquery>");

    return Boolean.FALSE;
  }

  public Object visit_iprivate(Parser.iprivate rule)
  {
    System.out.println("<iprivate>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</iprivate>");

    return Boolean.FALSE;
  }

  public Object visit_ifragment(Parser.ifragment rule)
  {
    System.out.println("<ifragment>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ifragment>");

    return Boolean.FALSE;
  }

  public Object visit_ipchar(Parser.ipchar rule)
  {
    System.out.println("<ipchar>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ipchar>");

    return Boolean.FALSE;
  }

  public Object visit_iunreserved(Parser.iunreserved rule)
  {
    System.out.println("<iunreserved>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</iunreserved>");

    return Boolean.FALSE;
  }

  public Object visit_pct_encoded(Parser.pct_encoded rule)
  {
    System.out.println("<pct-encoded>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</pct-encoded>");

    return Boolean.FALSE;
  }

  public Object visit_ucschar(Parser.ucschar rule)
  {
    System.out.println("<ucschar>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ucschar>");

    return Boolean.FALSE;
  }

  public Object visit_reserved(Parser.reserved rule)
  {
    System.out.println("<reserved>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</reserved>");

    return Boolean.FALSE;
  }

  public Object visit_gen_delims(Parser.gen_delims rule)
  {
    System.out.println("<gen-delims>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</gen-delims>");

    return Boolean.FALSE;
  }

  public Object visit_sub_delims(Parser.sub_delims rule)
  {
    System.out.println("<sub-delims>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</sub-delims>");

    return Boolean.FALSE;
  }

  public Object visit_unreserved(Parser.unreserved rule)
  {
    System.out.println("<unreserved>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</unreserved>");

    return Boolean.FALSE;
  }

  public Object visit_ALPHA(Parser.ALPHA rule)
  {
    System.out.println("<ALPHA>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</ALPHA>");

    return Boolean.FALSE;
  }

  public Object visit_BIT(Parser.BIT rule)
  {
    System.out.println("<BIT>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</BIT>");

    return Boolean.FALSE;
  }

  public Object visit_CHAR(Parser.CHAR rule)
  {
    System.out.println("<CHAR>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</CHAR>");

    return Boolean.FALSE;
  }

  public Object visit_CR(Parser.CR rule)
  {
    System.out.println("<CR>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</CR>");

    return Boolean.FALSE;
  }

  public Object visit_CRLF(Parser.CRLF rule)
  {
    System.out.println("<CRLF>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</CRLF>");

    return Boolean.FALSE;
  }

  public Object visit_CTL(Parser.CTL rule)
  {
    System.out.println("<CTL>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</CTL>");

    return Boolean.FALSE;
  }

  public Object visit_DIGIT(Parser.DIGIT rule)
  {
    System.out.println("<DIGIT>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</DIGIT>");

    return Boolean.FALSE;
  }

  public Object visit_DQUOTE(Parser.DQUOTE rule)
  {
    System.out.println("<DQUOTE>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</DQUOTE>");

    return Boolean.FALSE;
  }

  public Object visit_HEXDIG(Parser.HEXDIG rule)
  {
    System.out.println("<HEXDIG>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</HEXDIG>");

    return Boolean.FALSE;
  }

  public Object visit_HTAB(Parser.HTAB rule)
  {
    System.out.println("<HTAB>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</HTAB>");

    return Boolean.FALSE;
  }

  public Object visit_LF(Parser.LF rule)
  {
    System.out.println("<LF>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</LF>");

    return Boolean.FALSE;
  }

  public Object visit_LWSP(Parser.LWSP rule)
  {
    System.out.println("<LWSP>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</LWSP>");

    return Boolean.FALSE;
  }

  public Object visit_OCTET(Parser.OCTET rule)
  {
    System.out.println("<OCTET>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</OCTET>");

    return Boolean.FALSE;
  }

  public Object visit_SP(Parser.SP rule)
  {
    System.out.println("<SP>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</SP>");

    return Boolean.FALSE;
  }

  public Object visit_VCHAR(Parser.VCHAR rule)
  {
    System.out.println("<VCHAR>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</VCHAR>");

    return Boolean.FALSE;
  }

  public Object visit_WSP(Parser.WSP rule)
  {
    System.out.println("<WSP>");
    if (visitRules(rule.rules).booleanValue()) System.out.println("");
    System.out.println("</WSP>");

    return Boolean.FALSE;
  }

  public Object visit_StringValue(Parser.StringValue value)
  {
    System.out.print(value.spelling);
    return Boolean.TRUE;
  }

  public Object visit_NumericValue(Parser.NumericValue value)
  {
    System.out.print(value.spelling);
    return Boolean.TRUE;
  }

  private Boolean visitRules(ArrayList<Rule> rules)
  {
    Boolean terminal = Boolean.FALSE;
    for (Rule rule : rules)
      terminal = (Boolean)rule.visit(this);
    return terminal;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
