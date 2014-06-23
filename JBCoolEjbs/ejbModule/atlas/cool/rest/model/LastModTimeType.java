/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import atlas.cool.annotations.CoolQuery;
import atlas.cool.rest.utils.TimestampStringFormatter;

/**
 * <p>
 * This POJO represents the Last modification time for every folder and schema
 * in selected in a given DB instance.
 * </p>
 * <p>
 * The Queries defined for this POJO are:<br>
 * <b>QUERY_MODTABLE [cond_tools_pkg]</b><br>
 * This query takes as arguments the SCHEMA, DB, FLD, TBL, TMOD and retrieves a
 * list of matching folders providing the modification time is it is greater
 * than the TMOD date given in input; it uses internally the function
 * cond_tools_pkg.f_GetLastMod_Time(.....)
 * </p>
 * 
 * @author formica
 * @since 2014/06/18
 * 
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = LastModTimeType.QUERY_MODTABLE, query = "select   "
		+ " rownum, "
		+ " schema_name, "
		+ " db_name, "
		+ " node_name, "
		+ " cool_table_name, "
		+ " lastmod_time, "
		+ " seq_currvalue "
		+ " from table(cond_tools_pkg.f_GetLastMod_Time(:schema,:dbname,:fld,:tbl)) where lastmod_time > :tmod "
		+ " order by schema_name,  node_name ", resultClass = LastModTimeType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class LastModTimeType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2087444338434930666L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "SEQ_CURRVALUE")
	private BigDecimal seqId;

	/**
	 * 
	 */
	@Column(name = "SCHEMA_NAME", length = 255)
	private String schemaName;

	/**
	 * 
	 */
	@Column(name = "DB_NAME", length = 50)
	private String dbName;

	/**
	 * 
	 */
	@Column(name = "NODE_NAME", length = 255)
	private String nodeFullpath;

	/**
	 * 
	 */
	@Column(name = "COOL_TABLE_NAME", length = 255)
	private String coolTableName;

	/**
	 * 
	 */
	@Column(name = "LASTMOD_TIME")
	private Timestamp lastModtime;

	@CoolQuery(name = "cond.modtable", params = "schema;dbname;fld;tbl;tmod")
	public static final String QUERY_MODTABLE = "cond.modtable";


	/**
	 * @return the seqId
	 */
	@XmlElement
	public BigDecimal getSeqId() {
		return seqId;
	}

	/**
	 * @return the schemaName
	 */
	@XmlElement
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @return the dbName
	 */
	@XmlElement
	public String getDbName() {
		return dbName;
	}

	/**
	 * @return the nodeFullpath
	 */
	@XmlElement
	public String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @return the coolTableName
	 */
	@XmlElement
	public String getCoolTableName() {
		return coolTableName;
	}

	/**
	 * @return the lastModtime
	 */
	@XmlElement
	@XmlJavaTypeAdapter(atlas.cool.rest.utils.TimestampXmlAdapter.class)
	public Timestamp getLastModtime() {
		return lastModtime;
	}

	/**
	 * @return
	 */
	public final String getLastModtimeStr() {
		if (lastModtime == null) {
			return "";
		}
		final String ret = TimestampStringFormatter.format(
				"yyyy:MM:dd hh:mm:ss", lastModtime);
		// return sysInstime.toString();
		return ret;
	}

	/**
	 * @param rowid
	 *            the rowid to set
	 */
	public void setRowid(Long rowid) {
		this.rowid = rowid;
	}

	
	/**
	 * @param seqId the seqId to set
	 */
	public void setSeqId(BigDecimal seqId) {
		this.seqId = seqId;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @param nodeFullpath
	 *            the nodeFullpath to set
	 */
	public void setNodeFullpath(String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @param coolTableName
	 *            the coolTableName to set
	 */
	public void setCoolTableName(String coolTableName) {
		this.coolTableName = coolTableName;
	}

	/**
	 * @param lastModtime
	 *            the lastModtime to set
	 */
	public void setLastModtime(Timestamp lastModtime) {
		this.lastModtime = lastModtime;
	}

}
