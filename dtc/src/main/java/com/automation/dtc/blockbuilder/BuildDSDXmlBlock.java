package com.automation.dtc.blockbuilder;

import java.io.File;

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

public class BuildDSDXmlBlock {
	/* 
	 * If DTC_CODE parameter exists, we directly build the fixed values
	 * */
	public void build_fixed_values(Document doc, Element dtcCodeParameter) {
		NodeList fixedStatesList = dtcCodeParameter.getElementsByTagName("FixedStates");
		if (fixedStatesList.getLength() > 0) {
		    Element fixedStatesElement = (Element) fixedStatesList.item(0);
		    boolean exists = false;
		    NodeList fixedStateList = fixedStatesElement.getElementsByTagName("FixedState");
		    for(int dtc=0; dtc<DtcController.newRcdFinalData.size(); dtc++) {
			    for (int k = 0; k < fixedStateList.getLength(); k++) {
			        Element fs = (Element) fixedStateList.item(k);
			        if (DtcController.newRcdFinalData.get(dtc).get(0).equals(fs.getAttribute("Value"))) {
			            exists = true;
			        }
			    }

			    // Append new FixedState only if it doesn't already exist
			    if (!exists) {
			        Element fixedState = doc.createElement("FixedState");
			        fixedState.setAttribute("LongName", DtcController.newRcdFinalData.get(dtc).get(1));
			        fixedState.setAttribute("ShortName", "DTC_CODE_"+DtcController.newRcdFinalData.get(dtc).get(0));
			        fixedState.setAttribute("Value", DtcController.newRcdFinalData.get(dtc).get(0));
			        
			        fixedStatesElement.appendChild(fixedState);
			    }
			    exists = false;
		    }
		}
	}
	
	/* 
	 * If DTC_FAULT_CODE parameter exists, we directly build the fixed values
	 * */
	public void build_fault_type_fixed_values(Document doc, Element faultCodeParameter) {
		NodeList fixedStatesList = faultCodeParameter.getElementsByTagName("FixedStates");
		if (fixedStatesList.getLength() > 0) {
		    Element fixedStatesElement = (Element) fixedStatesList.item(0);
		    boolean exists = false;
		    NodeList fixedStateList = fixedStatesElement.getElementsByTagName("FixedState");
		    for(int fault=0; fault<DtcController.newRcdFinalData.size(); fault++) {
			    for (int k = 0; k < fixedStateList.getLength(); k++) {
			        Element fs = (Element) fixedStateList.item(k);
			        if (DtcController.newRcdFinalData.get(fault).get(2).equals(fs.getAttribute("Value"))) {
			            exists = true;
			        }
			    }

			    // Append new FixedState only if it doesn't already exist
			    if (!exists) {
			        Element fixedState = doc.createElement("FixedState");
			        fixedState.setAttribute("LongName", DtcController.newRcdFinalData.get(fault).get(3));
			        fixedState.setAttribute("ShortName", "DTC_FAULT_TYPE_"+DtcController.newRcdFinalData.get(fault).get(2));
			        fixedState.setAttribute("Value", DtcController.newRcdFinalData.get(fault).get(2));
			        
			        fixedStatesElement.appendChild(fixedState);
			    }
			    exists = false;
		    }
		}
	}
	
	public void build_dtc_code_parameter(Document doc) throws Exception {
		Node parametersNode = doc.getElementsByTagName("Parameters").item(0);
		Element newParam = doc.createElement("Parameter");
        newParam.setAttribute("LongName", "");
        newParam.setAttribute("ShortName", "DTC_CODE");

        Element addressedData = doc.createElement("AddressedData");
        Element addressedBytes = doc.createElement("AddressedBytes");
        addressedBytes.setAttribute("ByteLength", "2");
        addressedBytes.setAttribute("ByteOrder", "__BigEndian");
        addressedData.appendChild(addressedBytes);
        newParam.appendChild(addressedData);

        Element data = doc.createElement("Data");
        Element binary = doc.createElement("Binary");
        binary.setAttribute("Encoding", "HEXA");

        Element fixedStates = doc.createElement("FixedStates");
        binary.appendChild(fixedStates);
        
        Element intervalStates = doc.createElement("IntervalStates");
        binary.appendChild(intervalStates);
        
        data.appendChild(binary);
        newParam.appendChild(data);
        
        parametersNode.appendChild(newParam);
        build_fixed_values(doc, newParam);
	}
	
	public void build_fault_type_parameter(Document doc) throws Exception {
		Node parametersNode = doc.getElementsByTagName("Parameters").item(0);
		Element newParam = doc.createElement("Parameter");
        newParam.setAttribute("LongName", "");
        newParam.setAttribute("ShortName", "DTC_FAULT_TYPE");

        Element addressedData = doc.createElement("AddressedData");
        Element addressedBytes = doc.createElement("AddressedBytes");
        addressedBytes.setAttribute("ByteLength", "1");
        addressedData.appendChild(addressedBytes);
        newParam.appendChild(addressedData);

        Element data = doc.createElement("Data");
        Element binary = doc.createElement("Binary");
        binary.setAttribute("Encoding", "ENUM");

        Element fixedStates = doc.createElement("FixedStates");
        binary.appendChild(fixedStates);
        
        Element intervalStates = doc.createElement("IntervalStates");
        binary.appendChild(intervalStates);
        
        data.appendChild(binary);
        newParam.appendChild(data);
        
        parametersNode.appendChild(newParam);
        build_fault_type_fixed_values(doc, newParam);
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
