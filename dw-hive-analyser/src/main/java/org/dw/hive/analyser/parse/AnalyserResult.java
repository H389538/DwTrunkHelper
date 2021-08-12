package org.dw.hive.analyser.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dw.hive.analyser.entity.Entity;
import org.dw.hive.analyser.entity.Script;

public class AnalyserResult {
	private Script script;
	private List<Entity> readEntitys =  new ArrayList<Entity>();
	private List<Entity> writeEntitys =  new ArrayList<Entity>();
	private Map<String,  String> tableAliasMap = new HashMap<String, String>();
	private Map<String,Set<String>> aliasColumsMap = new HashMap<String,Set<String>>();
	
	public AnalyserResult(Script script) {
		this.script = script;
	}

	public Script getScript() {
		return script;
	}
	
	public List<Entity> getReadEntitys() {
		return readEntitys;
	}
	
	public List<Entity> getWriteEntitys() {
		return writeEntitys;
	}

	
	public Map<String, Set<String>> getTableColumsMap() {
		Map<String,Set<String>> tableColumsMap = new HashMap<String,Set<String>>();
		//没有写表名别名的字段,也就是干净的字段,那么这个字段必定只是在某一张表里面存在, 暂时将这个字段补充道所有的表里面
		Set<String>  columns = aliasColumsMap.get("unknown");
		for(Entity entity : readEntitys) {
			String table = entity.getDatabase() + "." + entity.getName();
			String alias = tableAliasMap.get(table);
			Set<String> aliasColumns = aliasColumsMap.containsKey(alias) ? aliasColumsMap.get(alias) : new HashSet<String>();
			if(columns != null && !columns.isEmpty()) {
				aliasColumns.addAll(columns);
			}
			tableColumsMap.put(table,  aliasColumns);
		}
		return tableColumsMap;
	}
	
	public Map<String, String> getTableAliasMap() {
		return tableAliasMap;
	}

	public Map<String, Set<String>> getAliasColumsMap() {
		return aliasColumsMap;
	}
}
