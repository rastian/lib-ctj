package main.library;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Book {
	private String title;
	private String author;
	private String age;
	private String isbn;
	private boolean complete;
	private String genre;
	private int uniqueWordCount;
	private int totalWordCount;
	private HashMap<String, Integer> wordMap;

	public Book(String title, String author, String age, String isbn, boolean complete, String genre, String filename) { 
		try {
			this.title = title;
			this.author = author;
			this.age = age;
			this.isbn = isbn;
			this.complete = complete;
			this.genre = genre;

			wordMap = new HashMap<>();
			// Get all words in text file given by filename
			Stream<String> words = Files.lines(Paths.get(filename))
					.flatMap(line -> Stream.of(line.split("[\\s+ ,.!;?\r\n\\h]")))
					.filter(word -> word.length() > 0)
					.map(word -> word.toLowerCase().trim());
			// Populate wordMap 
			words.forEach(w -> wordMap.put(w, wordMap.containsKey(w) ? wordMap.get(w) + 1 : 1));
		
			this.uniqueWordCount = wordMap.keySet().size();
			this.totalWordCount = wordMap.values().stream().mapToInt(Integer::intValue).sum();
		
			words.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public Book() { }

	public String toString() {
		String s = String.format("Book[title='%s', isbn='%s']", title, isbn);
		return s;
	}

	public boolean equals(Book b) {
		if (this.title.equals(b.title)) {
			return this.isbn.equals(b.isbn);
		}
		return false;
	}

	public void  setTitle(String title) { this.title = title; }
	public String getTitle() { return title; }

	public void setAuthor(String author) { this.author = author; }
	public String getAuthor() { return author; }

	public void setAge(String age) { this.age = age; }
	public String getAge() { return age; }

	public void setIsbn(String isbn) { this.isbn = isbn; }
	public String getIsbn() { return isbn; }
	
	public void setComplete(boolean complete) { this.complete = complete; }
	public boolean getComplete() { return complete; }
	
	public void setGenre(String genre) { this.genre = genre; }
	public String getGenre() { return genre; }

	public void setUniqueWordCount(int uniqueWordCount) { this.uniqueWordCount = uniqueWordCount; }
	public int getUniqueWordCount() { return uniqueWordCount; }

	public void setTotalWordCount(int totalWordCount) { this.totalWordCount = totalWordCount; }
	public int getTotalWordCount() { return totalWordCount; }

	public HashMap<String, Integer> getWordMap() {
		// returns copy of wordMap to prevent accidental changes
		return (HashMap<String, Integer>)wordMap.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
	}

	public void setWordMap(HashMap<String, Integer> wordMap) { this.wordMap = wordMap; }
}
