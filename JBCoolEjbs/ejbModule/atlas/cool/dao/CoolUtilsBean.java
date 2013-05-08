/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovType;

@Named
@Stateless
@Local(CoolUtilsDAO.class)

/**
 * @author formica
 *
 */
public class CoolUtilsBean implements CoolUtilsDAO {

	@Inject
	private CoolRepository coolrep;
	
	@Inject
	private CoolDAO cooldao;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public CoolUtilsBean() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolDAOUtils#computeIovRangeMap(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public Map<Long, CoolIovSummary> computeIovSummaryMap(String schema,
			String db, String node, String tag, String iovbase)
			throws CoolIOException {
		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDb(schema,
							db, node, tag);

			Map<Long, CoolIovSummary> iovsummary = getSummary(iovperchanList,schema,db,node,tag, iovbase);
			return iovsummary;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected Map<Long, CoolIovSummary> getSummary(List<IovType> iovperchanList, String schema,
			String db, String node, String tag, String iovbase) {
		
		Map<Long, CoolIovSummary> iovsummary = new HashMap<Long, CoolIovSummary>();

		for (IovType aniov : iovperchanList) {
			aniov.setIovBase(iovbase);
			Double isvalid = aniov.getIovHole().doubleValue();
			Long since = 0L;
			Long until = 0L;
			if (aniov.getIovBase().equals("time")) {
				since = CoolIov.getTime(aniov.getMiniovSince()
						.toBigInteger());
				until = CoolIov.getTime(aniov.getMaxiovUntil()
						.toBigInteger());
			} else {
				since = CoolIov.getRun(aniov.getMiniovSince()
						.toBigInteger());
				until = CoolIov.getRun(aniov.getMaxiovUntil()
						.toBigInteger());
			}

			// niovs += aniov.getNiovs();

			CoolIovSummary iovsumm = null;
			if (iovsummary.containsKey(aniov.getChannelId())) {
				iovsumm = iovsummary.get(aniov.getChannelId());
			} else {
				iovsumm = new CoolIovSummary(aniov.getChannelId());
				iovsumm.setIovbase(aniov.getIovBase());
				iovsumm.setChannelName(aniov.getChannelName());
				iovsumm.setSchema(schema);
				iovsumm.setDb(db);
				iovsumm.setNode(node);
				iovsumm.setTag(tag);
			}
			try {

				iovsumm.appendIov(since, until, aniov.getNiovs(), false);
				// If there is a hole then take its times from other
				// fields and add a line for the previous good iov and
				// the hole
				if (isvalid > 0) {

					since = until; // the since of the hole is the until
									// of the last good
					if (aniov.getIovBase().equals("time")) {
						until = CoolIov.getTime(aniov.getHoleUntil()
								.toBigInteger());
					} else {
						until = CoolIov.getRun(aniov.getHoleUntil()
								.toBigInteger());
					}
					iovsumm.appendIov(since, until, 0L, true);
				}
				iovsummary.put(aniov.getChannelId(), iovsumm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	
	protected Map<Long, CoolIovSummary> getSummaryOrigTime(List<IovType> iovperchanList, String schema,
			String db, String node, String tag, String iovbase) {
		
		Map<Long, CoolIovSummary> iovsummary = new HashMap<Long, CoolIovSummary>();

		for (IovType aniov : iovperchanList) {
			aniov.setIovBase(iovbase);
			Double isvalid = aniov.getIovHole().doubleValue();
			Long since = 0L;
			Long until = 0L;
			since = aniov.getMiniovSince().longValueExact();
			until = aniov.getMaxiovUntil().longValueExact();

			// niovs += aniov.getNiovs();

			CoolIovSummary iovsumm = null;
			if (iovsummary.containsKey(aniov.getChannelId())) {
				iovsumm = iovsummary.get(aniov.getChannelId());
			} else {
				iovsumm = new CoolIovSummary(aniov.getChannelId());
				iovsumm.setIovbase(aniov.getIovBase());
				iovsumm.setChannelName(aniov.getChannelName());
				iovsumm.setSchema(schema);
				iovsumm.setDb(db);
				iovsumm.setNode(node);
				iovsumm.setTag(tag);
			}
			try {

				iovsumm.appendIov(since, until, aniov.getNiovs(), false);
				// If there is a hole then take its times from other
				// fields and add a line for the previous good iov and
				// the hole
				if (isvalid > 0) {

					since = until; // the since of the hole is the until
									// of the last good
					until = aniov.getHoleUntil().longValueExact();
					iovsumm.appendIov(since, until, 0L, true);
				}
				iovsummary.put(aniov.getChannelId(), iovsumm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return iovsummary;
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#computeIovSummaryRangeMap(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.math.BigDecimal, java.math.BigDecimal)
	 */
	@Override
	public Map<Long, CoolIovSummary> computeIovSummaryRangeMap(String schema,
			String db, String node, String tag, String iovbase,
			BigDecimal since, BigDecimal until) throws CoolIOException {

		try {

			List<IovType> iovperchanList = cooldao
					.retrieveIovSummaryPerChannelFromNodeSchemaAndDbInRange(schema, db, node, tag, since, until);

			Map<Long, CoolIovSummary> iovsummary = getSummaryOrigTime(iovperchanList, schema, db, node, tag,iovbase);
			return iovsummary;
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
}
