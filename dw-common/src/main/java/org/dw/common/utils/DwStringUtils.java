package org.dw.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.dw.common.entity.CaseType;
import org.dw.common.entity.PlaceHolders;
import org.dw.common.entity.StackNode;

public class DwStringUtils extends StringUtils{
//	private static final Pattern pattern = Pattern.compile("(\\(\\s*'\\s*)(\\d{4}\\-\\d{2}\\-\\d{2})(\\s*'\\s*)([^-0123456789]+)(-?\\d+)(.*)");
	
	private static final Pattern pattern = Pattern.compile("(\\(\\s*'\\s*)(\\d{4}\\-\\d{2}\\-\\d{2})(\\s*'\\s*)([^-0123456789]+)(-?\\d+)(\\s*\\))");
	private static Set<String> DATE_FUNCTION_SET = new HashSet<String>(Arrays.asList(new String[] {"date_sub","date_add","add_months"}));
	private static final Pattern blank_line_pattern = Pattern.compile("\\s+");
	
	//根据指定的字符将字符串解析成字符串数组
	public static List<String> splitIgnoreQuota(String inputStr, char delimiter) {
		boolean inQuotes = false;
		boolean inSingleQuotes = false;
		List<String> result = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		List<Character> cache = new ArrayList<Character>();
		for (char localChar : inputStr.toCharArray()) {
			if (localChar == delimiter) {
				if(!inQuotes && !inSingleQuotes) {
					String localStr = sb.toString().trim();
					if(!DwStringUtils.isEmpty(localStr)) {
						Matcher matcher = blank_line_pattern.matcher(localStr);
						if(!matcher.matches()) {
							result.add(localStr);
						}
					}
					sb.delete(0, sb.length());
				}else {
					sb.append(localChar);
				}
			}else if(localChar == '\'') {
				inSingleQuotes = (inQuotes || isEscape(cache)) ? inSingleQuotes : !inSingleQuotes;
				sb.append(localChar);
			}else if(localChar == '\"') {
				inQuotes = (inSingleQuotes || isEscape(cache)) ? inQuotes : !inQuotes;
				sb.append(localChar);
			}else {
				sb.append(localChar);
			}
			cache.add(localChar);
		}
		String lastStr = sb.toString().trim();
		if(!DwStringUtils.isEmpty(lastStr)) {
			Matcher matcher = blank_line_pattern.matcher(lastStr);
			if(!matcher.matches()) {
				result.add(lastStr);
			}
		}
		return result;
	}
	
	
	public static String toCase(String str) {
		return toCase(str , CaseType.ORIGINAL);
	}
	/*SQL语句转换大小写,注意在单引号或者双引号里面的内容是不允许转换的*/
	public static String toCase(String str, CaseType caseType) {
		boolean inQuotes = false;
        boolean inSingleQuotes = false;
		StringBuilder sb = new StringBuilder();
		List<Character> cache = new ArrayList<Character>();
		for (char c : str.toCharArray()) {
			if(c == '\'') {
				inSingleQuotes = (inQuotes || isEscape(cache)) ? inSingleQuotes : !inSingleQuotes;
			}else if(c == '\"') {
				inQuotes = (inSingleQuotes || isEscape(cache)) ? inQuotes : !inQuotes;
			}
			if(inSingleQuotes || inQuotes) {
				sb.append(c);
				continue;
			}
			if(CaseType.LOWER==caseType) {
				sb.append(Character.toLowerCase(c));
			}else if(CaseType.UPPER==caseType) {
				sb.append(Character.toUpperCase(c));
			}else {
				sb.append(c);
			}
			cache.add(c);
		}
		return sb.toString();
	}
	
	
	//要求被处理的SQL不允许含有换行\等特殊字符
	public static String accurateDateFunciton(String inputStr) throws Exception{
		
		List<StackNode> origin = new ArrayList<StackNode>();
		List<StackNode> result = new ArrayList<StackNode>();
		result.add(new StackNode(null, inputStr));
		for(String method : DATE_FUNCTION_SET){
			origin = result;
			result = new ArrayList<StackNode>();
			for(StackNode stackNode : origin) {
				String localStr = stackNode.getCommand();
				String[] array = localStr.split(method);
				
//				System.out.println("localStr:"+localStr);
//				System.out.println("method:"+method);
			
				for(int i=0; i<array.length; i++) {
					
//					System.out.println("Method:"+(i== 0 ? stackNode.getMethod() : method )+" ##### Node:"+array[i]);
					
					result.add(new StackNode( i== 0 ? stackNode.getMethod() : method  , array[i]));
				}
				
//				System.out.println();
			}
		}
		Stack<StackNode> stack = new Stack<StackNode>();
		for(StackNode node : result) {
			stack.push(node);
		}
		return getStackStr(stack);
	}
	
