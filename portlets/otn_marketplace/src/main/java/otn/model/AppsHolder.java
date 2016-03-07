package otn.model;

import java.io.Serializable;
import java.util.List;

import otn.model.marketplace.AppMin;

public class AppsHolder implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<AppMin> apps;

	public List<AppMin> getApps() {
		return apps;
	}

	public void setApps(List<AppMin> apps) {
		this.apps = apps;
	}
	
}//end class
