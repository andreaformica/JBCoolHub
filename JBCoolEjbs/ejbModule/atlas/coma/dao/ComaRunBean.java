/**
 * 
 */
package atlas.coma.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import atlas.coma.model.NemoRun;
import atlas.coma.model.PeriodSummary;
import atlas.coma.model.RunSummary;
import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.utils.PrintPojo;

/**
 * @author formica
 * 
 */
@Named
@Stateless
@Local(ComaRunDAO.class)
public class ComaRunBean implements ComaRunDAO {

	/**
	 * 
	 */
	@Inject
	private CoolRepositoryDAO coolrep;

	/**
	 * 
	 */
	@Inject
	private EntityManager em;

	/**
	 * 
	 */
	@Inject
	private Logger log;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * atlas.coma.dao.ComaRunDAO#getRunSummaryRangeByRunNumber(java.lang.Integer
	 * , java.lang.Integer)
	 */
	@Override
	public final List<RunSummary> getRunSummaryRangeByRunNumber(
			final Integer runstart, final Integer runend) {

		final String qry = "select run_number, start_time, end_time, data_source, run_type, "
				+ " run_type_desc, "
				+ " p_project, p_desc, start_lbn, end_lbn, p_period, "
				+ " l1_events, l2_events, ef_events, recorded_events, "
				+ " partition_name, integ_lumi "
				+ "from CR_VIEW_RUNINFO "
				+ " where "
				+ " (run_number>=:rs and run_number<=:re) order by run_number asc";
		final Query q = em.createNativeQuery(qry);
		q.setParameter("rs", runstart);
		q.setParameter("re", runend);
		log.info("Execute query " + q.toString());

		try {
			final List<RunSummary> rlist = getRuns(q);
			return rlist;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaRunDAO#getRunSummaryRangeByTime(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public final List<RunSummary> getRunSummaryRangeByTime(final Date since,
			final Date until) {

		final String qry = "select run_number, start_time, end_time, data_source, "
				+ " run_type, run_type_desc, "
				+ " p_project, p_desc, start_lbn, end_lbn, p_period, "
				+ " l1_events, l2_events, ef_events, recorded_events, "
				+ " partition_name, integ_lumi "
				+ "from CR_VIEW_RUNINFO "
				+ " where "
				+ " (start_time>=:rs and start_time<=:re) order by run_number asc";
		final Query q = em.createNativeQuery(qry);
		q.setParameter("rs", since);
		q.setParameter("re", until);
		log.info("Execute query " + q.toString());

		try {
			final List<RunSummary> rlist = getRuns(q);
			return rlist;
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param qry
	 * @return
	 * @throws CoolIOException
	 */
	public final List<RunSummary> getRuns(final Query qry)
			throws CoolIOException {
		try {

			log.info("Execute query " + qry.toString());
			final List<?> results = qry.getResultList();
			log.info("Retrieved list of " + results.size() + " rows");
			final Iterator<?> it = results.iterator();
			final List<RunSummary> runlist = new ArrayList<RunSummary>();
			while (it.hasNext()) {
				final Object[] line = (Object[]) it.next();
				log.info("retrieved run " + line[0]);
				final Long runnum = line[0] != null ? ((BigDecimal) line[0])
						.longValue() : null;
				final Timestamp runs = line[1] != null ? (Timestamp) line[1]
						: null;
				final Timestamp rune = line[2] != null ? (Timestamp) line[2]
						: null;
				final String datasource = line[3] != null ? (String) line[3]
						: null;
				final String runtype = line[4] != null ? (String) line[4]
						: null;
				final String runtypedesc = line[5] != null ? (String) line[5]
						: null;
				final String project = line[6] != null ? (String) line[6]
						: null;
				final String projdesc = line[7] != null ? (String) line[7]
						: null;
				final Long startlbn = line[8] != null ? ((BigDecimal) line[8])
						.longValue() : null;
				final Long endlbn = line[9] != null ? ((BigDecimal) line[9])
						.longValue() : null;
				// Float duration = ((BigDecimal) line[10]).floatValue();
				final String period = line[10] != null ? (String) line[10]
						: null;
				final Long l1evts = line[11] != null ? ((BigDecimal) line[11])
						.longValue() : null;
				final Long l2evts = line[12] != null ? ((BigDecimal) line[12])
						.longValue() : null;
				final Long efevts = line[13] != null ? ((BigDecimal) line[13])
						.longValue() : null;
				final Long recevts = line[14] != null ? ((BigDecimal) line[14])
						.longValue() : null;
				final String partitionname = line[15] != null ? (String) line[15]
						: null;
				final BigDecimal runlumi = line[16] != null ? (BigDecimal) line[16]
						: null;

				Date runsince = (runs != null) ? new Date(runs.getTime())
						: null;
				Date rununtil = (rune != null) ? new Date(rune.getTime())
						: null;
				final RunSummary rsum = new RunSummary(runnum, runsince,
						rununtil, runtype, runtypedesc, datasource, projdesc,
						partitionname, project, period, null, l1evts, l2evts,
						efevts, recevts, startlbn, endlbn, null, runlumi);

				runlist.add(rsum);
				log.info("Adding object "
						+ new PrintPojo<RunSummary>(rsum).toString());
			}
			return runlist;
		} catch (final Exception e) {
			throw new CoolIOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cool.coma.dao.ComaRunDAO#getRunInfo(java.lang.Integer,
	 * java.lang.Integer)
	 */
	// public List<RunSummary> getRunInfo(final Integer runstart, final Integer
	// runend)
	// throws CoolIOException {
	// try {
	// String qry =
	// "select run_number, start_time, end_time, data_source, run_type, run_type_desc, "
	// + " p_project, p_desc, start_lbn, end_lbn, p_period, "
	// +
	// " l1_events, l2_events, ef_events, recorded_events, partition_name, integ_lumi "
	// + "from CR_VIEW_RUNINFO "
	// + " where "
	// + " (run_number>=:rs and run_number<=:re) order by run_number asc";
	// Query q = em.createNativeQuery(qry);
	// q.setParameter("rs", runstart);
	// q.setParameter("re", runend);
	// log.info("Execute query " + q.toString());
	// List<?> results = q.getResultList();
	// log.info("Retrieved list of " + results.size() + " rows");
	// Iterator<?> it = results.iterator();
	// List<RunSummary> runlist = new ArrayList<RunSummary>();
	// while (it.hasNext()) {
	// Object[] line = (Object[]) it.next();
	// log.info("retrieved run " + line[0]);
	// Long runnum = (line[0] != null) ? ((BigDecimal) line[0]).longValue() :
	// null ;
	// Timestamp runs = (line[1] != null) ?(Timestamp) line[1]: null;
	// Timestamp rune = (line[2] != null) ?(Timestamp) line[2]: null;
	// String datasource = (line[3] != null) ?(String) line[3]: null;
	// String runtype = (line[4] != null) ?(String) line[4]: null;
	// String runtypedesc = (line[5] != null) ?(String) line[5]: null;
	// String project = (line[6] != null) ?(String) line[6]: null;
	// String projdesc = (line[7] != null) ?(String) line[7]: null;
	// Long startlbn = (line[8] != null) ?((BigDecimal) line[8]).longValue():
	// null;
	// Long endlbn = (line[9] != null) ?((BigDecimal) line[9]).longValue():
	// null;
	// // Float duration = ((BigDecimal) line[10]).floatValue();
	// String period = (line[10] != null) ?(String) line[10]: null;
	// Long l1evts = (line[11] != null) ?((BigDecimal) line[11]).longValue():
	// null;
	// Long l2evts = (line[12] != null) ?((BigDecimal) line[12]).longValue():
	// null;
	// Long efevts = (line[13] != null) ?((BigDecimal) line[13]).longValue():
	// null;
	// Long recevts = (line[14] != null) ?((BigDecimal) line[14]).longValue():
	// null;
	// String partition_name = (line[15] != null) ?(String) line[15]: null;
	// BigDecimal runlumi = (line[16] != null) ?((BigDecimal) line[16]): null;
	// RunSummary rsum = new RunSummary(runnum, new Date(runs.getTime()), new
	// Date(
	// rune.getTime()), runtype, runtypedesc, datasource, projdesc,
	// partition_name, project, period, null, l1evts, l2evts, efevts, recevts,
	// startlbn, endlbn, null,runlumi);
	//
	// runlist.add(rsum);
	//
	// }
	// return runlist;
	// } catch (Exception e) {
	// throw new CoolIOException(e);
	// }
	// }
	//
	/*
	 * (non-Javadoc)
	 * 
	 * @see cool.coma.dao.ComaRunDAO#getRunInfo(java.lang.Integer)
	 */
	@Override
	public final RunSummary getRunInfo(final Integer runnumber)
			throws CoolIOException {
		try {
			final List<RunSummary> runlist = getRunSummaryRangeByRunNumber(
					runnumber, runnumber);
			if (runlist == null || runlist.size() == 0) {
				return null;
			}
			final RunSummary rs = runlist.get(0);

			return rs;
		} catch (final Exception e) {
			throw new CoolIOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaRunDAO#getPeriods()
	 */
	@Override
	public final List<PeriodSummary> getPeriods() throws CoolIOException {
		try {
			final String qry = "select "
					+ " p_project, p_period, delivered_lumi "
					+ "from CB_VIEW_PARENT_PERIODS_LUMI " + " where "
					+ " (p_project like :proj and p_period like :prd) "
					+ " order by p_period asc";
			final Query q = em.createNativeQuery(qry);
			q.setParameter("proj", "%");
			q.setParameter("prd", "%");
			log.info("Execute query " + q.toString());
			final List<?> results = q.getResultList();
			log.info("Retrieved list of " + results.size() + " rows");
			final Iterator<?> it = results.iterator();
			final List<PeriodSummary> plist = new ArrayList<PeriodSummary>();
			while (it.hasNext()) {
				final Object[] line = (Object[]) it.next();
				log.info("retrieved project " + line[0]);
				final String period = line[1] != null ? (String) line[1] : null;
				final String project = line[0] != null ? (String) line[0]
						: null;
				final BigDecimal dellumi = line[2] != null ? (BigDecimal) line[2]
						: null;

				final PeriodSummary psum = new PeriodSummary(period, project,
						dellumi);

				plist.add(psum);

			}
			return plist;
		} catch (final Exception e) {
			throw new CoolIOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaRunDAO#getSubPeriodsByProject(java.lang.String)
	 */
	@Override
	public final List<PeriodSummary> getSubPeriodsByProject(final String project)
			throws CoolIOException {
		try {
			final String qry = "select "
					+ " p_project, p_period, delivered_lumi "
					+ "from CB_VIEW_PERIODS_LUMI " + " where "
					+ " (p_project like :proj and p_period like :prd) "
					+ " order by p_period asc";
			final Query q = em.createNativeQuery(qry);
			q.setParameter("proj", project);
			q.setParameter("prd", "%");
			log.info("Execute query " + q.toString());
			final List<?> results = q.getResultList();
			log.info("Retrieved list of " + results.size() + " rows");
			final Iterator<?> it = results.iterator();
			final List<PeriodSummary> plist = new ArrayList<PeriodSummary>();
			while (it.hasNext()) {
				final Object[] line = (Object[]) it.next();
				log.info("retrieved project " + line[0]);
				final String period = line[1] != null ? (String) line[1] : null;
				final String prj = line[0] != null ? (String) line[0] : null;
				final BigDecimal dellumi = line[2] != null ? (BigDecimal) line[2]
						: null;

				final PeriodSummary psum = new PeriodSummary(period, prj,
						dellumi);

				plist.add(psum);
			}
			return plist;
		} catch (final Exception e) {
			throw new CoolIOException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaRunDAO#getNemoRunList(java.util.Date,
	 * java.util.Date)
	 */
	@Override
	public List<NemoRun> getNemoRunList(Date since, Date until)
			throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = new Long(since.getTime() / 1000L);
		params[1] = new Long(until.getTime() / 1000L);
		params[2] = "COMP200";
		List<NemoRun> runlist = null;
		if (params[0] != params[1]) {
			log.info("Using query " + NemoRun.QUERY_FINDTDAQRUNLIST_INTIMERANGE
					+ " with " + since + " " + until);
			runlist = (List<NemoRun>) coolrep.findCoolList(
					NemoRun.QUERY_FINDTDAQRUNLIST_INTIMERANGE, params);
		} else {
			log.info("Using query " + NemoRun.QUERY_FINDRUNLIST_ATTIME
					+ " with " + since + " " + until);
			Object[] singleparams = new Object[2];
			singleparams[0] = new Long(since.getTime() / 1000L);
			singleparams[1] = "COMP200";
			runlist = (List<NemoRun>) coolrep.findCoolList(
					NemoRun.QUERY_FINDTDAQRUNLIST_ATTIME, singleparams);
		}
		return runlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaRunDAO#getNemoRunList(java.lang.Long,
	 * java.lang.Long)
	 */
	@Override
	public List<NemoRun> getNemoRunList(final Integer rmin, final Integer rmax)
			throws CoolIOException {
		final Object[] params = new Object[3];
		params[0] = rmin;
		params[1] = rmax;
		params[2] = "COMP200";
		if (rmin == rmax) {
			params[1] = rmax+1;
		}
		log.info("Using query " + NemoRun.QUERY_FINDTDAQRUNLIST_INRANGE + " with "
				+ rmin + " " + rmax);
		final List<NemoRun> runlist = (List<NemoRun>) coolrep.findCoolList(
				NemoRun.QUERY_FINDTDAQRUNLIST_INRANGE, params);
		return runlist;
	}

}