	private static String getStackStr(Stack<StackNode> stack) throws Exception {
		String result = null;
		while(!stack.isEmpty()) {
			StackNode node = stack.pop();
			String command = node.getCommand();
			if(node.getMethod() == null) {
				result = command;
				break;
			}
			
//			System.out.println("###Command:"+command);
			int endIndex = command.indexOf(")") + 1;
			String parameters = command.substring(0, endIndex);
//			System.out.println("###Parameters:"+parameters);
			Matcher matcher = pattern.matcher(parameters);
			
			if(matcher.matches()) {
//				System.out.println("OK");
				String dateStrValue = accurateDateValue(node.getMethod(), matcher.group(2), matcher.group(5));
				command = String.format("'%s'%s", dateStrValue, command.substring(endIndex));
			}else {
				command = String.format("%s%s" ,node.getMethod() , command);
			}
			if(!stack.isEmpty()) {
				StackNode nextNode = stack.pop();
				stack.add(new StackNode(nextNode.getMethod(), nextNode.getCommand().concat(command)));
			}
		}
		return result;
	}
	
	public static String accurateDateValue(String method, String dateStr, String dayCountStr) throws Exception {
		if(!DATE_FUNCTION_SET.contains(method)) {
			throw new Exception("不支持日期函数:"+method+"自动转换!");
		}
		int dayCount = Integer.parseInt(dayCountStr);
		if("date_add".equals(method)) {
			return DateUtil.addDays(dateStr, dayCount, "yyyy-MM-dd");
		}else if("date_sub".equals(method)) {
			return DateUtil.addDays(dateStr, -dayCount, "yyyy-MM-dd");
		}else if("add_months".equals(method)) {
			return DateUtil.toDateStr(DateUtil.addMonth(DateUtil.toDate(dateStr), dayCount), "yyyy-MM-dd") ;
		}else {
			throw new Exception("Not Implement!");
		}
	}
	
	
	public static String replaceDateParameters(String str,Long scheduleTimeStamp,Date startDate) throws Exception{
		return replaceDateParameters(str, scheduleTimeStamp, DateUtil.toDateStr(startDate));
	}
	
	public static String replaceDateParameters(String str,Long scheduleTimeStamp,String startDate) throws Exception{
		str= str.replace(PlaceHolders.TIMESTAMP_BEFORE_ONEHOUR,String.valueOf(scheduleTimeStamp - 3600000));
		str= str.replace(PlaceHolders.SCHEDULE_TIMESTAMP,String.valueOf(scheduleTimeStamp));
		str= str.replace(PlaceHolders.START_DAY, String.format("'%s'", startDate));
		str= str.replace(PlaceHolders.DATA_DAY, startDate);
		str= str.replace(PlaceHolders.MONTH_BEGIN_DAY,DateUtil.toDateStr(DateUtil.getMonthBeginDate(startDate)));
		str= str.replace(PlaceHolders.MONTH_SUFFIX, DateUtil.getMonthSuffix(startDate));
		str= str.replace(PlaceHolders.MONTH_END_DAY, DateUtil.toDateStr(DateUtil.getLastDayInMonth(startDate)));
		str= str.replace(PlaceHolders.MONTH_ID, DateUtil.getMonthId(startDate));
		str= str.replace(PlaceHolders.PRE_MONTH_ID, DateUtil.getMonthId(DateUtil.addMonth(DateUtil.toDate(startDate), -1)));
		str= str.replace(PlaceHolders.PRE_MONTH_SUFFIX, DateUtil.getMonthSuffix(DateUtil.addMonth(DateUtil.toDate(startDate), -1)));
		str= str.replace(PlaceHolders.SHORT_START_DAY, startDate.replace("-", ""));
		str= str.replace(PlaceHolders.WEEK_ID, DateUtil.getWeekId(startDate));
		str= str.replace(PlaceHolders.PRE_WEEK_ID, DateUtil.getPreWeekId(startDate));
		Date todayDate = Calendar.getInstance().getTime();
		str= str.replace(PlaceHolders.PRE_TODAY_DATA_DAY, DateUtil.toDateStr(todayDate));
		str= str.replace(PlaceHolders.PRE_TODAY_SIMPLE_DATA_DAY, DateUtil.toDateStr(todayDate,"yyyyMMdd"));
		str= str.replace(PlaceHolders.PRE_TODAY_MONTH_ID, DateUtil.getPreMonthId(todayDate));
		str= str.replace(PlaceHolders.PRE_TODAY_WEEK_ID, DateUtil.getPreWeekId(todayDate));
		return str;
	}
	
	private static boolean isEscape(List<Character> cache) {
		int position = cache.size() - 1;
		int counter = 0;
		while(position>-1) {
			if(cache.get(position).charValue() == '\\') {
				counter = counter + 1;
				position = position - 1;
			}else {
				break;
			}
		}
		return  counter % 4 > 0;
	}
	
}
