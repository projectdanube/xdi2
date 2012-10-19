/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.2
 * Produced : Fri Oct 19 08:29:48 CEST 2012
 *
 * -----------------------------------------------------------------------------
 */

package xdi2.core.xri3.impl.parser;

import java.util.ArrayList;

public class Displayer implements Visitor
{

  public Object visit(Rule$xri rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_reference rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$relative_xri_ref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$relative_xri_part rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_hier_part rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_authority rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$global_subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$local_subseg rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$gcs_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$lcs_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$literal_nc rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xref rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xref_empty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xref_xri_reference rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xref_IRI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_path rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_path_abempty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_path_abs rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_path_noscheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_segment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_segment_nz rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_segment_nc rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_pchar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_pchar_nc rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_reserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_gen_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$xri_sub_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$IRI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$scheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ihier_part rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$iauthority rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$iuserinfo rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ihost rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$IP_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$IPvFuture rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$IPv6address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ls32 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$h16 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$IPv4address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$dec_octet rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ireg_name rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$port rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ipath_abempty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ipath_abs rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ipath_rootless rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ipath_empty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$isegment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$isegment_nz rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$iquery rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$iprivate rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ifragment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ipchar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$iunreserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$pct_encoded rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ucschar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$reserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$gen_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$sub_delims rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$unreserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$ALPHA rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$BIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$CHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$CR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$CRLF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$CTL rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$DIGIT rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$DQUOTE rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$HEXDIG rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$HTAB rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$LF rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$LWSP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$OCTET rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$SP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$VCHAR rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule$WSP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Terminal$StringValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  public Object visit(Terminal$NumericValue value)
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
