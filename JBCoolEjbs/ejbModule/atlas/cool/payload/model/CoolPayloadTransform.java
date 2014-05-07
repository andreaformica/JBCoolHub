/**
 * 
 */
package atlas.cool.payload.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.meta.ParserHeader;
import atlas.cool.rest.model.CoolIovType;

/**
 * @author formica
 * 
 */
public class CoolPayloadTransform {

	/**
	 * 
	 */
	private final CoolPayload pyld;

	/**
	 * Header string in the format proposed by Ivan.
	 */

	private final ParserHeader header = new ParserHeader();

	// Example of output in the header
	/*
	 * "{ parserHeader : { iovList : [ { objectId : Integer , channelId : Long,
	 * channelName : String, iovSince : Long , iovUntil : Long , tagName :
	 * String , iovBase : String , sinceCoolStr: String , untilCoolStr : String
	 * , payloadObj : { StripStatus : [{ status : Float, time : Float, timeRes :
	 * Float }], PanelRes : { dBversion : Float, nTracks : Float, nStrips :
	 * Float, eff : Float, effErr : Float, effGap : Float, effGapErr : Float,
	 * resCs1 : Float, cs1Err : Float, resCs2 : Float, cs2Err : Float,
	 * resCsOther : Float, csOtherErr : Float, noise : Float, noiseErr : Float,
	 * noiseCor : Float, noiseCorErr : Float, clusterSize : Float,
	 * clusterSizeErr : Float, fracCs1 : Float, fracCs2 : Float, fracCs38 :
	 * Float, averCs38 : Float, fracCs9up : Float, averCs9up : Float } } }]}}"
	 */

	/**
	 * 
	 */
	private final SimpleDateFormat df = new SimpleDateFormat(
			"yyyy-MM-dd_HH:mm:ss");

	/**
	 * @param pyld
	 */
	public CoolPayloadTransform(final CoolPayload pyld) {
		super();
		this.pyld = pyld;
	}

	/**
	 * @return
	 * @throws CoolIOException
	 */
	public final List<CoolIovType> getIovsWithPayload() throws CoolIOException {
		if (this.pyld == null) {
			return null;
		}
		try {
			boolean getheader = true;
			final List<CoolIovType> iovlist = new ArrayList<CoolIovType>();
			final List<Map<String, Object>> pyldmap = this.pyld.getDataList();

			final Set<Object> headeriovlist = new HashSet<Object>();
			final Map<String, Object> iovheader = new HashMap<String, Object>();
			final Map<String, Object> payloadobjheader = new HashMap<String, Object>();
			iovheader.put("objectId", "Long");
			iovheader.put("channelId", "Long");
			iovheader.put("channelName", "String");
			iovheader.put("iovSince", "Long");
			iovheader.put("iovUntil", "Long");
			// System.out.println("Parsing "+pyld.toString());
			for (final Map<String, Object> map : pyldmap) {
				final BigDecimal objectId = (BigDecimal) map.get("OBJECT_ID");
				final BigDecimal channelId = (BigDecimal) map.get("CHANNEL_ID");
				final String channelName = (String) map.get("CHANNEL_NAME");
				final BigDecimal iovSince = (BigDecimal) map.get("IOV_SINCE");
				final BigDecimal iovUntil = (BigDecimal) map.get("IOV_UNTIL");
				final BigDecimal tagId = (BigDecimal) map.get("USER_TAG_ID");
				final String sysInstimeStr = (String) map.get("SYS_INSTIME");
				final String lastmodDateStr = (String) map.get("LASTMOD_DATE");
				final BigDecimal newHeadId = (BigDecimal) map
						.get("NEW_HEAD_ID");
				String tagName = "unknown";
				if (map.containsKey("TAG_NAME")) {
					tagName = (String) map.get("TAG_NAME");
					if (!iovheader.containsKey("tagName")) {
						iovheader.put("tagName", "String");
					}
				}
				String iovBase = "unknown";
				if (map.containsKey("IOV_BASE")) {
					iovBase = (String) map.get("IOV_BASE");
					if (!iovheader.containsKey("iovBase")) {
						iovheader.put("iovBase", "String");
					}
				}

				Date sysInstime = null;
				Date lastmodDate = null;
				try {
					sysInstime = this.df.parse(sysInstimeStr.substring(0, 19));
					lastmodDate = this.df
							.parse(lastmodDateStr.substring(0, 19));
				} catch (final ParseException e) {
					throw new CoolIOException(e.getMessage());
				}

				final CoolIovType cooliov = new CoolIovType(objectId,
						channelId.longValue(), channelName, iovSince, iovUntil,
						tagId.longValue(), new Timestamp(sysInstime.getTime()),
						new Timestamp(lastmodDate.getTime()), newHeadId,
						tagName, iovBase);
				final Map<String, String> payloadcolumns = new LinkedHashMap<String, String>();
				final Map<String, Object> payloadObjcolumns = new LinkedHashMap<String, Object>();
				Map<String, Object> payloadheader = null;
				final Set<String> keys = map.keySet();
				for (final String akey : keys) {
					if (this.pyld.isNumber(akey)) {
						Object value = map.get(akey);
						// String header = "";
						payloadcolumns.put(akey,
								(value != null) ? value.toString() : null);
						if (this.pyld.getParser() != null) {
							value = this.pyld.getParser().parseClob(akey,
									(value != null) ? value.toString() : null);
							if (getheader) {
								payloadheader = this.pyld.getParser().header(
										akey);
								if (payloadheader != null) {
									payloadobjheader.put(akey,
											payloadheader.get(akey));
								}
							}
							if (value == null) {
								value = map.get(akey);
							}
						} else {
							String classname = value.getClass().getName();
							if (classname.contains(".")) {
								final String[] arrclass = classname
										.split("\\.");
								if (arrclass.length > 0) {
									classname = arrclass[arrclass.length - 1];
								}
							}
							payloadobjheader.put(akey, classname);
						}
						payloadObjcolumns.put(akey, value);
					}
				}
				// End of the first cooliov...now store the header
				if (getheader) {
					getheader = false;
					// pyldHeader += pyldContent;
					iovheader.put("payloadObj", payloadobjheader);
					headeriovlist.add(iovheader);
					String name = "Header";
					if (this.pyld.getParser() != null) {
						name = this.pyld.getParser().toString();
					}
					this.header.setName(name);
					this.header.setIovList(headeriovlist);
				}
				cooliov.setPayload(payloadcolumns);
				cooliov.setPayloadObj(payloadObjcolumns);
				iovlist.add(cooliov);
			}
			return iovlist;
		} catch (final Exception e) {
			throw new CoolIOException("Transform of payload got exception...."
					+ e.getMessage());
		}
	}

	public ParserHeader getPyldHeader() {
		return this.header;
	}
}
