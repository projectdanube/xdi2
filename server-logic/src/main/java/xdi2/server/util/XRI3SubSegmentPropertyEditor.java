package xdi2.server.util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.impl.XRI3SubSegment;

public class XRI3SubSegmentPropertyEditor extends PropertyEditorSupport implements PropertyEditor, Converter<String, XRI3SubSegment> {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {

		this.setValue(new XRI3SubSegment(text));
	}

	@Override
	public XRI3SubSegment convert(String string) {

		return new XRI3SubSegment(string);
	}
}
