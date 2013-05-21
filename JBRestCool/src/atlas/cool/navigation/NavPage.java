/**
 * 
 */
package atlas.cool.navigation;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;



@Named("navPage")
@SessionScoped
/**
 * @author formica
 *
 */
public class NavPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4016006216716263505L;
	private String page = "mainPage";
	private final String defaultpage = "mainPage";

	@Inject
	private FacesContext facesContext;

	@Inject
	private Logger log;
	
	@Inject
	private Event<String> selectedPage;

	/**
	 * 
	 */
	public NavPage() {
		// TODO Auto-generated constructor stub
	}

	public String getLocal() {
		String hostname = "localhost";
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			log.info("Hostname of local machine: " + localMachine.getHostName());
			hostname = localMachine.getHostName();
			}
			catch (java.net.UnknownHostException uhe) { // [beware typo in code sample -dmw]
			// handle exception
			}

		Properties props = System.getProperties();
		for (Object key : props.keySet()) {
			log.info("Found property key "+key+" with value "+props.get(key));
		}
		return hostname+":8080";
	}
	
	public void doNav() {
		log.info("Navigation ....");
		String str = facesContext.getExternalContext().getRequestParameterMap()
				.get("coolpage");
		addMessage("Navigating to " + str);
		//this.page = str;
		checkPage(str);
		selectedPage.fire(page);
	}

	protected void checkPage(String page) {
        try {
			URL _urlpage = FacesContext.getCurrentInstance().getExternalContext()
			            .getResource("/"+page+".xhtml");
			if (_urlpage == null) {
				log.info("Cannot acces page "+page);
				this.page = this.defaultpage;
			} else {
				log.info("page is "+page);
				this.page = page;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.page = this.defaultpage;
		}
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(String page) {
		this.page = page;
	}

	/**
	 * @return the defaultpage
	 */
	public String getDefaultpage() {
		return defaultpage;
	}

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
