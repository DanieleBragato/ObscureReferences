package it.infocamere.sipert.obscureReferences.util.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ParseXmlForSetLogFileName {
	
	private String logFileName;

	public InputStream setLogFileName(String logFileName, URL url) {
		
		this.logFileName = logFileName;
		
		InputStream is = null;
		
		try {
            URLConnection uConn = url.openConnection();
            uConn.setUseCaches(false);
            InputStream stream = uConn.getInputStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringComments(true);
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
			Document document = documentBuilder.parse(stream);
			if (document.hasChildNodes()) {
				checkNodeList(document.getChildNodes());
				///Add Doctype declaration to the extracted XML File
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer;
				ByteArrayOutputStream outputStream = null;
				try {
					transformer = transformerFactory.newTransformer();
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.METHOD, "xml");
					org.w3c.dom.DOMImplementation domImpl = document.getImplementation();
					DocumentType doctype = domImpl.createDocumentType("doctype", "SYSTEM" , "http://logging.apache.org/log4j/1.2/apidocs/org/apache/log4j/xml/doc-files/log4j.dtd");
					transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());
					DOMSource source = new DOMSource(document);
					outputStream = new ByteArrayOutputStream();
					DOMSource domsource = new DOMSource(document);
					Result outputTarget = new StreamResult(outputStream);
					transformer.transform(domsource, outputTarget);
				} catch (TransformerConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DOMException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (TransformerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (outputStream == null) {
					is = null;
				} else {
					is = new ByteArrayInputStream(outputStream.toByteArray());
				}
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return is;
	}

	private void checkNodeList(NodeList nodeList) {

		boolean cambioIlprossimo = false;
		boolean fermaCheck = false;
		
		for (int count = 0; count < nodeList.getLength() && !fermaCheck; count++) {
			Node elemNode = nodeList.item(count);
			if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = elemNode.getNodeName().trim();
				if (nodeName.equals("param")) {
					if (elemNode.hasAttributes()) {
						NamedNodeMap attrs = elemNode.getAttributes();
						for (int i = 0; i < attrs.getLength(); i++) {
							if (cambioIlprossimo && attrs.item(i).getNodeValue().equals("D:/SIPERT/ObscureReferencesLog.log")) {
								attrs.item(i).setNodeValue(this.logFileName);
								fermaCheck = true;
								break;
							}
							if (attrs.item(i).getNodeName().equals("name") && attrs.item(i).getNodeValue().equals("file")) cambioIlprossimo = true;
						 }
					}
				}
				if (elemNode.hasChildNodes() && !fermaCheck) {
					// se il nodo ha nodi figli allora chiamata ricorsiva
					checkNodeList(elemNode.getChildNodes());
				}  
			}
		}
	}	
	
}
