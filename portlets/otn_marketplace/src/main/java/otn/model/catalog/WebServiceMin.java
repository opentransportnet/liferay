/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model.catalog;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
/**
 *
 * @author sakis
 */
public class WebServiceMin implements Serializable{

    private Long id;
	
    private String name;
	
    private WebServiceCategoryMin category;
	
    private String description;
	
    private String pricing;
	
    private String paymentUrl;
	
    private String baseUrl;
               
    private Long providerID;
        
    private short paid;

    public WebServiceMin(){}
        
    public WebServiceMin(Long id, String baseUrl, String name, Integer categoryID, String categoryName,
                         String description, String pricing, String paymentUrl ){
        
        
            this.id = id;
            this.name = name;
            this.baseUrl = baseUrl;
            this.paymentUrl = paymentUrl;
            this.description = description;
            this.pricing = pricing;
            
            category = new WebServiceCategoryMin();
            category.setId(categoryID);
            category.setName(categoryName);
        
    }
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPricing() {
		return pricing;
	}

	public void setPricing(String pricing) {
		this.pricing = pricing;
	}

	public String getPaymentUrl() {
		return paymentUrl;
	}

	public void setPaymentUrl(String paymentUrl) {
		this.paymentUrl = paymentUrl;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

 
    public Long getProviderID() {
        return providerID;
    }

    public void setProviderID(Long providerID) {
        this.providerID = providerID;
    }

    public short getPaid() {
        return paid;
    }

    public void setPaid(short paid) {
        this.paid = paid;
    }

    public WebServiceCategoryMin getCategory() {
        return category;
    }

    public void setCategory(WebServiceCategoryMin category) {
        this.category = category;
    }
	
}//end class