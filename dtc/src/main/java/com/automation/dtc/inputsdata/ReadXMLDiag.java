package com.automation.dtc.inputsdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ReadXMLDiag {
	
	public static List<String> all_diag_dtc = new ArrayList<String>();
	
	public Map<String, List<String>> readXMLDtc(String file) {
		
		Map<String, List<String>> OLD_DIAG_DTC = new HashMap<String, List<String>>();
		try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            
            NodeList listOfProperties = doc.getElementsByTagName("GpcEcuDTCPropertyDef");
            if (listOfProperties.getLength() > 0) {
            	for(int i=0; i<listOfProperties.getLength(); i++) {
            		Element property = (Element) listOfProperties.item(i);
            		String dtcCode = property.getAttribute("ShortName").trim().replace("DTC_FAULT_TYPE_", "");
            		if(dtcCode.length() == 4) {
            			List<String> characterizations = new ArrayList<String>();
            			NodeList listChara = property.getElementsByTagName("GpcEcuDTCPropertyDiscretValue");
            			if(listChara.getLength() > 0) {
            				for (int j = 0; j < listChara.getLength(); j++) {
								Element charaElement = (Element) listChara.item(j);
								String chara = charaElement.getAttribute("ShortName").trim().replace("DTC_FAULT_TYPE_", "");
								if(!characterizations.contains(chara)) {
									characterizations.add(chara);
								}
							}
            				OLD_DIAG_DTC.put(dtcCode, characterizations);
            			}
            		}
            	}
            }
          
		} catch (Exception e) {
			 e.printStackTrace();
		}
		return OLD_DIAG_DTC;
	}
}
