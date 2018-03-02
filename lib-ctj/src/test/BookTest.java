import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;


import library.Book;

public class BookTest {
	
	@Test
	public void booksShouldHaveRightFields() {
		Book b = new Book(
				"A Fruit is a Suitcase for Seeds", 
				"Jean Richards",
				"PK",
				"978-0822559917",
				"lib-ctj/test_files/books/A Fruit is a Suitcase For Seeds.txt");
		assertEquals(b.getTitle(), "A Fruit is a Suitcase for Seeds");
		assertEquals(b.getAuthor(), "Jean Richards");
		assertEquals(b.getAge(), "PK");
		assertEquals(b.getIsbn(), "978-0822559917");
		assertEquals(b.getUniqueWordCount(), 126);
		assertEquals(b.getTotalWordCount(), 272);
	}
	
	@Test
	public void booksShouldBeEqual() {
		Book b1 = new Book(
				"A Life Like Mine: How Children Live Around the World",
				"Unicef",
				"PK",
				"978-0751339826",
				"lib-ctj/test_files/books/A life like mine.txt"
				);
		Book b2 = new Book(
				"A Life Like Mine: How Children Live Around the World",
				"Unicef",
				"PK",
				"978-0751339826",
				"lib-ctj/test_files/books/A life like mine.txt"
				);
		assertEquals(b1.equals(b2), true);
	}
}
