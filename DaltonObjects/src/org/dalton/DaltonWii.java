package org.dalton;

import java.util.ArrayList;
import java.util.List;

public class DaltonWii {
	List gestures = new ArrayList();
	boolean log = false;
	
	DaltonWii() {
		
	}
	
	public String getButton() {
		return "A";
	}
	
	public String getGesture() {
		return "Point";
	}
	
	public void logEvents(boolean log) {
		this.log = log;
	}
	
}
