/**
 * 
 */
package atlas.cool.rest.utils;

import atlas.cool.meta.CoolIov;

/**
 * @author formica
 * 
 */
public class SvgRestUtils {

	/**
	 * 
	 */
	Integer svglinewidth = 1000;
	/**
	 * 
	 */
	Long svgabsmin = 0L;
	/**
	 * 
	 */
	Long svgabsmax = CoolIov.COOL_MAX_DATE;
	/**
	 * 
	 */
	Integer linewidth = 3;
	/**
	 * 
	 */
	Long svgheight = 10L;

	/**
	 * 
	 */
	public SvgRestUtils() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param svglinewidth
	 * @param svgabsmin
	 * @param svgabsmax
	 * @param linewidth
	 * @param svgheight
	 */
	public SvgRestUtils(final Integer svglinewidth, final Long svgabsmin,
			final Long svgabsmax, final Integer linewidth, final Long svgheight) {
		super();
		this.svglinewidth = svglinewidth;
		this.svgabsmin = svgabsmin;
		this.svgabsmax = svgabsmax;
		this.linewidth = linewidth;
		this.svgheight = svgheight;
	}

	/**
	 * @param minsince
	 *            minimum since.
	 * @param minuntil
	 *            minimum until.
	 * @param maxsince
	 *            maximum since.
	 * @param maxuntil
	 *            maximum until.
	 */
	public final void computeBestRange(final Long minsince,
			final Long minuntil, final Long maxsince, final Long maxuntil) {
		this.setSvgabsmin(minsince);
		this.setSvgabsmax(maxuntil);
		double firstspan = minuntil - minsince;
		double lastspan = maxuntil - maxsince;
		final double timespan = maxsince - minuntil;
		System.out.println("computeBestRange: " + minsince + " " + minuntil
				+ " " + maxsince + " " + maxuntil + " spanning " + firstspan
				+ " " + lastspan);
		if (minuntil < maxsince) {
			// there are at least 3 IOV ranges
			System.out.println("computeBestRange: timespan " + timespan);
			if (timespan == 1) {
				firstspan = timespan * 100;
			} else if (timespan < 0.1 * firstspan) {
				firstspan = 10 * timespan;
			} else if (timespan < 0.4 * firstspan) {
				firstspan = 10 / 4 * timespan;
			} else if (timespan < 0.8 * firstspan) {
				firstspan = 10 / 8 * timespan;
			} else if (timespan < 1.2 * firstspan) {
				firstspan = timespan;
			} else if (timespan < 2 * firstspan) {
				firstspan = 0.5 * timespan;
			} else {
				firstspan = 0.2 * timespan;
			}
			if (timespan == 1) {
				lastspan = timespan * 100;
			} else if (timespan < 0.1 * lastspan) {
				lastspan = 10 * timespan;
			} else if (timespan < 0.4 * lastspan) {
				lastspan = 10 / 4 * timespan;
			} else if (timespan < 0.8 * lastspan) {
				lastspan = 10 / 8 * timespan;
			} else if (timespan < 1.2 * lastspan) {
				lastspan = timespan;
			} else if (timespan < 2 * lastspan) {
				lastspan = 0.5 * timespan;
			} else {
				lastspan = 0.2 * timespan;
			}
			this.setSvgabsmin(minuntil - (long) firstspan);
			this.setSvgabsmax(maxsince + (long) lastspan);
			System.out.println("computeBestRange: after changes " + svgabsmin
					+ " " + svgabsmax);
		}
	}

