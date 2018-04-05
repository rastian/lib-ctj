package main.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.library.Book;
import main.library.Library;

public class LibTab {
	private String name;
	private int tabCount;
	private Tab tab = new Tab();
	Library libObj = new Library();
	private MultiSelectTableView<Book> libData = new MultiSelectTableView<Book>();
	private ObservableList<Book> data =
	        FXCollections.observableArrayList(
	        	
	            
	        );
	public LibTab(Library lib, TabPane pane){
		tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
		    public void handle(Event e) 
		    {
		        
		    }
		});
		libData.setEditable(true);
		libObj = lib;
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
        TableColumn completeCol = new TableColumn("Complete");
        completeCol.setCellValueFactory(new PropertyValueFactory<Book, Boolean>("complete"));
        TableColumn genreCol = new TableColumn("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<Book, String>("genre"));
        TableColumn uniqueCol = new TableColumn("Unique Words");
        uniqueCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("uniqueWordCount"));
        TableColumn totalCol = new TableColumn("Total Words");
        totalCol.setCellValueFactory(new PropertyValueFactory<Book, Integer>("totalWordCount"));
        
        libData.setItems(data);
        libData.getColumns().addAll(titleCol, authorCol, isbnCol, ageCol, completeCol, genreCol, uniqueCol, totalCol);
        tab.setContent(libData);
        pane.getTabs().add(tab);
	}
	public TableView<Book> getLibTable(){
		return libData;
	}
	public void addNewBook(Book book) {
		if(!libObj.hasDuplicate(book)) {
			data.add(book);
			libObj.addBook(book);
		}
	}
	public void delBook(int index) {
		libObj.delete(index);
		data.remove(index);
	}
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
		tab.setText(name);
	}
	public int getLibCount() {
		return tabCount;
	}
	public Tab getTab() {
		return tab;
	}
	public Library getLib() {
		return libObj;
	}
	public void setLib(Library newLib) {
		libObj = newLib;
	}
}
