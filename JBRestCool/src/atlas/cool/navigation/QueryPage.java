package atlas.cool.navigation;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named("queryPage")
@SessionScoped
public class QueryPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8820357766341940028L;


	private String page = "mainPageQuery";
	private String chartpage = "mainPageChart";
	private final String defaultpage = "mainPageQuery";

	@Inject
	private FacesContext facesContext;

	@Inject
	private Logger log;

	public QueryPage() {
		// TODO Auto-generated constructor stub
	}

	public void reset() {
		log.info("Query reset....");
		this.page = defaultpage;
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	
	/**
	 * @return the chartpage
	 */
	public String getChartpage() {
		return chartpage;
	}

	/**
	 * @param chartpage the chartpage to set
	 */
	public void setChartpage(String chartpage) {
		System.out.println("Setting chart page to "+chartpage);
        try {
			URL _urlpage = FacesContext.getCurrentInstance().getExternalContext()
			            .getResource("/"+chartpage+".xhtml");
			if (_urlpage == null) {
				log.info("Cannot acces page "+chartpage);
				this.chartpage = "mainPageChart";
			} else {
				log.info("chart page is "+chartpage);
				this.chartpage = chartpage;
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.chartpage = "mainPageChart";
		}
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(String page) {
		System.out.println("Setting query page to "+page);
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
	 * @return the defaultpage
	 */
	public String getDefaultpage() {
		return defaultpage;
	}

	public void onSelPageChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final String page) {
		this.log.info("Handler received page selection change event...."+page);
		if (page != null) {
			setPage(page+"Query");
			setChartpage(page+"Chart");
		}
	}

	public void addMessage(String summary) {
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO,
				summary, null);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

}
