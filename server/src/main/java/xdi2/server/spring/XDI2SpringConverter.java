package xdi2.server.spring;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;

import xdi2.messaging.target.AbstractMessagingTarget;

public class XDI2SpringConverter implements GenericConverter {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingTarget.class);

	private static List<Converter<?, ?>> converters = Arrays.asList(new Converter<?, ?> [] {
			new StringXDI3SubSegmentConverter(),
			new StringXDI3SegmentConverter(),
			new StringXDI3StatementConverter(),
			new StringXDI3XRefConverter(),
			new StringPublicKeyConverter(),
			new ListContributorMapConverter()
	});

	private static Map<ConvertiblePair, Converter<?, ?>> convertibleTypes;

	static {

		convertibleTypes = new HashMap<ConvertiblePair, Converter<?, ?>> ();

		for (Converter<?, ?> converter : converters) convertibleTypes.put(getConvertiblePair(converter), converter);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {

		if (log.isDebugEnabled()) log.debug("Trying to convert from " + sourceType.getType().getSimpleName() + " to " + targetType.getType().getSimpleName());

		for (Map.Entry<ConvertiblePair, Converter<?, ?>> convertibleType : convertibleTypes.entrySet()) {

			if (log.isDebugEnabled()) log.debug("Considering converter from " + convertibleType.getKey().getSourceType().getSimpleName() + " to " + convertibleType.getKey().getTargetType().getSimpleName());

			if (convertibleType.getKey().getSourceType().isAssignableFrom(sourceType.getType()) && convertibleType.getKey().getTargetType().isAssignableFrom(targetType.getType())) {

				Converter<Object, Object> converter = (Converter<Object, Object>) convertibleType.getValue();

				Object target = converter.convert(source);

				if (log.isDebugEnabled()) log.debug("Converted from " + source.getClass().getSimpleName() + " to " + target.getClass().getSimpleName());

				return target;
			}
		}

		return null;
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {

		return convertibleTypes.keySet();
	}

	private static ConvertiblePair getConvertiblePair(Converter<?, ?> converter) {

		Method method = getConvertMethod(converter);
		if (method == null) return null;

		return new ConvertiblePair(method.getParameterTypes()[0], method.getReturnType());
	}

	private static Method getConvertMethod(Converter<?, ?> converter) {

		for (Method method : converter.getClass().getMethods()) {

			if ("convert".equals(method.getName())) return method;
		}

		return null;
	}
}
