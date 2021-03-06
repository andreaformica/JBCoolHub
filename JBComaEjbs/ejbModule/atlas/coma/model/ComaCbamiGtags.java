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
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * ComaCbamiGtags generated by hbm2java
 */
@Entity
@Table(name="COMA_CBAMI_GTAGS"
    ,schema="ATLAS_TAGS_METADATA"
)
@NamedQueries( {
	@NamedQuery(name = ComaCbamiGtags.QUERY_AMI_GTAGS, 
			query = " from ComaCbamiGtags amigtag "
			+" where amigtag.tagName like :gtag "
			+" order by dateFirstDataset ")
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)

public class ComaCbamiGtags  implements java.io.Serializable {


     private BigDecimal cbagtIndex;
     private String tagName;
     private String projectPrefix;
     private Boolean isActive;
     private BigDecimal datasetCount;
     private Date dateFirstDataset;
     private Date dateLastDataset;
     private Date comaInsDate;
     private Date comaUpdDate;

     
 	@CoolQuery(name="coma.amigtag",params="gtag;")
 	public static final String QUERY_AMI_GTAGS = "coma.amigtag";

    public ComaCbamiGtags() {
    }

	
    public ComaCbamiGtags(BigDecimal cbagtIndex, String tagName) {
        this.cbagtIndex = cbagtIndex;
        this.tagName = tagName;
    }
    public ComaCbamiGtags(BigDecimal cbagtIndex, String tagName, String projectPrefix, Boolean isActive, BigDecimal datasetCount, Date dateFirstDataset, Date dateLastDataset, Date comaInsDate, Date comaUpdDate) {
       this.cbagtIndex = cbagtIndex;
       this.tagName = tagName;
       this.projectPrefix = projectPrefix;
       this.isActive = isActive;
       this.datasetCount = datasetCount;
       this.dateFirstDataset = dateFirstDataset;
       this.dateLastDataset = dateLastDataset;
       this.comaInsDate = comaInsDate;
       this.comaUpdDate = comaUpdDate;
    }
   
     @Id 

    
    @Column(name="CBAGT_INDEX", nullable=false, precision=38, scale=0)
    public BigDecimal getCbagtIndex() {
        return this.cbagtIndex;
    }
    
    public void setCbagtIndex(BigDecimal cbagtIndex) {
        this.cbagtIndex = cbagtIndex;
    }

    
    @Column(name="TAG_NAME", nullable=false)
    public String getTagName() {
        return this.tagName;
    }
    
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    
    @Column(name="PROJECT_PREFIX", length=32)
    public String getProjectPrefix() {
        return this.projectPrefix;
    }
    
    public void setProjectPrefix(String projectPrefix) {
        this.projectPrefix = projectPrefix;
    }

    
    @Column(name="IS_ACTIVE", precision=1, scale=0)
    public Boolean getIsActive() {
        return this.isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    
    @Column(name="DATASET_COUNT", precision=38, scale=0)
    public BigDecimal getDatasetCount() {
        return this.datasetCount;
    }
    
    public void setDatasetCount(BigDecimal datasetCount) {
        this.datasetCount = datasetCount;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="DATE_FIRST_DATASET", length=7)
    public Date getDateFirstDataset() {
        return this.dateFirstDataset;
    }
    
    public void setDateFirstDataset(Date dateFirstDataset) {
        this.dateFirstDataset = dateFirstDataset;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="DATE_LAST_DATASET", length=7)
    public Date getDateLastDataset() {
        return this.dateLastDataset;
    }
    
    public void setDateLastDataset(Date dateLastDataset) {
        this.dateLastDataset = dateLastDataset;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="COMA_INS_DATE", length=7)
    public Date getComaInsDate() {
        return this.comaInsDate;
    }
    
    public void setComaInsDate(Date comaInsDate) {
        this.comaInsDate = comaInsDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="COMA_UPD_DATE", length=7)
    public Date getComaUpdDate() {
        return this.comaUpdDate;
    }
    
    public void setComaUpdDate(Date comaUpdDate) {
        this.comaUpdDate = comaUpdDate;
    }




}


