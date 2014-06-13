/**
 * 
 */
package atlas.cool.rest.post;

import javax.ws.rs.FormParam;

import atlas.cool.rest.utils.UrlLinkUtils;

/**
 * Gather input argument for tree producer
 * It is used to launch a script to retrieve payload via python and create 
 * a root tree
 * @author formica
 *
 */
public class RootTreeForm {

	@FormParam("schema")	
	private String schemaName;

	@FormParam("folder")	
	private String nodeFullpath;

	@FormParam("db")	
	private String db;
	
	@FormParam("tag")
	private String tagName;

	@FormParam("channel")	
	private String channel;

	@FormParam("since")	
	private String since;

	@FormParam("until")	
	private String until;
	
	@FormParam("tspan")	
	private String tspan;


	/**
	 * 
	 */
	public RootTreeForm() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}


	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}


	/**
	 * @return the nodeFullpath
	 */
	public String getNodeFullpath() {
		return nodeFullpath;
	}


	/**
	 * @param nodeFullpath the nodeFullpath to set
	 */
	public void setNodeFullpath(String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}


	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}


	/**
	 * @param db the db to set
	 */
	public void setDb(String db) {
		this.db = db;
	}


	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}


	/**
	 * @param tagName the tagName to set
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	/**
	 * @return the channel
	 */
	public String getChannel() {
		return channel;
	}


	/**
	 * @param channel the channel to set
	 */
	public void setChannel(String channel) {
		this.channel = channel;
	}


	/**
	 * @return the since
	 */
	public String getSince() {
		return since;
	}


	/**
	 * @param since the since to set
	 */
	public void setSince(String since) {
		this.since = since;
	}


	/**
	 * @return the until
	 */
	public String getUntil() {
		return until;
	}


	/**
	 * @param until the until to set
	 */
	public void setUntil(String until) {
		this.until = until;
	}


	/**
	 * @return the tspan
	 */
	public String getTspan() {
		return tspan;
	}


	/**
	 * @param tspan the tspan to set
	 */
	public void setTspan(String tspan) {
		this.tspan = tspan;
	}

	public String getUrl() {
		String chanqry = channel+"/channel/";
		String timeqry = since+"/"+until+"/"+tspan+"/data/list";
		String link = UrlLinkUtils.createJsonLink(schemaName, db,
				nodeFullpath, tagName,
				chanqry+timeqry);
		return link;
	}
}
