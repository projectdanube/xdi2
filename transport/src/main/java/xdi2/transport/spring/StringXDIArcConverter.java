package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.syntax.XDIArc;

public class StringXDIArcConverter implements Converter<String, XDIArc> {

	@Override
	public XDIArc convert(String string) {

		return XDIArc.create(string);
	}
}
