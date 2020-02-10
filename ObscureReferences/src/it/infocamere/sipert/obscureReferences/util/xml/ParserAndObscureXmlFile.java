package it.infocamere.sipert.obscureReferences.util.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.infocamere.sipert.obscureReferences.db.dto.GenericResultsDTO;
import it.infocamere.sipert.obscureReferences.main.Main;
import it.infocamere.sipert.obscureReferences.util.Constants;

public class ParserAndObscureXmlFile {

	GenericResultsDTO peanaiUpdRisultatiDTO;
	GenericResultsDTO sigetatRisultatiDTO;
	String codDipendente;
	String nuovoCodDipendente;
	String fileNameInput;
	String pathFileDiOutput;
	String pathCompletoFileDiOutput;
	
	Logger log;

	public boolean tryObscureReferencesOnFile(String pathCompletoFileDiInput, String fileNameInput, String pathFileDiOutput,
			GenericResultsDTO sigetatRisultatiDTO, GenericResultsDTO peanaiUpdRisultatiDTO) {
		
		log = Logger.getLogger(Main.class);

		this.fileNameInput = fileNameInput;
		this.pathFileDiOutput = pathFileDiOutput;
		this.peanaiUpdRisultatiDTO = peanaiUpdRisultatiDTO;
		this.sigetatRisultatiDTO = sigetatRisultatiDTO;

		if (fileNameInput != null && fileNameInput.length() > 22) {
			codDipendente = fileNameInput.substring(16, 22);
		} else {
			log.warn("Anomalia nel nome del File: " + fileNameInput
					+ " >> troppo corto oppure non valido > impossibile recuperare il codice del dipendente");
			return false;
		}

		if (pathFileDiOutput == null || (pathFileDiOutput != null && pathFileDiOutput.length() < 1)) {
			log.error("percorso sul quale creare i File di output non valido >> è null oppure non presente");
			return false;
		}

		if (!fileDaTrattare()) {
			log.info("File: " + fileNameInput + " >> NON trattato/oscurato");
			return false;
		}

		try {
			// creating a constructor of file class and parsing an XML file
			File file = new File(pathCompletoFileDiInput);
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(file);
			if (document.hasChildNodes()) {
				checkNodeList(document.getChildNodes());
				newFileWithObscureReferencesFromXml(document);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean fileDaTrattare() {

		String cddipend = "";
		String codice = "";

		for (LinkedHashMap<String, Object> map : peanaiUpdRisultatiDTO.getListLinkedHashMap()) {
			Set entrySet = map.entrySet();
			Iterator it = entrySet.iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				if (me.getKey() instanceof String) {
					if (me.getKey().equals(Constants.PEANAI_UPD_CDDIPEND)) {
						if (me.getValue() instanceof String)
							cddipend = (String) me.getValue();
					}
					if (me.getKey().equals(Constants.PEANAI_UPD_CODICE)) {
						if (me.getValue() instanceof String)
							codice = (String) me.getValue();
					}
				}
			}
			if (codDipendente.trim().equals(cddipend)) {
				nuovoCodDipendente = codice;
				return true;
			}
		}

		return false;
	}

	private void checkNodeList(NodeList nodeList) {

		for (int count = 0; count < nodeList.getLength(); count++) {
			Node elemNode = nodeList.item(count);
			if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
				checkNodeForObscure(elemNode);
				if (elemNode.hasChildNodes()) {
					// se il nodo ha nodi figli allora chiamata ricorsiva
					checkNodeList(elemNode.getChildNodes());
				}  
			}
		}
	}

	private void checkNodeForObscure(Node nodo) {

		String nodeName = nodo.getNodeName().trim();
		String nodeTextContent = null;

		String progr = "";
		String etichetta = "";
		String tipo = "";
		String stringa = "";

		for (LinkedHashMap<String, Object> map : sigetatRisultatiDTO.getListLinkedHashMap()) {
			Set entrySet = map.entrySet();
			Iterator it = entrySet.iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				if (me.getKey() instanceof String) {
					if (me.getKey().equals(Constants.SIGETAT_PROGR)) {
						if (me.getValue() instanceof String)
							progr = (String) me.getValue();
					}
					if (me.getKey().equals(Constants.SIGETAT_ETICHETTA)) {
						if (me.getValue() instanceof String)
							etichetta = (String) me.getValue();
					}
					if (me.getKey().equals(Constants.SIGETAT_TIPO)) {
						if (me.getValue() instanceof String)
							tipo = (String) me.getValue();
					}
					if (me.getKey().equals(Constants.SIGETAT_STRINGA)) {
						if (me.getValue() instanceof String)
							stringa = (String) me.getValue();
					}
				}
			}
			if (etichetta.trim().equals(nodeName)) {
				nodeTextContent = nodo.getTextContent() instanceof String ? nodo.getTextContent() : null;
				if (tipo.equals(Constants.SIGETAT_TIPO_B))
					nodo.setTextContent(Constants.SIGETAT_TIPO_B_VALUE_ALT0160);
					//nodo.setTextContent(StringEscapeUtils.unescapeXml(Constants.SIGETAT_TIPO_B_VALUE));
				if (tipo.equals(Constants.SIGETAT_TIPO_S))
					nodo.setTextContent(stringa.trim());
				if (tipo.equals(Constants.SIGETAT_TIPO_K))
					nodo.setTextContent(nuovoCodDipendente);  
				break;
			}
		}
	}

	private void newFileWithObscureReferencesFromXml(Document document) {

		pathCompletoFileDiOutput = pathFileDiOutput  + "\\" + fileNameInput.replace(codDipendente , nuovoCodDipendente);
		
		try {
			// scrittura del contenuto su un file xml
			DOMSource source = new DOMSource(document);
			FileWriter writer = new FileWriter(new File(pathCompletoFileDiOutput));
			StreamResult result = new StreamResult(writer);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			//transformer.setOutputProperty(OutputKeys.ENCODING, "ASCII");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");			
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log.info("Creato File: " + pathCompletoFileDiOutput + " - Cod.dipendente da "
					+ codDipendente + " a " + nuovoCodDipendente);
		}

	}

}
