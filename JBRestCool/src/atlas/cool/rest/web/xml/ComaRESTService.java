/**
 * 
 */
package atlas.cool.rest.web.xml;

import io.swagger.annotations.Api;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import atlas.coma.model.ComaCbClass;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.CrViewRuninfo;
import atlas.coma.model.NemoRun;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.impl.ComaRESTImpl;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.web.IComaREST;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the COMA
 * tables.
 * 
 * <p>
 * The base URL used by the following methods starts with
 * </p>
 * <p>
 * <b>URL: https://hostname:port/JBRestCool/rest/coma/</b>
 * </p>
 * <p>
 * Hostname: voatlas135
 * </p>
 * <p>
 * Port: 8443 [8080]
 * </p>
 * <p>
 * The protocol used is https for the moment
 * </p>
 * 
 * @author formica
 */
@Path("/coma")
@RequestScoped
@Api(value="/coma")
public class ComaRESTService extends ComaRESTImpl implements IComaREST {

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.IComaREST#listNemoRuns(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{start}/{end}/{tspan}/nemoruns")
	public List<NemoRun> listNemoRuns(@PathParam("start") String start,
			@PathParam("end") String end, @PathParam("tspan") String tspan) {
		// TODO Auto-generated method stub
		return super.listNemoRuns(start, end, tspan);
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.ComaRESTImpl#getNemoTimeRangeConversion(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{start}/{end}/{tspan}/nemotimerange")
	public Map<String, Object> getNemoTimeRangeConversion(@PathParam("start") String start,
			@PathParam("end") String end, @PathParam("tspan") String tspan) {
		// TODO Auto-generated method stub
		return super.getNemoTimeRangeConversion(start, end, tspan);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.math.BigDecimal,
	 * java.math.BigDecimal)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{runstart}/{runend}/runs")
	public List<CrViewRuninfo> listRuns(
			@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend) {
		// TODO Auto-generated method stub
		return super.listRuns(runstart, runend);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.ComaRESTImpl#listRuns(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{since}/{until}/{timespan}/runsbyiov")
	public List<CrViewRuninfo> listRunsByIov(@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		// TODO Auto-generated method stub
		return super.listRunsByIov(since, until, timespan);
	}

	/**
	 * @param state
	 * @return
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{state}/gtagstate")
	public List<ComaCbGtagStates> listGtagStates(
			@PathParam("state") final String state) {
		return super.listGtagStates(state);
	}

	/**
	 * @param state
	 * @return
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{state}/{since}/gtagstate")
	public List<ComaCbGtagStates> listGtagStatesAtTime(
			@PathParam("state") final String state,
			@PathParam("since") final String since) {
		return super.listGtagStatesAtTime(state, since);
	}

	/**
	 * @param runstart
	 * @param runend
	 * @param rtype
	 * @param period
	 * @return
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{runstart}/{runend}/{rtype}/{period}/runs")
	public List<CrViewRuninfo> listRunsBetween(
			@PathParam("runstart") final BigDecimal runstart,
			@PathParam("runend") final BigDecimal runend,
			@PathParam("rtype") final String rtype,
			@PathParam("period") final String period) {
		return super.listRunsBetween(runstart, runend, rtype, period);
	}

	/**
	 * @param start
	 * @param end
	 * @return
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{since}/{until}/{timespan}/{rtype}/{period}/runsbyiov")
	public List<CrViewRuninfo> listRunsByIovBetween(@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan,
			@PathParam("rtype") final String rtype,
			@PathParam("period") final String period) {
		return super.listRunsByIovBetween(since, until, timespan, rtype, period);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.IComaREST#listNodesInSchema(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/nodes")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema,
			@PathParam("db") String db) {
		return super.listNodesInSchema(schema, db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.IComaREST#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{node:.*}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node) {
		return super.listTagsInNodesSchema(schema, db, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.IComaREST#listGlobalTagsTagsInNodesSchema(java.lang
	 * .String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{gtag}/trace")
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		return super.listGlobalTagsTagsInNodesSchema(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.ComaRESTImpl#retrieveTagGtagsFromSchemaAndDb(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{db}/{tag}/backtrace")
	public List<NodeGtagTagType> retrieveTagGtagsFromSchemaAndDb(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("tag") String tag) throws CoolIOException {
		return super.retrieveTagGtagsFromSchemaAndDb(schema, db, tag);
	}


	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.ComaRESTImpl#getClassificationForSchemaAndNode(java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces({MediaType.APPLICATION_JSON, 
        MediaType.APPLICATION_XML})
	@Path("/{schema}/{node:.*}/classification")
	public List<ComaCbClass> getClassificationForSchemaAndNode(@PathParam("schema") String schema,
			@PathParam("node") String node) throws CoolIOException {
		return super.getClassificationForSchemaAndNode(schema, node);
	}

}
