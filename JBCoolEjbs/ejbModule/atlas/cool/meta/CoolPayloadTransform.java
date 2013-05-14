/**
 * 
 */
package atlas.cool.meta;

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

import javax.persistence.Column;
import javax.persistence.Id;

import atlas.cool.dao.CoolIOException;
import atlas.cool.rest.model.CoolIovType;

/**
 * @author formica
 *
 */
public class CoolPayloadTransform {

	private CoolPayload pyld;
	private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
	
	
	/**
	 * @param pyld
	 */
	public CoolPayloadTransform(CoolPayload pyld) {
		super();
		this.pyld = pyld;
	}
	
	public List<CoolIovType> getIovsWithPayload() throws CoolIOException{
		if (pyld == null)
			return null;
		
		List<CoolIovType> iovlist = new ArrayList<CoolIovType>();
		List<Map<String, Object>> pyldmap = pyld.getDataList();
		for (Map<String, Object> map : pyldmap) {
			BigDecimal objectId = (BigDecimal) map.get("OBJECT_ID"); 
			BigDecimal channelId = (BigDecimal)map.get("CHANNEL_ID");
			String channelName = (String)map.get("CHANNEL_NAME");
			BigDecimal iovSince = (BigDecimal)map.get("IOV_SINCE");
			BigDecimal iovUntil = (BigDecimal)map.get("IOV_UNTIL");
			BigDecimal tagId = (BigDecimal) map.get("USER_TAG_ID");
			String sysInstimeStr = (String)map.get("SYS_INSTIME");
			String  lastmodDateStr = (String)map.get("LASTMOD_DATE"); 
			BigDecimal newHeadId= (BigDecimal)map.get("NEW_HEAD_ID");
			String tagName="unkown";
			String iovBase="unkown";
			Date sysInstime = null;
			Date lastmodDate = null;
			try {
				sysInstime = df.parse(sysInstimeStr.substring(0, 19));
				lastmodDate = df.parse(lastmodDateStr.substring(0, 19));
			} catch (ParseException e) {
				throw new CoolIOException(e.getMessage());
			}
			
			CoolIovType cooliov = new CoolIovType(objectId, channelId.longValue(), channelName, iovSince, iovUntil, tagId.longValue(), new Timestamp(sysInstime.getTime()), new Timestamp(lastmodDate.getTime()), newHeadId, tagName, iovBase);
			Map<String,String> payloadcolumns = new LinkedHashMap<String,String>();
			Set<String> keys = map.keySet();
			for (String akey : keys) {
				if (pyld.isNumber(akey)) {
					Object value = map.get(akey);
					payloadcolumns.put(akey, value.toString());
				}
			}
			cooliov.setPayload(payloadcolumns);
			iovlist.add(cooliov);
		}
		return iovlist;
	}
}
