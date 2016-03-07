package otn.model;

import java.io.Serializable;
import java.util.List;

import otn.model.marketplace.ServiceMin;

public class ServicesHolder implements Serializable{

	private Categories category;
	private List<ServiceMin> services;
	
	public Categories getCategory() {
		return category;
	}
	public void setCategory(Categories category) {
		this.category = category;
	}
	public List<ServiceMin> getServices() {
		return services;
	}
	public void setServices(List<ServiceMin> services) {
		this.services = services;
	}
	
	
}//end class
