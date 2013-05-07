/**
 * 
 */
package atlas.cool.rest.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
public class CoolIovSummary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Long chanId;
	String summary;
	String iovbase;
	Long totalIovs = 0L;
	Long minsince = CoolIov.COOL_MAX_DATE;
	Long maxsince = 0L;
	Long minuntil = CoolIov.COOL_MAX_DATE;
	Long maxuntil = 0L;

	public class IovRange {
		public Long since;
		public Long until;
		public Long niovs;
		public Boolean ishole;
	}

	Map<Long, IovRange> iovs = new HashMap<Long, IovRange>();

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
		System.out.println("Inserting iov = " + since + " " + until + " "
				+ niovs + " " + ishole);
		if (iovs.containsKey(since)) {
			// modify existing iov if it is of the same type
			IovRange iov = iovs.get(since);
			if ((!(iov.ishole) && !(ishole)) && iov.until.compareTo(until) < 0) {
				iov.until = until;
				iov.niovs += niovs;
			} else {
				throw new Exception(
						"Cannot append an iov of different type for the same since!!");
			}
		} else {
			//
			Boolean updatediov = false;
			// Search if you find a previous iov of the same type (not a hole)
			Set<Long> sincetimes = iovs.keySet();
			for (Long asince : sincetimes) {
				IovRange aniov = iovs.get(asince);
				System.out.println("Found iov = " + aniov.since + " "
						+ aniov.until + " " + aniov.niovs + " is hole "
						+ aniov.ishole + "!");
				if (aniov.until.compareTo(since) == 0) {
					System.out.println("The since " + since
							+ " is equal to until " + aniov.until);
					if (!(aniov.ishole) && !(ishole)) {
						System.out.println("Both iovs are not holes....");
						aniov.until = until;
						aniov.niovs += niovs;
						if (aniov.since.compareTo(minsince) == 0)
							minuntil = until;
						updatediov = true;
						System.out.println("Update previous iov = "
								+ aniov.since + " " + aniov.until + " "
								+ aniov.niovs + "!");
					}
				} else {
					System.out.println("The since " + since
							+ " is DIFFERENT from until " + aniov.until);
				}
			}
			if (!updatediov) {
				// store a new iov
				IovRange iov = new IovRange();
				iov.since = since;
				iov.until = until;
				iov.niovs = niovs;
				iov.ishole = ishole;
				iovs.put(since, iov);
			}
		}
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
		System.out.println("Update times = " + minsince + " " + minuntil + " "
				+ maxsince + " " + maxuntil);
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

}