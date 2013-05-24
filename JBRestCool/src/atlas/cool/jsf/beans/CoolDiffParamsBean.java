/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.dao.CoolDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.GtagTagDiffType;
import atlas.cool.rest.model.GtagType;

@Named("cooldiffparams")
@SessionScoped
/**
 * @author formica
 *
 */
public class CoolDiffParamsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -46660747815270546L;

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolParamsBean coolparams;

	@Inject
	private Event<GtagType> changeGtagAselection;
	@Inject
	private Event<GtagType> changeGtagBselection;

	
	String selGtagA = null;
	String selGtagB = null;
	
	List<GtagTagDiffType> gtagDiffs = null;

	/**
	 * 
	 */
	public CoolDiffParamsBean() {
		// TODO Auto-generated constructor stub
	}


	public void retrieveGtagsDiffData() {
		try {
			log.info("Retrieving gtags differences for :"+coolparams.getSchemaName()+ " "+ coolparams.getDbName()
					+" "+selGtagA+" "+selGtagB+"!");
			gtagDiffs = cooldao.retrieveGtagsDiffFromSchemaAndDb(coolparams.getSchemaName()+"%", 
					coolparams.getDbName(), selGtagA, selGtagB);
			log.info("Retrieved gtags diffs of size :"+gtagDiffs.size());
		} catch (CoolIOException e) {
//			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @return the selGtagA
	 */
	public String getSelGtagA() {
		return selGtagA;
	}


	/**
	 * @param selGtagA the selGtagA to set
	 */
	public void setSelGtagA(String selGtagA) {
		log.info("Setting selected gtag A "+selGtagA);
		this.selGtagA = selGtagA;
	}


	/**
	 * @return the selGtagB
	 */
	public String getSelGtagB() {
		return selGtagB;
	}


	/**
	 * @param selGtagB the selGtagB to set
	 */
	public void setSelGtagB(String selGtagB) {
		log.info("Setting selected gtag B "+selGtagB);
		this.selGtagB = selGtagB;
	}


	/**
	 * @return the gtagDiffs
	 */
	public List<GtagTagDiffType> getGtagDiffs() {
		return gtagDiffs;
	}	
	
	
}
