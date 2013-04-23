/**
 * 
 */
package atlas.cool.rest.utils;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NameBinding;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;

import atlas.frontier.fdo.FrontierBlobTypedEncoder;
import atlas.frontier.fdo.FrontierEncoder;
import atlas.frontier.fdo.FrontierResponseFormat;

/**
 * @author formica
 *
 */
//@Provider
//@FrontierResponse
public class FrontierResponseFilter implements ContainerResponseFilter {

	public static boolean send_stale_if_error=true;

	@Inject
	private Logger log;


	protected Annotation isAnnotationPresent() {
		Annotation[] classannlist = this.getClass().getAnnotations();
		for (Annotation ann : classannlist) {
			log.info("Check annotation " + ann.annotationType());
			if (ann.annotationType().isAnnotationPresent(NameBinding.class)) {
				return ann;
			}
		}
		return null;
	}

	protected boolean applyFilter(ContainerResponseContext ctx) {
		Annotation bindingann = null;
		if ((bindingann = isAnnotationPresent()) != null) {
			// check if method is annotated in the same way
			Annotation[] ctxannlist = ctx.getEntityAnnotations();
			for (Annotation ann : ctxannlist) {
				log.info("Check entity annotation " + ann.annotationType());
				if (ann.equals(bindingann)) {
					log.info("Method is annotated with the same....bind it !");
					return true;
				}
			}
		}
		return false;
	}

	
	/* (non-Javadoc)
	 * @see javax.ws.rs.container.ContainerResponseFilter#filter(javax.ws.rs.container.ContainerRequestContext, javax.ws.rs.container.ContainerResponseContext)
	 */
	@Override
	public void filter(ContainerRequestContext ctx,
			ContainerResponseContext respctx) throws IOException {
		
		if (applyFilter(respctx)) {
			log.info("Method has been intercepted ... " + ctx);
		} else {
			log.info("Method is not filtered  ... " + ctx);
			return;
		}

		log.info("Frontier response filter :"+ctx.getMethod());
		log.info("         response context entity output stream "+respctx.getEntityStream().toString());
		try {
			MultivaluedMap<String,Object> mvm = respctx.getHeaders();
			Set<String> keys = mvm.keySet();
			for (String akey : keys) {
				log.info("Header "+akey+" is "+mvm.get(akey));
			}
			setAgeExpires(ctx, respctx, 5*60); // expire in 5 minutes.
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setAgeExpires(ContainerRequestContext req,
			ContainerResponseContext resp, long age) {
		// If max-stale is set by client, respond with corresponding
		// stale-if-error. With squid2.7, this causes an error to
		// be returned when the origin server can't be contacted and
		// an old copy exists in the cache. Before squid2.7 or without
		// this option it returns stale data. squid2.7 caches this
		// negative error for a configured period of time so the server
		// won't be hit too hard for requests.
		String cch = req.getHeaderString("cache-control");
		int idx = -1;
		if (cch != null)
			idx = cch.indexOf("max-stale");
		if (send_stale_if_error && (idx >= 0)) {
			idx = cch.indexOf('=', idx);
			if (idx >= 0) {
				idx += 1;
				int endidx = cch.indexOf(',', idx);
				if (endidx == -1)
					endidx = cch.length();
				resp.getHeaders().add("Cache-Control", "max-age=" + age
						+ ", stale-if-error=" + cch.substring(idx, endidx));
				return;
			}
		} else
			resp.getHeaders().add("Cache-Control", "max-age=" + age);
	}

}
