package com.automation.dtc.blockbuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.automation.dtc.controllers.DtcController;

public class BuildDIAGXmlBlock {
	
	public void create_unexisting_dtc_blocks(String file, Map<String, List<String>> dtc_to_add, List<String> dtc_labels) {
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            
            NodeList gpcEcuDTC = doc.getElementsByTagName("GpcEcuDTC");
            if(gpcEcuDTC.getLength()>0) {
            	NodeList gpcEcuDTCDef = doc.getElementsByTagName("GpcEcuDTCDef");
            	if(gpcEcuDTCDef.getLength()>0) {
            		for(Map.Entry<String, List<String>> entry : dtc_to_add.entrySet()) {
            			String dtc = entry.getKey();
            			/** 
            			 * if a DTC contains "&", this means it is a new DTC, never added before,
            			 * if it doesn't contain "&", this means only the property is new
            			 *  **/
            			if(dtc.contains("$")) {
            				List<String> caras = new ArrayList<String>();
            				boolean dtcAdded = false;
            				for(String s : dtc_labels) {
            					if(s.split("\\|")[0].equals(dtc.replace("$", ""))) {
            						if(!dtcAdded) {
            							build_dtc_code_parameter(file, doc, s);
            							dtcAdded = true;
            						}            						
            						if(!caras.contains(s)) {
            							caras.add(s);
            						}
            					}
            				}      				
            				build_fault_type_block(file, doc, caras);
            			}else {
            				List<String> caras = new ArrayList<String>();
            				for(String s : dtc_labels) {
            					if(s.split("\\|")[0].equals(dtc)) {
            						String cara = s.split("\\|")[2];
                					if(entry.getValue().contains(cara)) {
                						if(!caras.contains(s)) {
                							caras.add(s);
                						}
                					}
            					}
            				}
            				build_fault_type_values_only(file, doc, caras);
            			}
            		}
            	}
            }else {
            	create_full_dtc_code_block(doc, file, DtcController.newRcdFinalData);
            	create_full_cara_block(doc, file, DtcController.newRcdFinalData, dtc_to_add);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void build_fault_type_values_only(String file, Document doc, List<String> caras) throws Exception {
		NodeList gpcEcuDTCPropertyDef = doc.getElementsByTagName("GpcEcuDTCPropertyDef");
		if(gpcEcuDTCPropertyDef.getLength() > 0) {
			for(int i=0; i<gpcEcuDTCPropertyDef.getLength(); i++) {
				Element def = (Element) gpcEcuDTCPropertyDef.item(i);
				String dtc = def.getAttribute("ShortName").trim().replace("DTC_FAULT_TYPE_", "");
				if(dtc.equals(caras.get(0).split("\\|")[0])) {
					Node gpcEcuDTCPropertyDiscretValues = def.getElementsByTagName("GpcEcuDTCPropertyDiscretValues").item(0);
					for(int j=0; j<caras.size(); j++) {
						Element property = doc.createElement("GpcEcuDTCPropertyDiscretValue");
			        	property.setAttribute("Label", "@TTBT"+caras.get(j).split("\\|")[3]);
			        	property.setAttribute("ShortName", "DTC_FAULT_TYPE_"+caras.get(j).split("\\|")[2]);
			        	gpcEcuDTCPropertyDiscretValues.appendChild(property);
						
					}
				}
			}
		}
        clean_doc_break_lines(doc);
    	write_doc(doc, new File(file));
	}
	
	
	public void build_fault_type_block(String file, Document doc, List<String> caras) throws Exception {
		Node GpcEcuDTCPropertiesDef = doc.getElementsByTagName("GpcEcuDTCPropertiesDef").item(0);
		
		Element newPropertyDef = doc.createElement("GpcEcuDTCPropertyDef");
		newPropertyDef.setAttribute("Label", "@P14473-POLUXDATA");
		newPropertyDef.setAttribute("PropertyType", "FAULT_TYPE");
		newPropertyDef.setAttribute("ServiceParamName", "DTC_FAULT_TYPE");
		newPropertyDef.setAttribute("ShortName", "DTC_FAULT_TYPE_"+caras.get(0).split("\\|")[0]);

        Element propertiesList = doc.createElement("GpcEcuDTCPropertyDiscretValues");
        newPropertyDef.appendChild(propertiesList);
        
        for(int i=0; i<caras.size(); i++) {
        	 Element property = doc.createElement("GpcEcuDTCPropertyDiscretValue");
        	 property.setAttribute("Label", "@TTBT"+caras.get(i).split("\\|")[3]);
        	 property.setAttribute("ShortName", "DTC_FAULT_TYPE_"+caras.get(i).split("\\|")[2]);
        	 propertiesList.appendChild(property);
        }
        
        GpcEcuDTCPropertiesDef.appendChild(newPropertyDef);
        clean_doc_break_lines(doc);
    	write_doc(doc, new File(file));
	}
	
	public void build_dtc_code_parameter(String file, Document doc, String DTC) throws Exception {
		Node gpcEcuDTCDef = doc.getElementsByTagName("GpcEcuDTCsDef").item(0);
		
		Element newDtcDef = doc.createElement("GpcEcuDTCDef");
		newDtcDef.setAttribute("DTCCode", DTC.split("\\|")[0]);
		newDtcDef.setAttribute("Label", "@TTBT"+DTC.split("\\|")[1]);
		newDtcDef.setAttribute("LabelEtude", "");

        Element gpcEcuDTCPropertiesRef = doc.createElement("GpcEcuDTCPropertiesRef");
        newDtcDef.appendChild(gpcEcuDTCPropertiesRef);

        Element gpcEcuDTCPropertyRef = doc.createElement("GpcEcuDTCPropertyRef");
        gpcEcuDTCPropertyRef.setAttribute("PropertyName", "DTC_FAULT_TYPE_"+DTC.split("\\|")[0]);
        gpcEcuDTCPropertiesRef.appendChild(gpcEcuDTCPropertyRef);

        Element gpcEcuDTCPropertyStatus = doc.createElement("GpcEcuDTCPropertyRef");
        gpcEcuDTCPropertyStatus.setAttribute("PropertyName", "DTC_STATUS_1");
        gpcEcuDTCPropertiesRef.appendChild(gpcEcuDTCPropertyStatus);
        
        gpcEcuDTCDef.appendChild(newDtcDef);
        
        clean_doc_break_lines(doc);
    	write_doc(doc, new File(file));
	}
	
	public void create_full_dtc_code_block(Document doc, String file, List<List<String>> allDtc) throws Exception {
		Node gpcEcu = doc.getElementsByTagName("GpcEcu").item(0);
		Element gpcEcuDTC = doc.createElement("GpcEcuDTC");
		
		Element gpcEcuDTCsDef = doc.createElement("GpcEcuDTCsDef");
		gpcEcuDTC.appendChild(gpcEcuDTCsDef);
		List<String> developped = new ArrayList<String>();
		for(List<String> dtcData : allDtc) {
			if(!developped.contains(dtcData.get(0))) {
				Element newDtcDef = doc.createElement("GpcEcuDTCDef");
				newDtcDef.setAttribute("DTCCode", dtcData.get(0));
				newDtcDef.setAttribute("Label", "@TTBT"+dtcData.get(1));
				newDtcDef.setAttribute("LabelEtude", "");

		        Element gpcEcuDTCPropertiesRef = doc.createElement("GpcEcuDTCPropertiesRef");
		        newDtcDef.appendChild(gpcEcuDTCPropertiesRef);

		        Element gpcEcuDTCPropertyRef = doc.createElement("GpcEcuDTCPropertyRef");
		        gpcEcuDTCPropertyRef.setAttribute("PropertyName", "DTC_FAULT_TYPE_"+dtcData.get(0));
		        gpcEcuDTCPropertiesRef.appendChild(gpcEcuDTCPropertyRef);

		        Element gpcEcuDTCPropertyStatus = doc.createElement("GpcEcuDTCPropertyRef");
		        gpcEcuDTCPropertyStatus.setAttribute("PropertyName", "DTC_STATUS_1");
		        gpcEcuDTCPropertiesRef.appendChild(gpcEcuDTCPropertyStatus);
		        
		        gpcEcuDTCsDef.appendChild(newDtcDef);
		        developped.add(dtcData.get(0));
			}
		}
		gpcEcu.appendChild(gpcEcuDTC);		
        clean_doc_break_lines(doc);
    	write_doc(doc, new File(file));
	}
	
	
	public void create_full_cara_block(Document doc, String file, List<List<String>> allDtc, Map<String, List<String>> dtc_to_add) throws Exception {
		Node gpcEcuDTC = doc.getElementsByTagName("GpcEcuDTC").item(0);
		Element gpcEcuDTCPropertiesDef = doc.createElement("GpcEcuDTCPropertiesDef");
		
		/**
		 * Preparing DTC data : each DTC associated to its properties with labels
		 * **/
		List<String> dtcList = new ArrayList<String>();
		for(Map.Entry<String, List<String>> entry : dtc_to_add.entrySet()) {
			if(!dtcList.contains(entry.getKey())) {
				dtcList.add(entry.getKey());
			}
		}
		System.out.println(dtcList);
		Map<String, List<String>>  dtcCaras = new HashMap<String, List<String>>();
		for(String dtc : dtcList) {
			List<String> cara_label = new ArrayList<String>();
			for(List<String> fullDtc : allDtc) {
				if(dtc.replace("$", "").equals(fullDtc.get(0))) {
					cara_label.add(fullDtc.get(2)+"|"+fullDtc.get(3));
				}
			}
			
			dtcCaras.put(dtc, cara_label);
		}
		
		/**
		 *  Building discret values properties for each DTC
		 * */
		for(Map.Entry<String, List<String>> entry : dtcCaras.entrySet()) {
			Element newPropertyDef = doc.createElement("GpcEcuDTCPropertyDef");
			newPropertyDef.setAttribute("Label", "@P14473-POLUXDATA");
			newPropertyDef.setAttribute("PropertyType", "FAULT_TYPE");
			newPropertyDef.setAttribute("ServiceParamName", "DTC_FAULT_TYPE");
			newPropertyDef.setAttribute("ShortName", "DTC_FAULT_TYPE_"+entry.getKey().replace("$", ""));
			
			Element discretValuesList = doc.createElement("GpcEcuDTCPropertyDiscretValues");
			List<String> caras = entry.getValue();
			for(String cara : caras) {
				System.out.println(entry);
				Element discretValue = doc.createElement("GpcEcuDTCPropertyDiscretValue");
				discretValue.setAttribute("Label", "@TTBT"+cara.split("\\|")[1]);
				discretValue.setAttribute("ShortName", "DTC_FAULT_TYPE_"+cara.split("\\|")[0].replace("$", ""));
				discretValuesList.appendChild(discretValue);
			}
			
			newPropertyDef.appendChild(discretValuesList);
			gpcEcuDTCPropertiesDef.appendChild(newPropertyDef);
		}
		gpcEcuDTC.appendChild(gpcEcuDTCPropertiesDef);
		clean_doc_break_lines(doc);
    	write_doc(doc, new File(file));
	}
	
	public void clean_doc_break_lines(Document doc) throws Exception {
		/*
		 * This removes all whitespace-only text nodes from the XML document.
		 * This helps prevent unwanted blank lines or indentation issues
		 * when writing the XML back to a file using Transformer.
		 * It ensures a cleaner and more consistent XML output.
		 *  
		 * */
		XPath xp = XPathFactory.newInstance().newXPath();
		NodeList emptyTextNodes = (NodeList) xp.evaluate("//text()[normalize-space(.)='']", doc, XPathConstants.NODESET);
		for (int m = 0; m < emptyTextNodes.getLength(); m++) {
		    Node emptyTextNode = emptyTextNodes.item(m);
		    emptyTextNode.getParentNode().removeChild(emptyTextNode);
		}
	}
	
	public void write_doc(Document doc, File xmlFile) throws Exception {
		doc.normalizeDocument();
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(xmlFile);
		transformer.transform(source, result);
	}
}
