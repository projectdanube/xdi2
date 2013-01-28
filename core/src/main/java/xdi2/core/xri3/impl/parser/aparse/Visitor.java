/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Sun Nov 18 00:40:33 CET 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser.aparse;

public interface Visitor
{
  public Object visit(Rule$xdi_address rule);
  public Object visit(Rule$xdi_context rule);
  public Object visit(Rule$xdi_statement rule);
  public Object visit(Rule$xdi_subject rule);
  public Object visit(Rule$xdi_predicate rule);
  public Object visit(Rule$xdi_object rule);
  public Object visit(Rule$xdi_segment rule);
  public Object visit(Rule$xdi_subseg rule);
  public Object visit(Rule$xdi_global_subseg rule);
  public Object visit(Rule$xdi_local_subseg rule);
  public Object visit(Rule$xdi_xref rule);
  public Object visit(Rule$xdi_xref_empty rule);
  public Object visit(Rule$xdi_xref_IRI rule);
  public Object visit(Rule$xdi_xref_address rule);
  public Object visit(Rule$xri rule);
  public Object visit(Rule$xri_reference rule);
  public Object visit(Rule$relative_xri_ref rule);
  public Object visit(Rule$relative_xri_part rule);
  public Object visit(Rule$xri_hier_part rule);
  public Object visit(Rule$xri_authority rule);
  public Object visit(Rule$subseg rule);
  public Object visit(Rule$global_subseg rule);
  public Object visit(Rule$local_subseg rule);
  public Object visit(Rule$gcs_char rule);
  public Object visit(Rule$lcs_char rule);
  public Object visit(Rule$literal rule);
  public Object visit(Rule$literal_nc rule);
  public Object visit(Rule$xref rule);
  public Object visit(Rule$xref_empty rule);
  public Object visit(Rule$xref_xri_reference rule);
  public Object visit(Rule$xref_IRI rule);
  public Object visit(Rule$xri_path rule);
  public Object visit(Rule$xri_path_abempty rule);
  public Object visit(Rule$xri_path_abs rule);
  public Object visit(Rule$xri_path_noscheme rule);
  public Object visit(Rule$xri_segment rule);
  public Object visit(Rule$xri_segment_nz rule);
  public Object visit(Rule$xri_segment_nc rule);
  public Object visit(Rule$xri_pchar rule);
  public Object visit(Rule$xri_pchar_nc rule);
  public Object visit(Rule$xri_reserved rule);
  public Object visit(Rule$xri_gen_delims rule);
  public Object visit(Rule$xri_sub_delims rule);
  public Object visit(Rule$IRI rule);
  public Object visit(Rule$scheme rule);
  public Object visit(Rule$ihier_part rule);
  public Object visit(Rule$iauthority rule);
  public Object visit(Rule$iuserinfo rule);
  public Object visit(Rule$ihost rule);
  public Object visit(Rule$IP_literal rule);
  public Object visit(Rule$IPvFuture rule);
  public Object visit(Rule$IPv6address rule);
  public Object visit(Rule$ls32 rule);
  public Object visit(Rule$h16 rule);
  public Object visit(Rule$IPv4address rule);
  public Object visit(Rule$dec_octet rule);
  public Object visit(Rule$ireg_name rule);
  public Object visit(Rule$port rule);
  public Object visit(Rule$ipath_abempty rule);
  public Object visit(Rule$ipath_abs rule);
  public Object visit(Rule$ipath_rootless rule);
  public Object visit(Rule$ipath_empty rule);
  public Object visit(Rule$isegment rule);
  public Object visit(Rule$isegment_nz rule);
  public Object visit(Rule$iquery rule);
  public Object visit(Rule$iprivate rule);
  public Object visit(Rule$ifragment rule);
  public Object visit(Rule$ipchar rule);
  public Object visit(Rule$iunreserved rule);
  public Object visit(Rule$pct_encoded rule);
  public Object visit(Rule$ucschar rule);
  public Object visit(Rule$reserved rule);
  public Object visit(Rule$gen_delims rule);
  public Object visit(Rule$sub_delims rule);
  public Object visit(Rule$unreserved rule);
  public Object visit(Rule$ALPHA rule);
  public Object visit(Rule$BIT rule);
  public Object visit(Rule$CHAR rule);
  public Object visit(Rule$CR rule);
  public Object visit(Rule$CRLF rule);
  public Object visit(Rule$CTL rule);
  public Object visit(Rule$DIGIT rule);
  public Object visit(Rule$DQUOTE rule);
  public Object visit(Rule$HEXDIG rule);
  public Object visit(Rule$HTAB rule);
  public Object visit(Rule$LF rule);
  public Object visit(Rule$LWSP rule);
  public Object visit(Rule$OCTET rule);
  public Object visit(Rule$SP rule);
  public Object visit(Rule$VCHAR rule);
  public Object visit(Rule$WSP rule);

  public Object visit(Terminal$StringValue value);
  public Object visit(Terminal$NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
