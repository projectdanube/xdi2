/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Fri Oct 19 08:29:48 CEST 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser;

public interface Visitor
{
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
