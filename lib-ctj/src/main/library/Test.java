package main.library;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;


public class Test {

	public static void main(String[] args) 
			throws IOException, FileNotFoundException {
/*
		String home = System.getProperty("user.home");
		Path appDir = Paths.get(home + "\\Documents\\lib-ctj");
		Path b = Paths.get("test_files/a_life_like_mine.txt");

		if (!Files.exists(appDir)) {
			Files.createDirectory(appDir);
			Files.createDirectory(appDir.resolve("libs"));
			Files.createDirectory(appDir.resolve("dicts"));
			Files.createDirectory(appDir.resolve("books"));
		}
*/

		String testDirName = "test_files/";
		String[] testFiles = {
				testDirName + "a_fruit_is_a_suitcase_for_seeds.txt",
				testDirName + "a_life_like_mine.txt",
				testDirName + "a_picture_book_of_eleanor_roosevelt.txt",
				testDirName + "a_picture_book_of_martin_luther_king_jr.txt",
				testDirName + "a_picture_book_of_rosa_parks.txt",
				testDirName + "test.txt"
		};
		
		Book b1 = new Book("A Fruit Is A Suitcase for Seeds", "Jean Richards", "PK", "978-0822559917", testFiles[0]);
		Book b2 = new Book("A Life Like Mine", "Dorling Kindersley", "K", "978-0756618032", testFiles[1]);
		System.out.println(b1);
		System.out.println(b2);
		System.out.println();

		Library l = new Library(Paths.get("C:\\Users\\Christian\\Desktop\\library-example.xml"));
		System.out.println("Size: " + l.size());
		System.out.println("Books: ");
		for (int i = 0; i < l.size(); ++i) {
			System.out.println(l.getBook(i).getTitle());
		}

		System.out.println();
		l.addBook(b1);
		l.addBook(b2);
		
		System.out.println();
		System.out.println("Size: " + l.size());
		System.out.println("Books: ");
		for (int i = 0; i < l.size(); ++i) {
			System.out.println(l.getBook(i).getTitle());
		}
		
		l.save(Paths.get("C:\\Users\\Christian\\Desktop\\test.xml"));
	}

}
