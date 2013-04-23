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

import atlas.cool.rest.model.NodeGtagTagType;


@Named("nodegtagtagcolums")
@SessionScoped
/**
 * @author formica
 *
 */
public class NodeGtagTagColumns extends AbstractColumnHelper<NodeGtagTagType> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2979277932400056362L;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public NodeGtagTagColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init node gtagtag columns....");
		generator = new ColumnsGenerator<NodeGtagTagType>(NodeGtagTagType.class);
		initAllColumns();
		log.info("node gtagtag type columns = "+columns.size());
		log.info("Initialized list of columns for combo selection...."+allcolumns.size());
		exclusionPattern="^gtag.*|.*Id$|^nodeName|dbName";
		applyExclusionPattern();
	}
	
}
