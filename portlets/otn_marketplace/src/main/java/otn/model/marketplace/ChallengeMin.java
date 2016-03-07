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

/**
 * 
 * @author sakis
 */
@XmlRootElement
public class ChallengeMin extends Products implements Serializable {

	private String title;

	// private ChallengeCategoryMin category;
	private UserMin owner;

	private String output;

	private String skills;

	private String reward;

	private CityMin city;

	private List<String> skillList;
	
	private String documentUrl;

	private String DateString;
	
	private String url;

	public ChallengeMin() {
	}

	public ChallengeMin(Long id, String title, String description,
			Integer categoryID, String categoryName, Integer cityId,
			String cityName, String output, String skills, String reward,
			int totalRating, Long ownerId, long liferayId, String ownerName,
			String ownerEmail) {

		this.id = id;
		this.title = title;
		this.description = description;
		this.cityId = cityId;
		this.output = output;
		this.skills = skills;
		this.reward = reward;
		this.totalRating = totalRating;

		category = new ChallengeCategoryMin();
		category.setId(categoryID);
		category.setName(categoryName);

		city = new CityMin();
		city.setId(cityId);
		city.setName(cityName);

		owner = new UserMin();
		owner.setId(ownerId);
		owner.setLiferayId(liferayId);
		owner.setName(ownerName);
		owner.setEmail(ownerEmail);

	}

	public ChallengeMin(Long id, String title, String description,
			Integer categoryID, String categoryName, Integer cityId,
			String cityName, String output, String skills, String reward,
			int totalRating, Long ownerId, long liferayId, String ownerName,
			String ownerEmail, Date date, String documentUrl, String url) {

		this.id = id;
		this.title = title;
		this.description = description;
		this.cityId = cityId;
		this.output = output;
		this.skills = skills;
		this.reward = reward;
		this.totalRating = totalRating;
		this.date = date;
		this.documentUrl = documentUrl;
		this.url = url;
		// this.date = date.getTime();

		category = new ChallengeCategoryMin();
		category.setId(categoryID);
		category.setName(categoryName);

		city = new CityMin();
		city.setId(cityId);
		city.setName(cityName);

		owner = new UserMin();
		owner.setId(ownerId);
		owner.setLiferayId(liferayId);
		owner.setName(ownerName);
		owner.setEmail(ownerEmail);

	}

	public void setCategory(ChallengeCategoryMin category) {
		this.category = category;
	}

	public UserMin getOwner() {
		return owner;
	}

	public void setOwner(UserMin owner) {
		this.owner = owner;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public String getSkills() {
		return skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CityMin getCity() {
		return city;
	}

	public void setCity(CityMin city) {
		this.city = city;
	}

	public List<String> getSkillList() {
		return skillList;
	}

	public void setSkillList(List<String> skillList) {
		this.skillList = skillList;
	}

	public String getDateString() {
		return DateString;
	}

	public void setDateString(String dateString) {
		DateString = dateString;
	}
	
	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}// end class

