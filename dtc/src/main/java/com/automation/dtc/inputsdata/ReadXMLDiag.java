package com.automation.dtc.inputsdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReadXMLDiag {
	
	public Set<String> readXMLDtc(String file) {
			Set<String> dtcCodesInXml = new HashSet<>();
	        
	        try {
	        	
	            /*************************************************************
	             * Creating a DocumentBuilder and parsing the XML file to DOM
	             *************************************************************/
	            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	            Document doc = dBuilder.parse(file);
	            doc.getDocumentElement().normalize();

	            // Get DiagConfig element
	            
	            Map<String, List<String>> DTC_PROPERTY = new HashMap<>();
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
	                    						List<String> listProperties = new ArrayList<String>();
	                    						for(int k=0; k<GpcEcuDTCPropertyDiscretValues.getLength(); k++) {
	                    							Element GpcEcuDTCPropertyDiscretValElement = (Element) GpcEcuDTCPropertyDiscretValues.item(k);
	                    							String property = GpcEcuDTCPropertyDiscretValElement.getAttribute("ShortName").trim().replace("DTC_FAULT_TYPE_", "");
	                    							if(property.length() == 2 && !listProperties.contains(property)) {
	                    								listProperties.add(property);
	                    							}
	                    						}
	                    						DTC_PROPERTY.put(DTCCode, listProperties);
	                    					}
	                    				}
	                    			}
	                    		}
	                    	}
	                        dtcCodesInXml.add(DTCCode);
	                    }
	                }
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			return dtcCodesInXml;
	}
}
