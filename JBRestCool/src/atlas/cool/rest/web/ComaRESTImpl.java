package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.PathParam;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;

@RequestScoped
public class ComaRESTImpl implements IComaREST {

	@Inject
	protected ComaCbDAO comadao;
	@Inject
	protected Logger log;

	public ComaRESTImpl() {
		super();
	}

	public List<CrViewRuninfo> listRuns(@PathParam("runstart") BigDecimal runstart, @PathParam("runend") BigDecimal runend) {
		log.info("Calling listRuns..." + runstart + " " + runend);
		List<CrViewRuninfo> results = null;
		try {
			results = comadao.findRunsInRange(runstart, runend);
		} catch (ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

}