package main.library;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Book implements Comparable<Book> {
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
			List<String> lines = Files.lines(Paths.get(filename), Charset.defaultCharset()).collect(Collectors.toList());
			String word;
			Pattern pattern = Pattern.compile("([a-zA-Z']+)");
			Matcher matcher;
			for (String line : lines) {
				matcher = pattern.matcher(line);
				while (matcher.find()) {
					word = matcher.group().toLowerCase().trim();
					if (word.length() > 0) {
						if (wordMap.containsKey(word)) {
							int oldWordCount = wordMap.get(word);
							wordMap.put(word, oldWordCount + 1);
						}
						else {
							wordMap.put(word, 1);
						}
					}
				}
			}

			this.uniqueWordCount = wordMap.keySet().size();
			this.totalWordCount = wordMap.values().stream().mapToInt(Integer::intValue).sum();
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

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof Book)) {
			return false;
		}
		
		Book b = (Book) o;
		return this.title.toLowerCase().equals(b.title.toLowerCase()) && this.isbn.equals(b.isbn);
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
	public boolean isComplete() { return complete; }
	
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

	@Override
	public int compareTo(Book b) {
		return title.toLowerCase().compareTo(b.title.toLowerCase());
	}
}
