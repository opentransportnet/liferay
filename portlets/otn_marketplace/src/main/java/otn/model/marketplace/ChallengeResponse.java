/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.util.List;

/**
 * 
 * @author sofia
 */
public class ChallengeResponse {

	private String errorMsg;

	private ChallengeMin challenge;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public ChallengeMin getChallenge() {
		return challenge;
	}

	public void setChallenge(ChallengeMin challenge) {
		this.challenge = challenge;
	}

}// end class
