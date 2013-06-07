package atlas.cool.interceptors;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

/**
 * @author formica
 *
 */
public class WebRestCtxProvider {

	/**
	 * @param ic
	 * 	Invocation context.
	 * @return
	 * 	Object.
	 * @throws Exception
	 * 	Exception.
	 */
	@AroundInvoke
	public final Object injectMap(final InvocationContext ic) throws Exception {
		String orderByCondition = (String) ic.getContextData().get("OrderBy");
		WebRestContextHolder.put("OrderBy", orderByCondition);
		return ic.proceed();
	}
}
