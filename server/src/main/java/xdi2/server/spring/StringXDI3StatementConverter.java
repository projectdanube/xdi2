package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3Statement;

public class StringXDI3StatementConverter implements Converter<String, XDI3Statement> {

	@Override
	public XDI3Statement convert(String string) {

		return XDI3Statement.create(string);
	}
}
