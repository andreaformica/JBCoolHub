package atlas.coma.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbamiGtags;
import atlas.cool.dao.CoolIOException;
import atlas.cool.dao.CoolRepository;

/**
 * Session Bean implementation class ComaCbBean
 */
@Named
@Stateless(mappedName = "comacb")
@Local(ComaCbDAO.class)
@LocalBean
public class ComaCbBean implements ComaCbDAO {

	@Inject
	private CoolRepository coolrep;
	
	@Inject
	private Logger log;

    /**
     * Default constructor. 
     */
    public ComaCbBean() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ComaCbDAO#findGtagUsageInAmi(String)
     */
    public List<ComaCbamiGtags> findGtagUsageInAmi(String gtagname) throws ComaQueryException {

		Object[] params = new Object[3];
		params[0] = gtagname;
		log.info("Using query "+ComaCbamiGtags.QUERY_AMI_GTAGS+" with "+gtagname);
		List<ComaCbamiGtags> comalist = null;
		try {
			comalist = (List<ComaCbamiGtags>) coolrep.findCoolList(ComaCbamiGtags.QUERY_AMI_GTAGS,params);
		} catch (CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
//		log.info("Retrieved a list of "+nodelist.size()+" nodes");
		return comalist;

    }

}
