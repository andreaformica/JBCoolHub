package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;

/**
 * @author formica
 *
 */
@RequestScoped
public class ComaRESTImpl implements IComaREST {

	@Inject
	protected ComaCbDAO comadao;
	@Inject
	protected Logger log;

	/**
	 * 
	 */
	public ComaRESTImpl() {
		super();
	}

	@Override
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend) {
		log.info("Calling listRuns..." + runstart + " " + runend);
		List<CrViewRuninfo> results = null;
		try {
			results = comadao.findRunsInRange(runstart, runend);
		} catch (final ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.IComaREST#listGtagStates(java.lang.String)
	 */
	@Override
	public List<ComaCbGtagStates> listGtagStates(@PathParam("state") final String state) {
		List<ComaCbGtagStates> results = null;
		try {
			String gtagstate = "%" + state + "%";
			if (state.equals("all")) {
				gtagstate = "%";
			}
			results = comadao.findGtagState(gtagstate);
		} catch (final ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

}