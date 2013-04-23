package atlas.coma.model;
// Generated Apr 22, 2013 12:14:54 AM by Hibernate Tools 3.4.0.CR1


import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ComaCbOwnerInstances generated by hbm2java
 */
@Entity
@Table(name="COMA_CB_OWNER_INSTANCES"
    ,schema="ATLAS_TAGS_METADATA"
)
public class ComaCbOwnerInstances  implements java.io.Serializable {


     private BigDecimal cboiIndex;
     private ComaCbInstances comaCbInstances;
     private ComaCbOnoffs comaCbOnoffs;
     private ComaCbSchemas comaCbSchemas;
     private String ownerName;
     private String coolSchema;
     private Date comaInsDate;
     private Set<ComaCbGtToOis> comaCbGtToOises = new HashSet<ComaCbGtToOis>(0);
     private Set<ComaCbNodes> comaCbNodeses = new HashSet<ComaCbNodes>(0);

    public ComaCbOwnerInstances() {
    }

	
    public ComaCbOwnerInstances(BigDecimal cboiIndex, ComaCbInstances comaCbInstances, ComaCbOnoffs comaCbOnoffs, ComaCbSchemas comaCbSchemas, String ownerName, String coolSchema) {
        this.cboiIndex = cboiIndex;
        this.comaCbInstances = comaCbInstances;
        this.comaCbOnoffs = comaCbOnoffs;
        this.comaCbSchemas = comaCbSchemas;
        this.ownerName = ownerName;
        this.coolSchema = coolSchema;
    }
    public ComaCbOwnerInstances(BigDecimal cboiIndex, ComaCbInstances comaCbInstances, ComaCbOnoffs comaCbOnoffs, ComaCbSchemas comaCbSchemas, String ownerName, String coolSchema, Date comaInsDate, Set<ComaCbGtToOis> comaCbGtToOises, Set<ComaCbNodes> comaCbNodeses) {
       this.cboiIndex = cboiIndex;
       this.comaCbInstances = comaCbInstances;
       this.comaCbOnoffs = comaCbOnoffs;
       this.comaCbSchemas = comaCbSchemas;
       this.ownerName = ownerName;
       this.coolSchema = coolSchema;
       this.comaInsDate = comaInsDate;
       this.comaCbGtToOises = comaCbGtToOises;
       this.comaCbNodeses = comaCbNodeses;
    }
   
     @Id 

    
    @Column(name="CBOI_INDEX", nullable=false, precision=38, scale=0)
    public BigDecimal getCboiIndex() {
        return this.cboiIndex;
    }
    
    public void setCboiIndex(BigDecimal cboiIndex) {
        this.cboiIndex = cboiIndex;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CBI_NAME", nullable=false)
    public ComaCbInstances getComaCbInstances() {
        return this.comaCbInstances;
    }
    
    public void setComaCbInstances(ComaCbInstances comaCbInstances) {
        this.comaCbInstances = comaCbInstances;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CBO_NAME", nullable=false)
    public ComaCbOnoffs getComaCbOnoffs() {
        return this.comaCbOnoffs;
    }
    
    public void setComaCbOnoffs(ComaCbOnoffs comaCbOnoffs) {
        this.comaCbOnoffs = comaCbOnoffs;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="CBS_NAME", nullable=false)
    public ComaCbSchemas getComaCbSchemas() {
        return this.comaCbSchemas;
    }
    
    public void setComaCbSchemas(ComaCbSchemas comaCbSchemas) {
        this.comaCbSchemas = comaCbSchemas;
    }

    
    @Column(name="OWNER_NAME", nullable=false, length=32)
    public String getOwnerName() {
        return this.ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    
    @Column(name="COOL_SCHEMA", unique=true, nullable=false, length=32)
    public String getCoolSchema() {
        return this.coolSchema;
    }
    
    public void setCoolSchema(String coolSchema) {
        this.coolSchema = coolSchema;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="COMA_INS_DATE", length=7)
    public Date getComaInsDate() {
        return this.comaInsDate;
    }
    
    public void setComaInsDate(Date comaInsDate) {
        this.comaInsDate = comaInsDate;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="comaCbOwnerInstances")
    public Set<ComaCbGtToOis> getComaCbGtToOises() {
        return this.comaCbGtToOises;
    }
    
    public void setComaCbGtToOises(Set<ComaCbGtToOis> comaCbGtToOises) {
        this.comaCbGtToOises = comaCbGtToOises;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="comaCbOwnerInstances")
    public Set<ComaCbNodes> getComaCbNodeses() {
        return this.comaCbNodeses;
    }
    
    public void setComaCbNodeses(Set<ComaCbNodes> comaCbNodeses) {
        this.comaCbNodeses = comaCbNodeses;
    }




}


