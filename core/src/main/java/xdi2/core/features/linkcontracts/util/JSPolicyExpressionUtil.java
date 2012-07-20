package xdi2.core.features.linkcontracts.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.annotations.JSConstructor;

public class JSPolicyExpressionUtil extends ScriptableObject {
	private static final long serialVersionUID = 123470592527335642L;
	private static Scriptable scope = null;
	private static Context cx = null;

	// The zero-argument constructor used by Rhino runtime to create instances
	@JSConstructor
	public JSPolicyExpressionUtil() {

	}

	// The class name is defined by the getClassName method
	@Override
	public String getClassName() {
		return "JSPolicyExpressionUtil";
	}

	public static boolean evaluateJSExpression(String exprX) {
		boolean evalResult = true;
		
		if ((null == exprX) || (exprX.isEmpty())) {
			return evalResult;
		}
		String expr = "";
		try {
			expr = URLDecoder.decode(exprX,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initialize();
		try{
		Object result = cx.evaluateString(scope, expr, "policyExpression", 1,
				null);
		if (result != null && Context.toString(result).equals("true")) {
			evalResult = true;
		} else {
			evalResult = false;
		}	
		}catch(Exception ex){
			ex.printStackTrace();
			evalResult = false;
		}
		return evalResult;
	}

	public static void initialize() {
		if(cx == null || scope == null || !scope.has("XD2CoreJS", scope)){
		cx = Context.enter();
		scope = cx.initStandardObjects();

		try {
			ScriptableObject.defineClass(scope, JSPolicyExpressionUtil.class);
		} catch (IllegalAccessException e) {
			
			e.printStackTrace();
		} catch (InstantiationException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		}
		Object[] arg = {};
		Scriptable policyExpressionHelper = cx.newObject(scope,
				"JSPolicyExpressionUtil", arg);

		scope.put("XD2CoreJS", scope, policyExpressionHelper);
		}
	}

	public static void cleanup() {
		Context.exit();
	}
	public static Scriptable getJSExpressionScope() {
		return scope;
	}
	public static Context getJSExpressionContext() {
		return cx;
	}

}
