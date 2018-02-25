package library;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException; 
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.*;

public class Library {
	private String filename;
	private HashMap<Integer, Book> books;
	private int size;
	private Document doc;
	private Element root;
	
	public Library(String libXMLFilename) {
		books = new HashMap<>();
		File libXMLFile = new File(libXMLFilename);
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
			this.size = 0;
			for (int i = 0; i < bookNodeList.getLength(); ++i) {
				Node bookNode = bookNodeList.item(size);
				//System.out.println(node.getNodeName());
				if (bookNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eBook = (Element) bookNode;
					tmpBook = new Book();
					// Fill attributes
					tmpBook.setTitle(eBook.getAttribute("title").trim());
					tmpBook.setAuthor(eBook.getAttribute("author").trim());
					tmpBook.setAge(eBook.getAttribute("age").trim());
					tmpBook.setIsbn(eBook.getAttribute("isbn13").trim());
	
					NodeList wordNodeList = eBook.getChildNodes();
					for (int j = 0; j < wordNodeList.getLength(); ++j) {
						Node wordNode = wordNodeList.item(j);
						if (wordNode.getNodeType() == Node.ELEMENT_NODE) {
							Element eWord = (Element) wordNode;
							tmpBook.setUniqueWordCount(Integer.parseInt(eWord.getAttribute("unique_words")));
							tmpBook.setTotalWordCount(Integer.parseInt(eWord.getAttribute("total_count")));
						}
					}
					books.put(i, tmpBook);
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
			count.setValue(Integer.toString(0));
			root.setAttributeNode(count);
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasDuplicate(Book book) {
		for (Book b : books.values()) {
			if (book.equals(b)) return true;
		}
		return false;
	}
	
	public void addBook(Book book) {
		if (!this.hasDuplicate(book)) {
			books.put(size++, book);
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
			bookNode.appendChild(wordsNode);
			root.appendChild(bookNode);
		}
	}
	
	public int save() {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource src = new DOMSource(doc);
			StreamResult result = new StreamResult(new File("C:\\" + filename));
			transformer.transform(src, result);
			return 1;
		}
		catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int size() { return size; }

	public void setFilename(String filename) { this.filename = filename; }
	public String getFilename() { return filename; }

	public Book getBook(int index) { return books.get(index); }
}
