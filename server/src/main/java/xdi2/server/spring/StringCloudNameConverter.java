package xdi2.server.spring;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.CloudName;

public class StringCloudNameConverter implements Converter<String, CloudName> {

	@Override
	public CloudName convert(String string) {

		return CloudName.create(string);
	}
}
