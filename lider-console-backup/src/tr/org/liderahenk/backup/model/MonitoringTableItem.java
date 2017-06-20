package tr.org.liderahenk.backup.model;

import java.io.Serializable;

public class MonitoringTableItem implements Serializable {

	private static final long serialVersionUID = 4882144394899651394L;

	private String dn;
	private String percentage;
	private String estimation;

	public MonitoringTableItem(String dn, String percentage, String estimation) {
		super();
		this.dn = dn;
		this.percentage = percentage;
		this.estimation = estimation;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public String getEstimation() {
		return estimation;
	}

	public void setEstimation(String estimation) {
		this.estimation = estimation;
	}

}
