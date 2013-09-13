package atlas.cool.summary.model;

// Generated Jul 11, 2013 5:02:12 PM by Hibernate Tools 3.4.0.CR1

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import atlas.cool.annotations.CoolQuery;
import atlas.cool.rest.utils.MyPrinterHandler;

/**
 * CoolIovSummary generated by hbm2java.
 */
@Entity
@Table(name = "COOL_IOV_SUMMARY", schema = "ATLAS_COND_TOOLS")
@NamedQueries({
		@NamedQuery(name = CoolIovSummary.QUERY_FIND_SUMMARY, query = " "
				+ " from CoolIovSummary ciovs where ciovs.db = :db and ciovs.schemaName = :schema "
				+ "      and ciovs.coolNodeFullpath = :node and ciovs.coolTagName = :tag and ciovs.coolChannelId = :chanid"),
		@NamedQuery(name = CoolIovSummary.QUERY_FINDALL_SUMMARY, query = " "
				+ " from CoolIovSummary ciovs where ciovs.db = :db and ciovs.schemaName = :schema "
				+ "      and ciovs.coolNodeFullpath = :node and ciovs.coolTagName = :tag"),
				@NamedQuery(name = CoolIovSummary.QUERY_FIND_SUMMARY_FORSCHEMADB, query = " "
						+ " from CoolIovSummary ciovs where ciovs.db = :db and ciovs.schemaName = :schema "
				)				
})
@SequenceGenerator(name = "COOLIOVSUMMARY_SEQ", sequenceName = "COOLIOVSUMMARY_SEQ", allocationSize = 1)
public class CoolIovSummary implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3188915033771088282L;
	private BigDecimal coolIovsummaryId;
	private String db;
	private String schemaName;
	private String coolNodeFullpath;
	private String coolGlobalTagName;
	private String coolTagName;
	private String coolNodeIovbase;
	private String coolChannelName;
	private BigDecimal coolChannelId;
	private String coolSummary;
	private BigDecimal coolMiniovsince;
	private BigDecimal coolMaxiovsince;
	private BigDecimal coolMiniovuntil;
	private BigDecimal coolMaxiovuntil;
	private BigDecimal coolTotaliovs;
	private Set<CoolIovRanges> coolIovRangeses = new HashSet<CoolIovRanges>(0);

	@CoolQuery(name = "cool.findsummary", params = "schema;db;node;tag;chanid")
	public static final String QUERY_FIND_SUMMARY = "cool.findsummary";
	@CoolQuery(name = "cool.findallsummary", params = "schema;db;node;tag")
	public static final String QUERY_FINDALL_SUMMARY = "cool.findallsummary";
	@CoolQuery(name = "cool.findsummaryforschemadb", params = "schema;db")
	public static final String QUERY_FIND_SUMMARY_FORSCHEMADB = "cool.findsummaryforschemadb";

	/**
	 * 
	 */
	public CoolIovSummary() {
	}

	/**
	 * @param coolIovsummaryId
	 * @param db
	 * @param schemaName
	 * @param coolNodeFullpath
	 * @param coolTagName
	 * @param coolMiniovsince
	 * @param coolMaxiovsince
	 * @param coolMiniovuntil
	 * @param coolMaxiovuntil
	 */
	public CoolIovSummary(final BigDecimal coolIovsummaryId, final String db,
			final String schemaName, final String coolNodeFullpath,
			final String coolTagName, final BigDecimal coolMiniovsince,
			final BigDecimal coolMaxiovsince, final BigDecimal coolMiniovuntil,
			final BigDecimal coolMaxiovuntil) {
		this.coolIovsummaryId = coolIovsummaryId;
		this.db = db;
		this.schemaName = schemaName;
		this.coolNodeFullpath = coolNodeFullpath;
		this.coolTagName = coolTagName;
		this.coolMiniovsince = coolMiniovsince;
		this.coolMaxiovsince = coolMaxiovsince;
		this.coolMiniovuntil = coolMiniovuntil;
		this.coolMaxiovuntil = coolMaxiovuntil;
	}

	/**
	 * @param coolIovsummaryId
	 * @param db
	 * @param schemaName
	 * @param coolNodeFullpath
	 * @param coolTagName
	 * @param coolNodeIovbase
	 * @param coolChannelName
	 * @param coolChannelId
	 * @param coolSummary
	 * @param coolMiniovsince
	 * @param coolMaxiovsince
	 * @param coolMiniovuntil
	 * @param coolMaxiovuntil
	 * @param coolTotaliovs
	 * @param coolIovRangeses
	 */
	public CoolIovSummary(final BigDecimal coolIovsummaryId, final String db,
			final String schemaName, final String coolNodeFullpath,
			final String coolTagName, final String coolNodeIovbase,
			final String coolChannelName, final BigDecimal coolChannelId,
			final String coolSummary, final BigDecimal coolMiniovsince,
			final BigDecimal coolMaxiovsince, final BigDecimal coolMiniovuntil,
			final BigDecimal coolMaxiovuntil, final BigDecimal coolTotaliovs,
			final Set<CoolIovRanges> coolIovRangeses) {
		this.coolIovsummaryId = coolIovsummaryId;
		this.db = db;
		this.schemaName = schemaName;
		this.coolNodeFullpath = coolNodeFullpath;
		this.coolTagName = coolTagName;
		this.coolNodeIovbase = coolNodeIovbase;
		this.coolChannelName = coolChannelName;
		this.coolChannelId = coolChannelId;
		this.coolSummary = coolSummary;
		this.coolMiniovsince = coolMiniovsince;
		this.coolMaxiovsince = coolMaxiovsince;
		this.coolMiniovuntil = coolMiniovuntil;
		this.coolMaxiovuntil = coolMaxiovuntil;
		this.coolTotaliovs = coolTotaliovs;
		this.coolIovRangeses = coolIovRangeses;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "COOLIOVSUMMARY_SEQ")
	@Column(name = "COOL_IOVSUMMARY_ID", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCoolIovsummaryId() {
		return coolIovsummaryId;
	}

	public void setCoolIovsummaryId(final BigDecimal coolIovsummaryId) {
		this.coolIovsummaryId = coolIovsummaryId;
	}

	@Column(name = "DB", nullable = false, length = 20)
	public String getDb() {
		return db;
	}

	public void setDb(final String db) {
		this.db = db;
	}

	@Column(name = "SCHEMA_NAME", nullable = false, length = 60)
	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	@Column(name = "COOL_NODE_FULLPATH", nullable = false)
	public String getCoolNodeFullpath() {
		return coolNodeFullpath;
	}

	public void setCoolNodeFullpath(final String coolNodeFullpath) {
		this.coolNodeFullpath = coolNodeFullpath;
	}

	@Column(name = "COOL_TAG_NAME", nullable = false)
	public String getCoolTagName() {
		return coolTagName;
	}

	public void setCoolTagName(final String coolTagName) {
		this.coolTagName = coolTagName;
	}

	@Column(name = "COOL_NODE_IOVBASE", length = 20)
	public String getCoolNodeIovbase() {
		return coolNodeIovbase;
	}

	public void setCoolNodeIovbase(final String coolNodeIovbase) {
		this.coolNodeIovbase = coolNodeIovbase;
	}

	@Column(name = "COOL_CHANNEL_NAME", length = 50)
	public String getCoolChannelName() {
		return coolChannelName;
	}

	public void setCoolChannelName(final String coolChannelName) {
		this.coolChannelName = coolChannelName;
	}

	@Column(name = "COOL_CHANNEL_ID", precision = 22, scale = 0)
	public BigDecimal getCoolChannelId() {
		return coolChannelId;
	}

	public void setCoolChannelId(final BigDecimal coolChannelId) {
		this.coolChannelId = coolChannelId;
	}

	@Column(name = "COOL_SUMMARY", length = 255)
	public String getCoolSummary() {
		return coolSummary;
	}

	public void setCoolSummary(final String coolSummary) {
		this.coolSummary = coolSummary;
	}

	@Column(name = "COOL_MINIOVSINCE", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCoolMiniovsince() {
		return coolMiniovsince;
	}

	public void setCoolMiniovsince(final BigDecimal coolMiniovsince) {
		this.coolMiniovsince = coolMiniovsince;
	}

	@Column(name = "COOL_MAXIOVSINCE", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCoolMaxiovsince() {
		return coolMaxiovsince;
	}

	public void setCoolMaxiovsince(final BigDecimal coolMaxiovsince) {
		this.coolMaxiovsince = coolMaxiovsince;
	}

	@Column(name = "COOL_MINIOVUNTIL", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCoolMiniovuntil() {
		return coolMiniovuntil;
	}

	public void setCoolMiniovuntil(final BigDecimal coolMiniovuntil) {
		this.coolMiniovuntil = coolMiniovuntil;
	}

	@Column(name = "COOL_MAXIOVUNTIL", nullable = false, precision = 22, scale = 0)
	public BigDecimal getCoolMaxiovuntil() {
		return coolMaxiovuntil;
	}

	public void setCoolMaxiovuntil(final BigDecimal coolMaxiovuntil) {
		this.coolMaxiovuntil = coolMaxiovuntil;
	}

	@Column(name = "COOL_TOTALIOVS", precision = 22, scale = 0)
	public BigDecimal getCoolTotaliovs() {
		return coolTotaliovs;
	}

	public void setCoolTotaliovs(final BigDecimal coolTotaliovs) {
		this.coolTotaliovs = coolTotaliovs;
	}

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "coolIovSummary",cascade = CascadeType.PERSIST)
	public Set<CoolIovRanges> getCoolIovRangeses() {
		return coolIovRangeses;
	}

	public void setCoolIovRangeses(final Set<CoolIovRanges> coolIovRangeses) {
		this.coolIovRangeses = coolIovRangeses;
	}

	/**
	 * @return the coolGlobalTagName
	 */
	@Column(name = "COOL_GLOBAL_TAG_NAME", length = 255)
	public String getCoolGlobalTagName() {
		return coolGlobalTagName;
	}

	/**
	 * @param coolGlobalTagName
	 *            the coolGlobalTagName to set
	 */
	public void setCoolGlobalTagName(final String coolGlobalTagName) {
		this.coolGlobalTagName = coolGlobalTagName;
	}

    @Override
    public String toString() {
            MyPrinterHandler<CoolIovSummary> handler = new MyPrinterHandler<CoolIovSummary>(
                            this, " ");
            try {
                    handler.exclude("getCoolIovRangeses", new Integer("1"));
                    return handler.print();
            } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            return "none";
    }
}