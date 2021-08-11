package org.dw.common.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String MONTH_SUFFIX_PATTERN = "yyyyMM";
	
   /**
    * @param pattern
    * @return
    */
	private static SimpleDateFormat getDateParser(String pattern) {
	    return new SimpleDateFormat(pattern);
	}

   /**
    * 当前日期.
    * @return Data 
    */
	public static Date curDate() {
	    return new Date();
	}

   /**
    * 当前日期 （字符串）.
    * @param strFormat
    * @return String
    */
	public static String curDateStr(String strFormat) {
	    Date date = new Date();
	    return getDateParser(strFormat).format(date);
	}

	/**
	 *
	 * @return
	 */
	public static String curDateStr() {
		Date date = new Date();
	    return getDateParser(DEFAULT_DATE_PATTERN).format(date);
	}

	/**
	 *
	 * @return
	 */
	public static Timestamp curTimestamp(){
	    return new Timestamp(new Date().getTime());
	}

	/**
	 * 格式化日期 
	 * @param dateString
	 * @param pattern
	 * @return Date
	 */
	public static Date toDate(String dateString, String pattern) {
	    Date date = null;
	    try {
	        date = getDateParser(pattern).parse(dateString);
	    } catch(Exception e) {
	        return null;
	    }
	    return date;
	}

	/**
	 * 格式化日期(yyyy-MM-dd)
	 * @param dateString
	 * @return Date
	 */
	public static Date toDate(String dateString) {
	    Date date = null;
	    try {
	        date = getDateParser("yyyy-MM-dd").parse(dateString);
	    } catch (Exception e) {
	        return null;
	    }
	    return date;
	}

	/**
	 * 格式化日期(yyyy-MM-dd HH:mm:ss)
	 * @param dateString
	 * @return
	 */
	public static Date toDateTime(String dateString) {	
	    Date date = null;
	    try {
	        date = getDateParser("yyyy-MM-dd HH:mm:ss").parse(dateString);
	    } catch (Exception e) {
	        return null;
	    }
	    return date;
	}

	/**
	 * 日期-字符串
	 * @param date
	 * @param pattern
	 * @return String
	 */
	public static String toDateStr(Date date, String pattern) {	
	    if (date == null) {
	        return "";
	    }
	    return getDateParser(pattern).format(date);
	}

	/**
	 * 将日期转化为字符串(yyyy-MM-dd)
	 * @param date
	 * @return String
	 */
	 public static String toDateStr(Date date) {		 
	    if (date == null) {
	    	return "";
	    }
	    return getDateParser("yyyy-MM-dd").format(date);	
	 }

    /**
	 * (yyyy-MM-dd HH:mm:ss)
	 * @param date
	 * @return String
	 */
	public static String toDateTimeStr(Date date) {		
	    if (date == null) {
	        return "";
	    }
	    return getDateParser("yyyy-MM-dd HH:mm:ss").format(date);	
	}

	/**
	 * 日期增加或减少.
	 * @param date
	 * @param days
	 * @return Date
	 */
	public static Date addDays(Date date, int days) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.DATE, days);
	    return calendar.getTime();
	}

	/**
	 *
	 * @param date
	 * @param weeks
	 * @return
	 */
	public static Date addWeek(Date date, int weeks) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.WEEK_OF_YEAR, weeks);
	    return calendar.getTime();
	}

	/**
	 * 增加月.
	 * @param date
	 * @param months
	 * @return
	 */
	public static Date addMonth(Date date, int months) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.MONTH, months);
	    return calendar.getTime();
	}

	/**
	 * 日期增加或减少.(默认)
	 * @param date
	 * @param days
	 * @return Date
	 */
	public static String addDays(String date, int days, String pattern) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(toDate(date, pattern));
	    calendar.add(Calendar.DATE, days);
	    return toDateStr(calendar.getTime(), pattern);
	}

	/**
	 * 日期增加或减少.(默认)
	 * @param date
	 * @param days
	 * @return Date
	 */
	public static String addDays(Date date, int days, String pattern) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.DATE, days);
	    return toDateStr(calendar.getTime(), pattern);
	}

	/**
	 *
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date addMinute(Date date, int minute) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.MINUTE, minute);
	    return calendar.getTime();
	}
	
	/**
	 * 
	 * @param date
	 * @param dateType
	 * @param diff
	 * @return
	 */
	public static String addEndDate (Date date, String dateType, int diff) {
		if (date == null) {
			return null;
		}
		GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
		if (dateType.equals("day")) {
			calendar.add(Calendar.DATE, diff);
		} else if (dateType.equals("week")) {
			if (diff == 0) {
				calendar.add(Calendar.DATE, -1);
			} else {
				calendar.add(Calendar.WEEK_OF_YEAR, diff);
				setLastDayOfWeek(calendar);
			}
			//calendar.add(Calendar.WEEK_OF_YEAR, diff);
		} else if (dateType.equals("month")) {
			if (diff == 0) {
				calendar.add(Calendar.DATE, -1);
			} else {
				calendar.add(Calendar.MONTH, diff);
				setlastDayInMonth(calendar);
			}
		}
		return toDateStr(calendar.getTime());
	}

	/**
	 * 判断是否在两个日期之间.
	 * @param date
	 * @param before
	 * @param after
	 * @return boolean
	 */
	public static boolean isDateBetween(Date date, Date before, Date after){
	    return ( (before.before(date)) || (before.equals(date)) ) && 
	    	   ( (date.before(after))  || (date.equals(after)) );
	}

	/**
	 * 取两个日期间的时间.
	 * @param fromDate
	 * @param toDate
	 * @return int
	 */
	public static int getDaysInterval(Date fromDate, Date toDate) {
	    if((fromDate == null) || (toDate == null)) {
	        return 0;
	    }
	    long timeInterval = toDate.getTime() - fromDate.getTime();
	    //1000*60*60*24(86400000)
	    int daysInterval = (int)(timeInterval / 86400000L);
	    return daysInterval;
	}

	/**
	 * 取两个日期间间隔的分钟.
	 * @param fromDate
	 * @param toDate
	 * @return int
	 */
	public static int getSecInterval(Date fromDate, Date toDate) {
	    if((fromDate == null) || (toDate == null)) {
	        return 0;
	    }
	    long timeInterval = toDate.getTime() - fromDate.getTime();
	    //1000*60*60*24(86400000)
	    int daysInterval = (int)(timeInterval / (1000));
	    return daysInterval;
	}
	
	/**
	 * 一年当中的第几周(周日作为第一天).
	 * @param date
	 * @return int
	 */
	public static int getWeekOfYear(Date date) {
	    if (date == null) {
	        return -1;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setFirstDayOfWeek(Calendar.SUNDAY);
	    calendar.setTime(date);
	    System.out.println(calendar.get(Calendar.DAY_OF_YEAR));
	    int week = calendar.get(Calendar.WEEK_OF_YEAR);
	    
	    return week;
	}

	/**
	 * 一周中的第几天.
	 * @param date
	 * @return
	 */
	public static int getDayOfWeek(Date date) {
	    if (date == null) {
	        return -1;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    int day = calendar.get(Calendar.DAY_OF_WEEK);
	    day--;
	    if (0 == day) {
	    	day = 7;    	
	    }
	    return day;
	}

	/**
	 * .
	 * @param date
	 * @return Date
	 */
	public static Date getLastDayInMonth(Date date) {
		return getLastDayInMonth(date, 0);
	}
	
	/**
	 * .
	 * @param date
	 * @return Date
	 */
	public static boolean isLastDayInMonth(String date) {
		String last = toDateStr(getLastDayInMonth(toDate(date), 0));
		return date.equals(last);
	}

	/**
	 * .
	 * @param date
	 * @return
	 */
	public static Date getLastDayInNextMonth(Date date) {
		return getLastDayInMonth(date, 1);
	}

   /**
    * 一月中的最后一天.
    * @param date
    * @param i
    * @return Date
    */
	public static Date getLastDayInMonth(Date date, int i) {
	    if (date == null) {
	        return null;
	    }
	    GregorianCalendar calendar = new GregorianCalendar();
	    calendar.setTime(date);
	    calendar.add(Calendar.MONTH, i + 1);
	    calendar.set(Calendar.DATE, 1);
	    calendar.add(Calendar.DATE, -1);
	    return calendar.getTime();
	}

	/**
	 *
	 * @param calendar
	 */
	public static void setlastDayInMonth(Calendar calendar) {
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, -1);
	}

	/**
	 * .
	 * @param times
	 * @return String
	 */
	public static String toDateTime(long times) { 
		times /= 1000L;
	    long hours = times / 3600L;
	    times -= hours * 3600L;
	    long minutes = times / 60L;
	    times -= minutes * 60L;
	    long seconds = times;
	    String result = hours + "(h) " + minutes + "(m) " + seconds + "(s)";
	    return result;
	}

	/**
     * 获取当前时间所在周的开始日期.
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        //c.setMinimalDaysInFirstWeek(4);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    public static void setLastDayOfWeek(Calendar c) {
        c.setFirstDayOfWeek(Calendar.SUNDAY);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);
    }
    

	/**
	 *
	 * @param date
	 * @return
	 */
	public static Date getCurrentDay (Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		return calendar.getTime();
	}



	//得到指定日期所在月的第一天
	public static Date getMonthBeginDate(String date){
		return getMonthBeginDate(toDate(date));
	}
	public static Date getMonthBeginDate(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		return calendar.getTime();
	}
	//得到月份的后缀例如: 201711
	public static String getMonthSuffix(String date){
		return getMonthSuffix(toDate(date));
	}
	public static String getMonthSuffix(Date date){
		return toDateStr(date,MONTH_SUFFIX_PATTERN);
	}
	//月份的最后一天的日期
	public static Date getLastDayInMonth(String date) {
		return getLastDayInMonth(toDate(date));
	}
	//获取月份的ID
	public static String getMonthId(String date){
		return getMonthId(toDate(date));
	}
	public static String getMonthId(Date date){
		String monthId = toDateStr(date).replace("-", "M");
		return  monthId.substring(0, monthId.lastIndexOf("M"));
	}
	//获取前面一个月的月份ID
	public static String getPreMonthId(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		String monthId = toDateStr(calendar.getTime()).replace("-", "M");
		return  monthId.substring(0, monthId.lastIndexOf("M"));
	}
	public static String getPreMonthId(String date){
		return getPreMonthId(toDate(date));
	}

	//获取周的ID
	public static String getWeekId(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.setFirstDayOfWeek(Calendar.SUNDAY);
		calendar.setMinimalDaysInFirstWeek(3);
		int year = calendar.get(Calendar.YEAR);
		year = (calendar.get(Calendar.MONTH)==Calendar.JANUARY&&calendar.get(Calendar.WEEK_OF_YEAR)>50) ? year-1 : year;
		int week = calendar.get(Calendar.WEEK_OF_YEAR);
		return year + "W" + (week<10 ? "0" : "") + week;
	}
	public static String getWeekId(String date){
		return getWeekId(toDate(date));
	}

	//获取前面一个周的周ID
	public static String getPreWeekId(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		return getWeekId(calendar.getTime());
	}
	public static String getPreWeekId(String date){
		return getPreWeekId(toDate(date));
	}
}
