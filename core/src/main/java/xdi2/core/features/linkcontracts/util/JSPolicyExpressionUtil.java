package xdi2.core.features.linkcontracts.util;

import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

public class JSPolicyExpressionUtil extends ScriptableObject {
	private static final long serialVersionUID = 123470592527335642L;

	// The zero-argument constructor used by Rhino runtime to create instances
	@JSConstructor
	public JSPolicyExpressionUtil() {

	}

	// The class name is defined by the getClassName method
	@Override
	public String getClassName() {
		return "JSPolicyExpressionUtil";
	}

	public static boolean evaluateJSExpression(String expr) {
		boolean evalResult = true;
		if ((null == expr) || (expr.isEmpty())) {
			return evalResult;
		}
		try {
			Context cx = Context.enter();
			Scriptable scope = cx.initStandardObjects();

			ScriptableObject.defineClass(scope, JSPolicyExpressionUtil.class);
			Object[] arg = {};
			Scriptable policyExpressionHelper = cx.newObject(scope,
					"JSPolicyExpressionUtil", arg);

			scope.put("GlobalFunctions", scope, policyExpressionHelper);

			Object result = cx.evaluateString(scope, expr, "policyExpression",
					1, null);
			if (result != null && Context.toString(result).equals("true")) {
				evalResult = true;
			} else {
				evalResult = false;
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			Context.exit();
		}

		return evalResult;

	}

}
