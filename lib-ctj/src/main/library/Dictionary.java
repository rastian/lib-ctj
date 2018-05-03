package main.library;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
	private List<DictElement> entries;
	private int size;
	private Document doc;
	private Element root;
	
	public enum DictField {
		SPELLING, ARPABET, SYLLABLES, FREQUENCY, FUNCTION, 
		MORPHEMES, PSEGAVE, BIPHAVE, COGNATE, NEIGHBORHOOD
	}
	
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
								String cognate = eNode.getTextContent().trim();
								if (!cognate.equals("XXX"))
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

	public static Dictionary Dictionary(Library lib) {
		final Path userDir = Paths.get(System.getProperty("user.home")).resolve("lib-ctj");
		try {
			try {
				String newDictName = "dict-" + lib.getPath().getFileName().toString();
				Path newDictPath = lib.getPath().getParent().resolve(newDictName);
				ProcessBuilder pb = new ProcessBuilder(
						// the executable
						userDir.resolve("build_dictionary.exe").toString(), 
						// new dict name
						newDictPath.toAbsolutePath().toString(),
						// the cmu dict
						"-cmu", userDir.resolve("cmu-dict-0.0.7.txt").toString(),
						// the library
						"-lit", lib.getPath().toAbsolutePath().toString(),
						// the master dictionary
						"-old", userDir.resolve("dictionary-pk2g4-Aug-3rd-incomplete.xml").toString()
						);
				pb.redirectErrorStream(true);
				System.out.println(newDictName + " generating...");
				Process p = pb.start();
				p.waitFor();
				System.out.println(newDictName + " generated");
				return new Dictionary(newDictPath);
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	
	public int saveAsCSV(Path path, List<DictField> fields) {
		try {
			FileWriter fw = new FileWriter(path.toString().replaceAll(".xml", ".csv"));
			BufferedWriter bw = new BufferedWriter(fw);
			
			// Write Header Columns
			StringBuffer header = new StringBuffer(",");
			header.append(fields.contains(DictField.ARPABET) ? "ARPABET," : "");
			header.append(fields.contains(DictField.SYLLABLES) ? "Syllables," : "");
			header.append(fields.contains(DictField.FREQUENCY) ? "Frequency," : "");
			header.append(fields.contains(DictField.FUNCTION) ? "Function word," : "");
			header.append(fields.contains(DictField.MORPHEMES) ? "Morphemes," : "");
			header.append(fields.contains(DictField.PSEGAVE) ? "PSA," : "");
			header.append(fields.contains(DictField.BIPHAVE) ? "BIPHA," : "");
			header.append(fields.contains(DictField.COGNATE) ? "Cognate," : "");
			header.append(fields.contains(DictField.NEIGHBORHOOD) ? "Neighborhood,,,,,," : "");
			header.append("\n");
			bw.append(header);

			// Write fields for each DictElem
			for (DictElement e : entries) {
				bw.append(e.getSpelling()).append(",");
				bw.append(fields.contains(DictField.ARPABET) ? e.getArpabet() + "," : "");
				bw.append(fields.contains(DictField.SYLLABLES) ? Integer.toString(e.getSyllables()) + "," : "");
				bw.append(fields.contains(DictField.FREQUENCY) ? Integer.toString(e.getFrequency()) + "," : "");
				bw.append(fields.contains(DictField.FUNCTION) ? (e.isFunction() ? "TRUE" : "FALSE") + "," : "");
				bw.append(fields.contains(DictField.MORPHEMES) ? e.getMorphemes() + ",": "");
				bw.append(fields.contains(DictField.PSEGAVE) ? Double.toString(e.getPSegAve()) + "," : "");
				bw.append(fields.contains(DictField.BIPHAVE) ? Double.toString(e.getBiphAve()) + "," : "");
				bw.append(fields.contains(DictField.COGNATE) ? (e.getCognate() != null ? e.getCognate() : "") + "," : "");
				if (fields.contains(DictField.NEIGHBORHOOD)) {
					bw.append(Integer.toString(e.getNeighborhood().length)).append(",");
					for (String n : e.getNeighborhood()) {
						bw.append(n).append(",");
					}
				}
				bw.append("\n");
			}

			bw.close();
			return 1;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int saveAsCSV(Path path) {
		List<DictField> fields = new ArrayList<>();
		for (DictField field : DictField.values()) {
			fields.add(field);
		}
		return saveAsCSV(path, fields);
	}
	
	public List<DictElement> getElements() {
		return entries;
	}
	public Path getPath() {
		return path;
	}
}
