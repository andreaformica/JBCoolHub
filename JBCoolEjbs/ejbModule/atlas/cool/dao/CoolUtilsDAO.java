/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.IovType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;

/**
 * @author formica
 * 
 */
public interface CoolUtilsDAO {

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @return
	 * @throws CoolIOException
	 */
	Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @param tag
	 * @param iovbase
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeType> listNodesInSchema(String schema, String db)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 * @throws CoolIOException
	 */
	List<NodeType> listNodesInSchema(String schema, String db, String node)
			throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param node
	 * @return
	 * @throws CoolIOException
	 */
	List<SchemaNodeTagType> listTagsInNodesSchema(String schema,
			String db, String node) throws CoolIOException;


	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @return
	 * @throws CoolIOException
	 */
	List<IovType> getIovStatPerChannel(String schema, String db,
			String fld, String tag) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param channel
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param chanid
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listIovsInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param num
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listIovsLastNumInNodesSchemaTagAsList(String schema,
			String db, String fld, String tag, Integer num) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag1
	 * @param tag2
	 * @param channel
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listIovsDiffInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag1, String tag2, String channel, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag1
	 * @param tag2
	 * @param chanid
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listIovsDiffInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag1, String tag2, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param channel
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, String channel,
			BigDecimal since, BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param chanid
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	NodeType listPayloadInNodesSchemaTagRangeAsList(String schema,
			String db, String fld, String tag, Long chanid, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * @param schema
	 * @param db
	 * @param fld
	 * @param tag
	 * @param since
	 * @param until
	 * @return
	 * @throws CoolIOException
	 */
	Collection<CoolIovSummary> listIovsSummaryInNodesSchemaTagRangeAsList(
			String schema, String db, String fld, String tag, BigDecimal since,
			BigDecimal until) throws CoolIOException;

	/**
	 * <p>Produces an html output from the list of iov summary.</p>
	 * @param iovsummaryColl
	 * @return
	 */
	String dumpIovSummaryAsText(Collection<CoolIovSummary> iovsummaryColl);

	/**
	 * <p>Produces an html output from the list of iov summary.</p>
	 * @param iovsummaryColl
	 * @param since
	 * 			The since time for which we want to start the checking
	 * @param until
	 * 			The until time for which we want to end the checking
	 * @return
	 */
	String dumpIovSummaryAsText(
			Collection<CoolIovSummary> iovsummaryColl, 
			BigDecimal since, BigDecimal until);

	/**
	 * <p>Produces an html output with svg plots from the list of iov summary.</p>
	 * @param iovsummaryColl
	 * @return
	 */
	String dumpIovSummaryAsSvg(Collection<CoolIovSummary> iovsummaryColl);

	/**
	 * <p>Produces an html output with svg plots from the list of iov summary.</p>
	 * @param iovsummaryColl
	 * @param since
	 * 			The since time for which we want to start the checking
	 * @param until
	 * 			The until time for which we want to end the checking
	 * @return
	 */
	String dumpIovSummaryAsSvg(Collection<CoolIovSummary> iovsummaryColl, 
			BigDecimal since, BigDecimal until);

	/**
	 * <p>Produces an html output with the coverage check result.</p>
	 * <p>Uses COMA tables to gather information on runs</p>
	 * @param iovsummaryColl
	 * @return
	 * @throws ComaQueryException
	 */
	String checkHoles(Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException;
	
	/**
	 * <p>
	 * This method is used to parse the timespan string in the URL. Several
	 * format options are then available when asking for input time range. Users
	 * should know in advance, nevertheless, the format of the folder their are
	 * asking for data: time or run-lumi based.
	 * </p>
	 * <p>
	 * List of format depending on timespan field, in bold the type of folder
	 * for which they should be used:
	 * </p>
	 * <p>
	 * <ul>
	 * <li>time : give since and until times in Cool time format (nanoseconds
	 * from Epoch) <b>time</b></li>
	 * <li>date : give since and until times in date format yyyyMMddhhmmss
	 * <b>time</b></li>
	 * <li>runlb : give since and until times in run and lumi bloc as [run]-[lb]
	 * <b>run-lumi</b></li>
	 * <li>runtime: give since and until times in run number, will be converted
	 * in time using start and end of selected runs <b>time</b></li>
	 * <li>daterun: give since and until times in date format yyyyMMddhhmmss,
	 * will be converted in run range <b>run-lumi</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param since
	 * @param until
	 * @param timespan
	 * @return
	 * @throws CoolIOException
	 */
	Map<String, Object> getTimeRange(String since, String until,
			String timespan) throws CoolIOException;

	/**
	 * <p>
	 * This method is the same as the getTimeRange, but using NEMO table instead of COMA.
	 * This method is used to parse the timespan string in the URL. Several
	 * format options are then available when asking for input time range. Users
	 * should know in advance, nevertheless, the format of the folder their are
	 * asking for data: time or run-lumi based.
	 * </p>
	 * <p>
	 * List of format depending on timespan field, in bold the type of folder
	 * for which they should be used:
	 * </p>
	 * <p>
	 * <ul>
	 * <li>time : give since and until times in Cool time format (nanoseconds
	 * from Epoch) <b>time</b></li>
	 * <li>date : give since and until times in date format yyyyMMddhhmmss
	 * <b>time</b></li>
	 * <li>runlb : give since and until times in run and lumi bloc as [run]-[lb]
	 * <b>run-lumi</b></li>
	 * <li>runtime: give since and until times in run number, will be converted
	 * in time using start and end of selected runs <b>time</b></li>
	 * <li>daterun: give since and until times in date format yyyyMMddhhmmss,
	 * will be converted in run range <b>run-lumi</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param since
	 * @param until
	 * @param timespan
	 * @return
	 * @throws CoolIOException
	 */
	Map<String, Object> getNemoTimeRange(String since, String until,
			String timespan) throws CoolIOException;
	
	/**
	 * @param ivr
	 * @param iovbase
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> checkHoles(IovRange ivr, String iovbase)
			throws ComaQueryException;

	/**
	 * @param since
	 * @param until
	 * @param iovbase
	 * @return
	 * @throws ComaQueryException
	 */
	List<CrViewRuninfo> checkHoles(BigDecimal since, BigDecimal until, String iovbase)
			throws ComaQueryException;

}
