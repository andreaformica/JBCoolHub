/**
 * 
 */
package atlas.cool.jsf.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.CoolIov;
import atlas.cool.payload.model.CoolPayload;

/**
 * @author formica
 * 
 */
@Named("payloadchart")
@RequestScoped
public class PayloadChartBean implements Serializable {

	private CartesianChartModel linearModel = new CartesianChartModel();

	private Number minY = null;
	private Number maxY = null;
	private Date minX = null;
	private Date maxX = null;

	private Map<String, Integer> strDataMap = new HashMap<String, Integer>();
	private Integer minyStr = 0;

	@Inject
	private FacesContext facesContext;

	@Inject
	private Logger log;

	private CoolPayload payload = null;
	private List<Map<String,Object>> payloadMap = null;

	private String legend = "none";

	/**
	 * 
	 */
	public PayloadChartBean() {
		super();
	}

	public void resetChart() {
		strDataMap.clear();
		minyStr = 0;
		legend = "none";
		linearModel.clear();
	}

	protected LineChartSeries create(String label) {
		LineChartSeries serie = new LineChartSeries();
		if (label != null)
			serie.setLabel(label);
		else {
			log.info("Chart serie for " + label + " cannot be created...");
			return null;
		}
		log.info("Chart serie for " + label + " has been created...");
		linearModel.addSeries(serie);
		return serie;
	}

	protected ChartSeries getSerie(String label) {
		List<ChartSeries> series = linearModel.getSeries();
		for (ChartSeries aserie : series) {
			if (aserie.getLabel().equals(label)) {
				log.info("Chart serie for " + label
						+ " already stored in model...");
				return aserie;
			}
		}
		// If not serie has been found, then create a new one and add it to the
		// model
		LineChartSeries serie = create(label);
		return serie;
	}

	public void initModel(String coly) throws CoolIOException {
		try {
			Vector<Object> datax = payload.getDataColumn("IOV_SINCE");
			Vector<Object> datay = payload.getDataColumn(coly);
			Vector<Object> channels = payload.getDataColumn("CHANNEL_ID");
			Vector<Object> channelNames = payload.getDataColumn("CHANNEL_NAME");

			log.info("Prepare linear model...");
			log.info("setting chart series with x,y vectors of size "
					+ datax.size() + " " + datay.size());
			// linearModel.clear();
			for (int i = 0; i < datax.size(); i++) {
				LineChartSeries lineseries = null;
				BigDecimal xval = (BigDecimal) datax.get(i);
				Date xtime = new Date(CoolIov.getTime(xval.toBigInteger()));
				Number yval = null;
				Object yobj = datay.get(i);
				if (yobj instanceof String) {
					yval = getStrValue((String) yobj);
				} else if (yobj instanceof Boolean) {
					yval = ((Boolean) yobj) ? new Integer(1) : new Integer(0);
				} else if (yobj instanceof BigDecimal) {
					yval = ((BigDecimal) yobj).longValue();
				} else {
					yval = (Number) yobj;
				}
				if (channels != null) {
					BigDecimal chanId = (BigDecimal) channels.get(i);
					String chanName = null;
					if (channelNames != null) {
						chanName = (String) channelNames.get(i);
					}
					if (chanId != null) {
						String serieId = "ch:" + chanId.intValue();
						if (chanName != null)
							serieId = chanName;
						log.info("Getting chart serie for " + serieId);
						lineseries = (LineChartSeries) getSerie(serieId);
					} else
						throw new CoolIOException(
								"Cannot handle null channels in chart....");
				} else {
					// The payload does not have channels
					lineseries = (LineChartSeries) getSerie(coly);
				}
				log.fine("Entry " + xtime + " = " + yval);
				lineseries.set(xtime.getTime(), yval);
				if (minY == null) {
					minY = yval;
					maxY = yval;
					minX = xtime;
					maxX = xtime;
				}
				if (yval.doubleValue() < minY.doubleValue()) {
					minY = yval;
				}
				if (yval.doubleValue() > maxY.doubleValue()) {
					maxY = yval;
				}
				if (xtime.before(minX)) {
					minX = xtime;
				}
				if (xtime.after(maxX)) {
					maxX = xtime;
				}
			}
			log.info("Filled model...");
		} catch (Exception e) {
			log.warning("Exception occurred " + e.getMessage());
			throw new CoolIOException(e);
		}
	}
	
