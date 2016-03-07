package otn.beans;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import otn.model.marketplace.ServiceMin;

@ManagedBean(name="sessionDataBean")
@SessionScoped
public class SessionDataBean implements Serializable{

	/** The service the user has selected for details in the marketplace. */
	private ServiceMin selectedService;

	public SessionDataBean(){
				
	}
	
	public ServiceMin getSelectedService() {
		System.out.println("getting.."+selectedService);
		return selectedService;
	}

	public void setSelectedService(ServiceMin selectedService) {
		System.out.println("setting service..."+selectedService.getName());
		this.selectedService = selectedService;
	}
	
}//end class
