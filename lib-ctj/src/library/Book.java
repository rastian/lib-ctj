package library;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

public class Book {
	private String title;
	private String author;
	private String age;
	private String isbn;
	private int uniqueWords;
	private int totalCount;
	private HashMap<String, Integer> wordMap;
	
	public Book(String title, String author, String age, String isbn, String filename) 
			throws FileNotFoundException {
		this.title = title;
		this.author = author;
		this.age = age;
		this.isbn = isbn;

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
