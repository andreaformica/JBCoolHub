/**
 * 
 */
package atlas.cool.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author formica
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CoolQuery {
	String name() default "";
	String params() default "";
}
