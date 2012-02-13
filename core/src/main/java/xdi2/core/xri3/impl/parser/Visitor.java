package xdi2.core.xri3.impl.parser;

/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 0.5
 * Produced : Sat Dec 20 01:35:48 CET 2008
 *
 * -----------------------------------------------------------------------------
 */

public interface Visitor
{
  public void visit(Rule rule);

  public Object visit_xri(Parser.xri rule);
  public Object visit_xri_reference(Parser.xri_reference rule);
  public Object visit_relative_xri_ref(Parser.relative_xri_ref rule);
  public Object visit_relative_xri_part(Parser.relative_xri_part rule);
  public Object visit_xri_hier_part(Parser.xri_hier_part rule);
  public Object visit_xri_authority(Parser.xri_authority rule);
  public Object visit_subseg(Parser.subseg rule);
  public Object visit_global_subseg(Parser.global_subseg rule);
  public Object visit_local_subseg(Parser.local_subseg rule);
  public Object visit_gcs_char(Parser.gcs_char rule);
  public Object visit_lcs_char(Parser.lcs_char rule);
  public Object visit_literal(Parser.literal rule);
  public Object visit_literal_nc(Parser.literal_nc rule);
  public Object visit_xref(Parser.xref rule);
  public Object visit_xref_empty(Parser.xref_empty rule);
  public Object visit_xref_xri_reference(Parser.xref_xri_reference rule);
  public Object visit_xref_IRI(Parser.xref_IRI rule);
  public Object visit_xri_path(Parser.xri_path rule);
  public Object visit_xri_path_abempty(Parser.xri_path_abempty rule);
  public Object visit_xri_path_abs(Parser.xri_path_abs rule);
  public Object visit_xri_path_noscheme(Parser.xri_path_noscheme rule);
  public Object visit_xri_segment(Parser.xri_segment rule);
  public Object visit_xri_segment_nz(Parser.xri_segment_nz rule);
  public Object visit_xri_segment_nc(Parser.xri_segment_nc rule);
  public Object visit_xri_pchar(Parser.xri_pchar rule);
  public Object visit_xri_pchar_nc(Parser.xri_pchar_nc rule);
  public Object visit_xri_reserved(Parser.xri_reserved rule);
  public Object visit_xri_gen_delims(Parser.xri_gen_delims rule);
  public Object visit_xri_sub_delims(Parser.xri_sub_delims rule);
  public Object visit_IRI(Parser.IRI rule);
  public Object visit_scheme(Parser.scheme rule);
  public Object visit_ihier_part(Parser.ihier_part rule);
  public Object visit_iauthority(Parser.iauthority rule);
  public Object visit_iuserinfo(Parser.iuserinfo rule);
  public Object visit_ihost(Parser.ihost rule);
  public Object visit_IP_literal(Parser.IP_literal rule);
  public Object visit_IPvFuture(Parser.IPvFuture rule);
  public Object visit_IPv6address(Parser.IPv6address rule);
  public Object visit_ls32(Parser.ls32 rule);
  public Object visit_h16(Parser.h16 rule);
  public Object visit_IPv4address(Parser.IPv4address rule);
  public Object visit_dec_octet(Parser.dec_octet rule);
  public Object visit_ireg_name(Parser.ireg_name rule);
  public Object visit_port(Parser.port rule);
  public Object visit_ipath_abempty(Parser.ipath_abempty rule);
  public Object visit_ipath_abs(Parser.ipath_abs rule);
  public Object visit_ipath_rootless(Parser.ipath_rootless rule);
  public Object visit_ipath_empty(Parser.ipath_empty rule);
  public Object visit_isegment(Parser.isegment rule);
  public Object visit_isegment_nz(Parser.isegment_nz rule);
  public Object visit_iquery(Parser.iquery rule);
  public Object visit_iprivate(Parser.iprivate rule);
  public Object visit_ifragment(Parser.ifragment rule);
  public Object visit_ipchar(Parser.ipchar rule);
  public Object visit_iunreserved(Parser.iunreserved rule);
  public Object visit_pct_encoded(Parser.pct_encoded rule);
  public Object visit_ucschar(Parser.ucschar rule);
  public Object visit_reserved(Parser.reserved rule);
  public Object visit_gen_delims(Parser.gen_delims rule);
  public Object visit_sub_delims(Parser.sub_delims rule);
  public Object visit_unreserved(Parser.unreserved rule);
  public Object visit_ALPHA(Parser.ALPHA rule);
  public Object visit_BIT(Parser.BIT rule);
  public Object visit_CHAR(Parser.CHAR rule);
  public Object visit_CR(Parser.CR rule);
  public Object visit_CRLF(Parser.CRLF rule);
  public Object visit_CTL(Parser.CTL rule);
  public Object visit_DIGIT(Parser.DIGIT rule);
  public Object visit_DQUOTE(Parser.DQUOTE rule);
  public Object visit_HEXDIG(Parser.HEXDIG rule);
  public Object visit_HTAB(Parser.HTAB rule);
  public Object visit_LF(Parser.LF rule);
  public Object visit_LWSP(Parser.LWSP rule);
  public Object visit_OCTET(Parser.OCTET rule);
  public Object visit_SP(Parser.SP rule);
  public Object visit_VCHAR(Parser.VCHAR rule);
  public Object visit_WSP(Parser.WSP rule);
  public Object visit_StringValue(Parser.StringValue value);
  public Object visit_NumericValue(Parser.NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
