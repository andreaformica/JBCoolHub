/**
 * 
 */
package atlas.cool.rest.utils;

import java.math.BigDecimal;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author formica
 * 
 */
public class CoolIovXmlAdapter extends XmlAdapter<BigDecimal, String> {

	@Override
	public BigDecimal marshal(final String v) {
		return new BigDecimal(v);
	}

	@Override
	public String unmarshal(final BigDecimal v) {
		return "not implemented";
	}

}
