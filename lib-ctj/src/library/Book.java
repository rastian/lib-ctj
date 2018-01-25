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
		this.setAuthor(author);
		this.age = age;
		this.isbn = isbn;

		wordMap = new HashMap<>();
		// Populate wordMap
		Stream<String> s = Files.lines(Paths.get(filename))
				.flatMap(line -> Stream.of(line.split("[ ,.!;?\r\n]")))
				.filter(w -> w.length() > 0);
		s.forEach(w -> wordMap.put(w, wordMap.containsKey(w) ? wordMap.get(w) + 1 : 1));
		s.close();
	}
	
	public void  setTitle(String title) { this.title = title; }
	public String getTitle() { return title; }
	
	public void setAuthor(String author) { this.author = author; }
	public String getAuthor() { return author; }
	
	public void setAge(String age) { this.age = age; }
	public String getAge() { return age; }
	
	public void setISBN(String isbn) { this.isbn = isbn; }
	public String getISBN() { return isbn; }
	
	public int getUniqueWords() { return uniqueWords; }
	
	public int getTotalCount() { return totalCount; }
}
