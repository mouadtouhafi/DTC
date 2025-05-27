package com.automation.dtc.inputsdata;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReadXMLMessaging {
	
	/*
	 * This method read the xml file and checks the existance of the parameter with the shortName
	 * "DTC_CODE".
	 * If it exists, the method will return true.
	 * */
	public boolean dtc_code_parameter_exists(String file) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            NodeList Parameter = doc.getElementsByTagName("Parameters");
            if(Parameter.getLength()>0) {
            	for(int i=0; i<Parameter.getLength(); i++) {
            		Element entry = (Element) Parameter.item(i);
                	NodeList listParameters = entry.getElementsByTagName("Parameter");
                	for(int j=0; j<listParameters.getLength(); j++) {
                		Element parameter_j = (Element) listParameters.item(j);
                		String shortName = parameter_j.getAttribute("ShortName").trim();
                		System.out.println(shortName);
                		if(shortName.equals("DTC_CODE")) {
                			return true;
                		}
                	}
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
}
