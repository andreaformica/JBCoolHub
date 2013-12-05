/**
 * 
 */
package atlas.cool.summary.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = CondNodeStats.QUERY_NODESSTATINFO, query = "select "
		+ " rownum id,"
		+ " schema_name, "
		+ " db_name, "
		+ " node_name , "
		+ " tag_name , "
		+ " nchannels , "
		+ " totaliovs , "
		+ " totalholes, "
		+ " minsince, "
		+ " maxuntil, "
		+ " totalruns, "
		+ " totalrunsinhole, "
		+ " minrun, "
		+ " maxrun "
		+ "from table(cond_tools_pkg.f_getstat_nodes(:schema,:dbname,:node))", resultClass = CondNodeStats.class) })
@XmlRootElement
/////@--XmlAccessorType(XmlAccessType.FIELD)
public class CondNodeStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7140556046609827128L;

	@Id
	private Long id;

	@Column(name = "SCHEMA_NAME", length = 255)
	private String schemaName;
	@Column(name = "DB_NAME", length = 50)
	private String dbName;
	@Column(name = "NODE_NAME", length = 255)
	private String nodeName;
	@Column(name = "TAG_NAME", length = 255)
	private String tagName;
	@Column(name = "NCHANNELS", precision = 12)
	private Long nchannels;
	@Column(name = "TOTALIOVS", precision = 12)
	private Long totalIovs;
	@Column(name = "TOTALHOLES", precision = 12)
	private Long totalHoles;
	@Column(name = "MINSINCE", precision = 20, scale = 0)
	private BigDecimal minSince;
	@Column(name = "MAXUNTIL", precision = 20, scale = 0)
	private BigDecimal maxUntil;

	@Column(name = "TOTALRUNS", precision = 12)
	private Long totalRuns;
	@Column(name = "TOTALRUNSINHOLE", precision = 12)
	private Long totalRunsInHole;
	@Column(name = "MINRUN", precision = 12, scale = 0)
	private BigDecimal minRun;
	@Column(name = "MAXRUN", precision = 12, scale = 0)
	private BigDecimal maxRun;

	@CoolQuery(name = "cond.findnodesstat", params = "schema;dbname;node")
	public static final String QUERY_NODESSTATINFO = "cond.findnodesstat";

	/**
	 * 
	 */
	public CondNodeStats() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param schemaName
	 * @param dbName
	 * @param nodeName
	 * @param tagName
	 * @param nchannels
	 * @param totalIovs
	 * @param totalHoles
	 * @param minSince
	 * @param maxUntil
	 * @param totalRuns
	 * @param totalRunsInHole
	 * @param minRun
	 * @param maxRun
	 */
	public CondNodeStats(final String schemaName, final String dbName,
			final String nodeName, final String tagName, final Long nchannels,
			final Long totalIovs, final Long totalHoles, final BigDecimal minSince,
			final BigDecimal maxUntil, final Long totalRuns, final Long totalRunsInHole,
			final BigDecimal minRun, final BigDecimal maxRun) {
		super();
		this.schemaName = schemaName;
		this.dbName = dbName;
		this.nodeName = nodeName;
		this.tagName = tagName;
		this.nchannels = nchannels;
		this.totalIovs = totalIovs;
		this.totalHoles = totalHoles;
		this.minSince = minSince;
		this.maxUntil = maxUntil;
		this.totalRuns = totalRuns;
		this.totalRunsInHole = totalRunsInHole;
		this.minRun = minRun;
		this.maxRun = maxRun;
	}

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
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
		this.dbName = dbName;
	}

	/**
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName
	 *            the nodeName to set
	 */
	public void setNodeName(final String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the tagName
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 *            the tagName to set
	 */
	public void setTagName(final String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the nchannels
	 */
	public Long getNchannels() {
		return nchannels;
	}

	/**
	 * @param nchannels
	 *            the nchannels to set
	 */
	public void setNchannels(final Long nchannels) {
		this.nchannels = nchannels;
	}

	/**
	 * @return the totalIovs
	 */
	public Long getTotalIovs() {
		return totalIovs;
	}

	/**
	 * @param totalIovs
	 *            the totalIovs to set
	 */
	public void setTotalIovs(final Long totalIovs) {
		this.totalIovs = totalIovs;
	}

	/**
	 * @return the totalHoles
	 */
	public Long getTotalHoles() {
		return totalHoles;
	}

	/**
	 * @param totalHoles
	 *            the totalHoles to set
	 */
	public void setTotalHoles(final Long totalHoles) {
		this.totalHoles = totalHoles;
	}

	/**
	 * @return the minSince
	 */
	public BigDecimal getMinSince() {
		return minSince;
	}

	/**
	 * @param minSince
	 *            the minSince to set
	 */
	public void setMinSince(final BigDecimal minSince) {
		this.minSince = minSince;
	}

	/**
	 * @return the maxUntil
	 */
	public BigDecimal getMaxUntil() {
		return maxUntil;
	}

	/**
	 * @param maxUntil
	 *            the maxUntil to set
	 */
	public void setMaxUntil(final BigDecimal maxUntil) {
		this.maxUntil = maxUntil;
	}

	/**
	 * @return the totalRuns
	 */
	public Long getTotalRuns() {
		return totalRuns;
	}

	/**
	 * @param totalRuns
	 *            the totalRuns to set
	 */
	public void setTotalRuns(final Long totalRuns) {
		this.totalRuns = totalRuns;
	}

	/**
	 * @return the totalRunsInHole
	 */
	public Long getTotalRunsInHole() {
		return totalRunsInHole;
	}

	/**
	 * @param totalRunsInHole
	 *            the totalRunsInHole to set
	 */
	public void setTotalRunsInHole(final Long totalRunsInHole) {
		this.totalRunsInHole = totalRunsInHole;
	}

	/**
	 * @return the minRun
	 */
	public BigDecimal getMinRun() {
		return minRun;
	}

	/**
	 * @param minRun
	 *            the minRun to set
	 */
	public void setMinRun(final BigDecimal minRun) {
		this.minRun = minRun;
	}

	/**
	 * @return the maxRun
	 */
	public BigDecimal getMaxRun() {
		return maxRun;
	}

	/**
	 * @param maxRun
	 *            the maxRun to set
	 */
	public void setMaxRun(final BigDecimal maxRun) {
		this.maxRun = maxRun;
	}

	/**
	 * @return the name
	 */
	@XmlElement(name="name")
	public String getName() {
		return nodeName;
	}

	
}
