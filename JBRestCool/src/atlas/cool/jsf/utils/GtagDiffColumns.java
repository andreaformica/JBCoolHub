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

import atlas.cool.rest.model.GtagTagDiffType;


@Named("gtagdiffcolums")
@SessionScoped
/**
 * @author formica
 *
 */
public class GtagDiffColumns extends AbstractColumnHelper<GtagTagDiffType> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4396836775405065058L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public GtagDiffColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init gtag diff columns....");
		generator = new ColumnsGenerator<GtagTagDiffType>(GtagTagDiffType.class);
		initAllColumns();
		log.info("Initialized list of columns for selection...."+allcolumns.size());
		exclusionPattern="^gtagName.*";
		applyExclusionPattern();
	}

}
