package atlas.cool.schedulers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import atlas.coma.dao.ComaCbDAO;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;
import atlas.cool.rest.model.NodeGtagTagType;
import atlas.cool.rest.model.NodeType;

/**
 * Session Bean implementation class CoolGtagChecks.
 */
@Stateless
@LocalBean
public class CoolGtagChecks {

	@Inject
	private ComaCbDAO comadao;
	@Inject
	private CoolDAO cooldao;
	@Inject
	private CoolUtilsDAO coolutilsdao;

	@Inject
	private Logger log;

	/**
	 * Default constructor.
	 */
	public CoolGtagChecks() {
		// TODO Auto-generated constructor stub
	}

	/**
     * 
     */
	@Schedule(dayOfWeek = "*", hour = "*", minute = "*/5", persistent = false)
	public void checkCoverage() {
		final String globaltagname = "COMCOND-BLKPA-RUN1-01";
		final String db = "COMP200";
		try {
			List<NodeGtagTagType> nodeingtagList = null;
			nodeingtagList = cooldao.retrieveGtagTagsFromSchemaAndDb("ATLAS_COOL%", db,
					globaltagname);
			for (final NodeGtagTagType nodeGtagTagType : nodeingtagList) {
				final String schema = nodeGtagTagType.getSchemaName();
				log.info("Found schema " + schema + " and node "
						+ nodeGtagTagType.getNodeFullpath() + " ... search for iovs!");

				try {

					final Collection<CoolIovSummary> summarylist = coolutilsdao
							.listIovsSummaryInNodesSchemaTagRangeAsList(schema, db,
									nodeGtagTagType.getNodeFullpath(),
									nodeGtagTagType.getTagName(), new BigDecimal(0L),
									new BigDecimal(CoolIov.COOL_MAX_DATE));
					if (summarylist == null) {
						continue;
					}
					log.info("Found collection of iovsummary for tag "
							+ nodeGtagTagType.getTagName() + " of size "
							+ summarylist.size());

					if (hasHoles(summarylist)) {
						log.info("Node in schema " + schema + " / "
								+ nodeGtagTagType.getNodeFullpath()
								+ " ... has holes...!!!");
					}
				} catch (final CoolIOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (final CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param summarylist
	 * @return
	 */
	protected boolean hasHoles(final Collection<CoolIovSummary> summarylist) {
		final boolean hashole = false;

		for (final CoolIovSummary iovsummary : summarylist) {
			final Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				final Set<Long> sincetimes = timeranges.keySet();
				for (final Long asince : sincetimes) {
					final IovRange ivr = timeranges.get(asince);
					if (ivr.getIshole()) {
						return true;
					}
				}
			}
		}
		return hashole;
	}
}
