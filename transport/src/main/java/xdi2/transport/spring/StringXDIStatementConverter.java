package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.syntax.XDIStatement;

public class StringXDIStatementConverter implements Converter<String, XDIStatement> {

	@Override
	public XDIStatement convert(String string) {

		return XDIStatement.create(string);
	}
}
