package atlas.coma.model;
// Generated Apr 22, 2013 12:14:54 AM by Hibernate Tools 3.4.0.CR1


import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ComaCbRuns generated by hbm2java
 */
@Entity
@Table(name="COMA_CB_RUNS"
    ,schema="ATLAS_TAGS_METADATA"
)
public class ComaCbRuns  implements java.io.Serializable {


     private BigDecimal runIndex;
     private String runType;
     private String filenameTag;
     private String recordingEnabled;
     private String cleanStop;
     private Date startTime;
     private Date endTime;
     private Double duration;
     private Boolean cbrunFlag;

    public ComaCbRuns() {
    }

	
    public ComaCbRuns(BigDecimal runIndex) {
        this.runIndex = runIndex;
    }
    public ComaCbRuns(BigDecimal runIndex, String runType, String filenameTag, String recordingEnabled, String cleanStop, Date startTime, Date endTime, Double duration, Boolean cbrunFlag) {
       this.runIndex = runIndex;
       this.runType = runType;
       this.filenameTag = filenameTag;
       this.recordingEnabled = recordingEnabled;
       this.cleanStop = cleanStop;
       this.startTime = startTime;
       this.endTime = endTime;
       this.duration = duration;
       this.cbrunFlag = cbrunFlag;
    }
   
     @Id 

    
    @Column(name="RUN_INDEX", nullable=false, precision=38, scale=0)
    public BigDecimal getRunIndex() {
        return this.runIndex;
    }
    
    public void setRunIndex(BigDecimal runIndex) {
        this.runIndex = runIndex;
    }

    
    @Column(name="RUN_TYPE")
    public String getRunType() {
        return this.runType;
    }
    
    public void setRunType(String runType) {
        this.runType = runType;
    }

    
    @Column(name="FILENAME_TAG")
    public String getFilenameTag() {
        return this.filenameTag;
    }
    
    public void setFilenameTag(String filenameTag) {
        this.filenameTag = filenameTag;
    }

    
    @Column(name="RECORDING_ENABLED", length=10)
    public String getRecordingEnabled() {
        return this.recordingEnabled;
    }
    
    public void setRecordingEnabled(String recordingEnabled) {
        this.recordingEnabled = recordingEnabled;
    }

    
    @Column(name="CLEAN_STOP", length=10)
    public String getCleanStop() {
        return this.cleanStop;
    }
    
    public void setCleanStop(String cleanStop) {
        this.cleanStop = cleanStop;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="START_TIME", length=7)
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Temporal(TemporalType.DATE)
    @Column(name="END_TIME", length=7)
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    
    @Column(name="DURATION", precision=63, scale=0)
    public Double getDuration() {
        return this.duration;
    }
    
    public void setDuration(Double duration) {
        this.duration = duration;
    }

    
    @Column(name="CBRUN_FLAG", precision=1, scale=0)
    public Boolean getCbrunFlag() {
        return this.cbrunFlag;
    }
    
    public void setCbrunFlag(Boolean cbrunFlag) {
        this.cbrunFlag = cbrunFlag;
    }




}


