package org.dw.hive.analyser.parse;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseDriver;
import org.dw.hive.analyser.entity.Entity;
import org.dw.hive.analyser.entity.EntityType;
import org.dw.hive.analyser.entity.Script;

public class HiveLangugeParser extends AbstractLangugeParser{
	private Set<String> views = new HashSet<String>();
	private List<ASTNode> conditions = new ArrayList<ASTNode>();
	private List<AnalyserResult> results = new ArrayList<AnalyserResult>();
	
	
	@Override
	public void parseScript(List<Script> list) throws Exception {
		ParseDriver parseDriver = new ParseDriver();
		
		for(Script script : list) {
			
			conditions.clear();
			
			if(script.isRequireAuthority()) {
				ASTNode node = null;
				System.out.println(script.getFormatedScriptStr());
				
				try {
					node = parseDriver.parse(script.getFormatedScriptStr());
				}catch(Exception  ex) {
					throw new Exception("SQL转成ASTNode失败: "+ex.getMessage()+"\n"+script.getScriptStr());
				}
				
				System.out.println(node.toStringTree());
				
				AnalyserResult result = new AnalyserResult(script);
				
				analyseASTNodeTree(node,result);
				
				analyseConditionASTNodes(result);
				
				results.add(result);
			}
		}
	}
	
	
	private void analyseASTNodeTree(ASTNode node,AnalyserResult result) throws NoSuchAlgorithmException{
		analyseASTNode(node,result);
		for(int i=0;i<node.getChildCount();i++){
			ASTNode childNode = (ASTNode) node.getChild(i);
			analyseASTNodeTree(childNode,result);
		}
	}
	

	private void analyseASTNode(ASTNode node,AnalyserResult result) throws NoSuchAlgorithmException{
		if(node.isNil()) {return;}
		Entity entity = null;
		switch (node.getToken().getType()){
		   case HiveParser.TOK_SWITCHDATABASE:
			   defaultDataBase = node.getChild(0).getText();
			   break;
		   case HiveParser.TOK_DROPTABLE:
			   entity = getEntity(EntityType.TABLE, node);
			   addEntity(result.getWriteEntitys(), entity);
//			   removeEntity(output, entity);
			   break;
		   case HiveParser.TOK_DROPVIEW:
			   entity = getEntity(EntityType.VIEW, node);
			   addEntity(result.getWriteEntitys(), entity);
//			   removeEntity(output, entity);
			   break;
		   case HiveParser.TOK_TABREF:
			   entity = getEntity(EntityType.UNKNOWN, node);
			   addEntity(result.getReadEntitys(), entity);
			   //查询的源表有别名
			   if(node.getChildCount() == 2){
				   String alias=node.getChild(1).getText();
				   result.getTableAliasMap().put(String.format("%s.%s", entity.getDatabase(), entity.getName()), alias);
			   }
			   addEntity(input, entity);
			   break;
			   
		   case HiveParser.TOK_CREATEVIEW:
			   entity = getEntity(EntityType.VIEW, node);
			   addEntity(result.getWriteEntitys(), entity);
			   addEntity(output, entity);
			   break;
			   
		   case HiveParser.TOK_TAB:
		   case HiveParser.TOK_ALTERTABLE:
		   case HiveParser.TOK_CREATETABLE:
			   entity = getEntity(EntityType.TABLE, node);
			   addEntity(result.getWriteEntitys(), entity);
			   addEntity(output, entity);
			   break;
		   case HiveParser.TOK_WHERE:
			   storedConditionASTNodes(node);
			   break;
		   default:
			   break;
		}
	}
 
	
	private void storedConditionASTNodes(ASTNode node) {
		int childLen = node.getChildCount();
		for (int i = 0; i < childLen; i++) {
			ASTNode childASTNode = (ASTNode) node.getChild(i);
			if (childASTNode.getType() == HiveParser.TOK_TABLE_OR_COL){
				conditions.add(childASTNode);
			}
			storedConditionASTNodes(childASTNode);
		}
	}
	
	
	private void analyseConditionASTNodes(AnalyserResult result) {
		Map<String,Set<String>> aliasColumsMap  = result.getAliasColumsMap();
		for (ASTNode node : conditions) {
			String parentNodeText = node.getParent().getText();
			String alias =  ".".equals(parentNodeText) ? node.getChild(0).getText() : "unknown";
			String column = ".".equals(parentNodeText) ? node.getParent().getChild(1).getText() : node.getChild(0).getText();
			Set<String> columns = aliasColumsMap.containsKey(alias) ? aliasColumsMap.get(alias) : new HashSet<String>();
			columns.add(column);
			if(!aliasColumsMap.containsKey(alias)) {
				aliasColumsMap.put(alias, columns);
			}
 
		}
	}

	
	
	private Entity getEntity(EntityType entityType, ASTNode node) {
		Entity entity = null;
		
		for(int i=0; i<node.getChildCount(); i++) {
			
			ASTNode childNode = (ASTNode) node.getChild(i);
			
			if(childNode.getToken().getType() == HiveParser.TOK_TABNAME) {
				
				int childCount = childNode.getChildCount();
				String localDataBase = (childCount == 1) ? defaultDataBase : ((ASTNode) childNode.getChild(0)).getText();
				String localTable = (childCount == 1) ? ((ASTNode) childNode.getChild(0)).getText() : ((ASTNode) childNode.getChild(1)).getText();
				
				
				String fullName = String.format("%s.%s", localDataBase, localTable);
				if(entityType == EntityType.VIEW) {
					views.add(fullName);
				}
				
				if(entityType == EntityType.UNKNOWN){
					entityType = views.contains(fullName) ? EntityType.VIEW : EntityType.TABLE;
				}
				entity = new Entity(entityType, localDataBase,localTable );
				break;
			}
		}
		return entity;
	}


	public List<AnalyserResult> getResults() {
		return results;
	}
}
