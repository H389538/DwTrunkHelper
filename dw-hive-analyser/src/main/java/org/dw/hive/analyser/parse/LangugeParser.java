package org.dw.hive.analyser.parse;

import java.util.List;

import org.dw.hive.analyser.entity.Script;

public interface LangugeParser {
	
	List<Script> formatScript(String scriptStr) throws Exception;
	
	List<Script> formatScript(String scriptStr, String dateStr) throws Exception;
	
	void parseScript(List<Script> list) throws Exception;
}
