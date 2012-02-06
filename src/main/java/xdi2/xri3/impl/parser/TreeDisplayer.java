package xdi2.xri3.impl.parser;

import java.io.PrintStream;

/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 0.5
 * Produced : Sat Dec 20 01:35:48 CET 2008
 *
 * -----------------------------------------------------------------------------
 */



public class TreeDisplayer implements Visitor
{
  private int indent;
  private PrintStream stream;
  public TreeDisplayer(PrintStream stream)
  {
    this.indent = 0;
    this.stream = stream;
  }
  public void visit(Rule rule)
  {
    rule.visit(this);
  }

  public Object visit_xri(Parser.xri rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_reference(Parser.xri_reference rule)
  {
    return visitRules(rule);
  }

  public Object visit_relative_xri_ref(Parser.relative_xri_ref rule)
  {
    return visitRules(rule);
  }

  public Object visit_relative_xri_part(Parser.relative_xri_part rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_hier_part(Parser.xri_hier_part rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_authority(Parser.xri_authority rule)
  {
    return visitRules(rule);
  }

  public Object visit_subseg(Parser.subseg rule)
  {
    return visitRules(rule);
  }

  public Object visit_global_subseg(Parser.global_subseg rule)
  {
    return visitRules(rule);
  }

  public Object visit_local_subseg(Parser.local_subseg rule)
  {
    return visitRules(rule);
  }

  public Object visit_gcs_char(Parser.gcs_char rule)
  {
    return visitRules(rule);
  }

  public Object visit_lcs_char(Parser.lcs_char rule)
  {
    return visitRules(rule);
  }

  public Object visit_literal(Parser.literal rule)
  {
    return visitRules(rule);
  }

  public Object visit_literal_nc(Parser.literal_nc rule)
  {
    return visitRules(rule);
  }

  public Object visit_xref(Parser.xref rule)
  {
    return visitRules(rule);
  }

  public Object visit_xref_empty(Parser.xref_empty rule)
  {
    return visitRules(rule);
  }

  public Object visit_xref_xri_reference(Parser.xref_xri_reference rule)
  {
    return visitRules(rule);
  }

  public Object visit_xref_IRI(Parser.xref_IRI rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_path(Parser.xri_path rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_path_abempty(Parser.xri_path_abempty rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_path_abs(Parser.xri_path_abs rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_path_noscheme(Parser.xri_path_noscheme rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_segment(Parser.xri_segment rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_segment_nz(Parser.xri_segment_nz rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_segment_nc(Parser.xri_segment_nc rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_pchar(Parser.xri_pchar rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_pchar_nc(Parser.xri_pchar_nc rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_reserved(Parser.xri_reserved rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_gen_delims(Parser.xri_gen_delims rule)
  {
    return visitRules(rule);
  }

  public Object visit_xri_sub_delims(Parser.xri_sub_delims rule)
  {
    return visitRules(rule);
  }

  public Object visit_IRI(Parser.IRI rule)
  {
    return visitRules(rule);
  }

  public Object visit_scheme(Parser.scheme rule)
  {
    return visitRules(rule);
  }

  public Object visit_ihier_part(Parser.ihier_part rule)
  {
    return visitRules(rule);
  }

  public Object visit_iauthority(Parser.iauthority rule)
  {
    return visitRules(rule);
  }

  public Object visit_iuserinfo(Parser.iuserinfo rule)
  {
    return visitRules(rule);
  }

  public Object visit_ihost(Parser.ihost rule)
  {
    return visitRules(rule);
  }

  public Object visit_IP_literal(Parser.IP_literal rule)
  {
    return visitRules(rule);
  }

  public Object visit_IPvFuture(Parser.IPvFuture rule)
  {
    return visitRules(rule);
  }

  public Object visit_IPv6address(Parser.IPv6address rule)
  {
    return visitRules(rule);
  }

  public Object visit_ls32(Parser.ls32 rule)
  {
    return visitRules(rule);
  }

  public Object visit_h16(Parser.h16 rule)
  {
    return visitRules(rule);
  }

  public Object visit_IPv4address(Parser.IPv4address rule)
  {
    return visitRules(rule);
  }

  public Object visit_dec_octet(Parser.dec_octet rule)
  {
    return visitRules(rule);
  }

  public Object visit_ireg_name(Parser.ireg_name rule)
  {
    return visitRules(rule);
  }

  public Object visit_port(Parser.port rule)
  {
    return visitRules(rule);
  }

  public Object visit_ipath_abempty(Parser.ipath_abempty rule)
  {
    return visitRules(rule);
  }

  public Object visit_ipath_abs(Parser.ipath_abs rule)
  {
    return visitRules(rule);
  }

  public Object visit_ipath_rootless(Parser.ipath_rootless rule)
  {
    return visitRules(rule);
  }

  public Object visit_ipath_empty(Parser.ipath_empty rule)
  {
    return visitRules(rule);
  }

  public Object visit_isegment(Parser.isegment rule)
  {
    return visitRules(rule);
  }

  public Object visit_isegment_nz(Parser.isegment_nz rule)
  {
    return visitRules(rule);
  }

  public Object visit_iquery(Parser.iquery rule)
  {
    return visitRules(rule);
  }

  public Object visit_iprivate(Parser.iprivate rule)
  {
    return visitRules(rule);
  }

  public Object visit_ifragment(Parser.ifragment rule)
  {
    return visitRules(rule);
  }

  public Object visit_ipchar(Parser.ipchar rule)
  {
    return visitRules(rule);
  }

  public Object visit_iunreserved(Parser.iunreserved rule)
  {
    return visitRules(rule);
  }

  public Object visit_pct_encoded(Parser.pct_encoded rule)
  {
    return visitRules(rule);
  }

  public Object visit_ucschar(Parser.ucschar rule)
  {
    return visitRules(rule);
  }

  public Object visit_reserved(Parser.reserved rule)
  {
    return visitRules(rule);
  }

  public Object visit_gen_delims(Parser.gen_delims rule)
  {
    return visitRules(rule);
  }

  public Object visit_sub_delims(Parser.sub_delims rule)
  {
    return visitRules(rule);
  }

  public Object visit_unreserved(Parser.unreserved rule)
  {
    return visitRules(rule);
  }

  public Object visit_ALPHA(Parser.ALPHA rule)
  {
    return visitRules(rule);
  }

  public Object visit_BIT(Parser.BIT rule)
  {
    return visitRules(rule);
  }

  public Object visit_CHAR(Parser.CHAR rule)
  {
    return visitRules(rule);
  }

  public Object visit_CR(Parser.CR rule)
  {
    return visitRules(rule);
  }

  public Object visit_CRLF(Parser.CRLF rule)
  {
    return visitRules(rule);
  }

  public Object visit_CTL(Parser.CTL rule)
  {
    return visitRules(rule);
  }

  public Object visit_DIGIT(Parser.DIGIT rule)
  {
    return visitRules(rule);
  }

  public Object visit_DQUOTE(Parser.DQUOTE rule)
  {
    return visitRules(rule);
  }

  public Object visit_HEXDIG(Parser.HEXDIG rule)
  {
    return visitRules(rule);
  }

  public Object visit_HTAB(Parser.HTAB rule)
  {
    return visitRules(rule);
  }

  public Object visit_LF(Parser.LF rule)
  {
    return visitRules(rule);
  }

  public Object visit_LWSP(Parser.LWSP rule)
  {
    return visitRules(rule);
  }

  public Object visit_OCTET(Parser.OCTET rule)
  {
    return visitRules(rule);
  }

  public Object visit_SP(Parser.SP rule)
  {
    return visitRules(rule);
  }

  public Object visit_VCHAR(Parser.VCHAR rule)
  {
    return visitRules(rule);
  }

  public Object visit_WSP(Parser.WSP rule)
  {
    return visitRules(rule);
  }

  public Object visit_StringValue(Parser.StringValue value)
  {
    for (int i=0; i<indent*2; i++) stream.print(' ');
    stream.println('"' + value.spelling + '"');
    return null;
  }

  public Object visit_NumericValue(Parser.NumericValue value)
  {
    for (int i=0; i<indent*2; i++) stream.print(' ');
    stream.println('"' + value.spelling + '"');
    return null;
  }

  private Object visitRules(Rule rule)
  {
    for (int i=0; i<indent*2; i++) stream.print(' ');
    stream.println(rule.getClass().getSimpleName());
    indent++;
    for (Rule innerrule : rule.rules) {
      innerrule.visit(this);
    }
    indent--;
    return null;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
