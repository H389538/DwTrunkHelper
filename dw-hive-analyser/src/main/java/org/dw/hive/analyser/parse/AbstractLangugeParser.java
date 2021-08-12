package org.dw.hive.analyser.parse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.dw.common.entity.CaseType;
import org.dw.common.utils.CharQueue;
import org.dw.common.utils.DateUtil;
import org.dw.common.utils.DwStringUtils;
import org.dw.hive.analyser.entity.Entity;
import org.dw.hive.analyser.entity.Script;

public class AbstractLangugeParser implements LangugeParser, EntityManager{
	String defaultDataBase = "default";
	List<Entity> input = new ArrayList<Entity>();
	List<Entity> output = new ArrayList<Entity>();
	
	@Override
	public void addEntity(List<Entity> list, Entity entity) {
		for(Entity localEntity : list) {
			if(localEntity.equals(entity)) {
				return;
			}
		}
		list.add(entity);
	}

	@Override
	public void removeEntity(List<Entity> list, Entity entity) {
		Iterator<Entity> iterator= list.iterator();
		while(iterator.hasNext()) {
			Entity sourceEntity = iterator.next();
			if(sourceEntity.equals(entity)) {
				iterator.remove();
				break;
			}
		}
	}

	@Override
	public void removeEntity(List<Entity> source, List<Entity> target) {
		Iterator<Entity>  iterator= source.iterator();
		while(iterator.hasNext()) {
			Entity sourceEntity = iterator.next();
			for(Entity targetEntity : target) {
				if(sourceEntity.equals(targetEntity)) {
					iterator.remove();
					break;
				}
			}
		}		
	}
	
	
	@Override
	public List<Script> formatScript(String scriptStr) throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		return formatScript(scriptStr,  DateUtil.toDateStr(calendar.getTime()));
	}
	
	
	
	@Override
	public List<Script> formatScript(String scriptStr, String dateStr) throws Exception {
		List<Script> list = new ArrayList<Script>();
		CharQueue charQueue = new CharQueue();
		
		for(String localScriptStr : DwStringUtils.splitIgnoreQuota(scriptStr, ';')) {
			String formatedScriptStr = localScriptStr;
			formatedScriptStr = DwStringUtils.toCase(formatedScriptStr, CaseType.LOWER);
			formatedScriptStr = DwStringUtils.replaceDateParameters(formatedScriptStr, System.currentTimeMillis(), dateStr);
			formatedScriptStr = DwStringUtils.accurateDateFunciton(formatedScriptStr);
			try {
				formatedScriptStr = charQueue.parseInputStr(formatedScriptStr);
			}catch(Exception ex) {
				ex.printStackTrace();
			}
			list.add(new Script(localScriptStr, formatedScriptStr));
			
		}
		return list;
	}

	@Override
	public void parseScript(List<Script> list) throws Exception {
		
	}

	public List<Entity> getInput() {
		return input;
	}
	public List<Entity> getOutput() {
		return output;
	}
}
