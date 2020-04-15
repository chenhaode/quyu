package com.chd.chat.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 时间工具类 
 * @author: chd
 */
public class DateUtils {

	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * @Description: 当前时间转换成 yyyy-MM-dd HH:mm:ss"格式
	 * @return: Date     
	 * @throws  
	 */
	public static Date getNowDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s1 = sdf.format(date);
		Date t1 = null;
		try {
			t1 = sdf.parse(s1);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return t1;

	}

	/**
	 * @Description: 当前时间转换成 yyyy-MM-dd"格式
     * @return: Date     
     * @throws  
	 */
	public static Date getNowDateYMD() {
		Date date = new Date();
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String s1 = sdf.format(date);
		Date t1 = null;
		try {
			t1 = sdf.parse(s1);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return t1;

	}

	/**
	 * @Description: 时间转换成 yyyy-MM-dd HH:mm:ss"格式  
	 * @param: date
     * @return: Date 
	 */

	public static Date getDateYMDHMS(Date date) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s1 = sdf.format(date);
		Date t1 = null;
		try {
			t1 = sdf.parse(s1);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return t1;

	}
	
	/**
	 * @Description: 时间转换成 yyyy-MM-dd"格式  
	 * @param: date
     * @return: Date 
	 */

	public static Date getDateYMD(Date date) {
		SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
		String s1 = sdf.format(date);
		Date t1 = null;
		try {
			t1 = sdf.parse(s1);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return t1;

	}

	/**
	 * @Description: 当前时间转换成字符串 yyyy-MM-dd HH:mm:ss
	 * @param: @return     
	 * @return: String     
	 */
	public static String nowDateToStr() {
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * @Description: 时间转换成字符串  
	 * @param date  
	 * @return: String     
	 */
	public static String dateToStr(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}

	/**
	 * @Description: 字符串转换成日期 yyyy-MM-dd HH:mm:ss
	 * @param str  
     * @return: Date     
	 */
	public static Date strToDateYMDHMS(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return date;
	}

	/**
     * @Description: 字符串转换成日期 yyyy-MM-dd
	 * @param str 
	 * @return: Date
	 */
	public static Date strToDateYMD(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			logger.error("日期格式转换发生异常", e);
		}
		return date;
	}


	/**
	 * @Description: 获取日期的开始时刻  
	 * @param: date  
	 * @return: Date
	 */
	public static Date convertDayBeginTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String str = format.format(date);
		return strToDateYMDHMS(str);

	}

	/**
	 * 
	 * @Description: 获取日期的结束时刻  
	 * @param: date 
	 * @return: str
	 */
	public static Date convertDayEndTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
		String str = format.format(date);
		return strToDateYMDHMS(str);

	}

	/**
	 * @Description: 获取指定年份的天数
	 * @param: year
	 * @return: Integer   
	 */
	public static Integer getDaysByYear(Integer year) {
		
		Integer days = 0;
		// 闰年
		if((year % 4 == 0 && year % 100 !=0)||(year % 400 ==0)) {
			 days = 366;
		 }else {
			 days = 365;
		 }
		return days;  
	}
	
	/**
	 * @Description: 获取指定日期是指定年的第几天
	 * @param: @param time
	 * @param: @throws ParseException   
	 * @return: Integer    
	 */
	public static Integer getPastDaysByDate(String time) throws ParseException {
		DateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
		Date date = fm.parse(time);
		//得到time日期是在这年的第几天
		Integer day = Integer.parseInt(String.format("%tj",date));
		return day;
	}
	
	/**
	 * @Description: 获取指定日期前后日期
	 * @param: @param date
	 * @return: Date  
	 */
	public static Date getPastDate(Date date, Integer count) {
		DateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateYMDHMS(date));
		cal.add(cal.DATE, count);
	    return  cal.getTime();
		
	}

	
	public static void main(String[] args) throws ParseException {

		System.out.println(getPastDate(new Date(), -5));
	}


}
