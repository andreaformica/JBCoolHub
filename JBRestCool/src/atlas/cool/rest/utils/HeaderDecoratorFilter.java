/**
 * 
 */
package atlas.cool.rest.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.NameBinding;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

/**
 * @author formica
 * 
 */
@Provider
@FilteredResponse
public class HeaderDecoratorFilter implements ContainerResponseFilter {

	@Inject
	private Logger log;

	/**
	 * @return
	 */
	protected Annotation isAnnotationPresent() {
		final Annotation[] classannlist = this.getClass().getAnnotations();
		for (final Annotation ann : classannlist) {
			log.info("Check annotation " + ann.annotationType());
			if (ann.annotationType().isAnnotationPresent(NameBinding.class)) {
				log.info("return annotation " + ann.annotationType());
				return ann;
			}
		}
		log.info("return null ");
		return null;
	}

	/**
	 * @param ctx
	 * @return
	 */
	protected boolean applyFilter(final ContainerResponseContext ctx) {
		Annotation bindingann = isAnnotationPresent();
		if (isAnnotationPresent() != null) {
			// check if method is annotated as GET
			final Annotation[] ctxannlist = ctx.getEntityAnnotations();
			log.info("Annotation in container response "
					+ ((ctxannlist != null) ? ctxannlist.length : 0));
			for (final Annotation ann : ctxannlist) {
				log.info("Check method annotation " + ann.annotationType());
				if (ann.equals(bindingann)) {
					log.info("Method is annotated with the same..." + ann
							+ " , bind it !");
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container
	 * .ContainerRequestContext, javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(final ContainerRequestContext req,
			final ContainerResponseContext res) throws IOException {
		if (applyFilter(res)) {
			log.info("Method has been intercepted ... " + req);
		} else {
			log.info("Method is not filtered  ... " + req);
			return;
		}

		log.info("Header   response filter :" + req.getMethod());
		log.info("         response context entity output stream "
				+ res.getEntityStream().toString());
		try {
			final MultivaluedMap<String, Object> mvm = res.getHeaders();
			mvm.add("Access-Control-Allow-Origin", "*");
			mvm.add("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
			mvm.add("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
			final Set<String> keys = mvm.keySet();
			for (final String akey : keys) {
				log.info("Header " + akey + " is " + mvm.get(akey));
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
