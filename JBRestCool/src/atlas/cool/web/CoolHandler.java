/**
 * 
 */
package atlas.cool.web;

import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolIOException;
import atlas.cool.rest.model.NodeType;
import java.io.Serializable;

/**
 * @author formica
 *
 */
@Named("coolmgr")
@SessionScoped
public class CoolHandler implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4723637103967985737L;

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private CoolDAO cooldao;

	List<NodeType> nodeList;
	
	/**
	 * @return the nodelist
	 */
	@Produces
	@Named
	public List<NodeType> getNodeList() {
		return this.nodeList;
	}

	@PostConstruct
	public void retrieveAllNodes() {
		this.log.info("Retrieving nodes for MUONALIGN in COMP200... ");
		try {
			nodeList = cooldao.retrieveNodesFromSchemaAndDb("ATLAS_COOLOFL_MUONALIGN", "COMP200", "%");
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
