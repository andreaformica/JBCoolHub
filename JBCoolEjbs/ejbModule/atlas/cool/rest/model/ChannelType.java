/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * This POJO represents the COOL channel table. Cool channels are used in multi-version folders
 * and referenced inside the _IOVS table via the channel_id.
 * <p>
 * The Queries defined for this POJO are: <br>
 * 		<b>QUERY_FINDCHANNELS [cool_select_pkg]</b><br>
 * 		This query takes as arguments the SCHEMA, DB, NODE, CHAN and retrieves a list of channels;
 * 		it uses internally the function cool_select_pkg.f_Get_Channels(.....)
 * </p>
 * @author formica
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = ChannelType.QUERY_FINDCHANNELS, query = "select   "
		+ " channel_id, "
		+ " channel_name, "
		+ " description as channel_description "
		+ " from table(cool_select_pkg.f_Get_Channels(:schema,:dbname,:node,:chan)) "
		, resultClass = ChannelType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelType implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4191330973579854508L;
	
	@Id
	@Column(name = "CHANNEL_ID", precision = 20)
	BigDecimal channelId;
	@Column(name = "CHANNEL_NAME", length = 255)
	String channelName;
	@Column(name = "CHANNEL_DESCRIPTION", length = 255)
	String channelDescription;
	
	@CoolQuery(name = "cool.findchannels", params = "schema;dbname;node;chan")
	public static final String QUERY_FINDCHANNELS = "cool.findchannels";



	/**
	 * @return the channelId
	 */
	public BigDecimal getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(BigDecimal channelId) {
		this.channelId = channelId;
	}

	/**
	 * @return the channelName
	 */
	public String getChannelName() {
		return channelName;
	}

	/**
	 * @param channelName the channelName to set
	 */
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	/**
	 * @return the channelDescription
	 */
	public String getChannelDescription() {
		return channelDescription;
	}

	/**
	 * @param channelDescription the channelDescription to set
	 */
	public void setChannelDescription(String channelDescription) {
		this.channelDescription = channelDescription;
	}

	public String getLabel() {
		return channelId.toString()+": "+channelName;
	}

}
