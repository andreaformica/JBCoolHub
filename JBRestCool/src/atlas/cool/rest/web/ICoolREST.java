package atlas.cool.rest.web;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;

/**
 * @author formica
 *
 */
public interface ICoolREST {

	/**
	 * <p>
	 * Method : /{schema}/{db}/schemas.
	 * </p>
	 * <p>
	 * It retrieves a list of schemas in XML format, and the number of nodes in that schema
	 * </p>
	 * 
	 * @param schema.
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN, or ATLAS_COOLOFL
	 * @param db.
	 *            The Cool Instance name: e.g. COMP200
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/schemas") 
	List<SchemaType> listSchemasInDb(
			@PathParam("schema") String schema, @PathParam("db") String db);
	/**
	 * <p>
	 * Method : /{schema}/{db}/nodes
	 * </p>
	 * <p>
	 * It retrieves a list of nodes in XML format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @return
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/nodes")
	public abstract List<NodeType> listNodesInSchema(
			@PathParam("schema") String schema, @PathParam("db") String db);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{node}/tags
	 * </p>
	 * <p>
	 * It retrieves a list of tags in XML format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param node
	 *            The node name : a search string like MDT, in this case we do
	 *            not use full folder name
	 * @return An XML list of tags for the given node.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/tags")
	public abstract List<SchemaNodeTagType> listTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{node}/fld/{channel}/channels
	 * </p>
	 * <p>
	 * It retrieves a list of tags in XML format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param node
	 *            The node name : a search string like MDT, in this case we do
	 *            not use full folder name
	 * @param channame
	 * 			  The channel name search string, null argument means every channel, in URL put "all".
	 * @return A list of channels for the given node.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{node:.*}/fld/{channel}/channels")
	public abstract List<ChannelType> listChannelsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("node") String node, @PathParam("channel") String channame);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{gtag}/trace
	 * </p>
	 * <p>
	 * It retrieves a list of tags associated to the given global tag in XML
	 * format.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param gtag
	 *            The Cool global tag : COMCOND-BLKPA-006-09
	 * @return An XML list of schemas and folders and tags which are associated
	 *         to the global tag.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{gtag}/trace")
	public abstract List<NodeGtagTagType> listGlobalTagsTagsInNodesSchema(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("gtag") String gtag);

	/**
	 * <p>
	 * Method : /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs per channel.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @return A JSON list of iovs per channel.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/iovsperchan")
	public abstract List<IovType> getIovStatPerChannel(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag);

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel. The date format is a number
	 * representing a date: yyyyMMddhhmmss ; it does not take fractions of seconds. For details on how the
	 * timespan option is implemented see @See CoolRESTImpl documentation for method getTimeRange.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param channel
	 *            The channel selection, either an ID or a channel name, or <b>all</b> for selecting every channel.
	 * @param chansel
	 * 			  The channel selector: can be either <b>channel</b> or <b>chanid</b> 
	 * @param since
	 *            The COOL since time as a string following the format given by timespan.
	 * @param until
	 *            The COOL until time as a string following the format given by timespan.
	 * @param timespan
	 *            The COOL date format to be used: time, date, runlb.
	 *            
	 * @return A JSON file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public abstract NodeType listIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

	
	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag1:.*}/tag1/{tag2:.*}/tag2/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel selected only if they are different in the 2 tags. 
	 * The date format is a number
	 * representing a date: yyyyMMddhhmmss ; it does not take fractions of seconds. For details on how the
	 * timespan option is implemented see @See CoolRESTImpl documentation for method getTimeRange.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag1
	 *            The tag1 name.
	 * @param tag2
	 *            The tag2 name.
	 * @param channel
	 *            The channel selection, either an ID or a channel name, or <b>all</b> for selecting every channel.
	 * @param chansel
	 * 			  The channel selector: can be either <b>channel</b> or <b>chanid</b> 
	 * @param since
	 *            The COOL since time as a string following the format given by timespan.
	 * @param until
	 *            The COOL until time as a string following the format given by timespan.
	 * @param timespan
	 *            The COOL date format to be used: time, date, runlb.
	 *            
	 * @return A JSON file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag1:.*}/tag1/{tag2:.*}/tag2/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public abstract NodeType listDiffIovsInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, 
			@PathParam("tag1") String tag1,
			@PathParam("tag2") String tag2,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{since}/{until}/{timespan}/iovs/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel, and enabling ad hoc sorting mode.
	 * For details on how the timespan option is implemented 
	 * see @See CoolRESTImpl documentation for method getTimeRange.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param sort
	 * 			  The sort list in format name1-mode/name2-mode/ ... , where mode is either ASC or DESC.	
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/iovs/list")
	public abstract NodeType listIovsInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list
	 * </p>
	 * <p>
	 * It retrieves a list of iovs with their payload in a given range per every channel.
	 * For details on how the timespan option is implemented 
	 * see @See CoolRESTImpl documentation for method getTimeRange.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param channel
	 *            The channel selection, either an ID or a channel name, or <b>all</b> for selecting every channel.
	 * @param chansel
	 * 			  The channel selector: can be either <b>channel</b> or <b>chanid</b> 
	 * @param since
	 *            The COOL since time as a string following the format given by timespan.
	 * @param until
	 *            The COOL until time as a string following the format given by timespan.
	 * @param timespan
	 *            The COOL date format to be used: time, date, runlb.
	 *            
	 * @return A JSON file with payloads for selected channels in a given range.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public abstract NodeType listPayloadInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/data/list
	 * </p>
	 * <p>
	 * It retrieves iovs in a given range for every channel, and enabling ad hoc sorting mode.
	 * For details on how the timespan option is implemented 
	 * see @See CoolRESTImpl documentation for method getTimeRange.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param sort
	 * 			  The sort list in format name1-mode/name2-mode/ ... , where mode is either ASC or DESC.	
	 * @param since
	 *            The COOL since time as a string run-lb.
	 * @param until
	 *            The COOL until time as a string run-lb.
	 * @return An XML file with iovs for all channels.
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{sort:.*}/sort/{channel}/{chansel}/{since}/{until}/{timespan}/data/list")
	public abstract NodeType listPayloadInNodesSchemaTagRangeSortedAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("sort") String sort,
			@PathParam("channel") String channel,
			@PathParam("chansel") String chansel,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

	/**
	 * <p>
	 * Method :
	 * /{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/list
	 * /list
	 * </p>
	 * <p>
	 * It retrieves a summary of iovs in a given range per channel. The time is given
	 * as run number, without the lbs...since we are interested in summary we do not provide here
	 * a better time resolution for selecting the time range.
	 * </p>
	 * 
	 * @param schema
	 *            The Database Schema: e.g. ATLAS_COOLOFL_MUONALIGN
	 * @param db
	 *            The Cool Instance name: e.g. COMP200
	 * @param fld
	 *            The folder name: /MUONALIGN/MDT/BARREL
	 * @param tag
	 *            The tag name.
	 * @param since
	 *            The COOL since time following timespan convention.
	 * @param until
	 *            The COOL until time following timespan convention.
	 * @param timespan
	 *            The timespan type: time (cool format in nanosec), runlb (run-lb), date (yyyyMMddhhmmss)
	 * @return  A JSON page of summary for every channel over the given time range
	 */
	@GET
	@Produces("application/json")
	@Path("/{schema}/{db}/{fld:.*}/fld/{tag:.*}/tag/{since}/{until}/{timespan}/rangesummary/list")
	public abstract Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			@PathParam("schema") String schema, @PathParam("db") String db,
			@PathParam("fld") String fld, @PathParam("tag") String tag,
			@PathParam("since") String since, @PathParam("until") String until,
			@PathParam("timespan") String timespan);

}