/**
 * 
 */
package atlas.cool.meta;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

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
	
	public static Long getLumi(BigInteger atime) {
		if (atime == null)
			return null;
		if (atime.longValue() == COOL_MAX_DATE) {
			return 0L;
		}		
		BigInteger lumi = atime.and(lumimask);
		return lumi.longValue();
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

}
