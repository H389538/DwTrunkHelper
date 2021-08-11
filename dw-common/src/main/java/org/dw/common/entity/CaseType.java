package org.dw.common.entity;


public enum CaseType {
	ORIGINAL("original","保持不变"), LOWER("lower", "字符转小写"), UPPER("upper", "字符转大写");
	
	private String code;
    private String name;
    
    CaseType(String code, String name){
    	this.code = code;
    	this.name = name;
    }

	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
}
