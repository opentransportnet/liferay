/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServicesResponse implements Serializable{
	
    private String errorMsg;
	
    private List<ServiceMin> services;

    public String getErrorMsg() {
	return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    public List<ServiceMin> getServices() {
        return services;
    }

    public void setServices(List<ServiceMin> services) {
        this.services = services;
    }


}//end class