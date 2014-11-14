/**
 * 
 */
package atlas.cool.interceptors;

import java.util.logging.Logger;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import atlas.cool.annotations.Tracer;

/**
 * @author formica
 * 
 */
@Interceptor
@Tracer
public class ProfileInterceptor {

	@Inject
	private Logger log;

	
	/**
	 * Default ctor.
	 */
	public ProfileInterceptor() {
	}

	/**
	 * @param ctx
	 * 	The context.
	 * @return
	 * 	The object.
	 * @throws Exception
	 * 	Exception.
	 */
	@AroundInvoke
	public Object trace(final InvocationContext ctx) throws Exception {
		log.info("*** TracingInterceptor intercepting "
				+ ctx.getMethod().getName());
		long start = System.currentTimeMillis();
		String param = (String) ctx.getParameters()[0];

		if (param == null) {
			ctx.setParameters(new String[] { "default" });
		}

		try {
			return ctx.proceed();
		} catch (Exception e) {
			throw e;
		} finally {
			long time = System.currentTimeMillis() - start;
			String method = ctx.getClass().getName();
			log.info("*** TracingInterceptor invocation of " + method
					+ " took " + time + "ms");
		}
	}

}
