package main.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
import java.nio.file.Files;
import java.nio.file.Path;

import org.xml.sax.SAXException;

import javafx.collections.ObservableList; 

public class Library {
	private String name;
	private Path path;
	private List<Book> books;
	private int size;
	private Document doc;
	private Element root;
	
	public enum FilterFuncs {
		// Used for filtering string fields
		CONTAINS, STARTS_WITH, ENDS_WITH,
		// Used for filtering numeric fields
		EQUALS, LESS_THAN, GREATER_THAN
	}

	public enum BookFields {
		TITLE, AUTHOR, AGE, ISBN, COMPLETE, GENRE, UNIQUE_WORD_COUNT, TOTAL_WORD_COUNT
	}
	
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
					// Makes sure genre/complete fields exist and if not create and add the attributes to the DOM
					// along with default values of genre="unknown" and complete="yes"
					if (eBook.hasAttribute("complete")) {
						tmpBook.setComplete(eBook.getAttribute("complete").trim().equals("yes"));
					}
					else {
						Attr complete = doc.createAttribute("complete");
						complete.setValue("yes");
						tmpBook.setComplete(true);
						eBook.setAttributeNode(complete);
					}
					if (eBook.hasAttribute("genre")) {
						tmpBook.setGenre(eBook.getAttribute("genre").trim());
					}
					else {
						Attr genre = doc.createAttribute("genre");
						genre.setValue("unknown");
						tmpBook.setGenre("unknown");
						eBook.setAttributeNode(genre);
					}

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
			path = Files.createTempFile("new-", ".xml");
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
			Attr complete = doc.createAttribute("complete");
			Attr genre = doc.createAttribute("genre");
			age.setValue(book.getAge().toUpperCase());
			author.setValue(book.getAuthor());
			title.setValue(book.getTitle());
			isbn.setValue(book.getIsbn());
			complete.setValue(book.isComplete() ? "yes" : "no");
			genre.setValue(book.getGenre());
			bookNode.setAttributeNode(age);
			bookNode.setAttributeNode(author);
			bookNode.setAttributeNode(title);
			bookNode.setAttributeNode(isbn);
			bookNode.setAttributeNode(complete);
			bookNode.setAttributeNode(genre);
			
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

	public void delete(ObservableList<Book> selected) {
		NodeList bookNodes = root.getElementsByTagName("Book");
		for (Book book : selected) {
			String title = book.getTitle();
			String isbn = book.getIsbn();
			System.out.println("Deleting '" + title + "' '" +  isbn + "'");
			for (int i = 0; i < bookNodes.getLength(); ++i) {
				Element bookNode = (Element) bookNodes.item(i);
				if (bookNode.getAttribute("title").trim().equals(title) && bookNode.getAttribute("isbn13").trim().equals(isbn)) {
					System.out.println("Book Node: '" + bookNode.getAttribute("title") + "' '" + bookNode.getAttribute("isbn13") + "'");
					root.removeChild((Node)bookNode);
				}
			}
		}
		books.removeAll(selected);
		size -= selected.size();
		root.setAttribute("count", Integer.toString(size));
		
	}

