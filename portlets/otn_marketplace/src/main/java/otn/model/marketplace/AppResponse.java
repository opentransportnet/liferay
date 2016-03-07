/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.util.List;

public class AppResponse {

	private String errorMsg;

	private AppMin app;

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public AppMin getApp() {
		return app;
	}

	public void setApp(AppMin app) {
		this.app = app;
	}

}// end class
