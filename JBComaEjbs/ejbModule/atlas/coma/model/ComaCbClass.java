package atlas.coma.model;

// Generated Apr 22, 2013 12:14:54 AM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * ComaCbClass created by hand to map coma folder classification.
 */
@Entity
@Table(name = "COMA_CB_CLASS", schema = "ATLAS_TAGS_METADATA")
@NamedQueries({
		@NamedQuery(name = ComaCbClass.QUERY_FIND_FOLDER_CLASS, query = "FROM ComaCbClass classification "
				+ "WHERE ( classification.ownerName like :schema AND " 
				+ "classification.nodeFullpath like :node ) ")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ComaCbClass implements java.io.Serializable {


	private BigDecimal cbcIndex;
	private String cbcDataMc;
	private String ownerName;
	private String nodeFullpath;
	private String folderClass;
	private String gtagClass;
	private Date comaInsDate;
	private Date comaUpdDate;

	@CoolQuery(name = "coma.findfolderclass", params = "schema;node")
	public static final String QUERY_FIND_FOLDER_CLASS = "coma.findfolderclass";

	/**
	 * 
	 */
	public ComaCbClass() {
	}

	@Id
	@Column(name = "CBC_INDEX", nullable = false, precision = 38, scale = 0)
	@XmlElement
	public BigDecimal getCbcIndex() {
		return cbcIndex;
	}

	/**
	 * @return
	 */
	@Column(name = "CBC_DATAMC", nullable = false, length = 10)
	@XmlElement
	public String getCbcDataMc() {
		return cbcDataMc;
	}

	
	/**
	 * @return
	 */
	@Column(name = "OWNER_NAME", nullable = false)
	@XmlElement
	public String getOwnerName() {
		return ownerName;
	}


	/**
	 * @return
	 */
	@Column(name = "NODE_FULLPATH", nullable = false)
	@XmlElement
	public String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @return
	 */
	@Column(name = "FOLDER_CLASS", nullable = false)
	@XmlElement
	public String getFolderClass() {
		return folderClass;
	}

	/**
	 * @return
	 */
	@Column(name = "GTAG_CLASS", nullable = false)
	@XmlElement
	public String getGtagClass() {
		return gtagClass;
	}

	/**
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "COMA_INS_DATE", nullable = false, length = 7)
	@XmlElement
	public Date getComaInsDate() {
		return comaInsDate;
	}

	/**
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "COMA_UPD_DATE", nullable = false, length = 7)
	@XmlElement
	public Date getComaUpdDate() {
		return comaUpdDate;
	}

	/**
	 * @param cbcIndex the cbcIndex to set
	 */
	public void setCbcIndex(BigDecimal cbcIndex) {
		this.cbcIndex = cbcIndex;
	}

	/**
	 * @param cbcDataMc the cbcDataMc to set
	 */
	public void setCbcDataMc(String cbcDataMc) {
		this.cbcDataMc = cbcDataMc;
	}

	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * @param nodeFullpath the nodeFullpath to set
	 */
	public void setNodeFullpath(String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @param folderClass the folderClass to set
	 */
	public void setFolderClass(String folderClass) {
		this.folderClass = folderClass;
	}

	/**
	 * @param gtagClass the gtagClass to set
	 */
	public void setGtagClass(String gtagClass) {
		this.gtagClass = gtagClass;
	}

	/**
	 * @param comaInsDate the comaInsDate to set
	 */
	public void setComaInsDate(Date comaInsDate) {
		this.comaInsDate = comaInsDate;
	}

	/**
	 * @param comaUpdDate the comaUpdDate to set
	 */
	public void setComaUpdDate(Date comaUpdDate) {
		this.comaUpdDate = comaUpdDate;
	}

	
}
