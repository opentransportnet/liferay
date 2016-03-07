/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author KBoura
 */
@XmlRootElement
public class AppMin implements Serializable{
    
	private static final long serialVersionUID = 1L;

	private Long id;
    
    private String title;
    
    private String appAbstract;
    
    private String otnUrl;
    
    private String googleUrl;
    
    private String appleUrl;
    
    private String windowsUrl;
    
    private String pictureUrl;
    
    private CityMin city;
    
    private Integer cityId;
    
    private String cityName;

    public AppMin(){}
    
    public AppMin(Long id, String title, String appAbstract, String otnUrl, String googleUrl, 
    		String appleUrl, String windowsUrl, String pictureUrl, Integer cityId, String cityName){
    
        this.id = id;
        this.title = title;
        this.appAbstract = appAbstract;
        this.otnUrl = otnUrl;
        this.googleUrl = googleUrl;
        this.appleUrl = appleUrl;
        this.windowsUrl = windowsUrl;
        this.pictureUrl = pictureUrl;
        this.cityId = cityId;
        
        city = new CityMin();
		city.setId(cityId);
		city.setName(cityName);
        
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAppAbstract() {
		return appAbstract;
	}

	public void setAppAbstract(String appAbstract) {
		this.appAbstract = appAbstract;
	}

	public String getOtnUrl() {
		return otnUrl;
	}

	public void setOtnUrl(String otnUrl) {
		this.otnUrl = otnUrl;
	}

	public String getGoogleUrl() {
		return googleUrl;
	}

	public void setGoogleUrl(String googleUrl) {
		this.googleUrl = googleUrl;
	}

	public String getAppleUrl() {
		return appleUrl;
	}

	public void setAppleUrl(String appleUrl) {
		this.appleUrl = appleUrl;
	}

	public String getWindowsUrl() {
		return windowsUrl;
	}

	public void setWindowsUrl(String windowsUrl) {
		this.windowsUrl = windowsUrl;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}

	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}

	public CityMin getCity() {
		return city;
	}

	public void setCity(CityMin city) {
		this.city = city;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	
}//end class

