/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.catalog;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WebServicesResponse implements Serializable{
	
    private String errorMsg;
	
    private List<WebServiceMin> services;

    public String getErrorMsg() {
	return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    public List<WebServiceMin> getServices() {
        return services;
    }

    public void setServices(List<WebServiceMin> services) {
        this.services = services;
    }


}//end class