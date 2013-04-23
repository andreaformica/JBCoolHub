package atlas.cool.rest.utils;


import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 * This class uses CDI to alias Java EE resources, such as the persistence context, to CDI beans
 * 
 * <p>
 * Example injection on a managed bean field:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * private EntityManager em;
 * </pre>
 */
public class WarResources {
   // use @SuppressWarnings to tell IDE to ignore warnings about field not being referenced directly
/*   @SuppressWarnings("unused")
   @Produces
   @PersistenceContext
   private EntityManager em;
   
   @Produces
   public Logger produceLog(InjectionPoint injectionPoint) {
      return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getName());
   }
*/   
	public static String externalwebdir = System.getProperty("user.home")+"/myweb/deploy/web.war";
	
	@Inject
	private Logger log;
	
   @Produces
   @RequestScoped
   public FacesContext produceFacesContext() {
	  log.info("Using face context...");
      return FacesContext.getCurrentInstance();
   }
   
}
