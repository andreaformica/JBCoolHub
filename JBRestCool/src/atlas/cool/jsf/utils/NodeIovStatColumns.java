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

import atlas.cool.rest.model.IovStatType;
import atlas.cool.rest.model.NodeType;


@Named("iovstatcolums")
@RequestScoped
/**
 * @author formica
 *
 */
public class NodeIovStatColumns extends AbstractColumnHelper<IovStatType> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4396836775405065058L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public NodeIovStatColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init node columns....");
		generator = new ColumnsGenerator<IovStatType>(IovStatType.class);
		generator.addMethod("coolIovMinSince","getCoolIovMinSince");
		generator.addMethod("coolIovMaxSince","getCoolIovMaxSince");
		
		initAllColumns();
		log.info("Initialized list of node columns for selection...."+allcolumns.size());
		exclusionPattern="^node.*|^tag.*|dbName|schemaName";
		applyExclusionPattern();
	}
}
