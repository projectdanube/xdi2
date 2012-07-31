package xdi2.server.util;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import org.springframework.core.convert.converter.Converter;

import xdi2.core.xri3.impl.XRI3Segment;

public class XRI3SegmentPropertyEditor extends PropertyEditorSupport implements PropertyEditor, Converter<String, XRI3Segment> {

	@Override
	public void setAsText(String string) throws IllegalArgumentException {

		this.setValue(new XRI3Segment(string));
	}

	@Override
	public XRI3Segment convert(String string) {

		return new XRI3Segment(string);
	}
}
