package xdi2.messaging.target.contributor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContributorMount {

	String[] contributorXris() default { };
	boolean address() default true;
	boolean contextNodeStatement() default true;
	boolean relationStatement() default true;
	boolean literalStatement() default true;
	String[] contextNodeArcXris() default { };
	String[] relationArcXris() default { };
	String[] targetContextNodeXris() default { };
}
