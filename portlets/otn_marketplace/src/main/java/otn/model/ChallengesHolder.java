package otn.model;

import java.io.Serializable;
import java.util.List;

import otn.model.marketplace.ChallengeMin;

public class ChallengesHolder implements Serializable{

	private Categories category;
	private List<ChallengeMin> challenges;
	
	public Categories getCategory() {
		return category;
	}
	public void setCategory(Categories category) {
		this.category = category;
	}
	public List<ChallengeMin> getChallenges() {
		return challenges;
	}
	public void setChallenges(List<ChallengeMin> challenges) {
		this.challenges = challenges;
	}
	
}//end class
