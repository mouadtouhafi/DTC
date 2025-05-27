package com.automation.dtc.inputsdata;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<String> readXMLDsd(String file) {
		
		List<String> dtcCodesInXml = new ArrayList<>();
		try {

            /*************************************************************
             * Creating a DocumentBuilder and parsing the XML file to DOM
             *************************************************************/
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            
            NodeList GpcEcuDTCDefList = doc.getElementsByTagName("GpcEcuDTCDef");
            if (GpcEcuDTCDefList.getLength() > 0) {
            	for (int i = 0; i < GpcEcuDTCDefList.getLength(); i++) {
                	Element entry = (Element) GpcEcuDTCDefList.item(i);
                	String DTCCode = entry.getAttribute("DTCCode").trim();
                	
                    if (!DTCCode.isEmpty()) {
                    	NodeList GpcEcuDTCPropertyRef = entry.getElementsByTagName("GpcEcuDTCPropertyRef");
                    	if (GpcEcuDTCPropertyRef.getLength() > 0) {
                    		Element propertyElement = (Element) GpcEcuDTCPropertyRef.item(0);
                    		String PropertyName = propertyElement.getAttribute("PropertyName").trim();
                    			
                    		NodeList GpcEcuDTCPropertyDef = doc.getElementsByTagName("GpcEcuDTCPropertyDef");
                    		if(GpcEcuDTCPropertyDef.getLength()>0) {
                    			for(int j=0; j<GpcEcuDTCPropertyDef.getLength(); j++) {
                    				Element GpcEcuDTCPropertyDiscretValueElement = (Element) GpcEcuDTCPropertyDef.item(j);
                    				String shortName = GpcEcuDTCPropertyDiscretValueElement.getAttribute("ShortName");
                    				
                    				if(shortName.equals(PropertyName)) {
                    					NodeList GpcEcuDTCPropertyDiscretValues = GpcEcuDTCPropertyDiscretValueElement.getElementsByTagName("GpcEcuDTCPropertyDiscretValue");
                    					if(GpcEcuDTCPropertyDiscretValues.getLength()>0) {
                    						for(int k=0; k<GpcEcuDTCPropertyDiscretValues.getLength(); k++) {
                    							Element GpcEcuDTCPropertyDiscretValElement = (Element) GpcEcuDTCPropertyDiscretValues.item(k);
                    							String property = GpcEcuDTCPropertyDiscretValElement.getAttribute("ShortName").trim().replace("DTC_FAULT_TYPE_", "");
                    							String couple = DTCCode +"_"+ property;
                    							if(property.length() == 2 && !dtcCodesInXml.contains(couple)) {
                    								dtcCodesInXml.add(couple);
                    							}
                    						}
                    					}
                    				}
                    			}
                    		}
                    	}
                    }
                }
            }
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return dtcCodesInXml;
	}
}
