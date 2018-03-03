package main.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.library.Book;
import main.library.Library;

public class LibTab {
	private int tabId;
	private TableView<Book> libData = new TableView<Book>();
	private ObservableList<Book> data =
	        FXCollections.observableArrayList(
	        	
	            
	        );
	public LibTab(Library lib, Tab tab){
		libData.setEditable(true);
		for(int i = 0; i < lib.size(); i++) {
			data.add(lib.getBook(i));
		}
		TableColumn titleCol = new TableColumn("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<Book, String>("title"));
        TableColumn authorCol = new TableColumn("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<Book, String>("author"));
        TableColumn isbnCol = new TableColumn("ISBN-13");
        isbnCol.setCellValueFactory(new PropertyValueFactory<Book, String>("isbn"));
        TableColumn ageCol = new TableColumn("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<Book, String>("age"));
        TableColumn uniqueCol = new TableColumn("Unique Words");
        uniqueCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("uniqueWordCount"));
        TableColumn totalCol = new TableColumn("Total Words");
        totalCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("totalWordCount"));
        libData.setItems(data);
        libData.getColumns().addAll(titleCol, authorCol, isbnCol, ageCol, uniqueCol, totalCol);
	}
	public TableView<Book> getLibTable(){
		return libData;
	}
	public void addNewBook(Book book) {
		data.add(book);
	}
}
