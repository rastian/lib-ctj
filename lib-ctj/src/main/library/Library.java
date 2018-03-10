package main.library;

import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException; 
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;

public class Library {
	private Path path;
	private ArrayList<Book> books;
	private int size;
	private Document doc;
	private Element root;
	
	public Library(Path libXMLPath) {
		path = libXMLPath;
		books = new ArrayList<>();
		File libXMLFile = new File(path.toString());
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;

		try {
			// Read library from XML file into memory
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(libXMLFile);
			doc.getDocumentElement().normalize();
			
			root = doc.getDocumentElement();

			// Get all books
			NodeList bookNodeList = doc.getElementsByTagName("Book");
			Book tmpBook;
			HashMap<String, Integer> wordMap;
			this.size = 0;
			for (int i = 0; i < bookNodeList.getLength(); ++i) {
				Node bookNode = bookNodeList.item(size);
				if (bookNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eBook = (Element) bookNode;
					tmpBook = new Book();
					wordMap = new HashMap<>();
					// Fill attributes
					tmpBook.setTitle(eBook.getAttribute("title").trim());
					tmpBook.setAuthor(eBook.getAttribute("author").trim());
					tmpBook.setAge(eBook.getAttribute("age").trim());
					tmpBook.setIsbn(eBook.getAttribute("isbn13").trim());

					NodeList wordsNodeList = eBook.getChildNodes();
					for (int j = 0; j < wordsNodeList.getLength(); ++j) {
						Node wordsNode = wordsNodeList.item(j);
						if (wordsNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eWords = (Element) wordsNode;
							tmpBook.setUniqueWordCount(Integer.parseInt(eWords.getAttribute("unique_words")));
							tmpBook.setTotalWordCount(Integer.parseInt(eWords.getAttribute("total_count")));
							// Fill the book wordMap
							NodeList wordNodeList = eWords.getChildNodes();
							for (int k = 0; k < wordNodeList.getLength(); ++k) {
								Node wordNode = wordNodeList.item(k);
								if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eWord = (Element) wordNode;
									wordMap.put(eWord.getTextContent(), Integer.parseInt(eWord.getAttribute("freq")));
								}
							}
						}
					}
					tmpBook.setWordMap(wordMap);
					books.add(tmpBook);
					++size;
				}
			}	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		catch (SAXException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Library() {
		books = new ArrayList<>();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		size = 0;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.newDocument();
			
			// Create root element
			root = doc.createElement("Literature");
			doc.appendChild(root);
			Attr count = doc.createAttribute("count");
			count.setValue(Integer.toString(size));
			root.setAttributeNode(count);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasDuplicate(Book book) {
		if (this.isEmpty())
			return false;
		for (Book b : books) {
			if (book.equals(b)) return true;
		}
		return false;
	}
	
	public void addBook(Book book) {
		if (!this.hasDuplicate(book)) {
			books.add(book);
			++size;
			root.setAttribute("count", Integer.toString(size));

			/* Create Book Node */
			Element bookNode = doc.createElement("Book");
			// Create/Set book attributes
			Attr age = doc.createAttribute("age");
			Attr author = doc.createAttribute("author");
			Attr title = doc.createAttribute("title");
			Attr isbn = doc.createAttribute("isbn13");
			age.setValue(book.getAge());
			author.setValue(book.getAuthor());
			title.setValue(book.getTitle());
			isbn.setValue(book.getIsbn());
			bookNode.setAttributeNode(age);
			bookNode.setAttributeNode(author);
			bookNode.setAttributeNode(title);
			bookNode.setAttributeNode(isbn);
			
			/* Create Words Node */
			Element wordsNode = doc.createElement("Words");
			// Create/Set words attributes
			Attr uniqueWordCount = doc.createAttribute("unique_words");
			Attr totalWordCount = doc.createAttribute("total_count");
			uniqueWordCount.setValue(Integer.toString(book.getUniqueWordCount()));
			totalWordCount.setValue(Integer.toString(book.getTotalWordCount()));
			wordsNode.setAttributeNode(uniqueWordCount);
			wordsNode.setAttributeNode(totalWordCount);
			
			// Set individual word nodes
			Element word;
			HashMap<String, Integer> wordMap = book.getWordMap();
			for (String w : wordMap.keySet()) {
				word = doc.createElement("W");
				Attr freq = doc.createAttribute("freq");
				freq.setValue(Integer.toString(wordMap.get(w)));
				word.setAttributeNode(freq);
				word.appendChild(doc.createTextNode(w));
				wordsNode.appendChild(word);
			}
			root.appendChild(bookNode);
			bookNode.appendChild(wordsNode);
		}
	}
	
	public Book delete(int index) {
		if (index <= size) {
			// Find book to be deleted
			Book b = books.get(index);
			books.remove(index);
			--size;
			return b;
		}
		else {
			throw new IndexOutOfBoundsException();
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
	
	public Library merge(Library lib) {
		Library newLib = new Library();
		// Add books from first library
		for (Book b : this.books) {
			newLib.addBook(b);
		}
		// Add books from second library, except duplicates
		for (Book b : lib.books) {
			if (!this.hasDuplicate(b)) {
				newLib.addBook(b);
			}
		}
		return newLib;
	}
	
	public int size() { return size; }

	public void setPath(Path path) { this.path = path; }
	public Path getPath() { return path; }

	public Book getBook(int index) { return books.get(index); }
	public boolean isEmpty() {
		if(size == 0)
			return true;
		return false;
	}
}
