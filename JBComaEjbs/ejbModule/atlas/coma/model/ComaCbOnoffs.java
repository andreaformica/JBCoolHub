package atlas.coma.model;
// Generated Apr 22, 2013 12:14:54 AM by Hibernate Tools 3.4.0.CR1


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * ComaCbOnoffs generated by hbm2java
 */
@Entity
@Table(name="COMA_CB_ONOFFS"
    ,schema="ATLAS_TAGS_METADATA"
)
public class ComaCbOnoffs  implements java.io.Serializable {


     private String cboName;
     private String cboDesc;
     private Set<ComaCbOwnerInstances> comaCbOwnerInstanceses = new HashSet<ComaCbOwnerInstances>(0);

    public ComaCbOnoffs() {
    }

	
    public ComaCbOnoffs(String cboName) {
        this.cboName = cboName;
    }
    public ComaCbOnoffs(String cboName, String cboDesc, Set<ComaCbOwnerInstances> comaCbOwnerInstanceses) {
       this.cboName = cboName;
       this.cboDesc = cboDesc;
       this.comaCbOwnerInstanceses = comaCbOwnerInstanceses;
    }
   
     @Id 

    
    @Column(name="CBO_NAME", nullable=false, length=10)
    public String getCboName() {
        return this.cboName;
    }
    
    public void setCboName(String cboName) {
        this.cboName = cboName;
    }

    
    @Column(name="CBO_DESC", length=4000)
    public String getCboDesc() {
        return this.cboDesc;
    }
    
    public void setCboDesc(String cboDesc) {
        this.cboDesc = cboDesc;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="comaCbOnoffs")
    public Set<ComaCbOwnerInstances> getComaCbOwnerInstanceses() {
        return this.comaCbOwnerInstanceses;
    }
    
    public void setComaCbOwnerInstanceses(Set<ComaCbOwnerInstances> comaCbOwnerInstanceses) {
        this.comaCbOwnerInstanceses = comaCbOwnerInstanceses;
    }




}


