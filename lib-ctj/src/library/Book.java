package library;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Stream;

public class Book {
	private String title;
	private String author;
	private String age;
	private String isbn;
	private int uniqueWords;
	private int totalCount;
	private HashMap<String, Integer> wordMap;
	
	public Book(String title, String author, String age, String isbn, String filename) 
			throws IOException, FileNotFoundException {
		this.title = title;
		this.author = author;
		this.age = age;
		this.isbn = isbn;

		wordMap = new HashMap<>();
		// Populate wordMap
		Stream<String> s = Files.lines(Paths.get(filename))
				.flatMap(line -> Stream.of(line.split("[ ,.!;?\r\n]")));
		s.filter(w -> w.length() > 0)
		.forEach(w -> wordMap.put(w, wordMap.containsKey(w) ? wordMap.get(w) + 1 : 1));
		s.close();
	}
	
	void  setTitle(String title) { this.title = title; }
	String getTitle() { return title; }
	
	void setAuthor(String author) { this.author = author; }
	String getAuthor() { return author; }
	
	void setAge(String age) { this.age = age; }
	String getAge() { return age; }
	
	void setISBN(String isbn) { this.isbn = isbn; }
	String getISBN() { return isbn; }
	
	int getUniqueWords() { return uniqueWords; }
	
	int getTotalCount() { return totalCount; }
}
