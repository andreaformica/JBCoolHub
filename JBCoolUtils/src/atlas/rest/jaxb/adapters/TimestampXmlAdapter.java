/**
 * 
 */
package atlas.rest.jaxb.adapters;

import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author formica
 * 
 */
public class TimestampXmlAdapter extends XmlAdapter<Date, Timestamp> {

	@Override
	public final Date marshal(final Timestamp v) {
		return new Date(v.getTime());
	}

	@Override
	public final Timestamp unmarshal(final Date v) {
		return new Timestamp(v.getTime());
	}

}
