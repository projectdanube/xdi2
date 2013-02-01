package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.XDI3Segment;

public class StringXDI3SegmentConverter implements Converter<String, XDI3Segment> {

	@Override
	public XDI3Segment convert(String string) {

		return XDI3Segment.create(string);
	}
}
