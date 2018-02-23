package library;

import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
	
	public Library(String libXMLFilename) {
		books = new HashMap<>();
		File libXMLFile = new File(libXMLFilename);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		Document doc;
		try {
			// Read library from XML file into memory
			dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(libXMLFile);
			doc.getDocumentElement().normalize();

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
					tmpBook.setISBN(eBook.getAttribute("isbn13").trim());
	
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
		}
	}
	
	public int save() {
		return 1;
	}
	
	public int size() { return size; }

	public void setFilename(String filename) { this.filename = filename; }
	public String getFilename() { return filename; }

	public Book getBook(int index) { return books.get(index); }
}
