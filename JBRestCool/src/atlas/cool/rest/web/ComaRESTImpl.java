package atlas.cool.rest.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
@RequestScoped
public class ComaRESTImpl implements IComaREST {

	@Inject
	protected CoolUtilsDAO coolutilsdao;
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
	 * @see atlas.cool.rest.web.IComaREST#listRuns(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{since}/{until}/{timespan}/runsbyiov")
	public List<CrViewRuninfo> listRuns(@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		// TODO Auto-generated method stub
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			if (trmap.containsKey("runlist")) {
				final List<CrViewRuninfo> runlist = (List<CrViewRuninfo>) trmap
						.get("runlist");
				return runlist;
			}
			return new ArrayList<CrViewRuninfo>();
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.IComaREST#listGtagStatesInRange(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public List<ComaCbGtagStates> listGtagStatesAtTime(
			@PathParam("state") final String state, @PathParam("since") final String since) {
		List<ComaCbGtagStates> results = null;
		try {
			String gtagstate = "%" + state + "%";
			if (state.equals("all")) {
				gtagstate = "%";
			}
			// Time selection
			Date attime = null;
			if (since.equals("now")) {
				attime = new Date();
			} else {
				final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, since,
						"date");
				final BigDecimal lsince = (BigDecimal) trmap.get("since");
				attime = new Date(lsince.longValue() / CoolIov.TO_NANOSECONDS);
			}
			results = comadao.findGtagStateAtTime(gtagstate, attime);
		} catch (final ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.IComaREST#listRuns(java.math.BigDecimal,
	 * java.math.BigDecimal, java.lang.String)
	 */
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend,
			@PathParam("rtype") final String rtype,
			@PathParam("period") final String period) {
		log.info("Calling listRuns..." + runstart + " " + runend);
		List<CrViewRuninfo> results = null;
		try {
			String rt = rtype + "%";
			String pp = period + "%";
			if (rtype.equals("all")) {
				rt = "%";
			}
			if (period.equals("all")) {
				pp = "%";
			}
			results = comadao.findRunsInRange(runstart, runend, rt, pp);
		} catch (final ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.IComaREST#listRuns(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<CrViewRuninfo> listRuns(@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("rtype") final String rtype,
			@PathParam("period") final String period) {

		log.info("Calling listRuns..." + since + " " + until + " using timespan "
				+ timespan + " " + rtype + " " + period);
		List<CrViewRuninfo> results = null;
		try {
			String rt = rtype + "%";
			String pp = period + "%";
			if (rtype.equals("all")) {
				rt = "%";
			}
			if (period.equals("all")) {
				pp = "%";
			}
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getTimeRange(since, until,
					timespan);
			final BigDecimal ds = (BigDecimal) trmap.get("since");
			final BigDecimal du = (BigDecimal) trmap.get("until");
			final Timestamp ssince = new Timestamp(ds.longValue());
			final Timestamp suntil = new Timestamp(du.longValue());
			results = comadao.findRunsInRange(ssince, suntil, rt, pp);
			return results;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}