	public void initModelByMap(String coly) throws CoolIOException {
		try {
			

			log.info("Prepare linear model...");
			log.info("setting chart series from map of size "
					+ payloadMap.size());
			// linearModel.clear();
			for (Map<String,Object> row : payloadMap) {
				
				LineChartSeries lineseries = null;
				BigDecimal xval = (BigDecimal) row.get("IOV_SINCE");
				Date xtime = new Date(CoolIov.getTime(xval.toBigInteger()));
				Number yval = null;
				Object yobj = row.get(coly);
				if (yobj instanceof String) {
					yval = getStrValue((String) yobj);
				} else if (yobj instanceof Boolean) {
					yval = ((Boolean) yobj) ? new Integer(1) : new Integer(0);
				} else if (yobj instanceof BigDecimal) {
					yval = ((BigDecimal) yobj).longValue();
				} else {
					yval = (Number) yobj;
				}
				if (row.containsKey("CHANNEL_ID")) {
					BigDecimal chanId = (BigDecimal) row.get("CHANNEL_ID");
					String chanName = null;
					if (row.containsKey("CHANNEL_NAME")) {
						chanName = (String) row.get("CHANNEL_NAME");
					}
					if (chanId != null) {
						String serieId = "ch:" + chanId.intValue();
						if (chanName != null)
							serieId = chanName;
						log.info("Getting chart serie for " + serieId);
						lineseries = (LineChartSeries) getSerie(serieId);
					} else
						throw new CoolIOException(
								"Cannot handle null channels in chart....");
				} else {
					// The payload does not have channels
					lineseries = (LineChartSeries) getSerie(coly);
				}
				log.fine("Entry " + xtime + " = " + yval);
				lineseries.set(xtime.getTime(), yval);
				if (minY == null) {
					minY = yval;
					maxY = yval;
					minX = xtime;
					maxX = xtime;
				}
				if (yval.doubleValue() < minY.doubleValue()) {
					minY = yval;
				}
				if (yval.doubleValue() > maxY.doubleValue()) {
					maxY = yval;
				}
				if (xtime.before(minX)) {
					minX = xtime;
				}
				if (xtime.after(maxX)) {
					maxX = xtime;
				}
			}
			log.info("Filled model...");
		} catch (Exception e) {
			log.warning("Exception occurred " + e.getMessage());
			throw new CoolIOException(e);
		}
	}


	/**
	 * @return the linearModel
	 */
	public CartesianChartModel getLinearModel() {
		return linearModel;
	}

	/**
	 * @return the minY
	 */
	public Number getMinY() {
		return minY;
	}

	/**
	 * @return the maxY
	 */
	public Number getMaxY() {
		return maxY;
	}

	/**
	 * @return the minX
	 */
	public Date getMinX() {
		return minX;
	}

	/**
	 * @return the maxX
	 */
	public Date getMaxX() {
		return maxX;
	}

	/**
	 * @param payload
	 *            the payload to set
	 */
	public void setPayload(CoolPayload payload) {
		this.payload = payload;
	}

	/**
	 * @param payloadMap the payloadMap to set
	 */
	public void setPayloadMap(List<Map<String, Object>> payloadMap) {
		this.payloadMap = payloadMap;
	}

	protected Integer getStrValue(String obj) {
		if (!strDataMap.containsKey(obj)) {
			strDataMap.put(obj, minyStr++);
		}
		return strDataMap.get(obj);
	}

	/**
	 * @return the legend
	 */
	public String getLegend() {
		if (legend.equals("none")) {
			StringBuffer buf = new StringBuffer();
			for (String yval : strDataMap.keySet()) {
				buf.append("Y=" + strDataMap.get(yval) + ": " + yval + "\n");
			}
			legend = buf.toString();
		}
		return legend;
	}

}
