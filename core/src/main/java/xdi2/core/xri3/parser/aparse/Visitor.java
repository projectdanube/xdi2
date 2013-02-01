/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.3
 * Produced : Thu Jan 31 23:16:41 CET 2013
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.parser.aparse;

public interface Visitor
{
  public Object visit(Rule_xdi_address rule);
  public Object visit(Rule_xdi_context rule);
  public Object visit(Rule_xdi_inner_graph rule);
  public Object visit(Rule_xdi_statement rule);
  public Object visit(Rule_xdi_subject rule);
  public Object visit(Rule_xdi_predicate rule);
  public Object visit(Rule_xdi_object rule);
  public Object visit(Rule_xdi_segment rule);
  public Object visit(Rule_subseg rule);
  public Object visit(Rule_global_subseg rule);
  public Object visit(Rule_local_subseg rule);
  public Object visit(Rule_gcs_char rule);
  public Object visit(Rule_lcs_char rule);
  public Object visit(Rule_xref rule);
  public Object visit(Rule_xref_empty rule);
  public Object visit(Rule_xref_IRI rule);
  public Object visit(Rule_xref_context rule);
  public Object visit(Rule_xref_inner_graph rule);
  public Object visit(Rule_xref_statement rule);
  public Object visit(Rule_xref_literal rule);
  public Object visit(Rule_literal rule);
  public Object visit(Rule_xdi_pchar rule);
  public Object visit(Rule_xdi_pchar_nc rule);
  public Object visit(Rule_xdi_reserved rule);
  public Object visit(Rule_xdi_gen_delims rule);
  public Object visit(Rule_xdi_sub_delims rule);
  public Object visit(Rule_IRI rule);
  public Object visit(Rule_scheme rule);
  public Object visit(Rule_ihier_part rule);
  public Object visit(Rule_iauthority rule);
  public Object visit(Rule_iuserinfo rule);
  public Object visit(Rule_ihost rule);
  public Object visit(Rule_IP_literal rule);
  public Object visit(Rule_IPvFuture rule);
  public Object visit(Rule_IPv6address rule);
  public Object visit(Rule_ls32 rule);
  public Object visit(Rule_h16 rule);
  public Object visit(Rule_IPv4address rule);
  public Object visit(Rule_dec_octet rule);
  public Object visit(Rule_ireg_name rule);
  public Object visit(Rule_port rule);
  public Object visit(Rule_ipath_abempty rule);
  public Object visit(Rule_ipath_abs rule);
  public Object visit(Rule_ipath_rootless rule);
  public Object visit(Rule_ipath_empty rule);
  public Object visit(Rule_isegment rule);
  public Object visit(Rule_isegment_nz rule);
  public Object visit(Rule_iquery rule);
  public Object visit(Rule_iprivate rule);
  public Object visit(Rule_ifragment rule);
  public Object visit(Rule_ipchar rule);
  public Object visit(Rule_iunreserved rule);
  public Object visit(Rule_pct_encoded rule);
  public Object visit(Rule_ucschar rule);
  public Object visit(Rule_reserved rule);
  public Object visit(Rule_gen_delims rule);
  public Object visit(Rule_sub_delims rule);
  public Object visit(Rule_unreserved rule);
  public Object visit(Rule_ALPHA rule);
  public Object visit(Rule_BIT rule);
  public Object visit(Rule_CHAR rule);
  public Object visit(Rule_CR rule);
  public Object visit(Rule_CRLF rule);
  public Object visit(Rule_CTL rule);
  public Object visit(Rule_DIGIT rule);
  public Object visit(Rule_DQUOTE rule);
  public Object visit(Rule_HEXDIG rule);
  public Object visit(Rule_HTAB rule);
  public Object visit(Rule_LF rule);
  public Object visit(Rule_LWSP rule);
  public Object visit(Rule_OCTET rule);
  public Object visit(Rule_SP rule);
  public Object visit(Rule_VCHAR rule);
  public Object visit(Rule_WSP rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
