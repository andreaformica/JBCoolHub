/**
 * 
 */
package atlas.coma.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author formica
 *
 */
public class PeriodSummary implements Serializable {


	private String period;
	private String project;
	private BigDecimal deliveredLumi;
	
	public PeriodSummary(String period, String project, BigDecimal deliveredLumi) {
		super();
		this.period = period;
		this.project = project;
		this.deliveredLumi = deliveredLumi;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public BigDecimal getDeliveredLumi() {
		return deliveredLumi;
	}

	public void setDeliveredLumi(BigDecimal deliveredLumi) {
		this.deliveredLumi = deliveredLumi;
	}
	
}
