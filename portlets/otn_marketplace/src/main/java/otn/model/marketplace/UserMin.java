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
public class UserMin implements Serializable{
    
    private Long id;
    
    private long liferayId;
    
    private String name;
    
    private String email;
    

    public UserMin(){}
    
    public UserMin(Long id, long liferayId, String name, String email){
    
        this.id = id;
        this.liferayId = liferayId;
        this.name = name;
        this.email = email;
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getLiferayId() {
        return liferayId;
    }

    public void setLiferayId(long liferayId) {
        this.liferayId = liferayId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
    
}//end class

