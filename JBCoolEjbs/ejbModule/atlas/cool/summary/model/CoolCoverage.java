/**
 * 
 */
package atlas.cool.summary.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
@Entity
@Table(name = "COOL_COVERAGE", schema = "ATLAS_COND_TOOLS")
@NamedQueries({ @NamedQuery(name = CoolCoverage.QUERY_FINDALL, query = "FROM CoolCoverage "
		+ " WHERE globalTagName like :gtag") })

@NamedNativeQueries({ @NamedNativeQuery(name = CoolCoverage.QUERY_GETLOG, query = "select "
		+ " distinct cool_global_tag_name as global_tag_name, db as dbname,"
		+ " count(distinct schema_name) over (partition by cool_global_tag_name) as n_updated_schemas, "
		+ " count(schema_name) over (partition by cool_global_tag_name) as n_updated_folders, "
		+ " CURRENT_DATE as ins_time, "
		+ " 'none' as cov_comment "
		+ "from ("
		+ "select cool_global_tag_name, schema_name, cool_node_fullpath, db from ATLAS_COND_TOOLS.cool_iov_summary "
		+ " where cool_global_tag_name like :gtag "
		+ " group by cool_global_tag_name, schema_name, cool_node_fullpath, db )", resultClass = CoolCoverage.class) })

@XmlRootElement
// ///@--XmlAccessorType(XmlAccessType.FIELD)
public class CoolCoverage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7140556046609827128L;

	@Id
	@Column(name = "GLOBAL_TAG_NAME", length = 255)
	private String globalTagName;
	@Column(name = "DBNAME", length = 50)
	private String dbName;
	@Column(name = "INS_TIME")
	private Date insTime;
	@Column(name = "N_UPDATED_SCHEMAS")
	private Integer nUpdatedSchemas;
	@Column(name = "N_UPDATED_FOLDERS")
	private Integer nUpdatedFolders;
	@Column(name = "COV_COMMENT", length = 1000)
	private String covComment;

	@CoolQuery(name = "cond.findcov", params = "gtag;")
	public static final String QUERY_FINDALL = "cond.findcov";
	@CoolQuery(name = "cond.findcovlog", params = "gtag;")
	public static final String QUERY_GETLOG = "cond.findcovlog";

	/**
	 * Default ctor.
	 */
	public CoolCoverage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param globalTagName
	 */
	public CoolCoverage(final String globalTagName) {
		super();
		this.globalTagName = globalTagName;
		this.insTime = new Date();
		this.nUpdatedFolders = 0;
		this.nUpdatedSchemas = 0;
		this.covComment = "Empty initialization from ctor";
	}

	/**
	 * @param globalTagName
	 * @param dbName
	 * @param insTime
	 * @param nUpdatedSchemas
	 * @param nUpdatedFolders
	 * @param covComment
	 */
	public CoolCoverage(final String globalTagName, final String dbName,
			final Date insTime, final Integer nUpdatedSchemas,
			final Integer nUpdatedFolders, final String covComment) {
		super();
		this.globalTagName = globalTagName;
		this.dbName = dbName;
		this.insTime = insTime;
		this.nUpdatedSchemas = nUpdatedSchemas;
		this.nUpdatedFolders = nUpdatedFolders;
		this.covComment = covComment;
	}

	/**
	 * @return the globalTagName
	 */
	public String getGlobalTagName() {
		return globalTagName;
	}

	/**
	 * @param globalTagName
	 *            the globalTagName to set
	 */
	public void setGlobalTagName(final String globalTagName) {
		this.globalTagName = globalTagName;
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
	 * @return the insTime
	 */
	public Date getInsTime() {
		return insTime;
	}

	/**
	 * @param insTime
	 *            the insTime to set
	 */
	public void setInsTime(final Date insTime) {
		this.insTime = insTime;
	}

	/**
	 * @return the nUpdatedSchemas
	 */
	public Integer getnUpdatedSchemas() {
		return nUpdatedSchemas;
	}

	/**
	 * @param nUpdatedSchemas
	 *            the nUpdatedSchemas to set
	 */
	public void setnUpdatedSchemas(final Integer nUpdatedSchemas) {
		this.nUpdatedSchemas = nUpdatedSchemas;
	}

	/**
	 * @return the nUpdatedFolders
	 */
	public Integer getnUpdatedFolders() {
		return nUpdatedFolders;
	}

	/**
	 * @param nUpdatedFolders
	 *            the nUpdatedFolders to set
	 */
	public void setnUpdatedFolders(final Integer nUpdatedFolders) {
		this.nUpdatedFolders = nUpdatedFolders;
	}

	/**
	 * @return the covComment
	 */
	public String getCovComment() {
		return covComment;
	}

	/**
	 * @param covComment
	 *            the covComment to set
	 */
	public void setCovComment(final String covComment) {
		this.covComment = covComment;
	}

}
