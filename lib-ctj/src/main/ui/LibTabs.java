package main.ui;

import java.util.HashMap;

import javafx.scene.control.Tab;

public class LibTabs {
	private HashMap<Tab, LibTab> libMap = new HashMap<Tab, LibTab>();
	int tabCount;
	public LibTabs() {
		tabCount = 0;
	}
	public void addLibTab(Tab tab, LibTab libTab) {
		libMap.put(tab, libTab);
		tabCount++;
	}
	public void deleteLibTab(Tab tab, LibTab libTab) {
		libMap.remove(tab, libTab);
		tabCount --;
	}
	public LibTab getLibTab(Tab tab) {
		return libMap.get(tab);
	}
	public int getTabCount() {
		return tabCount;
	}
	public void lowerTabCount() {
		tabCount -= 1;
	}
	public boolean isEmpty() {
		if(tabCount == 0) {
			return true;
		}
		return false;
	}
	public HashMap<Tab, LibTab> getlibMap(){
		return libMap;
	}
	public boolean isSaved() {
		for(HashMap.Entry<Tab, LibTab> entry : libMap.entrySet()) {
			if(!entry.getValue().getIsSaved()) {
				return false;
			}
		}
		return true;
	}
}
