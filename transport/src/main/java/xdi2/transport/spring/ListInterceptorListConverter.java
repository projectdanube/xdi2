package xdi2.transport.spring;

import java.util.List;

import org.springframework.core.convert.converter.Converter;

import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.InterceptorList;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ListInterceptorListConverter implements Converter<List<Interceptor>, InterceptorList> {

	@Override
	public InterceptorList convert(List<Interceptor> source) {

		InterceptorList target = new InterceptorList();

		for (Interceptor item : source)
			target.addInterceptor(item);

		return target;
	}
}
