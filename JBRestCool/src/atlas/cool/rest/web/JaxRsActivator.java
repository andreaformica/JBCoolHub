package atlas.cool.rest.web;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.models.Info;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.reflections.Reflections;

import atlas.cool.rest.web.json.ComaRESTJsonService;
import atlas.cool.rest.web.json.CoolGtagRESTJsonService;
import atlas.cool.rest.web.json.CoolResourceRESTJsonService;
import atlas.cool.rest.web.xml.ComaCoolResourceRESTService;
import atlas.cool.rest.web.xml.ComaRESTService;
import atlas.cool.rest.web.xml.CoolCherryPyResourceRESTService;
import atlas.cool.rest.web.xml.CoolGtagRESTService;
import atlas.cool.rest.web.xml.CoolResourceRESTService;

/**
 * A class extending {@link Application} and annotated with @ApplicationPath is the Java EE 6
 * "no XML" approach to activating JAX-RS.
 * 
 * <p>
 * Resources are served relative to the servlet path specified in the {@link ApplicationPath}
 * annotation.
 * </p>
 */
@ApplicationPath("/rest")
public class JaxRsActivator extends Application {
	/* class body intentionally left blank */


	public JaxRsActivator() {
		// Swagger initialization
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setVersion("1.0.2");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setHost("localhost:8080");
		beanConfig.setBasePath("/JBRestCool/rest");
		beanConfig.setResourcePackage("atlas.cool.rest.web");
		Info info = new Info();
		info.setDescription("REST services for COOL access.");
		beanConfig.setInfo(info);
		beanConfig.setScan(true);
	}


	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<Class<?>>();

		resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
		resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		addRestResourceClasses(resources);
		return resources;
	}

	private void addRestResourceClasses(Set<Class<?>> resources) {
		resources.add(CoolResourceRESTService.class);
//		resources.add(CoolResourceRESTJsonService.class);
		resources.add(ComaRESTService.class);
//		resources.add(ComaRESTJsonService.class);
		resources.add(CoolGtagRESTService.class);
//		resources.add(CoolGtagRESTJsonService.class);
		resources.add(CoolCherryPyResourceRESTService.class);
		resources.add(ComaCoolResourceRESTService.class);
	}
}
