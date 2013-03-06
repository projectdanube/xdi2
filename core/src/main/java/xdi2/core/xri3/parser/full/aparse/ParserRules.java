package xdi2.core.xri3.parser.full.aparse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ParserRules {

	public static final String[] rules;

	static {

		List<String> rulesList = new ArrayList<String> ();

		for (Method method : Visitor.class.getMethods()) {

			if (! method.getName().equals("visit")) continue;

			Class<?> clazz = method.getParameterTypes()[0];
			rulesList.add(ruleNameForClass(clazz));
		}

		rules = rulesList.toArray(new String[rulesList.size()]);
	}

	static String ruleNameForClass(Class<?> clazz) {

		if (clazz.getSimpleName().startsWith("Rule_")) {

			return clazz.getSimpleName().substring(5).replace('_', '-');
		} else if (clazz.getSimpleName().startsWith("Terminal_")) {

			return "[" + clazz.getSimpleName().substring(9).toUpperCase() + "]";
		} else {

			return clazz.getSimpleName();
		}
	}
}