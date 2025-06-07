package com.automation.dtc.inputsdata;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.automation.dtc.blockbuilder.BuildDSDXmlBlock;

public class ReadXMLMessaging {

	BuildDSDXmlBlock buildDSDXmlBlock = new BuildDSDXmlBlock();

	/*
	 * This method read the xml file and checks the existance of the parameter with
	 * the shortName "DTC_CODE". 
	 * If it exists, the method will return true.
	 */
	public boolean dtc_code_parameter_exists(String file) {
		Element parameter_j = null;
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(file);
			doc.getDocumentElement().normalize();
			NodeList Parameter = doc.getElementsByTagName("Parameters");
			if (Parameter.getLength() > 0) {
				for (int i = 0; i < Parameter.getLength(); i++) {
					Element entry = (Element) Parameter.item(i);
					NodeList listParameters = entry.getElementsByTagName("Parameter");
					for (int j = 0; j < listParameters.getLength(); j++) {
						parameter_j = (Element) listParameters.item(j);
						String shortName = parameter_j.getAttribute("ShortName").trim();
						if (shortName.equals("DTC_CODE")) {
							System.out.println("exists case");
							buildDSDXmlBlock.build_fixed_values(doc, parameter_j);
							buildDSDXmlBlock.clean_doc_break_lines(doc);
							buildDSDXmlBlock.write_doc(doc, new File(file));
							return true;
						}
					}
				}
			}
			System.out.println("building build_dtc_code_parameter()");
			buildDSDXmlBlock.build_dtc_code_parameter(doc);
			buildDSDXmlBlock.clean_doc_break_lines(doc);
			buildDSDXmlBlock.write_doc(doc, new File(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/*
	 * This method read the xml file and checks the existance of the parameter with the shortName "DTC_FAULT_TYPE".
	 * If it exists, the method will return true.
	 * */
	public boolean fault_type_parameter_exists(String file) {
		Element parameter_j = null;
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
                		parameter_j = (Element) listParameters.item(j);
                		String shortName = parameter_j.getAttribute("ShortName").trim();
                		if(shortName.equals("DTC_FAULT_TYPE")) {
                			buildDSDXmlBlock.build_fault_type_fixed_values(doc, parameter_j);
                			buildDSDXmlBlock.clean_doc_break_lines(doc);
                			buildDSDXmlBlock.write_doc(doc, new File(file));
                			return true;
                		}
                	}
            	}
            }
            buildDSDXmlBlock.build_fault_type_parameter(doc);
            buildDSDXmlBlock.clean_doc_break_lines(doc);
            buildDSDXmlBlock.write_doc(doc, new File(file));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
