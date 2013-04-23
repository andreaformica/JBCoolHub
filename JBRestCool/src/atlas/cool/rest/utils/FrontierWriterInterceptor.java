/**
 * 
 */
package atlas.cool.rest.utils;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.NameBinding;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import atlas.frontier.fdo.FrontierBlobTypedEncoder;
import atlas.frontier.fdo.FrontierEncoder;
import atlas.frontier.fdo.FrontierResponseFormat;

/**
 * @author formica
 * 
 */
// FIXME: if we enable the following annotations, then the frontier restful services will get intercepted
//@FrontierResponse
//@Provider
public class FrontierWriterInterceptor extends FrontierUtils implements
		WriterInterceptor {

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

	protected boolean applyFilter(WriterInterceptorContext ctx) {
		Annotation bindingann = null;
		if ((bindingann = isAnnotationPresent()) != null) {
			// check if method is annotated in the same way
			Annotation[] ctxannlist = ctx.getAnnotations();
			for (Annotation ann : ctxannlist) {
				log.info("Check context annotation " + ann.annotationType());
				if (ann.equals(bindingann)) {
					log.info("Method is annotated with the same....bind it !");
					return true;
				}
			}
		}
		return false;
	}

	public void aroundWriteTo(WriterInterceptorContext ctx) throws IOException,
			WebApplicationException {

		if (applyFilter(ctx)) {
			log.info("Method has been intercepted ... " + ctx);
		} else {
			log.info("Method is not filtered  ... " + ctx);
			ctx.proceed();
			return;
		}
		log.info("Frontier response interceptor :" + ctx.getEntity());
		log.info("         response context output stream "
				+ ctx.getOutputStream().toString());
		OutputStream eos = (OutputStream) ctx.getOutputStream();
		//PrintStream localps = new PrintStream(eos);	
		ctx.getHeaders().remove("Content-Length");

		FrontierEncoder enc = null;
		try {
			enc = new FrontierBlobTypedEncoder(eos, "zip");
			// PrintStream pos = new PrintStream(eos);
			FrontierResponseFormat.begin(eos, "3.29", "1.0");
			List<?> objlist = (List<?>) ctx.getEntity();
			log.info("         response context entity is " + objlist.size());
			int ns = objlist.size();
			FrontierResponseFormat.transaction_start(eos, ns);
			FrontierResponseFormat.payload_start(eos, "frontier_request", "1",
					"BLOBzip");
			if (ns > 0) {
				writeMetaData(objlist.get(0), enc);
			}
			writeData(objlist, enc);
			enc.flush();
			FrontierResponseFormat.payload_end(eos, 0, "", "md5", ns, ns);
			FrontierResponseFormat.transaction_end(eos);
			FrontierResponseFormat.close(eos);
			// remove objlist
			objlist.clear();
			enc.flush();
			enc.close();
			ctx.proceed();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log.info("Frontier response interceptor after method execution :" + ctx.getEntity());
			log.info("         response context output stream "
					+ ctx.getOutputStream().toString());
			try {
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return;
	}

}
