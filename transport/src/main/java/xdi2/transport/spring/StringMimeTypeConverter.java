package xdi2.transport.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.io.MimeType;

public class StringMimeTypeConverter implements Converter<String, MimeType> {

	@Override
	public MimeType convert(String string) {

		return new MimeType(string);
	}
}
