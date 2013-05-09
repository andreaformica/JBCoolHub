/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
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
	private static final long serialVersionUID = 1L;

	Long chanId;
	String channelName;
	String schema;
	String db;
	String node;
	String tag;
	String summary;
	String iovbase;
	Long totalIovs = 0L;
	Long minsince = CoolIov.COOL_MAX_DATE;
	Long maxsince = 0L;
	Long minuntil = CoolIov.COOL_MAX_DATE;
	Long maxuntil = 0L;

	@XmlElement(name="iovrange", type=IovRange.class)
	Collection<IovRange> iovlist = null; 

	@XmlTransient
	Map<Long, IovRange> iovs = new TreeMap<Long, IovRange>();

	/**
	 * 
	 */
	public CoolIovSummary() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	public CoolIovSummary(Long chanId) {
		// TODO Auto-generated constructor stub
		this.chanId = chanId;
	}

	/**
	 * @return the chanId
	 */
	public Long getChanId() {
		return chanId;
	}

	/**
	 * @param chanId
	 *            the chanId to set
	 */
	public void setChanId(Long chanId) {
		this.chanId = chanId;
	}

	public void appendIov(Long since, Long until, Long niovs, Boolean ishole)
			throws Exception {
		
		if (iovs.containsKey(since)) {
			// modify existing iov if it is of the same type
			IovRange iov = iovs.get(since);
			if ((!(iov.getIshole()) && !(ishole)) && iov.getUntil().compareTo(until) < 0) {
				iov.setUntil(until);
				iov.setNiovs(niovs);
			} else {
				throw new Exception(
						"Cannot append an iov of different type for the same since:\n"
								+"new iov: "+since+" "+until+" "+niovs+" "+ishole+" \n"
								+"old iov: "+iov.getSince()+" "+iov.getUntil()+" "+iov.getNiovs()+" "+iov.getIshole()+" \n"
						);
			}
		} else {
			//
			Boolean updatediov = false;
			// Search if you find a previous iov of the same type (not a hole)
			Set<Long> sincetimes = iovs.keySet();
			for (Long asince : sincetimes) {
				IovRange aniov = iovs.get(asince);
				if (aniov.getUntil().compareTo(since) == 0) {
					if (!(aniov.ishole) && !(ishole)) {
						aniov.setUntil(until);
						aniov.setNiovs(niovs);
						if (aniov.getSince().compareTo(minsince) == 0)
							minuntil = until;
						updatediov = true;
					}
				} 
			}
			if (!updatediov) {
				// store a new iov
				IovRange iov = new IovRange(since,until,niovs,ishole,iovbase);
//				iov.setIovbase(iovbase);
				iovs.put(since, iov);
			}
		}
		setIovlist(iovs.values());
		
		totalIovs += niovs;
		if (!ishole) {
			if (since.longValue() < minsince.longValue())
				minsince = since;
			if (since.longValue() > maxsince.longValue())
				maxsince = since;

			if (until.longValue() < minuntil.longValue())
				minuntil = until;
			if (until.longValue() > maxuntil.longValue())
				maxuntil = until;
		}
		
	}


	public Map<Long, IovRange> getIovRanges() {
		return iovs;
	}

	public Long getMinTime() {
		Long minsince = CoolIov.COOL_MAX_DATE;
		for (Long since : iovs.keySet()) {
			if (since < minsince) {
				minsince = since;
			}
		}
		return minsince;
	}

	public Long getMaxTime() {
		Long maxsince = 0L;
		for (Long since : iovs.keySet()) {
			if (since > maxsince) {
				maxsince = since;
			}
		}
		return maxsince;
	}

	/**
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary
	 *            the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

	/**
	 * @return the iovbase
	 */
	public String getIovbase() {
		return iovbase;
	}

	/**
	 * @param iovbase
	 *            the iovbase to set
	 */
	public void setIovbase(String iovbase) {
		this.iovbase = iovbase;
	}

	/**
	 * @return the totalIovs
	 */
	public Long getTotalIovs() {
		return totalIovs;
	}

	/**
	 * @return the minsince
	 */
	public Long getMinsince() {
		return minsince;
	}

	/**
	 * @return the maxsince
	 */
	public Long getMaxsince() {
		return maxsince;
	}

	/**
	 * @return the minuntil
	 */
	public Long getMinuntil() {
		return minuntil;
	}

	/**
	 * @return the maxuntil
	 */
	public Long getMaxuntil() {
		return maxuntil;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the schema
	 */
	public String getSchema() {
		return schema;
	}

	/**
	 * @param schema the schema to set
	 */
	public void setSchema(String schema) {
		this.schema = schema;
	}

	/**
	 * @return the db
	 */
	public String getDb() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(String db) {
		this.db = db;
	}

	/**
	 * @return the node
	 */
	public String getNode() {
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(String node) {
		this.node = node;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the iovlist
	 */
	public Collection<IovRange> getIovlist() {
		return iovlist;
	}

	/**
	 * @param iovlist the iovlist to set
	 */
	public void setIovlist(Collection<IovRange> iovlist) {
		this.iovlist = iovlist;
	}

	
}
