/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.util.List;

/**
 *
 * @author sakis
 */
public class ChallengesResponse {
    
    private String errorMsg;
	
    private List<ChallengeMin> challenges;
    

    public String getErrorMsg() {
	return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
	this.errorMsg = errorMsg;
    }

    public List<ChallengeMin> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<ChallengeMin> challenges) {
        this.challenges = challenges;
    }

    
}//end class
