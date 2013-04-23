/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.model.RunSummary;


@Named("runcolums")
@RequestScoped
/**
 * @author formica
 *
 */
public class RunColumns extends AbstractColumnHelper<RunSummary> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4396836775405065058L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public RunColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init run columns....");
		generator = new ColumnsGenerator<RunSummary>(RunSummary.class);
		initAllColumns();
		log.info("Initialized list of node columns for selection...."+allcolumns.size());
		exclusionPattern="^detector.*|^folder.*";
		applyExclusionPattern();
	}
}
