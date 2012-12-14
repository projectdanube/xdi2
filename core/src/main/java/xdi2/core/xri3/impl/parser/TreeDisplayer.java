package xdi2.core.xri3.impl.parser;

import java.io.PrintStream;

public class TreeDisplayer implements Visitor
{
	private int indent;
	private PrintStream stream;
	public TreeDisplayer(PrintStream stream)
	{
		this.indent = 0;
		this.stream = stream;
	}

	@Override
	public Object visit(Rule$xdi_address rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_context rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_statement rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_subject rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_predicate rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_object rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_segment rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_subseg rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_global_subseg rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_local_subseg rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_xref rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_xref_empty rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_xref_address rule) {
		return visitRule(rule);
	}
	@Override
	public Object visit(Rule$xdi_xref_IRI rule) {
		return visitRule(rule);
	}

	public Object visit(Rule$xri rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_reference rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$relative_xri_ref rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$relative_xri_part rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_hier_part rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_authority rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$subseg rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$global_subseg rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$local_subseg rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$gcs_char rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$lcs_char rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$literal rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$literal_nc rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xref rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xref_empty rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xref_xri_reference rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xref_IRI rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_path rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_path_abempty rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_path_abs rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_path_noscheme rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_segment rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_segment_nz rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_segment_nc rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_pchar rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_pchar_nc rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_reserved rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_gen_delims rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$xri_sub_delims rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$IRI rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$scheme rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ihier_part rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$iauthority rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$iuserinfo rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ihost rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$IP_literal rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$IPvFuture rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$IPv6address rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ls32 rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$h16 rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$IPv4address rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$dec_octet rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ireg_name rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$port rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ipath_abempty rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ipath_abs rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ipath_rootless rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ipath_empty rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$isegment rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$isegment_nz rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$iquery rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$iprivate rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ifragment rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ipchar rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$iunreserved rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$pct_encoded rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ucschar rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$reserved rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$gen_delims rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$sub_delims rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$unreserved rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$ALPHA rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$BIT rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$CHAR rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$CR rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$CRLF rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$CTL rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$DIGIT rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$DQUOTE rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$HEXDIG rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$HTAB rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$LF rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$LWSP rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$OCTET rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$SP rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$VCHAR rule)
	{
		return visitRule(rule);
	}

	public Object visit(Rule$WSP rule)
	{
		return visitRule(rule);
	}

	public Object visit(Terminal$StringValue value)
	{
		stream.println('"' + value.spelling + '"');
		return null;
	}

	public Object visit(Terminal$NumericValue value)
	{
		stream.println('"' + value.spelling + '"');
		return null;
	}

	private Object visitRule(Rule rule)
	{
		stream.println(ParserRules.ruleNameForClass(rule.getClass()));
		for (Rule innerrule : rule.rules) {
			indent++;
			for (int i=0; i<indent*2; i++) stream.print(' ');
			innerrule.accept(this);
			indent--;
		}
		return null;
	}
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
