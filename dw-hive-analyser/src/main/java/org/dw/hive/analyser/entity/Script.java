package org.dw.hive.analyser.entity;

import org.dw.common.utils.DwStringUtils;

public class Script {
	private String scriptStr;
	private String formatedScriptStr;
	
	public Script(String scriptStr, String formatedScriptStr) {
		this.scriptStr = scriptStr;
		this.formatedScriptStr = formatedScriptStr.endsWith(";") ? formatedScriptStr.substring(0, formatedScriptStr.length()-1) : formatedScriptStr;
	}
	
	public boolean isRequireAuthority() {
		return (formatedScriptStr.startsWith("set ") || formatedScriptStr.startsWith("add ") || DwStringUtils.isEmpty(formatedScriptStr)) ? false : true;
	}
	
	public String getScriptStr() {
		return scriptStr;
	}
	public String getFormatedScriptStr() {
		return formatedScriptStr;
	}
}