	public Book delete(int index) {
		if (index <= size) {
			// Find book to be deleted
			Book b = books.get(index);
			books.remove(index);
			--size;
			NodeList bookNodes = root.getElementsByTagName("Book");
			for (int i = 0; i < bookNodes.getLength(); ++i) {
				Element bookNode = (Element) bookNodes.item(i);
				if (bookNode.getAttribute("title").trim().equals(b.getTitle()) && bookNode.getAttribute("isbn13").trim().equals(b.getIsbn())) {
					root.removeChild((Node)bookNode);
				}
			}
			root.setAttribute("count", Integer.toString(size));
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
	
	public static Library merge(List<Library> libs) {
		Library newLib = new Library();
		for (Library lib : libs) {
			for (int i = 0; i < lib.size(); ++i) {
				Book b = lib.getBook(i);
				if (!newLib.hasDuplicate(b)) {
					newLib.addBook(b);
				}
			}
		}
		return newLib;
	}
	
	public Library filter(HashMap<BookFields, Object> filterMap) {
		try {
			List<Book> filteredBooks = new ArrayList<>(books);
			Library fLib = new Library();
			// Create temporary file for the filtered library
			fLib.setPath(Files.createTempFile(
					"f-"+this.getPath().getFileName().toString(),
					".xml"));
			
			Object[] filterArr;
			FilterFuncs func;
			List<Book> tmpBooks;
			if (filterMap.containsKey(BookFields.TITLE)) {
				filterArr = (Object[])filterMap.get(BookFields.TITLE);
				func = (FilterFuncs)filterArr[0];
				List<String> titles = Arrays.asList((String[])filterArr[1]);
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					boolean delFlag = true;
					for (String title : titles) {
						if (b.getTitle().toLowerCase().contains(title)) 
							delFlag = false;
					}
					if (delFlag) filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.AUTHOR)) {
				filterArr = (Object[])filterMap.get(BookFields.AUTHOR);
				func = (FilterFuncs)filterArr[0];
				List<String> authors = Arrays.asList((String[])filterArr[1]);
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					boolean delFlag = true;
					for (String author : authors) {
						if (b.getAuthor().toLowerCase().contains(author))
							delFlag = false;
					}
					if (delFlag) filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.AGE)) {
				filterArr = (Object[])filterMap.get(BookFields.AGE);
				func = (FilterFuncs)filterArr[0];
				List<String> ages = Arrays.asList((String[])filterArr[1]);
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					boolean delFlag = true;
					for (String age : ages) {
						if (b.getAge().toLowerCase().equals(age))
							delFlag = false;
					}
					if (delFlag) filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.ISBN)) {
				filterArr = (Object[])filterMap.get(BookFields.ISBN);
				func = (FilterFuncs)filterArr[0];
				List<String> isbns = Arrays.asList((String[])filterArr[1]);
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					boolean delFlag = true;
					for (String isbn : isbns) {
						if (b.getIsbn().toLowerCase().equals(isbn))
							delFlag = false;
					}
					if (delFlag) filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.COMPLETE)) {
				filterArr = (Object[])filterMap.get(BookFields.COMPLETE);
				func = (FilterFuncs)filterArr[0];
				boolean complete = (boolean)filterArr[1];
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					if (b.isComplete() != complete)
						filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.GENRE)) {
				filterArr = (Object[])filterMap.get(BookFields.GENRE);
				func = (FilterFuncs)filterArr[0];
				List<String> genres = Arrays.asList((String[])filterArr[1]);
				tmpBooks = new ArrayList<>(filteredBooks);
				for (Book b : tmpBooks) {
					boolean delFlag = true;
					for (String genre : genres) {
						if (b.getGenre().toLowerCase().equals(genre))
							delFlag = false;
					}
					if (delFlag) filteredBooks.remove(b);
				}
			}
			if (filterMap.containsKey(BookFields.UNIQUE_WORD_COUNT)) {
				HashMap<FilterFuncs, Integer> funcs = (HashMap<FilterFuncs, Integer>)filterMap.get(BookFields.UNIQUE_WORD_COUNT);
				tmpBooks = new ArrayList<>(filteredBooks);
				if (funcs.containsKey(FilterFuncs.GREATER_THAN)) {
					int lowerLimit = funcs.get(FilterFuncs.GREATER_THAN);
					for (Book b : tmpBooks) {
						if (b.getUniqueWordCount() < lowerLimit) filteredBooks.remove(b); 
					}
				}
				tmpBooks = new ArrayList<>(filteredBooks);
				if (funcs.containsKey(FilterFuncs.LESS_THAN)) {
					int upperLimit = funcs.get(FilterFuncs.LESS_THAN);
					for (Book b : tmpBooks) {
						if (b.getUniqueWordCount() > upperLimit) filteredBooks.remove(b);
					}
				}
			}
			if (filterMap.containsKey(BookFields.TOTAL_WORD_COUNT)) {
				HashMap<FilterFuncs, Integer> funcs = (HashMap<FilterFuncs, Integer>)filterMap.get(BookFields.TOTAL_WORD_COUNT);
				tmpBooks = new ArrayList<>(filteredBooks);
				if (funcs.containsKey(FilterFuncs.GREATER_THAN)) {
					int lowerLimit = funcs.get(FilterFuncs.GREATER_THAN);
					for (Book b : tmpBooks) {
						if (b.getTotalWordCount() < lowerLimit) filteredBooks.remove(b); 
					}
				}
				tmpBooks = new ArrayList<>(filteredBooks);
				if (funcs.containsKey(FilterFuncs.LESS_THAN)) {
					int upperLimit = funcs.get(FilterFuncs.LESS_THAN);
					for (Book b : tmpBooks) {
						if (b.getTotalWordCount() > upperLimit) filteredBooks.remove(b);
					}
				}
			}

			// Add all filtered books to fLib
			for (Book b : filteredBooks) {
				fLib.addBook(b);
			}
			return fLib;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public int size() { return size; }

	public void setPath(Path path) { this.path = path; }
	public Path getPath() { return path; }

	public Book getBook(int index) { return books.get(index); }
	
	public String getName() {return name; }
	public void setName(String name) { this.name = name; }
	
	public boolean isEmpty() {
		return size == 0;
	}
}
