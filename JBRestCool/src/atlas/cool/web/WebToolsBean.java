/**
 * 
 */
package atlas.cool.web;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.dao.ComaCbDAO;
import atlas.coma.exceptions.ComaQueryException;
import atlas.coma.model.CrViewRuninfo;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovSummary;
import atlas.cool.rest.model.IovRange;

@Named
@RequestScoped
/**
 * @author formica
 *
 */
public class WebToolsBean implements WebToolDAO {

	@Inject
	private ComaCbDAO comadao;

	
	/**
	 * 
	 */
	public WebToolsBean() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see atlas.cool.dao.CoolUtilsDAO#checkHoles(java.util.Collection)
	 */
	@Override
	public String checkHoles(Collection<CoolIovSummary> iovsummaryColl)
			throws ComaQueryException {
		StringBuffer results = new StringBuffer();
		// List<NodeGtagTagType> nodeingtagList = null;
		String colorgoodtagstart = "<span style=\"color:#20D247\">";
		String colorseptagstart = "<span style=\"color:#1A91C4\">";
		String colorbadtagstart = "<span style=\"color:#B43613\">";
		String colortagend = "</span>";
		results.append("<head><style>" + "h1 {font-size:25px;} "
				+ "h2 {font-size:20px;}" + "h3 {font-size:15px;}"
				+ "hr {color:sienna;}" + "p {font-size:14px;}"
				+ "p.small {line-height:80%;}" + "</style></head>");
		results.append("<body>");

		results.append("<h1>Coverage verification.... </h1>");

		CoolIovSummary firstsumm = iovsummaryColl.iterator().next();
		results.append("<h2>" + colorseptagstart + firstsumm.getSchema()
				+ " > " + " " + firstsumm.getNode() + " ; "
				+ firstsumm.getTag() + colortagend + "</h2>" + "<br>");
		Boolean ishole = false;
		Boolean coverageerror = true;
		for (CoolIovSummary iovsummary : iovsummaryColl) {
			Map<Long, IovRange> timeranges = iovsummary.getIovRanges();
			if (timeranges != null) {
				Set<Long> sincetimes = timeranges.keySet();
				String colortagstart = colorgoodtagstart;
				String iovDump = "";
				int iiov = 0;
				for (Long asince : sincetimes) {
					IovRange ivr = timeranges.get(asince);
					colortagstart = colorgoodtagstart;
					String holedump = "";
					if (ivr.getIshole()) {
						if (iiov==0) {
						results.append("<p class=\"small\">" + iovsummary.getChanId() + " "
								+ iovsummary.getChannelName() + " - "
								+ iovsummary.getIovbase() + " : ");
						}
						iiov++;
						ishole = true;
						colortagstart = colorbadtagstart;
						List<CrViewRuninfo> runlist = null;
						long timespan = ivr.getUntil() - ivr.getSince();
						if (iovsummary.getIovbase().equals("time")) {
							timespan = timespan / 1000L;
							holedump = "[" + timespan + "] ";
							Timestamp _since = new Timestamp(ivr.getSince()/CoolIov.TO_NANOSECONDS);
							Timestamp _until = new Timestamp(ivr.getUntil()/CoolIov.TO_NANOSECONDS);
							runlist = comadao.findRunsInRange(_since, _until);

						} else if (iovsummary.getIovbase().equals("run-lumi")) {
							Long runsince = CoolIov.getRun(ivr.getSince());
							Long rununtil = CoolIov.getRun(ivr.getUntil());
							timespan = rununtil - runsince;
							holedump = "[" + timespan +"]";
							BigDecimal _since = new BigDecimal(runsince);
							BigDecimal _until = new BigDecimal(rununtil);
							// Verify holes with run ranges
							runlist = comadao.findRunsInRange(_since, _until);
						}
						for (CrViewRuninfo arun : runlist) {
							if (arun.getPPeriod() != null && arun.getPProject() != null) {
								if (arun.getPProject().startsWith("data")) {
									coverageerror = false;
									iovDump = colortagstart
											+ ivr.getNiovs()
											+ " ["
											+ arun.getRunNumber()+" "+arun.getPProject()
											+ "] " + holedump
											+ colortagend;

									results.append(" | " + iovDump);									
								}
							}
						}
					}
				}
				if (ishole)
					results.append("</p>");
			}
		}
		if (coverageerror) {
			results.append("All relevant runs are covered...");
		}
		results.append("</body>");
		return results.toString();
	}

}
