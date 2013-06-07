/**
 * 
 */
package atlas.cool.navigation;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;


/**
 * @author formica
 * 
 */
@Named("navPage")
@SessionScoped
public class NavPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4016006216716263505L;
	/**
	 * 
	 */
	private String page = "mainPage";
	/**
	 * 
	 */
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

	/**
	 * @return
	 */
	public String getLocal() {
		String hostname = "localhost";
		try {
			final java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			log.info("Hostname of local machine: " + localMachine.getHostName());
			hostname = localMachine.getHostName();
		} catch (final java.net.UnknownHostException uhe) {
			log.log(Level.SEVERE, "Error in navigation " + uhe.getMessage());
			// [beware typo in
			// code
			// sample -dmw]
			// handle exception
		}
		// final Properties props = System.getProperties();
		// for (final Object key : props.keySet()) {
		// log.info("Found property key " + key + " with value " +
		// props.get(key));
		// }
		return hostname + ":8080";
	}

	/**
	 * 
	 */
	public void doNav() {
		log.info("Navigation ....");
		final String str = facesContext.getExternalContext().getRequestParameterMap()
				.get("coolpage");
		addMessage("Navigating to " + str);
		// this.page = str;
		checkPage(str);
		selectedPage.fire(page);
	}

	/**
	 * @param ppage
	 */
	protected void checkPage(final String ppage) {
		try {
			final URL lurlpage = FacesContext.getCurrentInstance().getExternalContext()
					.getResource("/" + ppage + ".xhtml");
			if (lurlpage == null) {
				log.info("Cannot acces page " + ppage);
				this.page = defaultpage;
			} else {
				log.info("page is " + ppage);
				this.page = ppage;
			}
		} catch (final MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.page = defaultpage;
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
	public void setPage(final String page) {
		this.page = page;
	}

	/**
	 * @return the defaultpage
	 */
	public String getDefaultpage() {
		return defaultpage;
	}

	/**
	 * @param summary
	 */
	public void addMessage(final String summary) {
		final FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
}
