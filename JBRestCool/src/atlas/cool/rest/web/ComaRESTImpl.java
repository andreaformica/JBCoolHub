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
import atlas.coma.dao.ComaRunDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbClass;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;
import atlas.coma.model.NemoRun;
import atlas.cool.dao.ComaDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 * 
 */
@RequestScoped
public class ComaRESTImpl implements IComaREST {

	@Inject
	protected CoolUtilsDAO coolutilsdao;
	@Inject
	protected ComaCbDAO comacbdao;
	@Inject
	protected ComaDAO comadao;
	@Inject
	protected ComaRunDAO comarundao;
	@Inject
	protected Logger log;

	/**
	 * 
	 */
	public ComaRESTImpl() {
		super();
	}
		

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#listNemoRuns(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{start}/{end}/{tspan}/nemoruns")
	public List<NemoRun> listNemoRuns(@PathParam("start") String start, @PathParam("end") String end,
			@PathParam("tspan") String tspan) {
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getNemoTimeRange(start, end,
					tspan);
			
			if (trmap == null) {
				return null;
			}
			for (String key : trmap.keySet()) {
				log.info("Key "+key+" has value "+trmap.get(key).toString());
			}

			Date since = null;
			Date until = null;
			Integer rmin = (Integer)trmap.get("runmin");
			Integer rmax = (Integer)trmap.get("runmax");
			if (rmin == null || rmax == null) {
				since = new Date((Long)trmap.get("since") * 1000L);
				until = new Date((Long)trmap.get("until") * 1000L);
				return comarundao.getNemoRunList(since,until);
			} else {
				return comarundao.getNemoRunList(rmin, rmax);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#getNemoTimeRangeConversion(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/xml")
	@Path("/{start}/{end}/{tspan}/nemotimerange")
	public Map<String, Object> getNemoTimeRangeConversion(@PathParam("start") String start,
			@PathParam("end") String end, @PathParam("tspan") String tspan) {
		try {
			// Time selection
			final Map<String, Object> trmap = coolutilsdao.getNemoTimeRange(start, end,
					tspan);
			
			if (trmap == null) {
				return null;
			}
			for (String key : trmap.keySet()) {
				log.info("Key "+key+" has value "+trmap.get(key).toString());
			}
			return trmap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public List<CrViewRuninfo> listRuns(@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend) {
		log.info("Calling listRuns..." + runstart + " " + runend);
		List<CrViewRuninfo> results = null;
		try {
			results = comacbdao.findRunsInRange(runstart, runend);
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
			results = comacbdao.findGtagState(gtagstate);
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
			results = comacbdao.findGtagStateAtTime(gtagstate, attime);
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
			results = comacbdao.findRunsInRange(runstart, runend, rt, pp);
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
			results = comacbdao.findRunsInRange(ssince, suntil, rt, pp);
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

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#listNodesInSchema(java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		log.info("Calling listNodesInSchema..." + schema + " " + db);
		List<NodeType> results = null;
		try {
			results = comadao.retrieveNodesFromSchemaAndDb(schema + "%", db, "%");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#listTagsInNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node) {
		List<SchemaNodeTagType> results = null;
		String lnode = node;
		try {
		if (node.equals("all")) {
			lnode = "%";
		} else {
			lnode = "%" + node + "%";
		}
		results = comadao.retrieveTagsFromNodesSchemaAndDb(schema + "%", db, lnode, null);
			} catch (final CoolIOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return results;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#listGlobalTagsTagsInNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		log.info("Calling listGlobalTagsTagsInNodesSchema..." + schema + " " + db);
		List<NodeGtagTagType> results = null;
		try {
			results = comadao.retrieveGtagTagsFromSchemaAndDb(schema + "%", db, "%"
					+ gtag + "%");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

    /* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#retrieveTagGtagsFromSchemaAndDb(java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<NodeGtagTagType> retrieveTagGtagsFromSchemaAndDb(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("tag") String tag) throws CoolIOException {
		log.info("Calling retrieveTagGtagsFromSchemaAndDb..." + schema + " " + db);
		List<NodeGtagTagType> results = null;
		try {
			results = comadao.retrieveTagGtagsFromSchemaAndDb(schema + "%", db, "%"
					+ tag + "%");
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}


	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.IComaREST#getClassificationForSchemaAndNode(java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{node:.*}/classification")
	public List<ComaCbClass> getClassificationForSchemaAndNode(@PathParam("schema") String schema,
			@PathParam("node") String node) throws CoolIOException {
		log.info("Calling getClassificationForSchemaAndNode..." + schema + " " + node);
		List<ComaCbClass> results = null;
		try {
			if (node.equals("all"))
				node = "";
			results = comacbdao.findFolderClassification("%"+schema+"%", "%"+node+"%");
		} catch (ComaQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

}