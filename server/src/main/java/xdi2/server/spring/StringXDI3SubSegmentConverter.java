package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3SubSegment;

public class StringXDI3SubSegmentConverter implements Converter<String, XDI3SubSegment> {

	@Override
	public XDI3SubSegment convert(String string) {

		return XDI3SubSegment.create(string);
	}
}
