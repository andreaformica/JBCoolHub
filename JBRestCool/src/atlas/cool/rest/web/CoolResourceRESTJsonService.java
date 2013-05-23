/**
 * 
 */
package atlas.cool.rest.web;


import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;


/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the Cool
 * tables via PL/SQL. It is the same interface as @See CoolResourceRESTService but the output is in JSON.
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
@RequestScoped
public class CoolResourceRESTJsonService extends CoolRESTImpl implements ICoolREST {

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#setSort(java.lang.String)
	 */
	@Override
	protected void setSort(String orderByName) {
		// TODO Auto-generated method stub
		super.setSort(orderByName);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listNodesInSchema(java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/nodes")
	public List<NodeType> listNodesInSchema(@PathParam("schema") String schema, @PathParam("db") String db) {
		// TODO Auto-generated method stub
		return super.listNodesInSchema(schema, db);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listTagsInNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public List<SchemaNodeTagType> listTagsInNodesSchema(@PathParam("schema") String schema, @PathParam("db") String db, @PathParam("node") String node) {
		// TODO Auto-generated method stub
		return super.listTagsInNodesSchema(schema, db, node);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listGlobalTagsTagsInNodesSchema(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/trace")
	public List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag) {
		// TODO Auto-generated method stub
		return super.listGlobalTagsTagsInNodesSchema(schema, db, gtag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#getIovStatPerChannel(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public List<IovType> getIovStatPerChannel(@PathParam("schema") String schema, @PathParam("db") String db, @PathParam("fld") String fld,
			@PathParam("tag") String tag) {
		// TODO Auto-generated method stub
		return super.getIovStatPerChannel(schema, db, fld, tag);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag, @PathParam("channel") String channel, @PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until, @PathParam("timespan") String timespan) {

		return super.listIovsInNodesSchemaTagRangeAsList(schema, db, fld, tag, channel,
				chansel, since, until, timespan);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listIovsInNodesSchemaTagRangeSortedAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public NodeType listIovsInNodesSchemaTagRangeSortedAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag, @PathParam("sort") String sort, @PathParam("channel") String channel,
			@PathParam("chansel") String chansel, @PathParam("since") String since, @PathParam("until") String until, @PathParam("timespan") String timespan) {

		return super.listIovsInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag, @PathParam("channel") String channel, @PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until, @PathParam("timespan") String timespan) {
		return super.listPayloadInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				channel, chansel, since, until, timespan);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listPayloadInNodesSchemaTagRangeSortedAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public NodeType listPayloadInNodesSchemaTagRangeSortedAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag, @PathParam("sort") String sort, @PathParam("channel") String channel,
			@PathParam("chansel") String chansel, @PathParam("since") String since, @PathParam("until") String until, @PathParam("timespan") String timespan) {

		return super.listPayloadInNodesSchemaTagRangeSortedAsList(schema, db, fld, tag,
				sort, channel, chansel, since, until, timespan);
	}

	/* (non-Javadoc)
	 * @see atlas.cool.rest.web.CoolRESTImpl#listIovsSummaryInNodesSchemaTagRangeAsList(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/list")
	public Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(@PathParam("schema") String schema,
			@PathParam("db") String db, @PathParam("fld") String fld, @PathParam("tag") String tag, @PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan) {

		return super.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db, fld, tag,
				since, until, timespan);
	}

}
