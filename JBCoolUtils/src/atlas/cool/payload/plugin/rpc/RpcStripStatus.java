/**
 * 
 */
package atlas.cool.payload.plugin.rpc;

import java.io.Serializable;

/**
 * @author formica
 * 
 */
public class RpcStripStatus implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6065168338382042500L;

	private Float status;
	private Float time;
	private Float timeRes;

	
	/**
	 * 
	 */
	public RpcStripStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param status
	 * @param time
	 * @param timeRes
	 */
	public RpcStripStatus(final Float status, final Float time,
			final Float timeRes) {
		super();
		this.status = status;
		this.time = time;
		this.timeRes = timeRes;
	}

	/**
	 * @return the status
	 */
	public Float getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(final Float status) {
		this.status = status;
	}

	/**
	 * @return the time
	 */
	public Float getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final Float time) {
		this.time = time;
	}

	/**
	 * @return the timeRes
	 */
	public Float getTimeRes() {
		return timeRes;
	}

	/**
	 * @param timeRes
	 *            the timeRes to set
	 */
	public void setTimeRes(final Float timeRes) {
		this.timeRes = timeRes;
	}

}
