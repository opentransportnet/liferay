/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package otn.model.marketplace;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ServiceScreenshotMin implements Serializable {

    private int id;

    private String url;

    private ServiceMin service;

    public ServiceScreenshotMin() {
    }

    public ServiceScreenshotMin(int id, String url, long serviceId) {

        this.id = id;
        this.url = url;
        service = new ServiceMin();
        service.setId(serviceId);

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ServiceMin getService() {
        return service;
    }

    public void setService(ServiceMin service) {
        this.service = service;
    }

}//end class
