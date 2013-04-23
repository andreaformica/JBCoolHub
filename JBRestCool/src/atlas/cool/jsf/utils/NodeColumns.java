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

import atlas.cool.rest.model.NodeType;


@Named("nodecolums")
@RequestScoped
/**
 * @author formica
 *
 */
public class NodeColumns extends AbstractColumnHelper<NodeType> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -4396836775405065058L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public NodeColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init node columns....");
		generator = new ColumnsGenerator<NodeType>(NodeType.class);
		initAllColumns();
		log.info("Initialized list of node columns for selection...."+allcolumns.size());
		exclusionPattern="nodeName|dbName|nodeInstime|nodeIsleaf|nodeId|lastmodDate|^folder.*";
		applyExclusionPattern();
	}
}
