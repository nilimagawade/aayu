package com.ebixcash.aayu.customvalidator;


import java.io.Serializable;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

@Component
public class ErrorMessages implements Serializable {
    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private ArrayList<Object> errors;

    public ErrorMessages() {
        errors = new ArrayList();
    }
    public void addError(String message, String controlName,String errCode) {
        errors.add(new Error(message, controlName,errCode));
    }
    public void addError(String message, String controlName) {
        errors.add(new Error(message, controlName));
    }
    public void addError(String message) {
        errors.add(new Error(message));
    }
    public Error getError(int index) {
        return (Error) errors.get(index);
    }
	/*
	 * public String toXMLString() { StringBuffer xml = new StringBuffer();
	 * xml.append("<root><ERROR>"); for (int i=0;i<errors.size(); i++)
	 * xml.append(((Error)errors.get(i)).toJSONString());
	 * xml.append("</ERROR></root>"); return xml.toString(); }
	 */
    
    public String toJsonString() {
        StringBuilder json = new StringBuilder();
        json.append("{ \"root\": { \"ERROR\": [");

        for (int i = 0; i < errors.size(); i++) {
            Error error = (Error) errors.get(i);
            if (i != 0) {
                json.append(", ");
            }
            json.append(error.toJSONString());
        }
        json.append("] } }");
        return json.toString();
    }

    
    public int getSize() {
        return errors.size();
    }
    public boolean isEmpty() {
        return (errors.size()==0);
    }
	
	public ArrayList<Object> getErrors() {
		return errors;
	}
	public void setErrors(ArrayList<Object> errors) {
		this.errors = errors;
	}
	@Override
	public String toString() {
		return "ErrorMessages [errors=" + errors + "]";
	}
    
}



