/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.marketplace;

import java.io.Serializable;

import otn.model.Categories;

/**
 * 
 * @author sakis
 */
public class ChallengeCategoryMin extends Categories implements Serializable {

	public ChallengeCategoryMin() {
	}

	public ChallengeCategoryMin(Integer id, String name) {

		this.id = id;
		this.name = name;

	}

}// end class
