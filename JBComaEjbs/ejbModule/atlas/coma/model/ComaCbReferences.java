package atlas.coma.model;

// Generated Apr 22, 2013 12:14:54 AM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * ComaCbReferences generated by hbm2java.
 */
@Entity
@Table(name = "COMA_CB_REFERENCES", schema = "ATLAS_TAGS_METADATA")
public class ComaCbReferences implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4792780746148252761L;
	private BigDecimal cbfIndex;
	private ComaCbNodes comaCbNodes;
	private String folderIovtablename;
	private String folderTagtablename;
	private String folderIov2tagtablename;
	private String folderChanneltablename;
	private String folderPayloadExtref;
	private Date comaInsDate;
	private Date comaUpdDate;

	/**
	 * 
	 */
	public ComaCbReferences() {
	}

	/**
	 * @param comaCbNodes
	 */
	public ComaCbReferences(final ComaCbNodes comaCbNodes) {
		this.comaCbNodes = comaCbNodes;
	}

	/**
	 * @param comaCbNodes
	 * @param folderIovtablename
	 * @param folderTagtablename
	 * @param folderIov2tagtablename
	 * @param folderChanneltablename
	 * @param folderPayloadExtref
	 * @param comaInsDate
	 * @param comaUpdDate
	 */
	public ComaCbReferences(final ComaCbNodes comaCbNodes,
			final String folderIovtablename, final String folderTagtablename,
			final String folderIov2tagtablename, final String folderChanneltablename,
			final String folderPayloadExtref, final Date comaInsDate,
			final Date comaUpdDate) {
		this.comaCbNodes = comaCbNodes;
		this.folderIovtablename = folderIovtablename;
		this.folderTagtablename = folderTagtablename;
		this.folderIov2tagtablename = folderIov2tagtablename;
		this.folderChanneltablename = folderChanneltablename;
		this.folderPayloadExtref = folderPayloadExtref;
		this.comaInsDate = comaInsDate;
		this.comaUpdDate = comaUpdDate;
	}

	/**
	 * @return
	 */
	@GenericGenerator(name = "generator", strategy = "foreign", parameters = @Parameter(name = "property", value = "comaCbNodes"))
	@Id
	@GeneratedValue(generator = "generator")
	@Column(name = "CBF_INDEX", nullable = false, precision = 38, scale = 0)
	public final BigDecimal getCbfIndex() {
		return cbfIndex;
	}

	public void setCbfIndex(final BigDecimal cbfIndex) {
		this.cbfIndex = cbfIndex;
	}

	@OneToOne(fetch = FetchType.LAZY)
	public ComaCbNodes getComaCbNodes() {
		return comaCbNodes;
	}

	public void setComaCbNodes(final ComaCbNodes comaCbNodes) {
		this.comaCbNodes = comaCbNodes;
	}

	@Column(name = "FOLDER_IOVTABLENAME")
	public String getFolderIovtablename() {
		return folderIovtablename;
	}

	public void setFolderIovtablename(final String folderIovtablename) {
		this.folderIovtablename = folderIovtablename;
	}

	@Column(name = "FOLDER_TAGTABLENAME")
	public String getFolderTagtablename() {
		return folderTagtablename;
	}

	public void setFolderTagtablename(final String folderTagtablename) {
		this.folderTagtablename = folderTagtablename;
	}

	@Column(name = "FOLDER_IOV2TAGTABLENAME")
	public String getFolderIov2tagtablename() {
		return folderIov2tagtablename;
	}

	public void setFolderIov2tagtablename(final String folderIov2tagtablename) {
		this.folderIov2tagtablename = folderIov2tagtablename;
	}

	@Column(name = "FOLDER_CHANNELTABLENAME")
	public String getFolderChanneltablename() {
		return folderChanneltablename;
	}

	public void setFolderChanneltablename(final String folderChanneltablename) {
		this.folderChanneltablename = folderChanneltablename;
	}

	@Column(name = "FOLDER_PAYLOAD_EXTREF")
	public String getFolderPayloadExtref() {
		return folderPayloadExtref;
	}

	public void setFolderPayloadExtref(final String folderPayloadExtref) {
		this.folderPayloadExtref = folderPayloadExtref;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "COMA_INS_DATE", length = 7)
	public Date getComaInsDate() {
		return comaInsDate;
	}

	public void setComaInsDate(final Date comaInsDate) {
		this.comaInsDate = comaInsDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "COMA_UPD_DATE", length = 7)
	public Date getComaUpdDate() {
		return comaUpdDate;
	}

	public void setComaUpdDate(final Date comaUpdDate) {
		this.comaUpdDate = comaUpdDate;
	}

}
