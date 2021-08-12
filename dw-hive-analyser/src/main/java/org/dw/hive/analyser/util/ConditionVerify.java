package org.dw.hive.analyser.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dw.hive.analyser.entity.Entity;
import org.dw.hive.analyser.entity.Response;
import org.dw.hive.analyser.parse.AnalyserResult;

public class ConditionVerify {

	
	private static Map<String, List<String>> map  = new HashMap<String,List<String>>();
	static {
		map.put("bi_dw.dw_tsp_order",  Arrays.asList(new String[]{"create_dt"}));
		map.put("bi_dm.dm_rvl_app_transporter_city_day",  Arrays.asList(new String[]{"cal_dt"}));
	}
	
	
	private static List<String> listQueryTableNames(AnalyserResult result){
		List<String> list = new ArrayList<String>();
		for(Entity entity : result.getReadEntitys()) {
			list.add(entity.getDatabase() + "." + entity.getName());
		}
		return list;
	}
	
	private static List<String> getTablePartitionColumns(String table){
		return map.containsKey(table) ? map.get(table) : new ArrayList<String>();
	}
	
	public static Response  verify(List<AnalyserResult> results) {
		for(AnalyserResult result : results) {
			Response response = verify(result);
			if(!response.isStatus()) {
				return response;
			}
		}
		return new Response(true, "ok");
	}
	
	private static Response  verify(AnalyserResult result) {
		Response response = new Response(true, "ok");
		try {
			List<String> list =  listQueryTableNames(result);
			Map<String, Set<String>> aliasColumsMap = result.getTableColumsMap();
			for(String table : list) {
				StringBuilder sb = new StringBuilder();
				List<String> catchPartitionColumns = getTablePartitionColumns(table);
				
				if(!catchPartitionColumns.isEmpty()) {
					Set<String> partitionColumns = aliasColumsMap.get(table);
					
					if(partitionColumns.isEmpty()) {
						response.setStatus(false);
						sb.append("SQL没含表：").append(table).append("中[");
						for(String column : catchPartitionColumns) {
							sb.append(column).append(",");
						}
						sb.replace(sb.length()-1, sb.length(), "]分区字段");
						response.setMessage(sb.toString());
						break;
					}
			
 
					
					String firstPartitionColumn = catchPartitionColumns.get(0);
					if(!partitionColumns.contains(firstPartitionColumn)) {
						sb.append("SQL没含表：").append(table).append("的[");
						sb.append(firstPartitionColumn).append("]分区字段");
						response.setStatus(false);
						response.setMessage(sb.toString());
					}
				}
			}
		}catch(Exception ex) {
			ex.printStackTrace();
			response.setStatus(false);
			response.setMessage(ex.getMessage());
		}
		
		return response;
	}
	
	
}
