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
public @interface CoolPayloadParser {
	/**
	 * @return
	 */
	String schema() default "";
	/**
	 * @return
	 */
	String folder() default "";
}
