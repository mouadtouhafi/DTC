package com.automation.dtc.inputsdata;

import java.io.File;

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

public class ReadXMLMessaging {
	
	/*
	 * This method read the xml file and checks the existance of the parameter with the shortName "DTC_CODE".
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
                			build_fixed_values(doc, parameter_j);
                			clean_doc_break_lines(doc);
                			write_doc(doc, new File(file));
                			return true;
                			//build discret values
                		}
                	}
            	}
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	/* 
	 * If DTC_CODE parameter exists, we directly build the fixed values
	 * */
	public void build_fixed_values(Document doc, Element dtcCodeParameter) {
		NodeList fixedStatesList = dtcCodeParameter.getElementsByTagName("FixedStates");
		if (fixedStatesList.getLength() > 0) {
		    Element fixedStatesElement = (Element) fixedStatesList.item(0);
		    boolean exists = false;
		    NodeList fixedStateList = fixedStatesElement.getElementsByTagName("FixedState");
		    for(int dtc=0; dtc<ReadDtcTable.rcdFinalData.size(); dtc++) {
			    for (int k = 0; k < fixedStateList.getLength(); k++) {
			        Element fs = (Element) fixedStateList.item(k);
			        if (ReadDtcTable.rcdFinalData.get(dtc).get(0).equals(fs.getAttribute("Value"))) {
			            exists = true;
			        }
			    }

			    // Append new FixedState only if it doesn't already exist
			    if (!exists) {
			        Element fixedState = doc.createElement("FixedState");
			        fixedState.setAttribute("LongName", ReadDtcTable.rcdFinalData.get(dtc).get(1));
			        fixedState.setAttribute("ShortName", "DTC_CODE_"+ReadDtcTable.rcdFinalData.get(dtc).get(0));
			        fixedState.setAttribute("Value", ReadDtcTable.rcdFinalData.get(dtc).get(0));
			        
			        fixedStatesElement.appendChild(fixedState);
			    }
			    exists = false;
		    }
		}
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
