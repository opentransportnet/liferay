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
 * @author sakis
 */
@XmlRootElement
public class CityMin implements Serializable{
    
    private Integer id;
    
    private String name;

    public CityMin(){}
    
    public CityMin(Integer id, String name){
    
        this.id = id;
        this.name = name;
        
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    } 
    
}//end class

