package com.ebixcash.aayu.customvalidator;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Error {
    private String errorNo="";
    private String errorType="";
    private String errorMessage="";
    private String controlName="";
    private String errorCode="";
    private String[] arguments=null;

    public Error(String errorMessage, String controlName) {
        this("0","E",errorMessage,controlName);
    }
    public Error(String errorMessage, String controlName,String errCode) {
        this("0","E",errorMessage,controlName,errCode);
    }
    public Error(String errorNo, String errorType, String errorMessage, String controlName,String errCode) {
        this.errorNo=errorNo;
        this.errorType=errorType;
        this.errorMessage=errorMessage;
        this.controlName=controlName;
        this.errorCode=errCode;
    }
    
    
    public Error(String errorNo, String errorType, String errorMessage, String controlName) {
        this.errorNo=errorNo;
        this.errorType=errorType;
        this.errorMessage=errorMessage;
        this.controlName=controlName;
    }

    /*public Error(String errorNo, String errorType, String errorMessage, String controlName) {
            this.errorNo=errorNo;
            this.errorType=errorType;
            this.errorMessage=errorMessage;
            this.controlName=controlName;
        }*/


    /*public Error(String errorNo, String errorType, String errorMessage) {
        this(errorNo,errorType,errorMessage);
    }*/

    public Error(String errorMessage) {
		// TODO Auto-generated constructor stub
    	this.errorMessage=errorMessage;
	}
	public String getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(String errorNo) {
        this.errorNo = errorNo;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getControlName() {
        return controlName;
    }

    public void setControlName(String controlName) {
        this.controlName = controlName;
    }

   // public int getControlIndex() {
    //    return controlIndex;
    //}

   // public void setControlIndex(int controlIndex) {
    //    this.controlIndex = controlIndex;
    //}

    public String [] getArguments() {
        return arguments;
    }
    
    public String toJSONString() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("ERRORNO", errorNo);
        jsonMap.put("TYPE", errorType);
        jsonMap.put("ERRORCODE", errorCode);
        jsonMap.put("DESC", errorMessage);

        if (controlName != null && !controlName.isEmpty()) {
            jsonMap.put("ctl", controlName);
        }

        try {
            return mapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            // Handle JSON processing exception
            e.printStackTrace();
            return null;
        }
    }

	/*
	 * public String toXMLString() { StringBuffer xml = new StringBuffer();
	 * xml.append("<ROW>"); if (!"".equals(errorNo)){ xml.append("<ERRORNO>");
	 * xml.append(errorNo); xml.append("</ERRORNO>"); }
	 * 
	 * if (!"".equals(errorType)){ xml.append("<TYPE>"); xml.append(errorType);
	 * xml.append("</TYPE>"); } if (!"".equals(errorCode)){
	 * xml.append("<ERRORCODE>"); xml.append(errorCode); xml.append("</ERRORCODE>");
	 * }
	 * 
	 * xml.append("<DESC><![CDATA["); if (null!=errorMessage)
	 * xml.append(errorMessage); xml.append("]]></DESC>"); if
	 * ((null!=controlName)&&(controlName.length()>0)) { xml.append("<ctl>"); if
	 * (null!=controlName) xml.append(controlName); xml.append("</ctl>");
	 * //xml.append("</ctl><idx>"); // xml.append(controlIndex); //
	 * xml.append("</idx>"); }
	 * 
	 * 
	 * xml.append("</ROW>"); return xml.toString(); }
	 */

}