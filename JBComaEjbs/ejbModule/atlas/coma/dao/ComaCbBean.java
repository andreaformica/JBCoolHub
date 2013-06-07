package atlas.coma.dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.exceptions.ComaQueryException;
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

	/**
	 * @see ComaCbDAO#findGtagUsageInAmi(String)
	 */
	@Override
	public final List<ComaCbamiGtags> findGtagUsageInAmi(final String gtagname)
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
	@Override
	public final List<CrViewRuninfo> findRunsInRange(final BigDecimal runstart,
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
	public final List<CrViewRuninfo> findRunsInRange(final Timestamp since,
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
	public final CrViewRuninfo findRun(final BigDecimal run) throws ComaQueryException {
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
