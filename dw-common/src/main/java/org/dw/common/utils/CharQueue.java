package org.dw.common.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dw.common.entity.Keyword;


/*
 * 这个类主要是SQL清洗,将一个SQL文件中的注释、无意义的内容清理掉
 * 支持:  每一行#开头的的注释 , 单行注释,多行注释等
 * 
 * 使用hive 1.1.0 的版本支持语法解析. 该版本不支持SQL语句中含有 ` 字符
 * 所有需要将 ` 全部去掉, 但是部分关键字是数据库字段所以针对这样的就把这个关键字加上一个dw_field_前缀然后去掉`
 * 还要一种特殊的情况比如用户提交的SQL是一个查询, 查询里面包含有中文 例如: xxx  as `城市ID`,这样的情况把``直接按内容直接MD5然后去掉`
 * 
 * */

public class CharQueue {
	private boolean inQuotes = false;
	private boolean inSingleQuotes = false;
	private StringBuilder result = new StringBuilder();
	private List<Character> queueCache = new ArrayList<Character>();
	private static final char[] markCharArray = {'-', '/', '#', ';' , '\n' ,'\r',  '`'};
	
	private void initCharQueue() {
		inQuotes = false;
		queueCache.clear();
		inSingleQuotes = false;
		result.delete(0, result.length());
	}
	
	public String parseInputStr(String inputStr) throws Exception {
		initCharQueue();
		inputStr = inputStr.trim();
		for(char c : inputStr.toCharArray()) {
			joinQueue(c);
		}
		if(!inputStr.endsWith(";")) {
			joinQueue(';');
		}
		return result.toString();
	}
	
	private void joinQueue(char c) throws Exception {
		char firstQueueCacheChar = firstQueueCacheChar();
		char lastQueueCacheChar = lastQueueCacheChar();
		
		if(c == '\'') {
			inSingleQuotes = (inQuotes || isEscape() || isSingleRowComment() || isMultRowComment()) ? inSingleQuotes : !inSingleQuotes;
		}else if(c == '\"') {
			inQuotes = (inSingleQuotes || isEscape() || isSingleRowComment() || isMultRowComment()) ? inQuotes : !inQuotes;
		}
		if(isMarkChar(c)) {
			
			String queueCacheStr = getQueueCacheStr();
			if(!isMultRowComment() && !isSingleRowComment()) {
				
				if(firstQueueCacheChar == '`' && c != '`') {
					queueCache.add(c);
					return;
				}
				
				if(c == ';' || c == '#' || (c == '`' && c != firstQueueCacheChar) || ((c == '-' || c == '/') && lastQueueCacheChar != c)) {
					queueCacheStr = queueCacheStr.trim();
					addBlankSpaceChar(result);
					result.append(queueCacheStr);
					queueCache.clear();
					if(c == ';') {
						result.append(c);
						return;
					}
				}
			}
		 
			if(c == '\n' || c == '\r' || (c == '`' &&  c == firstQueueCacheChar)) {
				if(!isMultRowComment()) {
					if(!isSingleRowComment()) {
						queueCacheStr = queueCacheStr.trim();
						if(c == '`') {
							queueCacheStr = queueCacheStr.substring(1);
							queueCacheStr = isContainChinese(queueCacheStr) ? MD5.getMD5Code(queueCacheStr) : queueCacheStr;
							queueCacheStr = Keyword.matches(queueCacheStr) ? "dw_field_".concat(queueCacheStr) : queueCacheStr;						
						}
						addBlankSpaceChar(result);
						result.append(queueCacheStr);
					}
					queueCache.clear();
					return;
				}
			}
			
			if(isMultRowComment()) {
				if(c == '/' && lastQueueCacheChar == '*' && c == firstQueueCacheChar) {
					queueCache.clear();
					return;
				}
			}
		}
		if(lastQueueCacheChar == ' ' && (c == ' ' || c == ',' || c == '(' || c ==')')) {
			queueCache.remove(queueCache.size() - 1);
		}
		queueCache.add(c);
	}

	
	private boolean isContainChinese(String str) {
		Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}
	
	
	private void addBlankSpaceChar(StringBuilder sb) {
		if(sb.length() > 0) {
			char[] charArray  = new char[1];
			charArray[0] = sb.charAt(sb.length() - 1);
			String charArrayStr = new String(charArray).trim();
			if(!",".equals(charArrayStr) && !";".equals(charArrayStr) && !StringUtils.isEmpty(charArrayStr)) {
				sb.append(" ");
			}
		}
	} 
	private boolean isEscape() {
		int position = queueCache.size() - 1;
		int counter = 0;
		while(position>-1) {
			char c = queueCache.get(position).charValue();
			if(c == '\\') {
				counter = counter + 1;
				position = position - 1;
			}else {
				break;
			}
		}
		return  counter % 4 > 0;
	}
	private boolean isMultRowComment() throws Exception {
		return queueCache.size() >= 2 ? ("/*".equals(getQueueCacheStr(2))) : false;
	}
	private boolean isSingleRowComment() throws Exception {
		if(queueCache.size() == 0) {return false;}
		int length = queueCache.size() >= 2 ? 2 : queueCache.size();
		String queueStr = getQueueCacheStr(length);
		return queueStr.startsWith("#") || queueStr.equals("//") || queueStr.equals("--");
	}
	
	private String getQueueCacheStr() throws Exception {
		return getQueueCacheStr(queueCache.size());
	}
	private String getQueueCacheStr(int length) throws Exception {
		if(length > queueCache.size()) {
			throw new Exception("设定的字符个数超过缓存队列的字符个数!");
		}
		char[] charArray  = new char[length];
		for(int i=0; i<charArray.length; i++) {
			charArray[i] = queueCache.get(i).charValue();
		}
		return new String(charArray);
	}
	
	private char firstQueueCacheChar() {
		return queueCache.isEmpty() ? '.' : queueCache.get(0).charValue();
	}
	
	private char lastQueueCacheChar() {
		return queueCache.isEmpty() ? '.' : queueCache.get(queueCache.size() - 1).charValue();
	}
	private boolean isMarkChar(char c) {
		for(char localChar : markCharArray) {
			if(localChar == c && !inQuotes && !inSingleQuotes) {
				return true;
			}
		}
		return false;
	}
}
