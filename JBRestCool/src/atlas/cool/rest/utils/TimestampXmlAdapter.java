/**
 * 
 */
package atlas.cool.rest.utils;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author formica
 * 
 */
public class TimestampXmlAdapter extends XmlAdapter<Date, Timestamp> {

	@Override
	public Date marshal(final Timestamp v) {
		return new Date(v.getTime());
	}

	@Override
	public Timestamp unmarshal(final Date v) {
		return new Timestamp(v.getTime());
	}

}
