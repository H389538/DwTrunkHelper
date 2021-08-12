package org.dw.hive.analyser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.hadoop.yarn.webapp.hamlet.HamletSpec.SCRIPT;
import org.dw.common.entity.CaseType;
import org.dw.common.utils.CharQueue;
import org.dw.common.utils.DateUtil;
import org.dw.common.utils.DwStringUtils;
import org.dw.hive.analyser.entity.Entity;
import org.dw.hive.analyser.entity.Script;
import org.dw.hive.analyser.parse.HiveLangugeParser;

public class Main {

	public static void main(String[] args) throws Exception {

		
		StringBuilder sb = new StringBuilder("----计算当天数据\r\n"+ 
		        "drop table if exists bi_temp.hxy_001;\r\n" + 
				"create table bi_temp.hxy_001 as\r\n" + 
				"select order_id,city_id,create_dt,order_amt,order_weight\r\n" + 
				"from bi_dw.dw_usr_tsp_order_daily\r\n" + 
				"where create_dt = '${data_dt}';\r\n"+ 
				"/*统计每天的订单量*/\r\n" + 
				"drop table if exists bi_temp.hxy_002;\r\n" + 
				"create TABLE bi_temp.hxy_002 as\r\n" + 
				"select create_dt,count(1) as order_cnt\r\n" + 
				"FROM bi_dw.dw_usr_tsp_order_daily\r\n" + 
				"where create_dt >=date_sub('${data_dt}',10)\r\n" +
				"AND city_name in ('SHANGHAI','BEIJING')\r\n" + 
				"AND address in ('20#;2001','20#;2002','20#;2003')\r\n" + 
				"GROUP by create_dt\r\n" + 
				"order by create_dt;\r\n" + 
				"insert overwrite  table bi_st.tsp_order_day partition(create_dt ='${data_dt}') select cnt from  bi_temp.hxy_002;");
		
		String originStr = sb.toString();
		System.out.println("originStr:" + originStr);
		
		//清洗脚本,得到感觉的SQL
		CharQueue charQueue = new CharQueue();
		String formatStr = charQueue.parseInputStr(originStr);
		System.out.println("formatStr:" + formatStr);
		
		//替换时间参数
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH,  -1);
		formatStr = DwStringUtils.replaceDateParameters(formatStr, System.currentTimeMillis(), calendar.getTime());
		System.out.println("formatStr:" + formatStr);
		
		
		//自动计算日期函数得到具体值
		formatStr = DwStringUtils.accurateDateFunciton(formatStr);
		System.out.println("formatStr:" + formatStr);

		//SQL换成小写
		formatStr = DwStringUtils.toCase(formatStr, CaseType.LOWER);
		System.out.println("formatStr:" + formatStr);
		
		
		//得到每条干净的SQL
		List<String> list = DwStringUtils.splitIgnoreQuota(formatStr, ';');
		for(String script : list) {
			System.out.println("Script: "+ script);
			
		}
		 
		
		System.out.println("===========================");
		HiveLangugeParser hiveLangugeParser = new HiveLangugeParser();
		List<Script> scripts = hiveLangugeParser.formatScript(originStr, DateUtil.toDateStr(calendar.getTime()));
		hiveLangugeParser.parseScript(scripts);
		
		
		List<Entity> targets = hiveLangugeParser.getOutput();
		System.out.println("输出表：");
		for(Entity  target : targets) {
			System.out.println("\t"+target.getDatabase()+"."+target.getName());
		}
		
		
		List<Entity> sources = hiveLangugeParser.getInput();
		sources.removeAll(targets);
		System.out.println("输入表：");
		for(Entity  source : sources) {
			System.out.println("\t"+source.getDatabase()+"."+source.getName());
		}
		
		
		
	}

}
