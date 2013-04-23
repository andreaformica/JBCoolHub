/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.jsf.utils.ColumnsGenerator.ColumnModel;
import atlas.cool.rest.model.GtagType;
import atlas.cool.rest.model.NodeGtagTagType;


@Named("colhelper")
@SessionScoped
/**
 * @author formica
 *
 */
public class ColumnHelperBean implements Serializable {


	@Inject
	private Logger log;

	private String selectedType;
	
	private List<ColumnModel> gtagscolumns = new ArrayList<ColumnModel>();
	private List<ColumnModel> nodegtagtagscolumns = new ArrayList<ColumnModel>();

    private String columnTemplate = "";  

	private List<ColumnModel> columns = null;
	List<String> VALID_COLUMN_KEYS = null;
	
	/**
	 * 
	 */
	public ColumnHelperBean() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initColumns() {
		log.info("Init gtag columns....");
		gtagscolumns = new ColumnsGenerator<GtagType>(GtagType.class).getColumns();
		log.info("gtag type columns = "+gtagscolumns.size());

		ColumnsGenerator<NodeGtagTagType> colgen = new ColumnsGenerator<NodeGtagTagType>(NodeGtagTagType.class);
//		colgen.maskMethod("gtagId");
//		colgen.maskMethod("gtagName");
//		colgen.maskMethod("gtagDescription");
//		colgen.maskMethod("gtagLockStatus");
//		colgen.maskMethod("dbName");
//		colgen.maskMethod("nodeId");
//		colgen.maskMethod("nodeName");
		nodegtagtagscolumns = colgen.getColumns();
		log.info("nodegtagtag type columns = "+nodegtagtagscolumns.size());	
	}
	
	

	/**
	 * @return the columnTemplate
	 */
	public String getColumnTemplate() {
		if (columnTemplate == null) {
	        for(String columnKey : VALID_COLUMN_KEYS) {  
	            columnTemplate += (" "+columnKey); 
	        }  
		}
		return columnTemplate;
	}

	/**
	 * @param columnTemplate the columnTemplate to set
	 */
	public void setColumnTemplate(String columnTemplate) {
		this.columnTemplate = columnTemplate;
	}

	public void createDynamicColumns() {  
        String[] columnKeys = columnTemplate.split(" "); 
        List<String> selheaders = new ArrayList<String>();
        for(String columnKey : columnKeys) {  
            String key = columnKey.trim();  
              
            if(VALID_COLUMN_KEYS.contains(key)) {  
            	selheaders.add(key);  
            }  
        }  

        for (ColumnModel cmod : columns) {
        	String header = cmod.getHeader();
        	if (selheaders.contains(header)) {
        		// do nothing
        	} else {
        		columns.remove(cmod);
        	}
        }
    } 	
	
	
	protected List<String> validColumnsInModel(List<ColumnModel> amodelList) {
		List<String> valList = new ArrayList<String>();
		for (ColumnModel cmod : amodelList) {
			valList.add(cmod.getHeader());
		}
		return valList;
	}
	
	/**
	 * @return the columns
	 */
	public List<ColumnModel> getColumns() {
		if (selectedType.equals(NodeGtagTagType.class.getName())) {
			columns = nodegtagtagscolumns;
			VALID_COLUMN_KEYS = validColumnsInModel(nodegtagtagscolumns);
		} else if (selectedType.equals(GtagType.class.getName())) {
			columns = gtagscolumns;
			VALID_COLUMN_KEYS = validColumnsInModel(gtagscolumns);
		}
		return columns;
	}

	/**
	 * @return the gtagscolumns
	 */
	public List<ColumnModel> getGtagscolumns() {
		selectedType = GtagType.class.getName();
		return getColumns();
//		return gtagscolumns;
	}

	/**
	 * @return the nodegtagtagscolumns
	 */
	public List<ColumnModel> getNodegtagtagscolumns() {
		selectedType = NodeGtagTagType.class.getName();
		return getColumns();
//		return nodegtagtagscolumns;
	}

	/**
	 * @return the selectedType
	 */
	public String getSelectedType() {
		return selectedType;
	}

	/**
	 * @param selectedType the selectedType to set
	 */
	public void setSelectedType(String selectedType) {
		this.selectedType = selectedType;
	}

	
	
}
