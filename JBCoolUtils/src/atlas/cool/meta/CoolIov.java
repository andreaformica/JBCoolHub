/**
 * 
 */
package atlas.cool.meta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;

import atlas.cool.rest.utils.TimestampStringFormatter;

/**
 * @author formica
 *
 */
public class CoolIov implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3930530117599626608L;

	public static long TO_NANOSECONDS = 1000000L;

	public static long TO_MILLISECONDS = 1000L;

	public static long COOL_MAX_DATE = 9223372036854775807L;
	public static long COOL_MAX_RUN = 2147483647L;
	
	
	public static BigInteger lumimask = new BigInteger("00000000FFFFFFFF",16);
	public static BigDecimal toNanoSeconds = new BigDecimal(1000000L);
	
	public static Long getRun(BigInteger atime) {
		if (atime == null)
			return null;
		if (atime.longValue() == COOL_MAX_DATE || atime.longValue() == COOL_MAX_RUN) {
			return COOL_MAX_DATE;
		}
		BigInteger run = atime.shiftRight(32);
		return run.longValue();
	}
	
	public static BigDecimal getCoolRun(String arun) {
		if (arun == null)
			return null;
		if (arun.equals("Inf")) {
			return new BigDecimal(COOL_MAX_DATE);
		}
		BigInteger coolrun = new BigInteger(arun);
		BigInteger run = coolrun.shiftLeft(32);
		return new BigDecimal(run);
	}

	public static Long getLumi(BigInteger atime) {
		if (atime == null)
			return null;
		if (atime.longValue() == COOL_MAX_DATE) {
			return 0L;
		}		
		BigInteger lumi = atime.and(lumimask);
		return lumi.longValue();
	}

	public static BigDecimal getCoolRunLumi(String arun, String lb) {
		BigInteger _run = null;
		BigInteger _lb = null; 
//		System.out.println("Received "+arun+" "+lb);
		BigInteger runlumi = null;
		BigInteger run =  null;
		if (arun == null)
			return null;
		else if (arun.equals("Inf")) {
			_run = new BigInteger(new Long(COOL_MAX_DATE).toString());
			_lb = new BigInteger("0");
			runlumi = _run;
		} else {
			_run = new BigInteger(arun);
			if (lb == null)
				_lb = new BigInteger("0");
			else
				_lb = new BigInteger(lb);
			run = _run.shiftLeft(32);
			runlumi = run.or(_lb);
		}
		return new BigDecimal(runlumi);
	}

	public static Long getTime(BigInteger atime) {
		if (atime == null)
			return null;
		if (atime.longValue() == COOL_MAX_DATE) {
			return COOL_MAX_DATE;
		}
		BigInteger timeInMilliSec = atime.divide(toNanoSeconds.toBigInteger());
		return timeInMilliSec.longValue();
	}
	
	public static String getCoolTimeString(Long time, String iovBase) {
		String iovstr = "";
		if (iovBase.equals("run-lumi")) {
			if (time == CoolIov.COOL_MAX_DATE)
				return "Inf";
			iovstr = time.toString();
		} else {
			if (time == 0)
				return "0";
			if (time == CoolIov.COOL_MAX_DATE)
				return "Inf";
			Date iov = new Date(time);
			iovstr = TimestampStringFormatter.format(null, iov);
		}
		return iovstr;
	}
	
	public static String getCoolTimeRunLumiString(Long time, String iovBase) {
		String iovstr = "";
		Calendar endofatlasyear = Calendar.getInstance();
		endofatlasyear.set(2100, 1, 1);
		if (iovBase.equals("run-lumi")) {
			if (time == CoolIov.COOL_MAX_DATE)
				return "Inf";
			Long run = getRun(new BigInteger(time.toString()));
			Long lb = getLumi(new BigInteger(time.toString()));
			iovstr = run + " - "+lb;
		} else if (iovBase.equals("time")) {
			if (time == 0)
				return "0";
			if (time == CoolIov.COOL_MAX_DATE)
				return "Inf";
			Long timeInMilliSec = time/TO_NANOSECONDS;
			Date iov = new Date(timeInMilliSec);
			iovstr = TimestampStringFormatter.format(null, iov);
			
		} else {
			// Try to guess...
			// Suppose that it is a time....
			if (time == 0)
				return "0";
			if (time == CoolIov.COOL_MAX_DATE)
				return "Inf";
			Long timeInMilliSec = time/TO_NANOSECONDS;
			Date iov = new Date(timeInMilliSec);
			iovstr = TimestampStringFormatter.format(null, iov);
			
			Calendar iovcal = Calendar.getInstance();
			iovcal.setTime(iov);
			int iovyear = iovcal.get(Calendar.YEAR);
			if (iovyear > endofatlasyear.get(Calendar.YEAR)) {
				Long run = getRun(new BigInteger(time.toString()));
				Long lb = getLumi(new BigInteger(time.toString()));
				iovstr = run + " - "+lb;
			}
		}
		return iovstr;
	}


}
