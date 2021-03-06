/**
 * 
 */
package atlas.cool.rest.web.json;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.impl.CoolRESTImpl;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;
import atlas.cool.rest.utils.FilteredResponse;
import atlas.cool.rest.web.ICoolREST;

import org.jboss.resteasy.annotations.GZIP;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the Cool tables
 * via PL/SQL. It is the same interface as @See CoolResourceRESTService but the
 * output is in JSON.
 * 
 * <p>
 * The base URL used by the following methods starts with
 * </p>
 * <p>
 * <b>URL: https://hostname:port/JBRestCool/rest/plsqlcooljson/</b>
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
 */
@Path("/plsqlcooljson")
@FilteredResponse
@RequestScoped
/////@Api(value="/plsqlcooljson")
public class CoolResourceRESTJsonService extends CoolRESTImpl implements ICoolREST {

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.CoolRESTImpl#setSort(java.lang.String)
	 */
	@Override
	protected void setSort(final String orderByName) {
		// TODO Auto-generated method stub
		super.setSort(orderByName);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listSchemasInDb(java.lang.String, java.lang.String)
	 */
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/schemas")
////	@ApiOperation(value = "Find list of schemas",
////    notes = "Use incomplete schema name for a global search: ATLAS_COOLOFL_M; the instance name should be complete")
	public List<SchemaType> listSchemasInDb(
			@PathParam("schema") final String schema, @PathParam("db") final String db) {
		return super.listSchemasInDb(schema, db);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNodesInSchema(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/nodes")
	@FilteredResponse
	public List<NodeType> listNodesInSchema(@PathParam("schema") final String schema,
			@PathParam("db") final String db) {
		// TODO Auto-generated method stub
		return super.listNodesInSchema(schema, db);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNodesInSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/nodes")
	@FilteredResponse
	public List<NodeType> listNodesInSchemaNode(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("node") String node) {
		// TODO Auto-generated method stub
		return super.listNodesInSchemaNode(schema, db, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listTagsInNodesSchema(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/tags")
	@FilteredResponse
	public List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node) {
		// TODO Auto-generated method stub
		return super.listTagsInNodesSchema(schema, db, node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listChannelsInNodesSchema(java.lang.
	 * String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/fld/{channel}/channels")
	@FilteredResponse
	public List<ChannelType> listChannelsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("node") final String node,
			@PathParam("channel") final String channame) {
		// TODO Auto-generated method stub
		return super.listChannelsInNodesSchema(schema, db, node, channame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listGlobalTagsTagsInNodesSchema(java
	 * .lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/trace")
	@FilteredResponse
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("gtag") final String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInNodesSchema(schema, db, gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listGlobalTagsTagsInBranchNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/fulltrace")	
	public List<NodeGtagTagType> listGlobalTagsTagsInBranchNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInBranchNodesSchema(schema, db, gtag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#getIovStatPerChannel(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public List<IovType> getIovStatPerChannel(@PathParam("schema") final String schema,
			@PathParam("db") final String db, @PathParam("fld") final String fld,
			@PathParam("tag") final String tag) {
		// TODO Auto-generated method stub
		return super.getIovStatPerChannel(schema, db, fld, tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeAsList(
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag, channel,
				chansel, since, until, timespan);
	}

	
	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNumIovsInNodesSchemaTagAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{num}/lastiovs")
	public NodeType listNumIovsInNodesSchemaTagAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("num") String num) {
		// TODO Auto-generated method stub
		return super.listNumIovsInNodesSchemaTagAsList(schema, db, fld, tag, num);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeSortedAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	@FilteredResponse
	public NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {
		return super.listPayloadInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeSortedAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("sort") final String sort,
			@PathParam("channel") final String channel,
			@PathParam("chansel") final String chansel,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listPayloadInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.cool.rest.web.CoolRESTImpl#listIovsSummaryInNodesSchemaTagRangeAsList
	 * (java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@GZIP
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") final String schema, @PathParam("db") final String db,
			@PathParam("fld") final String fld, @PathParam("tag") final String tag,
			@PathParam("since") final String since,
			@PathParam("until") final String until,
			@PathParam("timespan") final String timespan) {

		return super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				since, until, timespan);
	}

}
