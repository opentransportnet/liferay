/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import otn.model.Products;

@XmlRootElement
public class ServiceMin extends Products implements Serializable {


	private String name;

	private float price;

	private String thumbnail;

	private String serviceUrl;

	private UserMin provider;

	private String format;
	
	private String dateString;
	
	private CityMin city;
	
    private String license;
        
	private List<ServiceScreenshotMin> screenshotList;
    

	public ServiceMin() {
	}

	
	public ServiceMin(Long id, String name, String description,
			Integer categoryID, String categoryName, Integer cityId, String cityName,
			int totalRating, Long providerId, long liferayId,
			String providerName, String providerEmail, float price,
			String thumbnail, String serviceUrl, String format, String license, Date date) {

		this.id = id;
		this.name = name;
		this.description = description;
		this.totalRating = totalRating;
		this.price = price;
		this.thumbnail = thumbnail;
		this.serviceUrl = serviceUrl;
		this.cityId = cityId;
		this.format = format;
		this.license = license;
		this.date = date;		

		category = new ServiceCategoryMin();
		category.setId(categoryID);
		category.setName(categoryName);
		
		city = new CityMin();
		city.setId(cityId);
		city.setName(cityName);

		provider = new UserMin();
		provider.setId(providerId);
		provider.setLiferayId(liferayId);
		provider.setName(providerName);
		provider.setEmail(providerEmail);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCategory(ServiceCategoryMin category) {
		this.category = category;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public UserMin getProvider() {
		return provider;
	}

	public void setProvider(UserMin provider) {
		this.provider = provider;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getDateString() {
		return dateString;
	}
	
	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public CityMin getCity() {
		return city;
	}

	public void setCity(CityMin city) {
		this.city = city;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public List<ServiceScreenshotMin> getScreenshotList() {
		return screenshotList;
	}

	public void setScreenshotList(List<ServiceScreenshotMin> screenshotList) {
		this.screenshotList = screenshotList;
	}


}// end class