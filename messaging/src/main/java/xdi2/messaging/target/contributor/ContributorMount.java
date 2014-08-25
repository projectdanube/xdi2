package xdi2.messaging.target.contributor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContributorMount {

	String[] contributorAddresses() default { };
	String[] operationAddresses() default { };
	String[] contextNodeArcs() default { };
	String[] relationAddresses() default { };
	String[] targetContextNodeXDIAddresses() default { };
}
