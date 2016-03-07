/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package otn.model;

import java.io.Serializable;

/**
 *
 * @author sakis
 */
public class Response<T> implements Serializable{
    
    private String errorMsg;
    
    private T response;

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }
    
}
