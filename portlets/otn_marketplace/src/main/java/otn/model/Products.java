package otn.model;

import java.io.Serializable;
import java.util.Date;

import otn.model.marketplace.UserMin;

public class Products implements Serializable {

	protected Long id;

	protected String description;

	protected int totalRating;

	protected Integer cityId;

	protected Categories category;

	protected Date date;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Categories getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getCityId() {
		return cityId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public int getTotalRating() {
		return totalRating;
	}

	public void setTotalRating(int totalRating) {
		this.totalRating = totalRating;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}// end class
