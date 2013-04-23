/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.rest.model.GtagType;


@Named("gtagcolums")
@SessionScoped
/**
 * @author formica
 *
 */
public class GtagColumns extends AbstractColumnHelper<GtagType> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -895615938585231195L;

	@Inject
	private Logger log;
	
	/**
	 * 
	 */
	public GtagColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init gtag columns....");
		generator = new ColumnsGenerator<GtagType>(GtagType.class);
		initAllColumns();
		log.info("gtag type columns = "+columns.size());
		applyExclusionPattern();
	}
	

}
