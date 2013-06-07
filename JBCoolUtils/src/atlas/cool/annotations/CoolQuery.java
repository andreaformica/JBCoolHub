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
	/**
	 * @return
	 */
	String name() default "";
	/**
	 * @return
	 */
	String params() default "";
}
