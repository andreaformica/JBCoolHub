/**
 * 
 */
package atlas.client.main;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * @author formica
 * 
 */
public final class CoolIov implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3930530117599626608L;

	/**
	 * Use this to convert from and to COOL nanoseconds. For example: new
	 * Date().getTime() * TO_NANOSECONDS
	 * 
	 */
	public static long TO_NANOSECONDS = 1000000L;

	/**
	 * Use this for conversion of seconds to milliseconds.
	 */
	public static long TO_MILLISECONDS = 1000L;

	/**
	 * Cool Max data in COOL format.
	 */
	public static long COOL_MAX_DATE = 9223372036854775807L;
	/**
	 * Cool Max run in COOL format.
	 */
	public static long COOL_MAX_RUN = 2147483647L;
	/**
	 * Cool Max lumi block in COOL format.
	 */
	public static long COOL_MAX_LUMIBLOCK = 4294967295L;

	/**
	 * 
	 */
	private static int cooliov_run_mask = 32;

	/**
	 * 
	 */
	public static BigInteger lumimask = new BigInteger("00000000FFFFFFFF", 16);
	/**
	 * 
	 */
	public static BigDecimal toNanoSeconds = new BigDecimal(1000000L);

	
	/**
	 * 
	 */
	protected static final SimpleDateFormat df = new SimpleDateFormat();

	/**
	 * 
	 */
	protected static final String lfmt = "yyyy/MM/dd hh:mm:ss";

	/**
	 * Ctor is private since this class is only static.
	 */
	private CoolIov() {
		
	}
	
	/**
	 * @param atime
	 *            The COOL time.
	 * @return The run number.
	 */
	public static Long getRun(final BigInteger atime) {
		if (atime == null) {
			return null;
		}
		if (atime.longValue() == COOL_MAX_DATE
				|| atime.longValue() == COOL_MAX_RUN) {
			return COOL_MAX_DATE;
		}
		final BigInteger run = atime.shiftRight(cooliov_run_mask);
		return run.longValue();
	}

	/**
	 * @param atime
	 *            The time in long.
	 * @return The run number.
	 */
	public static Long getRun(final Long atime) {
		if (atime == null) {
			return null;
		}
		final BigDecimal time = new BigDecimal(atime);
		return getRun(time.toBigInteger());
	}

	/**
	 * @param arun
	 *            The run number
	 * @return The COOL time.
	 */
	public static BigDecimal getCoolRun(final String arun) {
		if (arun == null) {
			return null;
		}
		if (arun.equals("Inf")) {
			return new BigDecimal(COOL_MAX_DATE);
		}
		final BigInteger coolrun = new BigInteger(arun);
		final BigInteger run = coolrun.shiftLeft(cooliov_run_mask);
		return new BigDecimal(run);
	}

	/**
	 * @param atime
	 *            The Cool time.
	 * @return The lumi block.
	 */
	public static Long getLumi(final BigInteger atime) {
		if (atime == null) {
			return null;
		}
		if (atime.longValue() == COOL_MAX_DATE) {
			return 0L;
		}
		final BigInteger lumi = atime.and(lumimask);
		return lumi.longValue();
	}

	/**
	 * @param atime
	 *            The COOL time in long format.
	 * @return The lumi block.
	 * 
	 */
	public static Long getLumi(final Long atime) {
		if (atime == null) {
			return null;
		}
		final BigDecimal time = new BigDecimal(atime);
		return getLumi(time.toBigInteger());
	}

	/**
	 * @param arun
	 *            The run number as a String.
	 * @param lb
	 *            The lumi block as a String.
	 * @return The COOL time.
	 */
	public static BigDecimal getCoolRunLumi(final String arun, final String lb) {
		Long runlong = null;
		Long lblong = null;
		if (arun == null) {
			return null;
		}
		if (arun.equals("Inf")) {
			runlong = COOL_MAX_RUN;
			lblong = COOL_MAX_LUMIBLOCK;
		} else {
			runlong = new Long(arun);
			lblong = new Long(lb);
		}
		return getCoolRunLumi(runlong, lblong);
	}

	/**
	 * @param arun
	 *            The run in long.
	 * @param lb
	 *            The lb in long.
	 * @return The COOL time.
	 */
	public static BigDecimal getCoolRunLumi(final Long arun, final Long lb) {
		BigInteger irun = null;
		BigInteger ilb = null;
		// System.out.println("Received "+arun+" "+lb);
		BigInteger runlumi = null;
		BigInteger run = null;
		if (arun == null) {
			return null;
		} else {
			irun = new BigDecimal(arun).toBigIntegerExact();
			if (lb == null) {
				ilb = new BigDecimal(0L).toBigIntegerExact();
			} else {
				ilb = new BigDecimal(lb).toBigIntegerExact();
			}
			run = irun.shiftLeft(cooliov_run_mask);
			runlumi = run.or(ilb);
		}
		return new BigDecimal(runlumi);
	}

	/**
	 * @param atime
	 *            The COOL time as biginteger.
	 * @return The time as Long, in milliseconds.
	 */
	public static Long getTime(final BigInteger atime) {
		if (atime == null) {
			return null;
		}
		if (atime.longValue() == COOL_MAX_DATE) {
			return COOL_MAX_DATE;
		}
		final BigInteger timeInMilliSec = atime.divide(toNanoSeconds
				.toBigInteger());
		return timeInMilliSec.longValue();
	}

	/**
	 * @param time
	 *            The time in milliseconds.
	 * @param iovBase
	 *            The String determining the format.
	 * @return A String with the COOL iov interpreted.
	 */
	public static String getCoolTimeString(final Long time, final String iovBase) {
		String iovstr = "";
		if (iovBase.startsWith("run-")) {
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			iovstr = time.toString();
		} else {
			if (time == 0) {
				return "0";
			}
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			final Date iov = new Date(time);
			iovstr = format(null, iov);
		}
		return iovstr;
	}

	/**
	 * @param time
	 *            The time in millisec.
	 * @param iovBase
	 *            The COOL folder type.
	 * @return A String with the COOL iov.
	 */
	public static String getCoolTimeRunLumiString(final Long time,
			final String iovBase) {
		String iovstr = "";
		final Calendar endofatlasyear = Calendar.getInstance();
		endofatlasyear.set(2100, 1, 1);
		if (iovBase.startsWith("run-")) {
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			final Long run = getRun(time);
			final Long lb = getLumi(time);
			iovstr = run + " - " + lb;
			if (lb == COOL_MAX_LUMIBLOCK) {
				iovstr = run + " - maxlb";
			}
		} else if (iovBase.equals("time")) {
			if (time == 0) {
				return "0";
			}
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			final Long timeInMilliSec = time / TO_NANOSECONDS;
			final Date iov = new Date(timeInMilliSec);
			iovstr = format(null, iov);

		} else {
			// Try to guess...
			// Suppose that it is a time....
			if (time == 0) {
				return "0";
			}
			if (time == CoolIov.COOL_MAX_DATE) {
				return "Inf";
			}
			final Long timeInMilliSec = time / TO_NANOSECONDS;
			final Date iov = new Date(timeInMilliSec);
			iovstr = format(null, iov);

			final Calendar iovcal = Calendar.getInstance();
			iovcal.setTime(iov);
			final int iovyear = iovcal.get(Calendar.YEAR);
			if (iovyear > endofatlasyear.get(Calendar.YEAR)) {
				final Long run = getRun(new BigInteger(time.toString()));
				final Long lb = getLumi(new BigInteger(time.toString()));
				iovstr = run + " - " + lb;
			}
		}
		return iovstr;
	}

	/**
	 * @param format
	 * @param adate
	 * @return
	 */
	public static String format(final String format, final Date adate) {
		try {
			String lformat = format;
			if (format == null) {
				lformat = lfmt;
			}
			df.applyPattern(lformat);
			return df.format(adate);
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param format
	 * @param adate
	 * @return
	 */
	public static String formatTs(final String format, final Timestamp adate) {
		try {
			String lformat = format;
			if (format == null) {
				lformat = lfmt;
			}
			df.applyPattern(lformat);
			return df.format(new Date(adate.getTime()));
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String message(String test) {
		return "You said "+test;
	}
}
