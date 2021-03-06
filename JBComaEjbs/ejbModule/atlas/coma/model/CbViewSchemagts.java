package atlas.coma.model;

// Generated May 23, 2013 12:11:29 PM by Hibernate Tools 3.4.0.CR1

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * CbViewSchemagts generated by hbm2java
 */
@Entity
@Table(name = "CB_VIEW_SCHEMAGTS", schema = "ATLAS_COND_TOOLS")
public class CbViewSchemagts implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5865149535880821081L;
	private CbViewSchemagtsId id;
	private BigDecimal nodeId;
	private BigDecimal tagId;
	private Boolean gtagLockStatus;
	private String gtagDescription;
	private String sysInstime;
	private Serializable sysdateInstime;

	public CbViewSchemagts() {
	}

	public CbViewSchemagts(CbViewSchemagtsId id, BigDecimal nodeId,
			BigDecimal tagId, Boolean gtagLockStatus, String gtagDescription,
			String sysInstime, Serializable sysdateInstime) {
		this.id = id;
		this.nodeId = nodeId;
		this.tagId = tagId;
		this.gtagLockStatus = gtagLockStatus;
		this.gtagDescription = gtagDescription;
		this.sysInstime = sysInstime;
		this.sysdateInstime = sysdateInstime;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gtagName", column = @Column(name = "GTAG_NAME", nullable = false)),
			@AttributeOverride(name = "schemaName", column = @Column(name = "SCHEMA_NAME", nullable = false, length = 32)),
			@AttributeOverride(name = "dbName", column = @Column(name = "DB_NAME", nullable = false, length = 10)) })
	public CbViewSchemagtsId getId() {
		return this.id;
	}

	public void setId(CbViewSchemagtsId id) {
		this.id = id;
	}

	@Column(name = "NODE_ID", nullable = false, precision = 22, scale = 0)
	public BigDecimal getNodeId() {
		return this.nodeId;
	}

	public void setNodeId(BigDecimal nodeId) {
		this.nodeId = nodeId;
	}

	@Column(name = "TAG_ID", nullable = false, precision = 38, scale = 0)
	public BigDecimal getTagId() {
		return this.tagId;
	}

	public void setTagId(BigDecimal tagId) {
		this.tagId = tagId;
	}

	@Column(name = "GTAG_LOCK_STATUS", nullable = false, precision = 1, scale = 0)
	public Boolean getGtagLockStatus() {
		return this.gtagLockStatus;
	}

	public void setGtagLockStatus(Boolean gtagLockStatus) {
		this.gtagLockStatus = gtagLockStatus;
	}

	@Column(name = "GTAG_DESCRIPTION", nullable = false)
	public String getGtagDescription() {
		return this.gtagDescription;
	}

	public void setGtagDescription(String gtagDescription) {
		this.gtagDescription = gtagDescription;
	}

	@Column(name = "SYS_INSTIME", nullable = false)
	public String getSysInstime() {
		return this.sysInstime;
	}

	public void setSysInstime(String sysInstime) {
		this.sysInstime = sysInstime;
	}

	@Column(name = "SYSDATE_INSTIME", nullable = false)
	public Serializable getSysdateInstime() {
		return this.sysdateInstime;
	}

	public void setSysdateInstime(Serializable sysdateInstime) {
		this.sysdateInstime = sysdateInstime;
	}

}
