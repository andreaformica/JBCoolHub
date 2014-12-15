/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.chart.CartesianChartModel;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolPayloadDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.jsf.utils.DbList;
import atlas.cool.jsf.utils.PayloadHelperBean;
import atlas.cool.payload.model.CoolPayload;
import atlas.cool.rest.model.ChannelType;
import atlas.cool.rest.model.IovStatType;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;
import atlas.cool.rest.model.SchemaNodeTagType;
import atlas.cool.rest.model.SchemaType;
import atlas.cool.rest.utils.WarResources;
import atlas.cool.web.FileDownloadController;

/**
 * @author formica
 * 
 */
@Named("coolschemaparams")
@SessionScoped
public class CoolSchemaParamsBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1233009248481081187L;

	@Inject
	private Logger log;

	@Inject
	private FacesContext facesContext;

	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolPayloadDAO coolpylddao;
	@Inject
	private PayloadHelperBean pyldHelper;
	@Inject
	private CalendarBean calendar;

	@Inject
	private Event<NodeType> changeNodeselection;
	@Inject
	private Event<SchemaNodeTagType> changeNodeTagselection;
	@Inject
	private Event<SchemaType> changeSchemaselection;

	@Inject
	private PayloadChartBean chart;

	private String schemaName = "";
	private String dbName = "";
	// String nodeName = "";
	// String tagName = "";
	private final String chanName = "";

	private List<String> dbList = null;
	private List<SchemaType> schemaList = null;
	private List<NodeType> nodeList = null;
	private List<NodeType> nodeListFiltered = null;
	private List<SchemaNodeTagType> nodetagList = null;
	private List<IovStatType> nodeiovstatList = null;
	private List<SchemaNodeTagType> nodetagListFiltered = null;
	private List<ChannelType> channelList = null;
	private List<NodeGtagTagType> nodegtagfortagList = null;

	private ChannelType defaultChannel = null;
	private SchemaType selSchema = null;
	private NodeType selNode = null;
	private SchemaNodeTagType selNodeTag = null;

	private ChannelType selChannel = null;
	private String pyldFileName = "none";
	private String selColumn = "";

	private Integer tabIndex = 0;
	private Boolean viewrunlumi = false;
	private Boolean viewtime = false;

	private CoolPayload payload = null;

	private List<String> payloadColumns;

	private List<Map<String, Object>> payloadData;

	private List<Map<String, Object>> filteredPayloadData;

	private FileDownloadController dwn;

	private StreamedContent payloadFile = null;

	private CartesianChartModel linearModel = null;

	private String chartTitle;

	private List<String> payloadNumberColumns;

	/**
	 * 
	 */
	public CoolSchemaParamsBean() {
		// TODO Auto-generated constructor stub
		initDbs();
	}

	/**
	 * 
	 */
	protected void initDbs() {

		if (dbList == null) {
			dbList = DbList.createDbList();
		}
	}

	/**
	 * 
	 */
	protected void initChannels() {
		if (defaultChannel == null) {
			defaultChannel = new ChannelType();
			defaultChannel.setChannelId(new BigDecimal(-1));
			defaultChannel.setChannelName("all");
		}
	}

	/**
	 * 
	 */
	public void retrieveNodeData() {
		try {
			log.info("Retrieving nodes for :" + schemaName + " " + dbName + " " + "!");
			nodeList = cooldao
					.retrieveNodesFromSchemaAndDb(schemaName + "%", dbName, "%");
			log.info("Retrieved nodes of size :" + nodeList.size());
			if (nodeListFiltered != null) {
				nodeListFiltered.clear();
			}
			if (nodetagList != null) {
				nodetagList.clear();
			}
			if (nodetagListFiltered != null) {
				nodetagListFiltered.clear();
			}
			if (channelList != null) {
				channelList.clear();
			}
			if (filteredPayloadData != null) {
				filteredPayloadData.clear();
			}
			selChannel = null;
			selNode = null;
			selNodeTag = null;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void retrieveNodeTagsData() {
		try {
			log.info("Retrieving node tags for :" + schemaName + " " + dbName + " "
					+ selNode.getNodeFullpath() + "!");
			if (selNode.getFolderVersioning() == 0) {
				// this is a single version with no tags
				log.info("Single version folder selected...");
			} else {
				nodetagList = cooldao.retrieveTagsFromNodesSchemaAndDb(schemaName,
						dbName, selNode.getNodeFullpath(), "%");
				log.info("Retrieved nodegtagtag of size :" + nodetagList.size());
			}
			if (nodetagListFiltered != null) {
				nodetagListFiltered.clear();
			}
			selNodeTag = null;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void retrieveNodeGtagForTag() {
		try {
			if (selNodeTag != null) {
				log.info("Retrieving gtags for selected node and tag "
						+ selNodeTag.getTagName());
				nodegtagfortagList = cooldao.retrieveGtagFromSchemaDbNodeTag(schemaName,
						dbName, "%", selNode.getNodeFullpath(), selNodeTag.getTagName());
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void retrieveIovStatData() {
		try {
			final String tagname = selNode.getFolderVersioning() > 0 ? selNodeTag
					.getTagName() : "SINGLE_VERSION";
			log.info("Retrieving iov stats for :" + schemaName + " " + dbName + " "
					+ selNode.getNodeFullpath() + " " + tagname + "!");
			nodeiovstatList = cooldao.retrieveIovStatFromNodeSchemaAndDb(schemaName,
					dbName, getSelNodeName(), tagname);
			for (final IovStatType iovstat : nodeiovstatList) {
				log.info("statistic is " + iovstat.getCoolIovMinSince() + " "
						+ iovstat.getCoolIovMaxSince());
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void savePayload() {
		try {
			if (payload != null) {
				final String tagname = selNodeTag != null ? selNodeTag.getTagName()
						: "SINGLE_VERSION";

				final String fld = selNode.getNodeFullpath();
				final String fldname = fld.replaceAll("/", "_");
				pyldFileName = pyldHelper.dump2FileCoolPayload(payload,
						WarResources.externalwebdir + "/", schemaName + fldname + "_"
								+ tagname);

				dwn = new FileDownloadController(schemaName + fldname + "_" + tagname);
				if (dwn != null) {
					payloadFile = dwn.getFile();
				}

			}
		} catch (final SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the payloadFile
	 */
	public StreamedContent getPayloadFile() {
		final String[] path = pyldFileName.split("/");
		if (path.length <= 1) {
			return null;
		}
		final String fname = path[1];
		log.info("using fname " + fname + " from payload file " + pyldFileName);
		dwn = new FileDownloadController(fname);
		if (dwn != null) {
			payloadFile = dwn.getFile();
		}
		log.info("payload file is " + payloadFile.getContentType() + " "
				+ payloadFile.toString());
		return payloadFile;
	}

	/**
	 * 
	 */
	public void loadSchemas() {
		try {
			log.info("Retrieving schemas for :" + schemaName + " " + dbName);
			schemaList = cooldao.retrieveSchemasFromNodeSchemaAndDb("ATLAS_COOL%",
					dbName, "%");
			log.info("Retrieved schemas of size :" + schemaList.size());
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void loadPayload() {
		try {
			log.info("Calling loadPayload....");
			BigDecimal stime = null;
			BigDecimal etime = null;

			if (payloadData != null) {
				payloadData.clear();
				payloadData = null;
			}
			if (filteredPayloadData != null) {
				filteredPayloadData.clear();
				filteredPayloadData = null;
			}

			if (viewrunlumi) {
				stime = calendar.getCoolRunSince();
				etime = calendar.getCoolRunUntil();
				log.info("run-lumi based iov....");
			} else if (viewtime) {
				stime = calendar.getCoolTimeSince();
				etime = calendar.getCoolTimeUntil();
				log.info("time based iov....");
			}

			if (selNode == null) {
				log.info("Cannot retrieve payload since no node is selected...");
				return;
			}
			// String channelId = selChannel.getChannelId().toString();
			final String channelId = selChannel.getChannelName();
			log.info("Retrieving payload for :" + schemaName + " " + dbName + " "
					+ selNode.getNodeFullpath() + " " + channelId + " " + stime + " "
					+ etime);

			Long chId = null;

			// if (channelId.equals("") || channelId.equals("all")) {
			if (channelId != null && channelId.equals("all")) {
				log.info("no cut on channel ID...");
			} else {
				chId = selChannel.getChannelId().longValue();
			}
			// chId = new Integer(channelId);

			// String tagname = (selNodeTag != null) ? selNodeTag.getTagName()
			// : "SINGLE_VERSION";
			final String tagname = selNode.getFolderVersioning() > 0 ? selNodeTag
					.getTagName() : "SINGLE_VERSION";
			// ResultSet pyld = coolpylddao.getPayloads(schemaName, dbName,
			// selNode.getNodeFullpath(), tagname, stime, etime, chId);
			// if (pyld != null) {

			// payload = pyldHelper.resultSetToPayload(pyld);
			payload = coolpylddao.getPayloadsObj(schemaName, dbName,
					selNode.getNodeFullpath(), tagname, stime, etime, chId);
			if (payload != null) {
				payloadColumns = payload.getColumns();
				payloadNumberColumns = payload.getNumberColumns();
				payloadData = payload.getDataList();
			}
			// tabIndex = 2;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// coolpylddao.remove();
		}
	}

	/**
	 * @return the payloadData
	 */
	public List<Map<String, Object>> getPayloadData() {
		// log.info("Retrieving list of size "+payloadData.size());
		return payloadData;
	}

	/**
	 * @return the filteredPayloadData
	 */
	public List<Map<String, Object>> getFilteredPayloadData() {
		return filteredPayloadData;
	}

	/**
	 * @param filteredPayloadData
	 *            the filteredPayloadData to set
	 */
	public void setFilteredPayloadData(final List<Map<String, Object>> filteredPayloadData) {
		this.filteredPayloadData = filteredPayloadData;
	}

	/**
	 * @return the payloadColumns
	 */
	public List<String> getPayloadColumns() {
		return payloadColumns;
	}

	/**
	 * @return the payloadNumberColumns
	 */
	public List<String> getPayloadNumberColumns() {
		return payloadNumberColumns;
	}

	/**
	 * @return the payloadColumns
	 */
	public List<String> getPayloadChartColumns() {
		if (payload != null) {
			log.info("Retrieve columns from payload...");
			return payload.getNumberColumns();
		} else {
			return new ArrayList<String>();
		}
	}

	/**
	 * @return the selColumn
	 */
	public String getSelColumn() {
		return selColumn;
	}

	/**
	 * @param selColumn
	 *            the selColumn to set
	 */
	public void setSelColumn(final String selColumn) {
		this.selColumn = selColumn;
	}

	/**
	 * @return the dbName
	 */
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(final String dbName) {
		String dbn = dbName;
		if (dbName.contains("%")) {
			dbn = dbName.replaceAll("%", "");
		}
		if (!dbn.equals(this.dbName)) {
			this.dbName = dbn;
			loadSchemas();
		}
	}

	/**
	 * @return the dbList
	 */
	public List<String> getDbList() {
		return dbList;
	}

	/**
	 * @return the schemaList
	 */
	public List<SchemaType> getSchemaList() {
		return schemaList;
	}

	/**
	 * @param schemaList
	 *            the schemaList to set
	 */
	public void setSchemaList(final List<SchemaType> schemaList) {
		this.schemaList = schemaList;
	}

	/**
	 * @return the nodeList
	 */
	public List<NodeType> getNodeList() {
		return nodeList;
	}

	/**
	 * @param nodeList
	 *            the nodeList to set
	 */
	public void setNodeList(final List<NodeType> nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * @return the nodetagList
	 */
	public List<SchemaNodeTagType> getNodetagList() {
		return nodetagList;
	}

	/**
	 * @param nodetagList
	 *            the nodetagList to set
	 */
	public void setNodetagList(final List<SchemaNodeTagType> nodetagList) {
		this.nodetagList = nodetagList;
	}

	/**
	 * @return the nodeiovstatList
	 */
	public List<IovStatType> getNodeiovstatList() {
		return nodeiovstatList;
	}

	/**
	 * @param nodeiovstatList
	 *            the nodeiovstatList to set
	 */
	public void setNodeiovstatList(final List<IovStatType> nodeiovstatList) {
		this.nodeiovstatList = nodeiovstatList;
	}

	/**
	 * @param dbList
	 *            the dbList to set
	 */
	public void setDbList(final List<String> dbList) {
		this.dbList = dbList;
	}

	/**
	 * @return the selNode
	 */
	public NodeType getSelNode() {
		return selNode;
	}

	/**
	 * @param selNode
	 *            the selNode to set
	 */
	public void setSelNode(final NodeType selNode) {
		boolean changeselection = false;
		if (this.selNode != null && this.selNode.equals(selNode)) {
			log.info("Ignoring changes of selection if node tags data are loaded");
			if (nodetagList != null && nodetagList.size() > 0) {
				log.info("Node tags data are loaded");
			} else {
				changeselection = true;
			}
		} else {
			changeselection = true;
		}

		if (changeselection && selNode != null) {
			this.selNode = selNode;
			log.info("Selected Node " + selNode);
			changeNodeselection.fire(this.selNode);
		}
		log.info("After fire changes...selected node is" + selNode);
		this.selNode = selNode;
	}

	protected void cleanAllLists() {
		nodegtagfortagList = null;
		selNode = null;
		selNodeTag = null;
		nodetagList = null;
		nodetagListFiltered = null;
		filteredPayloadData = null;
		payloadData = null;
		selChannel = null;
		channelList = null;
	}

	/**
	 * @param schema
	 */
	public void onSelSchemaChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final SchemaType schema) {
		log.info("Schema selection changed...");
		// retrieveNodeData();
	}

	/**
	 * @param schema
	 */
	public void onSelNodeTagChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final SchemaNodeTagType schema) {
		log.info("Node tag selection changed...");
		// retrieveIovStatData();
		retrieveNodeGtagForTag();
	}

	/**
	 * @param node
	 */
	public void onSelNodeChanged(
			@Observes(notifyObserver = Reception.IF_EXISTS) final NodeType node) {
		try {

			log.info("Node selection changed, new node is " + node);
			if (node == null) {
				// the selected node did not change
				log.info("selected node is null... ");
				return;
			}
			// tagName = "";
			selNodeTag = null;
			retrieveNodeTagsData();
			initChannels();

			if (nodeiovstatList != null) {
				nodeiovstatList.clear();
			}

			// if (selNode.getFolderVersioning() == 0) {
			// retrieveIovStatData();
			// }

			if (channelList != null) {
				channelList.clear();
			}
			channelList = cooldao.retrieveChannelsFromNodeSchemaAndDb(schemaName, dbName,
					node.getNodeFullpath(), chanName);
			if (channelList != null) {
				log.info("Loaded list of " + channelList.size() + " channels ");
				for (final ChannelType chan : channelList) {
					log.info("channel " + chan.getChannelId() + " " + chan.getChannelName());
				}
				channelList.add(defaultChannel);
			}
			// tabIndex = 1;

			if (node.getNodeIovBase().equals("run-lumi")) {
				viewrunlumi = true;
				viewtime = false;
			} else if (node.getNodeIovBase().equals("time")) {
				viewrunlumi = false;
				viewtime = true;
			} else {
				viewrunlumi = true;
				viewtime = false;
			}

			// channelList.add(0,defaultChannel);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the selNodeTag
	 */
	public SchemaNodeTagType getSelNodeTag() {
		return selNodeTag;
	}

	/**
	 * @param selNodeTag
	 *            the selNodeTag to set
	 */
	public void setSelNodeTag(final SchemaNodeTagType selNodeTag) {
		log.info("Selecting nodetag " + selNodeTag);
		if (selNodeTag != null && !selNodeTag.equals(this.selNodeTag)) {
			log.info("Selecting tag " + selNodeTag.getTagName());
			this.selNodeTag = selNodeTag;
			changeNodeTagselection.fire(selNodeTag);
		}
	}

	/**
	 * @return the nodeListFiltered
	 */
	public List<NodeType> getNodeListFiltered() {
		return nodeListFiltered;
	}

	/**
	 * @param nodeListFiltered
	 *            the nodeListFiltered to set
	 */
	public void setNodeListFiltered(final List<NodeType> nodeListFiltered) {
		this.nodeListFiltered = nodeListFiltered;
	}

	/**
	 * @return the nodetagListFiltered
	 */
	public List<SchemaNodeTagType> getNodetagListFiltered() {
		return nodetagListFiltered;
	}

	/**
	 * @param nodetagListFiltered
	 *            the nodetagListFiltered to set
	 */
	public void setNodetagListFiltered(final List<SchemaNodeTagType> nodetagListFiltered) {
		this.nodetagListFiltered = nodetagListFiltered;
	}

	/**
	 * @return the channelList
	 */
	public List<ChannelType> getChannelList() {
		return channelList;
	}

	/**
	 * @param channelList
	 *            the channelList to set
	 */
	public void setChannelList(final List<ChannelType> channelList) {
		this.channelList = channelList;
	}

	/**
	 * @return the selChannel
	 */
	public ChannelType getSelChannel() {
		return selChannel;
	}

	/**
	 * @param selChannel
	 *            the selChannel to set
	 */
	public void setSelChannel(final ChannelType selChannel) {
		log.info("Setting channel selection to " + selChannel);
		if (selChannel != null) {
			this.selChannel = selChannel;
		}
	}

	/**
	 * @return the selSchema
	 */
	public SchemaType getSelSchema() {
		log.info("Selected schema from " + this + " is " + selSchema);
		return selSchema;
	}

	/**
	 * @param selSchema
	 *            the selSchema to set
	 */
	public void setSelSchema(final SchemaType selSchema) {
		log.info("Schema selection is setting " + selSchema + " replacing old "
				+ this.selSchema);
		if (selSchema != null) {
			if (this.selSchema != null && this.selSchema.equals(selSchema)) {
				log.info("Ignore schema changes...it is the same schema "
						+ selSchema.getSchemaName());
			} else {
				log.info("setting schema name..." + selSchema.getSchemaName());
				this.selSchema = selSchema;
				schemaName = selSchema.getSchemaName();
				changeSchemaselection.fire(selSchema);
			}
		}
	}

	/**
	 * @return
	 */
	public String getSelSchemaName() {
		if (selSchema != null) {
			return selSchema.getSchemaName();
		}
		return "none";
	}

	/**
	 * @return
	 */
	public String getSelNodeName() {
		if (selNode != null) {
			return selNode.getNodeFullpath();
		}
		return "none";
	}

	/**
	 * @return
	 */
	public String getSelNodeTagName() {
		if (selNodeTag != null) {
			return selNodeTag.getTagName();
		}
		return "none";
	}

	/**
	 * @return the nodegtagfortagList
	 */
	public List<NodeGtagTagType> getNodegtagfortagList() {
		return nodegtagfortagList;
	}

	/**
	 * @return the pyldFileName
	 */
	public String getPyldFileName() {
		final ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		log.info("Path info is " + externalContext.getRequestPathInfo());
		final HttpServletRequest req = (HttpServletRequest) externalContext.getRequest();
		log.info("Request URI is " + req.getRequestURI());
		log.info("Request Protocol is " + req.getProtocol());
		log.info("Request Server is " + req.getServerName());
		log.info("Request Port is " + req.getServerPort());
		return "http://" + req.getServerName() + ":" + req.getServerPort() + "/"
				+ pyldFileName;
	}

	/**
	 * @return the tabIndex
	 */
	public Integer getTabIndex() {
		return tabIndex;
	}

	/**
	 * @param tabIndex
	 *            the tabIndex to set
	 */
	public void setTabIndex(final Integer tabIndex) {
		this.tabIndex = tabIndex;
	}

	/**
	 * @return the viewrunlumi
	 */
	public Boolean getViewrunlumi() {
		return viewrunlumi;
	}

	/**
	 * @return the viewtime
	 */
	public Boolean getViewtime() {
		return viewtime;
	}

	// Methods to handle charts

	/**
	 * 
	 */
	public void initModel() {
		try {
			log.info("Using selected column " + selColumn);
			chart.setPayload(payload);
			if (filteredPayloadData == null) {
				filteredPayloadData = payloadData;
			}
			chart.setPayloadMap(filteredPayloadData);
			chart.initModelByMap(selColumn);
			linearModel = chart.getLinearModel();
			chartTitle = "Plot " + selColumn + " VS Time";
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void resetChart() {
		chart.resetChart();
	}

	/**
	 * @return
	 */
	public CartesianChartModel getLinearModel() {
		log.info("Get linear model...");
		if (linearModel == null) {
			linearModel = chart.getLinearModel();
		}
		return linearModel;
	}

	/**
	 * @return
	 */
	public boolean getChartHasData() {
		log.info("Evaluate has data ");
		if (linearModel != null && linearModel.getSeries() != null) {
			if (linearModel.getSeries().size() > 0) {
				log.info("returning true ");
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public double getMinY() {
		return chart.getMinY().doubleValue();
	}

	/**
	 * @return
	 */
	public double getMaxY() {
		return chart.getMaxY().doubleValue();
	}

	public Long getMaxX() {
		return chart.getMaxX().getTime();
	}

	/**
	 * @return
	 */
	public Long getMinX() {
		return chart.getMinX().getTime();
	}

	/**
	 * @return the chartTitle
	 */
	/**
	 * @return
	 */
	public String getChartTitle() {
		return chartTitle;
	}

	/**
	 * @return the chartLegend
	 */
	/**
	 * @return
	 */
	public String getChartLegend() {
		return chart.getLegend();
	}

}

/*
 * pyldFileName = pyldHelper.dump2FileResultSet(pyld,
 * WarResources.externalwebdir + "/", schemaName + fldname + "_" + tagname);
 */