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
public class AppsResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private String errorMsg;
	
    private List<AppMin> apps;

    public String getErrorMsg() {
	return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

	public List<AppMin> getApps() {
		return apps;
	}

	public void setApps(List<AppMin> apps) {
		this.apps = apps;
	}

}//end class