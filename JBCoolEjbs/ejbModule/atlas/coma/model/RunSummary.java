/**
 * @author formica
 */
package atlas.coma.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class RunSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6038490075674366239L;
	
	private Long runNumber = null;
	private Date since = null;
	private Date till = null;
	private String type = null;
	private String typename = null;
	private String dataSource = null;
	private String configSource = null;
	private String partition = null;
	private String filenameTag = null;
	private String period_name = null;
	private Long detectorMask = null;
	private Long l1Events = null;
	private Long l2Events = null;
	private Long efEvents = null;
	private BigDecimal runlumi = null;
	private Long recordedEvents = null;
	private Long slb = null;
	private Long elb = null;
	private Integer nlb = null;

	/**
	 * 
	 */
	public RunSummary() {
		super();
	}

	/**
	 * @param runNumber
	 * @param since
	 * @param till
	 * @param magnets
	 */
	public RunSummary(final Long runNumber, final Date since, final Date till) {
		super();
		this.runNumber = runNumber;
		this.since = since;
		this.till = till;
	}

	/**
	 * @param runNumber
	 * @param since
	 * @param till
	 * @param type
	 * @param dataSource
	 * @param partition
	 * @param filenameTag
	 * @param detectorMask
	 * @param l1Events
	 * @param l2Events
	 * @param eFEvents
	 * @param recordedEvents
	 */
	public RunSummary(final Long runNumber, final Date since, final Date till, final String type,
			final String dataSource, final String partition, final String filenameTag,
			final Long detectorMask, final Long l1Events, final Long l2Events, final Long eFEvents,
			final Long recordedEvents) {
		super();
		this.runNumber = runNumber;
		this.since = since;
		this.till = till;
		this.type = type;
		this.dataSource = dataSource;
		this.partition = partition;
		this.filenameTag = filenameTag;
		this.detectorMask = detectorMask;
		this.l1Events = l1Events;
		this.l2Events = l2Events;
		this.efEvents = eFEvents;
		this.recordedEvents = recordedEvents;
	}

	/**
	 * @param runNumber
	 * @param since
	 * @param till
	 * @param type
	 * @param typename
	 * @param dataSource
	 * @param configSource
	 * @param partition
	 * @param filenameTag
	 * @param period_name
	 * @param detectorMask
	 * @param l1Events
	 * @param l2Events
	 * @param eFEvents
	 * @param recordedEvents
	 * @param slb
	 * @param elb
	 * @param nlb
	 */
	public RunSummary(final Long runNumber, final Date since, final Date till, final String type,
			final String typename, final String dataSource, final String configSource,
			final String partition, final String filenameTag, final String period_name,
			final Long detectorMask, final Long l1Events, final Long l2Events, final Long eFEvents,
			final Long recordedEvents, final Long slb,
			final Long elb, final Integer nlb,final BigDecimal integ_lumi) {
		this.runNumber = runNumber;
		this.since = since;
		this.till = till;
		this.type = type;
		this.typename = typename;
		this.dataSource = dataSource;
		this.configSource = configSource;
		this.partition = partition;
		this.filenameTag = filenameTag;
		this.period_name = period_name;
		this.detectorMask = detectorMask;
		this.l1Events = l1Events;
		this.l2Events = l2Events;
		this.efEvents = eFEvents;
		this.runlumi = integ_lumi;
		this.recordedEvents = recordedEvents;
		this.slb = slb;
		this.elb = elb;
		this.nlb = nlb;
	}

	/**
	 * @return the runNumber
	 */
	public final Long getRunNumber() {
		return runNumber;
	}

	/**
	 * @param runNumber
	 *            the runNumber to set
	 */
	public final void setRunNumber(final Long runNumber) {
		this.runNumber = runNumber;
	}

	/**
	 * @return the since
	 */
	public final Date getSince() {
		return since;
	}

	/**
	 * @param since
	 *            the since to set
	 */
	public final void setSince(final Date since) {
		this.since = since;
	}

	/**
	 * @return the till
	 */
	public final Date getTill() {
		return till;
	}

	/**
	 * @param till
	 *            the till to set
	 */
	public void setTill(final Date till) {
		this.till = till;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(final String type) {
		this.type = type;
	}

	/**
	 * @return the dataSource
	 */
	public String getDataSource() {
		return dataSource;
	}

	/**
	 * @param dataSource
	 *            the dataSource to set
	 */
	public void setDataSource(final String dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * @return the partition
	 */
	public String getPartition() {
		return partition;
	}

	/**
	 * @param partition
	 *            the partition to set
	 */
	public void setPartition(final String partition) {
		this.partition = partition;
	}

	/**
	 * @return the detectorMask
	 */
	public Long getDetectorMask() {
		return detectorMask;
	}

	/**
	 * @param detectorMask
	 *            the detectorMask to set
	 */
	public void setDetectorMask(final Long detectorMask) {
		this.detectorMask = detectorMask;
	}

	/**
	 * @return the l1Events
	 */
	public Long getL1Events() {
		return l1Events;
	}

	/**
	 * @param l1Events
	 *            the l1Events to set
	 */
	public void setL1Events(final Long l1Events) {
		this.l1Events = l1Events;
	}

	/**
	 * @return the l2Events
	 */
	public Long getL2Events() {
		return l2Events;
	}

	/**
	 * @param l2Events
	 *            the l2Events to set
	 */
	public void setL2Events(final Long l2Events) {
		this.l2Events = l2Events;
	}

	/**
	 * @return the eFEvents
	 */
	public Long getEfEvents() {
		return efEvents;
	}

	/**
	 * @param eFEvents
	 *            the eFEvents to set
	 */
	public void setEfEvents(final Long eFEvents) {
		this.efEvents = eFEvents;
	}

	/**
	 * @return the recordedEvent
	 */
	public Long getRecordedEvents() {
		return recordedEvents;
	}

	/**
	 * @param recordedEvent
	 *            the recordedEvent to set
	 */
	public void setRecordedEvents(final Long recordedEvents) {
		this.recordedEvents = recordedEvents;
	}

	/**
	 * @return the filenameTag
	 */
	public String getFilenameTag() {
		return filenameTag;
	}

	/**
	 * @param filenameTag
	 *            the filenameTag to set
	 */
	public void setFilenameTag(final String filenameTag) {
		this.filenameTag = filenameTag;
	}

	/**
	 * @return the period_name
	 */
	public final String getPeriod_name() {
		return period_name;
	}

	/**
	 * @param period_name
	 *            the period_name to set
	 */
	public final void setPeriod_name(final String period_name) {
		this.period_name = period_name;
	}

	/**
	 * @return the typename
	 */
	public final String getTypename() {
		return typename;
	}

	/**
	 * @param typename
	 *            the typename to set
	 */
	public final void setTypename(final String typename) {
		this.typename = typename;
	}

	/**
	 * @return the configSource
	 */
	public final String getConfigSource() {
		return configSource;
	}

	/**
	 * @param configSource
	 *            the configSource to set
	 */
	public final void setConfigSource(final String configSource) {
		this.configSource = configSource;
	}

	/**
	 * @return the slb
	 */
	public final Long getSlb() {
		return slb;
	}

	/**
	 * @param slb
	 *            the slb to set
	 */
	public final void setSlb(final Long slb) {
		this.slb = slb;
	}

	/**
	 * @return the elb
	 */
	public final Long getElb() {
		return elb;
	}

	/**
	 * @param elb
	 *            the elb to set
	 */
	public final void setElb(final Long elb) {
		this.elb = elb;
	}

	/**
	 * @return the nlb
	 */
	public final Integer getNlb() {
		return nlb;
	}

	/**
	 * @param nlb
	 *            the nlb to set
	 */
	public final void setNlb(final Integer nlb) {
		this.nlb = nlb;
	}

	
	public BigDecimal getRunlumi() {
		return runlumi;
	}

	public void setRunlumi(BigDecimal runlumi) {
		this.runlumi = runlumi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Run " + runNumber + " : " + since + " - " + till + " " + l1Events;
	}

}
