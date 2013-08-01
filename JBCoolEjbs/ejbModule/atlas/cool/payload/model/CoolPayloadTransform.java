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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.rest.model.CoolIovType;

/**
 * @author formica
 * 
 */
public class CoolPayloadTransform {

	/**
	 * 
	 */
	private CoolPayload pyld;
	/**
	 * 
	 */
	private final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

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
		if (pyld == null) {
			return null;
		}
		try {
			List<CoolIovType> iovlist = new ArrayList<CoolIovType>();
			final List<Map<String, Object>> pyldmap = pyld.getDataList();
			//System.out.println("Parsing "+pyld.toString());
			for (final Map<String, Object> map : pyldmap) {
				final BigDecimal objectId = (BigDecimal) map.get("OBJECT_ID");
				final BigDecimal channelId = (BigDecimal) map.get("CHANNEL_ID");
				final String channelName = (String) map.get("CHANNEL_NAME");
				final BigDecimal iovSince = (BigDecimal) map.get("IOV_SINCE");
				final BigDecimal iovUntil = (BigDecimal) map.get("IOV_UNTIL");
				final BigDecimal tagId = (BigDecimal) map.get("USER_TAG_ID");
				final String sysInstimeStr = (String) map.get("SYS_INSTIME");
				final String lastmodDateStr = (String) map.get("LASTMOD_DATE");
				final BigDecimal newHeadId = (BigDecimal) map.get("NEW_HEAD_ID");
				String tagName = "unknown";
				if (map.containsKey("TAG_NAME")) {
					tagName = (String) map.get("TAG_NAME");
				}
				String iovBase = "unknown";
				if (map.containsKey("IOV_BASE")) {
					iovBase = (String) map.get("IOV_BASE");
				}

				Date sysInstime = null;
				Date lastmodDate = null;
				try {
					sysInstime = df.parse(sysInstimeStr.substring(0, 19));
					lastmodDate = df.parse(lastmodDateStr.substring(0, 19));
				} catch (final ParseException e) {
					throw new CoolIOException(e.getMessage());
				}

				
				final CoolIovType cooliov = new CoolIovType(objectId,
						channelId.longValue(), channelName, iovSince, iovUntil,
						tagId.longValue(), new Timestamp(sysInstime.getTime()),
						new Timestamp(lastmodDate.getTime()), newHeadId, tagName, iovBase);
				final Map<String, String> payloadcolumns = new LinkedHashMap<String, String>();
				final Map<String, Object> payloadObjcolumns = new LinkedHashMap<String, Object>();
				final Set<String> keys = map.keySet();
				for (final String akey : keys) {
					if (pyld.isNumber(akey)) {
						Object value = map.get(akey);
						payloadcolumns.put(akey, (value != null) ? value.toString() : null);
						if (pyld.getParser() != null) {
							value = pyld.getParser().parseClob(akey, (value != null) ? value.toString() : null);
							if (value == null) {
								value = map.get(akey);
							}
						}
						payloadObjcolumns.put(akey, value);
					}
				}
				
				cooliov.setPayload(payloadcolumns);
				cooliov.setPayloadObj(payloadObjcolumns);
				iovlist.add(cooliov);
			}
			return iovlist;
		} catch (Exception e) {
			throw new CoolIOException("Transform of payload got exception...."+e.getMessage());
		}
	}
}
