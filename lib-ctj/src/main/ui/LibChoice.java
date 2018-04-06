package main.ui;

import main.library.Library;

public class LibChoice {
	Library lib;
	String name;
	LibChoice(Library lib, String name){
		this.lib = lib; 
		this.name = name;
	}
	public Library getLibrary() {
		return lib;
	}
	public String getName() {
		return name;
	}
	public void setLibrary(Library lib) {
		this.lib = lib;
	}
	public void setName(String name) {
		this.name = name;
	}
}
