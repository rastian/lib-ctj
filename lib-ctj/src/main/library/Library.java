package main.library;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
import java.nio.file.Paths;

import org.xml.sax.SAXException;


public class Library {
	private Path path;
	private List<Book> books;

	public enum FilterFuncs {
		// Used for filtering string fields
		CONTAINS, STARTS_WITH, ENDS_WITH,
		// Used for filtering numeric fields or for string equality
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
			Document doc = dBuilder.parse(libXMLFile);
			doc.getDocumentElement().normalize();
			
			// Ensure that XML document is a Library file
			Element root = doc.getDocumentElement();
			if (root.getTagName().equals("Literature")) {
				NodeList bookNodeList = doc.getElementsByTagName("Book");
				Book tmpBook;
				HashMap<String, Integer> wordMap;
				for (int i = 0; i < bookNodeList.getLength(); ++i) {
					Node bookNode = bookNodeList.item(i);
					if (bookNode.getNodeType() == Node.ELEMENT_NODE && bookNode.getNodeName().equals("Book")) {
						Element bookElem = (Element) bookNode;
						tmpBook = new Book();
						wordMap = new HashMap<>();
						// Fill basic book attributes
						tmpBook.setTitle(bookElem.getAttribute("title").trim());
						tmpBook.setAuthor(bookElem.getAttribute("author").trim());
						tmpBook.setAge(bookElem.getAttribute("age").toUpperCase().trim());
						tmpBook.setIsbn(bookElem.getAttribute("isbn13").trim());
						// Make sure genre/complete fields exist and if not create and add the attributes to the DOM
						// along with default values of genre="unknown" and complete="yes"
						if (bookElem.hasAttribute("complete")) {
							tmpBook.setComplete(bookElem.getAttribute("complete").trim().equals("yes"));
						}
						else {
							Attr complete = doc.createAttribute("complete");
							complete.setValue("yes");
							tmpBook.setComplete(true);
							bookElem.setAttributeNode(complete);
						}
						if (bookElem.hasAttribute("genre")) {
							tmpBook.setGenre(bookElem.getAttribute("genre").toLowerCase().trim());
						}
						else {
							Attr genre = doc.createAttribute("genre");
							genre.setValue("unknown");
							tmpBook.setGenre("unknown");
							bookElem.setAttributeNode(genre);
						}

						// Read in all words/counts from Book
						NodeList bookElemChildren = bookElem.getChildNodes();
						for (int j = 0; j < bookElemChildren.getLength(); ++j) {
							Node wordsListNode = bookElemChildren.item(j);
							if (wordsListNode.getNodeType() == Node.ELEMENT_NODE && wordsListNode.getNodeName().equals("Words")) {
								Element wordsListElem = (Element) wordsListNode;
								tmpBook.setUniqueWordCount(Integer.parseInt(wordsListElem.getAttribute("unique_words")));
								tmpBook.setTotalWordCount(Integer.parseInt(wordsListElem.getAttribute("total_count")));
								// Fill the book wordMap
								NodeList wordsListChildren = wordsListElem.getChildNodes();
								for (int k = 0; k < wordsListChildren.getLength(); ++k) {
									Node wordNode = wordsListChildren.item(k);
									if (wordNode.getNodeType() == Node.ELEMENT_NODE && wordNode.getNodeName().equals("W")) {
										Element wordElem = (Element) wordNode;
										wordMap.put(wordElem.getTextContent(), Integer.parseInt(wordElem.getAttribute("freq")));
									}
								}
							}
						}
						tmpBook.setWordMap(wordMap);
						books.add(tmpBook);
					}
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

	public Library() {
		books = new ArrayList<>();
		try {
			path = Files.createTempFile("new-", ".xml");
		} catch (IOException e) {
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
		}
	}

	public void delete(List<Book> selected) {
		books.removeAll(selected);
	}

	public Book delete(int index) {
		if (index <= books.size()) {
			// Find book to be deleted
			Book b = books.get(index);
			books.remove(index);
			return b;
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}
	
	public int save(Path path) {
		try {
			// Dump state of library into XML file
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			
			Element root = doc.createElement("Literature");
			root.setAttribute("count", Integer.toString(books.size()));
			doc.appendChild(root);
			
			// Create all book nodes
			Collections.sort(books);
			for (Book book : books) {
				// Create Book node
				Element bookNode = doc.createElement("Book");
				// Set book node attributes
				bookNode.setAttribute("age", book.getAge().trim().toUpperCase());
				bookNode.setAttribute("author", book.getAuthor());
				bookNode.setAttribute("title", book.getTitle());
				bookNode.setAttribute("isbn13", book.getIsbn());
				bookNode.setAttribute("complete", book.isComplete() ? "yes" : "no");
				bookNode.setAttribute("genre", book.getGenre());
				
				// Create Words node
				Element wordsNode = doc.createElement("Words");
				// set words node attributes
				wordsNode.setAttribute("unique_words", Integer.toString(book.getUniqueWordCount()));
				wordsNode.setAttribute("total_count", Integer.toString(book.getTotalWordCount()));
				
				// Create invididual word elements
				Element word;
				Map<String, Integer> wordMap = book.getWordMap();
				List<String> words = new ArrayList<>(wordMap.keySet());
				Collections.sort(words);
				for (String w : words) {
					word = doc.createElement("W");
					word.setAttribute("freq", Integer.toString(wordMap.get(w)));
					word.appendChild(doc.createTextNode(w));
					wordsNode.appendChild(word);
				}
				root.appendChild(bookNode);
				bookNode.appendChild(wordsNode);
			}

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
		List<Book> filteredBooks = new ArrayList<>(books);
		Library fLib = new Library();

		String libFilename = this.getPath().getFileName().toString();
		StringBuilder newFilename = new StringBuilder(libFilename.substring(0, libFilename.indexOf(".xml")));

		Object[] filterArr;
		FilterFuncs func;
		List<Book> tmpBooks;
		if (filterMap.containsKey(BookFields.TITLE)) {
			filterArr = (Object[])filterMap.get(BookFields.TITLE);
			func = (FilterFuncs)filterArr[0];
			List<String> titles = Arrays.asList((String[])filterArr[1]);
			for (String title : titles) {
				newFilename.append("-").append(title);
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				boolean delFlag = true;
				for (String title : titles) {
					if (b.getTitle().toLowerCase().contains(title)) { 
						delFlag = false;
					}
				}
				if (delFlag) {
					filteredBooks.remove(b);
				}
			}
		}
		if (filterMap.containsKey(BookFields.AUTHOR)) {
			filterArr = (Object[])filterMap.get(BookFields.AUTHOR);
			func = (FilterFuncs)filterArr[0];
			List<String> authors = Arrays.asList((String[])filterArr[1]);
			for (String author : authors) {
				newFilename.append("-").append(author);
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				boolean delFlag = true;
				for (String author : authors) {
					if (b.getAuthor().toLowerCase().contains(author)) {
						delFlag = false;
					}
				}
				if (delFlag) {
					filteredBooks.remove(b);
				}
			}
		}
		if (filterMap.containsKey(BookFields.AGE)) {
			filterArr = (Object[])filterMap.get(BookFields.AGE);
			func = (FilterFuncs)filterArr[0];
			List<String> ages = Arrays.asList((String[])filterArr[1]);
			for (String age : ages) {
				newFilename.append("-").append(age);
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				boolean delFlag = true;
				for (String age : ages) {
					if (b.getAge().toLowerCase().equals(age)) {
						delFlag = false;
					}
				}
				if (delFlag) {
					filteredBooks.remove(b);
				}
			}
		}
		if (filterMap.containsKey(BookFields.ISBN)) {
			filterArr = (Object[])filterMap.get(BookFields.ISBN);
			func = (FilterFuncs)filterArr[0];
			List<String> isbns = Arrays.asList((String[])filterArr[1]);
			for (String isbn : isbns) {
				newFilename.append("-").append(isbn);
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				boolean delFlag = true;
				for (String isbn : isbns) {
					if (b.getIsbn().toLowerCase().equals(isbn)) {
						delFlag = false;
					}
				}
				if (delFlag) {
					filteredBooks.remove(b);
				}
			}
		}
		if (filterMap.containsKey(BookFields.COMPLETE)) {
			filterArr = (Object[])filterMap.get(BookFields.COMPLETE);
			func = (FilterFuncs)filterArr[0];
			boolean complete = (boolean)filterArr[1];
			if (complete) {
				newFilename.append("-").append("complete");
			}
			else {
				newFilename.append("-").append("incomplete");
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				if (b.isComplete() != complete) {
					filteredBooks.remove(b);
				}
			}
		}
		if (filterMap.containsKey(BookFields.GENRE)) {
			filterArr = (Object[])filterMap.get(BookFields.GENRE);
			func = (FilterFuncs)filterArr[0];
			List<String> genres = Arrays.asList((String[])filterArr[1]);
			for (String genre : genres) {
				newFilename.append("-").append(genre);
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			for (Book b : tmpBooks) {
				boolean delFlag = true;
				for (String genre : genres) {
					if (b.getGenre().toLowerCase().equals(genre)) {
						delFlag = false;
					}
				}
				if (delFlag) filteredBooks.remove(b);
			}
		}
		if (filterMap.containsKey(BookFields.UNIQUE_WORD_COUNT)) {
			HashMap<FilterFuncs, Integer> funcs = (HashMap<FilterFuncs, Integer>)filterMap.get(BookFields.UNIQUE_WORD_COUNT);
			tmpBooks = new ArrayList<>(filteredBooks);
			if (funcs.containsKey(FilterFuncs.GREATER_THAN)) {
				int lowerLimit = funcs.get(FilterFuncs.GREATER_THAN);
				newFilename.append("-UWgreater").append(lowerLimit);
				for (Book b : tmpBooks) {
					if (b.getUniqueWordCount() < lowerLimit) {
						filteredBooks.remove(b); 
					}
				}
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			if (funcs.containsKey(FilterFuncs.LESS_THAN)) {
				int upperLimit = funcs.get(FilterFuncs.LESS_THAN);
				newFilename.append("-UWless").append(upperLimit);
				for (Book b : tmpBooks) {
					if (b.getUniqueWordCount() > upperLimit) { 
						filteredBooks.remove(b);
					}
				}
			}
		}
		if (filterMap.containsKey(BookFields.TOTAL_WORD_COUNT)) {
			HashMap<FilterFuncs, Integer> funcs = (HashMap<FilterFuncs, Integer>)filterMap.get(BookFields.TOTAL_WORD_COUNT);
			tmpBooks = new ArrayList<>(filteredBooks);
			if (funcs.containsKey(FilterFuncs.GREATER_THAN)) {
				int lowerLimit = funcs.get(FilterFuncs.GREATER_THAN);
				newFilename.append("-TWgreater").append(lowerLimit);
				for (Book b : tmpBooks) {
					if (b.getTotalWordCount() < lowerLimit) {
						filteredBooks.remove(b); 
					}
				}
			}
			tmpBooks = new ArrayList<>(filteredBooks);
			if (funcs.containsKey(FilterFuncs.LESS_THAN)) {
				int upperLimit = funcs.get(FilterFuncs.LESS_THAN);
				newFilename.append("-TWless").append(upperLimit);
				for (Book b : tmpBooks) {
					if (b.getTotalWordCount() > upperLimit) {
						filteredBooks.remove(b);
					}
				}
			}
		}
		newFilename.append(".xml");
		fLib.setPath(Paths.get(path.getParent().resolve(newFilename.toString()).toString()));

		// Add all filtered books to fLib
		for (Book b : filteredBooks) {
			fLib.addBook(b);
		}
		return fLib;
	}
	
	
	public int size() { return books.size(); }

	public void setPath(Path path) { this.path = path; }
	public Path getPath() { return path; }

	public Book getBook(int index) { return books.get(index); }

	public String getName() { return this.getPath().getFileName().toString(); }

	public boolean isEmpty() {
		return books.size() == 0;
	}
}
