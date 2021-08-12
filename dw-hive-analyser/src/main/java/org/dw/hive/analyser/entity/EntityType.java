package org.dw.hive.analyser.entity;

public enum EntityType {
	UNKNOWN(0, "unknown"),TABLE(1, "table"),VIEW(2, "view"), FILE(3, "file");
	private int id;
    private String name;
    
    EntityType(int id, String name){
    	this.id = id;
    	this.name = name;
    }
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
}
