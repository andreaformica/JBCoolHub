/**
 * 
 */
package atlas.cool.rest.utils;

import java.util.Date;

import atlas.cool.meta.CoolIov;

/**
 * @author formica
 *
 */
public class SvgRestUtils {

	
	Integer svglinewidth = 1000;
	Long svgabsmin = 0L;
	Long svgabsmax = CoolIov.COOL_MAX_DATE;
	Integer linewidth = 3;
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
	public SvgRestUtils(Integer svglinewidth, Long svgabsmin, Long svgabsmax,
			Integer linewidth, Long svgheight) {
		super();
		this.svglinewidth = svglinewidth;
		this.svgabsmin = svgabsmin;
		this.svgabsmax = svgabsmax;
		this.linewidth = linewidth;
		this.svgheight = svgheight;
	}
	
	

	/**
	 * <p>Description: convert point from iov in real times to iov in a range
	 * 0-1000 for svg visualization. </p>
	 * <p>In the following , INF is the infinity
	 * (from Cool iovs), MW is the maximum width (set to 1000). f(x) = a.x + b
	 * ==> f(t0) = a.t0 + b = 0 a = sinTh / cosTh = MW/(INF-t0) ==> t0 . MW/
	 * (INF-t0) + b = 0 ==> b = -t0.MW/(INF-t0)</p>
	 * 
	 * @param point
	 * @param endrange
	 * @return
	 */
	private Double convert(Long point, Long endrange) {
		// map the point from the range svgabsmin - inf to 0 svglinewidth
		Double b = -(svgabsmin.doubleValue() * (Double) (svglinewidth
				.doubleValue() / (endrange.doubleValue() - svgabsmin
				.doubleValue())));
		Double newpoint = (point.doubleValue())
				* (Double) (svglinewidth.doubleValue() / (endrange
						.doubleValue() - svgabsmin.doubleValue())) + b;
		// log.info("conversion gives " + point.doubleValue() + " ==> " +
		// newpoint
		// + " using " + svgabsmin);
		return newpoint;
	}

	public String getSvgLine(Long start, Long end, Long ichan,
			String iovtype, Boolean ishole) {
		StringBuffer svgline = new StringBuffer();

		Long infinity = new Date().getTime();
		if (!iovtype.equals("time")) {
			// infinity = CoolIov.COOL_MAX_RUN;
			infinity = svgabsmax;
		}

		if (start > infinity)
			start = infinity;
		if (end > infinity)
			end = infinity;
		if (start < svgabsmin) {
			start = svgabsmin;
		}
		if (ishole) {
			/*
			 * svgline = "<circle"; Double radius = ((convert(end, infinity) -
			 * convert(start,infinity)) / 2); Double xcenter =
			 * convert(start,infinity) + (radius); svgline +=
			 * (" cx=\""+xcenter+"\" cy=\""
			 * +ichan*linewidth+"\" r=\""+radius*5+"\" ");
			 */
			// svgline = "<rect";
			// Double width = ((convert(end, infinity) -
			// convert(start,infinity)));
			// svgline +=
			// (" x=\""+convert(start,infinity)+"\" y=\""+0+"\" width=\""+width+"\" height=\""+svgheight*linewidth+"\"");
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ (ichan * linewidth + svgheight) + "\" x2=\""
					+ convert(end, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");
			svgline.append(" stroke=\"red\" stroke-width=\"" + linewidth
					+ "\"/>");

			// Now add a vertical line plus a text giving the limits in the hole
			// time range
			/*
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ (2) + "\" x2=\"" + convert(start, infinity) + "\" y2=\""
					+ (ichan * linewidth + svgheight) + "\"");
			svgline.append(" stroke=\"black\" stroke-width=\"" + 1 + "\"/>");
			svgline.append("<text x=\"" + convert(start, infinity)
					+ "\" y=\"2\">");
			svgline.append(CoolIov.getCoolTimeString(start, iovtype));
			svgline.append("</text>");
			 */
		} else {
			svgline.append("<line");
			svgline.append(" x1=\"" + convert(start, infinity) + "\" y1=\""
					+ (ichan * linewidth + svgheight) + "\" x2=\"" + convert(end, infinity)
					+ "\" y2=\"" + (ichan * linewidth + svgheight) + "\"");
			svgline.append(" stroke=\"green\" stroke-width=\"" + linewidth
					+ "\"/>");
		}

		return svgline.toString();
	}

	/**
	 * @return the svglinewidth
	 */
	public Integer getSvglinewidth() {
		return svglinewidth;
	}

	/**
	 * @param svglinewidth the svglinewidth to set
	 */
	public void setSvglinewidth(Integer svglinewidth) {
		this.svglinewidth = svglinewidth;
	}

	/**
	 * @return the svgabsmin
	 */
	public Long getSvgabsmin() {
		return svgabsmin;
	}

	/**
	 * @param svgabsmin the svgabsmin to set
	 */
	public void setSvgabsmin(Long svgabsmin) {
		this.svgabsmin = svgabsmin;
	}

	/**
	 * @return the svgabsmax
	 */
	public Long getSvgabsmax() {
		return svgabsmax;
	}

	/**
	 * @param svgabsmax the svgabsmax to set
	 */
	public void setSvgabsmax(Long svgabsmax) {
		this.svgabsmax = svgabsmax;
	}

	/**
	 * @return the linewidth
	 */
	public Integer getLinewidth() {
		return linewidth;
	}

	/**
	 * @param linewidth the linewidth to set
	 */
	public void setLinewidth(Integer linewidth) {
		this.linewidth = linewidth;
	}

	/**
	 * @return the svgheight
	 */
	public Long getSvgheight() {
		return svgheight;
	}

	/**
	 * @param svgheight the svgheight to set
	 */
	public void setSvgheight(Long svgheight) {
		this.svgheight = svgheight;
	}

}
