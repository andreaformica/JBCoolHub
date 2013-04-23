/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.jsf.utils.ColumnsGenerator.ColumnModel;
import atlas.cool.rest.model.SchemaNodeTagType;


@Named("tagcolums")
@RequestScoped
/**
 * @author formica
 *
 */
public class TagColumns extends AbstractColumnHelper<SchemaNodeTagType> implements Serializable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -2979277932400056362L;

	@Inject
	private Logger log;

	String defaultexclusionPattern="^schema.*|^db.*|^node.*|^folder.*|.*Date$";
	String optionexclusionPattern="^db.*|nodeName|nodeTinstime|nodeId|nodeInstime|nodeIsleaf|nodeDescription|^folder.*|.*Date$";

	/**
	 * 
	 */
	public TagColumns() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init node tag columns....");
		generator = new ColumnsGenerator<SchemaNodeTagType>(SchemaNodeTagType.class);
		initAllColumns();
		log.info("node tag type columns = "+columns.size());
		log.info("Initialized list of columns for combo selection...."+allcolumns.size());
//		exclusionPattern="^schema.*|^db.*|^node.*|^folder.*|.*Date$";
		exclusionPattern=defaultexclusionPattern;
		applyExclusionPattern();
	}	
	
	public List<ColumnModel> getColumnsRich() {
		defaultSelectionExclusion(optionexclusionPattern);
		return getColumns();
	}
}
