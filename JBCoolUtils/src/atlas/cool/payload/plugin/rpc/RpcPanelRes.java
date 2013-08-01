/**
 * 
 */
package atlas.cool.payload.plugin.rpc;

import java.io.Serializable;

/**
 * @author formica
 * 
 */
public class RpcPanelRes implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1379864763179107108L;
	private Float dBversion;
	private Float nTracks;
	private Float nStrips;
	private Float eff;
	private Float effErr;
	private Float effGap;
	private Float effGapErr;
	private Float resCs1;
	private Float cs1Err;
	private Float resCs2;
	private Float cs2Err;
	private Float resCsOther;
	private Float csOtherErr;
	private Float noise;
	private Float noiseErr;
	private Float noiseCor;
	private Float noiseCorErr;
	private Float clusterSize;
	private Float clusterSizeErr;
	private Float fracCs1;
	private Float fracCs2;
	private Float fracCs38;
	private Float averCs38;
	private Float fracCs9up;
	private Float averCs9up;

	/**
	 * 
	 */
	public RpcPanelRes() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param dBversion
	 * @param nTracks
	 * @param nStrips
	 * @param eff
	 * @param effErr
	 * @param effGap
	 * @param effGapErr
	 * @param resCs1
	 * @param cs1Err
	 * @param resCs2
	 * @param cs2Err
	 * @param resCsOther
	 * @param csOtherErr
	 * @param noise
	 * @param noiseErr
	 * @param noiseCor
	 * @param noiseCorErr
	 * @param clusterSize
	 * @param clusterSizeErr
	 * @param fracCs1
	 * @param fracCs2
	 * @param fracCs38
	 * @param averCs38
	 * @param fracCs9up
	 * @param averCs9up
	 */
	public RpcPanelRes(Float dBversion, Float nTracks, Float nStrips,
			Float eff, Float effErr, Float effGap, Float effGapErr,
			Float resCs1, Float cs1Err, Float resCs2, Float cs2Err,
			Float resCsOther, Float csOtherErr, Float noise, Float noiseErr,
			Float noiseCor, Float noiseCorErr, Float clusterSize,
			Float clusterSizeErr, Float fracCs1, Float fracCs2, Float fracCs38,
			Float averCs38, Float fracCs9up, Float averCs9up) {
		super();
		this.dBversion = dBversion;
		this.nTracks = nTracks;
		this.nStrips = nStrips;
		this.eff = eff;
		this.effErr = effErr;
		this.effGap = effGap;
		this.effGapErr = effGapErr;
		this.resCs1 = resCs1;
		this.cs1Err = cs1Err;
		this.resCs2 = resCs2;
		this.cs2Err = cs2Err;
		this.resCsOther = resCsOther;
		this.csOtherErr = csOtherErr;
		this.noise = noise;
		this.noiseErr = noiseErr;
		this.noiseCor = noiseCor;
		this.noiseCorErr = noiseCorErr;
		this.clusterSize = clusterSize;
		this.clusterSizeErr = clusterSizeErr;
		this.fracCs1 = fracCs1;
		this.fracCs2 = fracCs2;
		this.fracCs38 = fracCs38;
		this.averCs38 = averCs38;
		this.fracCs9up = fracCs9up;
		this.averCs9up = averCs9up;
	}

	/**
	 * @return the dBversion
	 */
	public Float getDBversion() {
		return dBversion;
	}

	/**
	 * @param dBversion
	 *            the dBversion to set
	 */
	public void setDBversion(Float dBversion) {
		this.dBversion = dBversion;
	}

	/**
	 * @return the nTracks
	 */
	public Float getNTracks() {
		return nTracks;
	}

	/**
	 * @param nTracks
	 *            the nTracks to set
	 */
	public void setNTracks(Float nTracks) {
		this.nTracks = nTracks;
	}

	/**
	 * @return the nStrips
	 */
	public Float getNStrips() {
		return nStrips;
	}

	/**
	 * @param nStrips
	 *            the nStrips to set
	 */
	public void setNStrips(Float nStrips) {
		this.nStrips = nStrips;
	}

	/**
	 * @return the eff
	 */
	public Float getEff() {
		return eff;
	}

	/**
	 * @param eff
	 *            the eff to set
	 */
	public void setEff(Float eff) {
		this.eff = eff;
	}

	/**
	 * @return the effErr
	 */
	public Float getEffErr() {
		return effErr;
	}

	/**
	 * @param effErr
	 *            the effErr to set
	 */
	public void setEffErr(Float effErr) {
		this.effErr = effErr;
	}

	/**
	 * @return the effGap
	 */
	public Float getEffGap() {
		return effGap;
	}

	/**
	 * @param effGap
	 *            the effGap to set
	 */
	public void setEffGap(Float effGap) {
		this.effGap = effGap;
	}

	/**
	 * @return the effGapErr
	 */
	public Float getEffGapErr() {
		return effGapErr;
	}

	/**
	 * @param effGapErr
	 *            the effGapErr to set
	 */
	public void setEffGapErr(Float effGapErr) {
		this.effGapErr = effGapErr;
	}

	/**
	 * @return the resCs1
	 */
	public Float getResCs1() {
		return resCs1;
	}

	/**
	 * @param resCs1
	 *            the resCs1 to set
	 */
	public void setResCs1(Float resCs1) {
		this.resCs1 = resCs1;
	}

	/**
	 * @return the cs1Err
	 */
	public Float getCs1Err() {
		return cs1Err;
	}

	/**
	 * @param cs1Err
	 *            the cs1Err to set
	 */
	public void setCs1Err(Float cs1Err) {
		this.cs1Err = cs1Err;
	}

	/**
	 * @return the resCs2
	 */
	public Float getResCs2() {
		return resCs2;
	}

	/**
	 * @param resCs2
	 *            the resCs2 to set
	 */
	public void setResCs2(Float resCs2) {
		this.resCs2 = resCs2;
	}

	/**
	 * @return the cs2Err
	 */
	public Float getCs2Err() {
		return cs2Err;
	}

	/**
	 * @param cs2Err
	 *            the cs2Err to set
	 */
	public void setCs2Err(Float cs2Err) {
		this.cs2Err = cs2Err;
	}

	/**
	 * @return the resCsOther
	 */
	public Float getResCsOther() {
		return resCsOther;
	}

	/**
	 * @param resCsOther
	 *            the resCsOther to set
	 */
	public void setResCsOther(Float resCsOther) {
		this.resCsOther = resCsOther;
	}

	/**
	 * @return the csOtherErr
	 */
	public Float getCsOtherErr() {
		return csOtherErr;
	}

	/**
	 * @param csOtherErr
	 *            the csOtherErr to set
	 */
	public void setCsOtherErr(Float csOtherErr) {
		this.csOtherErr = csOtherErr;
	}

	/**
	 * @return the noise
	 */
	public Float getNoise() {
		return noise;
	}

	/**
	 * @param noise
	 *            the noise to set
	 */
	public void setNoise(Float noise) {
		this.noise = noise;
	}

	/**
	 * @return the noiseErr
	 */
	public Float getNoiseErr() {
		return noiseErr;
	}

	/**
	 * @param noiseErr
	 *            the noiseErr to set
	 */
	public void setNoiseErr(Float noiseErr) {
		this.noiseErr = noiseErr;
	}

	/**
	 * @return the noiseCor
	 */
	public Float getNoiseCor() {
		return noiseCor;
	}

	/**
	 * @param noiseCor
	 *            the noiseCor to set
	 */
	public void setNoiseCor(Float noiseCor) {
		this.noiseCor = noiseCor;
	}

	/**
	 * @return the noiseCorErr
	 */
	public Float getNoiseCorErr() {
		return noiseCorErr;
	}

	/**
	 * @param noiseCorErr
	 *            the noiseCorErr to set
	 */
	public void setNoiseCorErr(Float noiseCorErr) {
		this.noiseCorErr = noiseCorErr;
	}

	/**
	 * @return the clusterSize
	 */
	public Float getClusterSize() {
		return clusterSize;
	}

	/**
	 * @param clusterSize
	 *            the clusterSize to set
	 */
	public void setClusterSize(Float clusterSize) {
		this.clusterSize = clusterSize;
	}

	/**
	 * @return the clusterSizeErr
	 */
	public Float getClusterSizeErr() {
		return clusterSizeErr;
	}

	/**
	 * @param clusterSizeErr
	 *            the clusterSizeErr to set
	 */
	public void setClusterSizeErr(Float clusterSizeErr) {
		this.clusterSizeErr = clusterSizeErr;
	}

	/**
	 * @return the fracCs1
	 */
	public Float getFracCs1() {
		return fracCs1;
	}

	/**
	 * @param fracCs1
	 *            the fracCs1 to set
	 */
	public void setFracCs1(Float fracCs1) {
		this.fracCs1 = fracCs1;
	}

	/**
	 * @return the fracCs2
	 */
	public Float getFracCs2() {
		return fracCs2;
	}

	/**
	 * @param fracCs2
	 *            the fracCs2 to set
	 */
	public void setFracCs2(Float fracCs2) {
		this.fracCs2 = fracCs2;
	}

	/**
	 * @return the fracCs38
	 */
	public Float getFracCs38() {
		return fracCs38;
	}

	/**
	 * @param fracCs38
	 *            the fracCs38 to set
	 */
	public void setFracCs38(Float fracCs38) {
		this.fracCs38 = fracCs38;
	}

	/**
	 * @return the averCs38
	 */
	public Float getAverCs38() {
		return averCs38;
	}

	/**
	 * @param averCs38
	 *            the averCs38 to set
	 */
	public void setAverCs38(Float averCs38) {
		this.averCs38 = averCs38;
	}

	/**
	 * @return the fracCs9up
	 */
	public Float getFracCs9up() {
		return fracCs9up;
	}

	/**
	 * @param fracCs9up
	 *            the fracCs9up to set
	 */
	public void setFracCs9up(Float fracCs9up) {
		this.fracCs9up = fracCs9up;
	}

	/**
	 * @return the averCs9up
	 */
	public Float getAverCs9up() {
		return averCs9up;
	}

	/**
	 * @param averCs9up
	 *            the averCs9up to set
	 */
	public void setAverCs9up(Float averCs9up) {
		this.averCs9up = averCs9up;
	}

}
