/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CoolIovSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6861013465081549944L;

	/**
	 * 
	 */
	private Long chanId;
	/**
	 * 
	 */
	private String channelName;
	/**
	 * 
	 */
	private String schema;
	/**
	 * 
	 */
	private String db;
	/**
	 * 
	 */
	private String node;
	/**
	 * 
	 */
	private String tag;
	/**
	 * 
	 */
	private String summary;
	/**
	 * 
	 */
	private String iovbase;
	/**
	 * 
	 */
	private Long totalIovs = 0L;
	/**
	 * 
	 */
	private Long minsince = CoolIov.COOL_MAX_DATE;
	/**
	 * 
	 */
	private Long maxsince = 0L;
	/**
	 * 
	 */
	private Long minuntil = CoolIov.COOL_MAX_DATE;
	/**
	 * 
	 */
	private Long maxuntil = 0L;

	/**
	 * 
	 */
	@XmlElement(name = "iovrange", type = IovRange.class)
	private Collection<IovRange> iovlist = null;

	/**
	 * 
	 */
	@XmlTransient
	private Map<Long, IovRange> iovs = new TreeMap<Long, IovRange>();

	/**
	 * 
	 */
	public CoolIovSummary() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public CoolIovSummary(final Long chanId) {
		// TODO Auto-generated constructor stub
		this.chanId = chanId;
	}

	/**
	 * @return the chanId
	 */
	public final Long getChanId() {
		return chanId;
	}

	/**
	 * @param chanId
	 *            the chanId to set
	 */
	public final void setChanId(Long chanId) {
		this.chanId = chanId;
	}

	/**
	 * @param since
	 * 	The since time.
	 * @param until
	 * 	The until time.
	 * @param niovs
	 * 	The number of iovs.
	 * @param ishole
	 * 	Is it a hole ?
	 * @throws Exception
	 */
	public final void appendIov(final Long since, 
			final Long until, 
			final Long niovs, 
			final Boolean ishole)
			throws Exception {

		if (iovs.containsKey(since)) {
			// modify existing iov if it is of the same type
			IovRange iov = iovs.get(since);
			if ((!(iov.getIshole()) && !(ishole)) 
						&& iov.getUntil().compareTo(until) < 0) {
				iov.setUntil(until);
				iov.addNIovs(niovs);
			} else {
				throw new Exception(
						"Cannot append an iov of different type for the same since:\n"
								+ "new iov: " + since + " " + until + " " + niovs + " "
								+ ishole + " \n" + "old iov: " + iov.getSince() + " "
								+ iov.getUntil() + " " + iov.getNiovs() + " "
								+ iov.getIshole() + " \n");
			}
		} else {
			//
			Boolean updatediov = false;
			// Search if you find a previous iov of the same type (not a hole)
			Set<Long> sincetimes = iovs.keySet();
			for (Long asince : sincetimes) {
				IovRange aniov = iovs.get(asince);
				if (aniov.getUntil().compareTo(since) == 0) {
					if (!(aniov.getIshole()) && !(ishole)) {
						aniov.setUntil(until);
						aniov.addNIovs(niovs);
						aniov.setUntilCoolStr(CoolIov.getCoolTimeRunLumiString(until,
								iovbase));
						if (aniov.getSince().compareTo(minsince) == 0) {
							minuntil = until;
						}
						updatediov = true;
						// System.out.println("Update old iovrange "+since+" "+until+" "+aniov.getSinceCoolStr()+" "+aniov.getUntilCoolStr()+" "+aniov.getIshole());
					}
				}
			}
			if (!updatediov) {
				// store a new iov
				IovRange iov = new IovRange(since, until, niovs, ishole, iovbase);
				// iov.setIovbase(iovbase);
				iovs.put(since, iov);
				// log.info("Store new iovrange "+since+" "+until+" "+iov.getSinceCoolStr()+" "+iov.getUntilCoolStr()+" "+iov.getIshole());
			}
		}
		setIovlist(iovs.values());

		totalIovs += niovs;
		if (!ishole) {
			if (since.longValue() < minsince.longValue()) {
				minsince = since;
			}
			if (since.longValue() > maxsince.longValue()) {
				maxsince = since;
			}

			if (until.longValue() < minuntil.longValue()) {
				minuntil = until;
			}
			if (until.longValue() > maxuntil.longValue()) {
				maxuntil = until;
			}
		}

	}

	/**
	 * @return
	 */
	public Map<Long, IovRange> getIovRanges() {
		return iovs;
	}

	/**
	 * @return
	 */
	public final Long getMinTime() {
		Long lminsince = CoolIov.COOL_MAX_DATE;
		for (Long since : iovs.keySet()) {
			if (since < lminsince) {
				lminsince = since;
			}
		}
		return lminsince;
	}

	/**
	 * @return
	 */
	public final Long getMaxTime() {
		Long lmaxsince = 0L;
		for (Long since : iovs.keySet()) {
			if (since > lmaxsince) {
				lmaxsince = since;
			}
		}
		return lmaxsince;
	}

	/**
	 * @return the summary
	 */
	public final String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public final void setSummary(final String summary) {
		this.summary = summary;
	}

	/**
	 * @return the iovbase
	 */
	public final String getIovbase() {
		return iovbase;
	}

	/**
	 * @param iovbase
	 *            the iovbase to set
	 */
	public final void setIovbase(final String iovbase) {
		this.iovbase = iovbase;
	}

	/**
	 * @return the totalIovs
	 */
	public final Long getTotalIovs() {
		return totalIovs;
	}

	/**
	 * @return the minsince
	 */
	public final Long getMinsince() {
		return minsince;
	}

	/**
	 * @return the maxsince
	 */
	public final Long getMaxsince() {
		return maxsince;
	}

	/**
	 * @return the minuntil
	 */
	public final Long getMinuntil() {
		return minuntil;
	}

	/**
	 * @return the maxuntil
	 */
	public final Long getMaxuntil() {
		return maxuntil;
	}

	/**
	 * @return the channelName
	 */
	public final String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName
	 *            the channelName to set
	 */
	public final void setChannelName(final String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the schema
	 */
	public final String getSchema() {
		return schema;
	}

	/**
	 * @param schema
	 *            the schema to set
	 */
	public final void setSchema(final String schema) {
		this.schema = schema;
	}

	/**
	 * @return the db
	 */
	public final String getDb() {
		return db;
	}

	/**
	 * @param db
	 *            the db to set
	 */
	public final void setDb(final String db) {
		this.db = db;
	}

	/**
	 * @return the node
	 */
	public final String getNode() {
		return node;
	}

	/**
	 * @param node
	 *            the node to set
	 */
	public final void setNode(final String node) {
		this.node = node;
	}

	/**
	 * @return the tag
	 */
	public final String getTag() {
		return tag;
	}

	/**
	 * @param tag
	 *            the tag to set
	 */
	public final void setTag(final String tag) {
		this.tag = tag;
	}

	/**
	 * @return the iovlist
	 */
	public final Collection<IovRange> getIovlist() {
		return iovlist;
	}

	/**
	 * @param iovlist
	 *            the iovlist to set
	 */
	public final void setIovlist(final Collection<IovRange> iovlist) {
		this.iovlist = iovlist;
	}

}
