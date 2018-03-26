package main.library;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Dictionary {
	private Path path;
	private ArrayList<DictElement> entries;
	private int size;
	private Document doc;
	private Element root;
	
	public Dictionary(Path dictXMLPath) {
		path = dictXMLPath;
		entries = new ArrayList<>();
		File dictXMLFile = new File(path.toString());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		
		try {
			// Read dictionary from XML file into memory
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(dictXMLFile);
			doc.getDocumentElement().normalize();
			
			root = doc.getDocumentElement();
			
			// Get all entries
			NodeList entryNodes = doc.getElementsByTagName("W");
			DictElement tmpEntry;
			this.size = 0;
			for (int i = 0; i < entryNodes.getLength(); ++i) {
				Node entryNode = entryNodes.item(size);
				if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
					Element entry = (Element) entryNode;
					tmpEntry = new DictElement();
					
					// Fill attributes
					tmpEntry.setFunction(entry.getAttribute("function").trim().equals("yes"));
					tmpEntry.setFrequency(Integer.parseInt(entry.getAttribute("frequency").trim()));
					tmpEntry.setSyllables(Integer.parseInt(entry.getAttribute("syllables").trim()));
					tmpEntry.setSpelling(entry.getAttribute("spelling").trim());
					
					NodeList childNodes = entry.getChildNodes();
					for (int j = 0; j < childNodes.getLength(); ++j) {
						Node childNode = childNodes.item(j);
						if (childNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eNode = (Element) childNode;
							switch (eNode.getTagName()) {
							case "Arpabet": 
								tmpEntry.setArpabet(eNode.getTextContent().trim());
								break;
							case "Morphemes":
								tmpEntry.setMorphemes(eNode.getTextContent().trim());
								break;
							case "Cognate":
								tmpEntry.setCognate(eNode.getTextContent().trim());
								break;
							case "BiphAve":
								tmpEntry.setBiphAve(Double.parseDouble(eNode.getTextContent().trim()));
								break;
							case "PSegAve":
								tmpEntry.setpSegAve(Double.parseDouble(eNode.getTextContent().trim()));
								break;
							case "Neighborhood":
								NodeList neighborList = eNode.getElementsByTagName("Neighbor");
								String[] neighborhood = new String[neighborList.getLength()];
								for (int k = 0; k < neighborList.getLength(); ++k) {
									Node neighborNode = neighborList.item(k);
									if (neighborNode.getNodeType() == Node.ELEMENT_NODE) {
										Element neighborElem = (Element) neighborNode;
										neighborhood[k] = neighborElem.getTextContent().trim();
									}
								}
								tmpEntry.setNeighborhood(neighborhood);
								break;
							default:
								break;
							}
						}
					}
					++size;
					entries.add(tmpEntry);
				}
			}
		}
		catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public int save(Path path) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource src = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path.toString()));
			transformer.transform(src, result);
			return 1;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int save() {
		return save(path);
	}
	
	public ArrayList<DictElement> getElements(){
		return entries;
	}
	public Path getPath() {
		return path;
	}
}
