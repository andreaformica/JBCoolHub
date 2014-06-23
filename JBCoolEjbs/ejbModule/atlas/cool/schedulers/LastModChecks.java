package atlas.cool.schedulers;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jboss.ejb3.annotation.TransactionTimeout;

import atlas.coma.dao.ComaCbDAO;
import atlas.cool.dao.CondToolsDAO;
import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.CoolUtilsDAO;
import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.LastModTimeType;
import atlas.cool.rest.utils.MailSender;
import atlas.cool.rest.utils.PrintPojo;

/**
 * Session Bean implementation class LastModChecks.
 * This Session Bean is used to perform checks on modified IOVs and TAGs tables.
 * It also verify if IOVs inserted should go to new instance.
 * 
 * @author formica
 * @since 2014/06/18
 * 
 */
@Stateless
@LocalBean
public class LastModChecks {

	@Inject
	private ComaCbDAO comadao;
	@Inject
	private CoolDAO cooldao;
	@Inject
	private CondToolsDAO condtoolsdao;
	@Inject
	private CoolUtilsDAO coolutilsdao;
	@Inject
	private MailSender mailsender;

	@Inject
	private Logger log;


	private final List<String> destAddrs = new ArrayList<String>();

	private final long runSeparatorRun2 = 222222L;
	/**
	 * Default constructor.
	 */
	public LastModChecks() {

		destAddrs.add("andrea.formica1971@gmail.com");
		destAddrs.add("Elizabeth.Gallas@cern.ch");
	}

	/**
     * 
     */
//	@//Schedule(dayOfWeek = "*", hour = "*", minute = "*/5", persistent = false)
	@Schedule(dayOfWeek = "*", hour = "6", persistent = false)
	@TransactionTimeout(unit = TimeUnit.MINUTES, value = 20)
	public void checkLastModTime() {
		final String db = "COMP200";
		String schema = "ATLAS_COOL";
		String node = "/";
		String tbl = "IOVS";
		Date tmodsince = null;
		final Date starttime = new Date();
		Calendar cal = Calendar.getInstance();
		try {
			StringBuffer buf = new StringBuffer();
			cal.setTime(starttime);
			//int monthtosubtract=4;
			int hourtosubtract=24;
			//cal.set(Calendar.MONTH, -monthtosubtract);
			cal.set(Calendar.HOUR, -hourtosubtract);
			tmodsince=cal.getTime();
			BigDecimal instanceRunSeparator = CoolIov.getCoolTime(runSeparatorRun2, "run-lb");
			BigDecimal instanceTimeSeparator = CoolIov.getCoolTime(runSeparatorRun2, "time");
			PrintPojo<LastModTimeType> printer = new PrintPojo<LastModTimeType>();
			List<LastModTimeType> folderlist = condtoolsdao.findLastModTime(schema, db, node, tbl, tmodsince);
			for (LastModTimeType lastModTimeType : folderlist) {
				log.info("Found folder "+lastModTimeType.getSchemaName()+" "+lastModTimeType.getNodeFullpath()+" "+lastModTimeType.getLastModtimeStr()+" "+lastModTimeType.getSeqId());
				printer.setObjPrint(lastModTimeType);
				String lastmod = printer.toString();
				Date lmd = lastModTimeType.getLastModtime();
				BigDecimal seqid = lastModTimeType.getSeqId();
				buf.append(">>>>> "+lastmod.toString()+":");
				List<CoolIovType> lmdiovs = cooldao.retrieveLastModIovsFromNodeSchemaAndDb(
						lastModTimeType.getSchemaName(), db, lastModTimeType.getNodeFullpath(), 
						seqid, new Timestamp(lmd.getTime())); 
				log.info("Found list of size "+((lmdiovs != null) ? lmdiovs.size() : "null list"));
				if (lmdiovs != null && lmdiovs.size()>0) {
					CoolIovType lmdiov = lmdiovs.get(0);
					if (lmdiov.getIovBase().startsWith("run")) {
						if (lmdiov.getIovSince().compareTo(instanceRunSeparator) > 0) {
							buf.append("<CONDBR2>");
						}
					} else if (lmdiov.getIovBase().startsWith("tim")) {
						if (lmdiov.getIovSince().compareTo(instanceTimeSeparator) > 0) {
							buf.append("<CONDBR2>");
						}						
					}
					buf.append(" since/until "+lmdiov.getIovSince()+"/"+lmdiov.getIovUntil()+" chan "+lmdiov.getChannelId()+" sincestr["+lmdiov.getIovBase()+"]="+lmdiov.getSinceCoolStr());
				}
				buf.append("\n========================\n");
			}
			mailsender
					.sendMail(destAddrs.toArray(new String[0]),
							"COOL modifications in the last 24 hours" + new Date().toString(),
							buf.toString());

		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log.info("End of last modified folders check");
		}
	}

}