	/**
	 * <p>
	 * Description: convert point from iov in real times to iov in a range
	 * 0-1000 for svg visualization.
	 * </p>
	 * <p>
	 * In the following , INF is the infinity (from Cool iovs), MW is the
	 * maximum width (set to 1000). f(x) = a.x + b ==> f(t0) = a.t0 + b = 0 a =
	 * sinTh / cosTh = MW/(INF-t0) ==> t0 . MW/ (INF-t0) + b = 0 ==> b =
	 * -t0.MW/(INF-t0)
	 * </p>
	 * 
	 * @param point
	 *            The point.
	 * @param endrange
	 *            The range.
	 * @return A Double.
	 */
	private Double convert(final Long point, final Long endrange) {
		// map the point from the range svgabsmin - inf to 0 svglinewidth
		final Double b = -(svgabsmin.doubleValue() * (svglinewidth
				.doubleValue() / (endrange.doubleValue() - svgabsmin
				.doubleValue())));
		final Double newpoint = point.doubleValue()
				* (svglinewidth.doubleValue() / (endrange.doubleValue() - svgabsmin
						.doubleValue())) + b;
		// log.info("conversion gives " + point.doubleValue() + " ==> " +
		// newpoint
		// + " using " + svgabsmin);
		return newpoint;
	}

	/**
	 * @return the linewidth
	 */
	public final Integer getLinewidth() {
		return linewidth;
	}

	/**
	 * @return the svgabsmax
	 */
	public final Long getSvgabsmax() {
		return svgabsmax;
	}

	/**
	 * @return the svgabsmin
	 */
	public final Long getSvgabsmin() {
		return svgabsmin;
	}

	/**
	 * @return the svgheight
	 */
	public final Long getSvgheight() {
		return svgheight;
	}

	/**
	 * @param start
	 *            The start.
	 * @param end
	 *            The end.
	 * @param ichan
	 *            channel index.
	 * @param iovtype
	 *            iov type.
	 * @param ishole
	 *            is a hole ?
	 * @return A string with the line tag in svg.
	 */
	public final String getSvgLine(Long start, Long end, final Long ichan,
			final String iovtype, final Boolean ishole, final String color) {
		final StringBuffer svgline = new StringBuffer();

		final Long infinity = svgabsmax;
		// Long infinity = new Date().getTime();
		// if (!iovtype.equals("time")) {
		// // infinity = CoolIov.COOL_MAX_RUN;
		// infinity = svgabsmax;
		// }

		if (start > infinity) {
			start = infinity;
		}
		if (end > infinity) {
			end = infinity;
		}
		if (start < svgabsmin) {
			start = svgabsmin;
		}
		if (ishole) {
			svgline.append("<line");
			svgline.append(" x1=\"" + this.convert(start, infinity)
					+ "\" y1=\"" + (ichan * linewidth + svgheight) + "\" x2=\""
					+ this.convert(end, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");

		} else {
			svgline.append("<line");
			svgline.append(" x1=\"" + this.convert(start, infinity)
					+ "\" y1=\"" + (ichan * linewidth + svgheight) + "\" x2=\""
					+ this.convert(end, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");
		}
		svgline.append(" stroke=\"" + color + "\" stroke-width=\"" + linewidth
				+ "\"/>");

		return svgline.toString();
	}

	/**
	 * @return the svglinewidth
	 */
	public final Integer getSvglinewidth() {
		return svglinewidth;
	}

	/**
	 * @param linewidth
	 *            the linewidth to set
	 */
	public final void setLinewidth(final Integer linewidth) {
		this.linewidth = linewidth;
	}

	/**
	 * @param svgabsmax
	 *            the svgabsmax to set
	 */
	public final void setSvgabsmax(final Long svgabsmax) {
		this.svgabsmax = svgabsmax;
	}

	/**
	 * @param svgabsmin
	 *            the svgabsmin to set
	 */
	public final void setSvgabsmin(final Long svgabsmin) {
		this.svgabsmin = svgabsmin;
	}

	/**
	 * @param svgheight
	 *            the svgheight to set
	 */
	public final void setSvgheight(final Long svgheight) {
		this.svgheight = svgheight;
	}

	/**
	 * @param svglinewidth
	 *            the svglinewidth to set
	 */
	public final void setSvglinewidth(final Integer svglinewidth) {
		this.svglinewidth = svglinewidth;
	}

}
