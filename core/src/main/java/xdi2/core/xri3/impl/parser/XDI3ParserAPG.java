package xdi2.core.xri3.impl.parser;

import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3Statement;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XDI3XRef;
import xdi2.core.xri3.impl.parser.apg.XDI3Grammar;

import com.coasttocoastresearch.apg.Ast;
import com.coasttocoastresearch.apg.Grammar;
import com.coasttocoastresearch.apg.Parser;

public class XDI3ParserAPG extends XDI3Parser {
	
	@Override
	public XDI3Statement parseXDI3Statement(String string) {

		Grammar g = XDI3Grammar.getInstance();
		Parser p = new Parser(g);
		p.setStartRule(XDI3Grammar.RuleNames.XDI_STATEMENT.ruleID());
		Ast a = p.enableAst(true);
		
		return null;
	}

	@Override
	public XDI3Segment parseXDI3Segment(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XDI3SubSegment parseXDI3SubSegment(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XDI3XRef parseXDI3XRef(String string) {
		// TODO Auto-generated method stub
		return null;
	}

}
