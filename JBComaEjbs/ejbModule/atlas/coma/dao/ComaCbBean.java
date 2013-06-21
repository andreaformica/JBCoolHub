package atlas.coma.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.ComaCbGtagStates;
import atlas.coma.model.ComaCbSchemas;
import atlas.coma.model.ComaCbamiGtags;
import atlas.coma.model.CrViewRuninfo;
import atlas.connection.dao.CoolRepositoryDAO;
import atlas.cool.exceptions.CoolIOException;

/**
 * Session Bean implementation class ComaCbBean.
 */
@Named
@Stateless(mappedName = "comacb")
@Local(ComaCbDAO.class)
@LocalBean
public class ComaCbBean implements ComaCbDAO {

	@Inject
	private CoolRepositoryDAO coolrep;

	@Inject
	private Logger log;

	/**
	 * Default constructor.
	 */
	public ComaCbBean() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaCbDAO#findSchemas(java.lang.String)
	 */
	@Override
	public List<ComaCbSchemas> findSchemas(final String name) throws ComaQueryException {
		final Object[] params = new Object[1];
		params[0] = name;
		log.info("Using query " + ComaCbSchemas.QUERY_FINDSCHEMAS + " with " + name);
		List<ComaCbSchemas> comalist = null;
		try {
			comalist = (List<ComaCbSchemas>) coolrep.findCoolList(
					ComaCbSchemas.QUERY_FINDSCHEMAS, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		return comalist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaCbDAO#findGtagState(java.lang.String)
	 */
	@Override
	public List<ComaCbGtagStates> findGtagState(final String state)
			throws ComaQueryException {
		final Object[] params = new Object[1];
		params[0] = state;
		log.info("Using query " + ComaCbGtagStates.QUERY_FINDSTATE + " with " + state);
		List<ComaCbGtagStates> comalist = null;
		try {
			comalist = (List<ComaCbGtagStates>) coolrep.findCoolList(
					ComaCbGtagStates.QUERY_FINDSTATE, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		return comalist;
	}

	/* (non-Javadoc)
	 * @see atlas.coma.dao.ComaCbDAO#findGtagStateAtTime(java.lang.String, java.util.Date)
	 */
	@Override
	public List<ComaCbGtagStates> findGtagStateAtTime(final String state, final Date time)
			throws ComaQueryException {
		final Object[] params = new Object[2];
		params[0] = state;
		params[1] = time;
		log.info("Using query " + ComaCbGtagStates.QUERY_FINDSTATEATTIME + " with " + state + " time " + time);
		List<ComaCbGtagStates> comalist = null;
		try {
			comalist = (List<ComaCbGtagStates>) coolrep.findCoolList(
					ComaCbGtagStates.QUERY_FINDSTATEATTIME, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		return comalist;
	}

	/**
	 * @see ComaCbDAO#findGtagUsageInAmi(String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ComaCbamiGtags> findGtagUsageInAmi(final String gtagname)
			throws ComaQueryException {

		final Object[] params = new Object[1];
		params[0] = gtagname;
		log.info("Using query " + ComaCbamiGtags.QUERY_AMI_GTAGS + " with " + gtagname);
		List<ComaCbamiGtags> comalist = null;
		try {
			comalist = (List<ComaCbamiGtags>) coolrep.findCoolList(
					ComaCbamiGtags.QUERY_AMI_GTAGS, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		// log.info("Retrieved a list of "+nodelist.size()+" nodes");
		return comalist;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaCbDAO#findRunsInRange(java.math.BigDecimal,
	 * java.math.BigDecimal)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CrViewRuninfo> findRunsInRange(final BigDecimal runstart,
			final BigDecimal runend) throws ComaQueryException {
		final Object[] params = new Object[2];
		params[0] = runstart;
		params[1] = runend;
		log.info("Using query " + CrViewRuninfo.QUERY_FINDRUNS + " with " + runstart
				+ " " + runend);
		List<CrViewRuninfo> runlist = null;
		try {
			runlist = (List<CrViewRuninfo>) coolrep.findCoolList(
					CrViewRuninfo.QUERY_FINDRUNS, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		return runlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaCbDAO#findRunsInRange(java.sql.Timestamp,
	 * java.sql.Timestamp)
	 */
	@Override
	public List<CrViewRuninfo> findRunsInRange(final Timestamp since,
			final Timestamp until) throws ComaQueryException {
		final Object[] params = new Object[2];
		params[0] = since;
		params[1] = until;
		log.info("Using query " + CrViewRuninfo.QUERY_FINDRUNS_BYTIME + " with " + since
				+ " " + until);
		List<CrViewRuninfo> runlist = null;
		try {
			runlist = (List<CrViewRuninfo>) coolrep.findCoolList(
					CrViewRuninfo.QUERY_FINDRUNS_BYTIME, params);
		} catch (final CoolIOException e) {
			throw new ComaQueryException(e.getMessage());
		}
		return runlist;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.coma.dao.ComaCbDAO#findRun(java.math.BigDecimal)
	 */
	@Override
	public CrViewRuninfo findRun(final BigDecimal run) throws ComaQueryException {
		CrViewRuninfo therun = null;
		try {
			therun = coolrep.findObj(CrViewRuninfo.class, run);
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return therun;
	}

}
