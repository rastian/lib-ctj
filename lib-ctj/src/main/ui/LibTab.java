package main.ui;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.library.Book;
import main.library.Library;

public class LibTab {
	private boolean isSaved;
	private String name;
	private int tabCount;
	private Tab tab = new Tab();
	Library libObj = new Library();
	private MultiSelectTableView<Book> libData = new MultiSelectTableView<Book>();
	private ObservableList<Book> data =
	        FXCollections.observableArrayList(
	        	
	            
	        );
	public LibTab(Library lib, TabPane pane, Stage stage){
		tab.setOnCloseRequest(new EventHandler<Event>() {
			@Override
		    public void handle(Event e) 
		    {
		        if(isSaved != true) {
                	Alert alert = new Alert(AlertType.CONFIRMATION);
                	alert.setTitle("Save");
                	alert.setHeaderText("Do you want to save this library?");
                	ButtonType yes = new ButtonType("Save");
                	ButtonType no = new ButtonType("Do Not Save");
                	ButtonType cancel = new ButtonType("Cancel");
                	alert.getButtonTypes().setAll(yes, no, cancel);
                	Optional <ButtonType> result = alert.showAndWait();
                	if(result.get() == yes) {
                		FileChooser chooser = new FileChooser();
	                	String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
	                	chooser.setInitialDirectory(new File(currentPath));
	                    File file = chooser.showSaveDialog(stage);
	                    libObj.save(file.toPath());
	                    libObj.setName(file.getName());
	                    name = file.getName();
	                    isSaved = true;
                	}
                	if(result.get() == cancel) {
                		e.consume();
                	}
		        }
		    }
		});
		
		isSaved = false;
		libData.setEditable(true);
		libObj = lib;
		for(int i = 0; i < lib.size(); i++) {
			data.add(lib.getBook(i));
		}
		TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        TableColumn<Book, String> authorCol = new TableColumn<>("Author");
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN-13");
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        TableColumn<Book, String> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Book, Boolean> completeCol = new TableColumn<>("Complete");
        completeCol.setCellValueFactory(new PropertyValueFactory<>("complete"));
        TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        TableColumn<Book, Integer> uniqueCol = new TableColumn<>("Unique Words");
        uniqueCol.setCellValueFactory(new PropertyValueFactory<>("uniqueWordCount"));
        TableColumn<Book, Integer> totalCol = new TableColumn<>("Total Words");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalWordCount"));
        
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
			isSaved = false;
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String newName) {
		name = newName;
		libObj.setName(newName);
		tab.setText(name);
	}
	public ObservableList<Book> getBookList(){
		return data;
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
	
	public boolean getIsSaved() {
		return isSaved;
	}
	public void setIsSaved(boolean bool) {
		isSaved = bool;
	}
	public ObservableList<Book> getData(){
		return data;
	}
}
