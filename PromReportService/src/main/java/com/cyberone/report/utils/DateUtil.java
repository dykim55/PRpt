package com.cyberone.report.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
/**
 * 
 * 
 * create: 2012. 7. 10.
 * @author hylee
 *
 */

public class DateUtil {
	
	public static String getCurrDate(String pattern){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	public static boolean checkExpireDate(String date, long time){
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(date.substring(0,4)),
				Integer.parseInt(date.substring(4,6))-1,
				Integer.parseInt(date.substring(6,8)),
				Integer.parseInt(date.substring(8,10)),
				Integer.parseInt(date.substring(10,12)),
				Integer.parseInt(date.substring(12)));
		
		return new Date().before(new Date(cal.getTimeInMillis()+time));
	}

	/**
	* 현재 날짜를 yyyy년 MM월 dd일 HH시 mm분의 형태로 값을 얻어낸다.
	*
	* @return yyyy년 MM월 dd일 HH시 mm분의 형태로 바뀐 현재 시간값
	*/
	public static String getTime() {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초");
		return sdf.format(d);
	}


	/**
	* 현재 날짜를 입력받은 포맷의 형태로 변환하여 결과값을 리턴하도로 한다.
	* 예) format : yyyyMMddHHmmss --> 20031130124130 로 결과값 반환
	*/
	public static String getTime(String format) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	/**
	* 오늘을 기준으로 입력받은 날자의 날짜를 알아낸다
	*
	* @return yyyy/mm/dd형태로 변경되된 문자열값
	*/
	public static String getDayInterval(String format, int distance){
		Calendar cal = getCalendar();
		cal.add(Calendar.DATE , distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	/**
	* 입력받은 날자의 날짜를 기준으로 해당일의 과거나 미래일을 알아낸다
	*
	* @return format형태로 변경되된 문자열값
	*/
	public static String getDayInterval(String dateString, String format, int distance){
		Calendar cal = getCalendar(dateString);
		cal.add(Calendar.DATE , distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	/**
	 * 입력받은 날자의 월을 기준으로 해당월의 과거나 미래월을 알아낸다
	 * 
	 * @return format형태로 변경되된 문자열값
	 */
	public static String getMonthInterval(String format, int distance){
		return getMonthInterval(format, distance, getTime("yyyyMM"));
	}
	public static String getMonthInterval(String format, int distance, String dateString){
		Calendar cal = getCalendar(dateString, format);		
		cal.add(Calendar.MONTH, distance);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(d);
	}
	
	/**
	* GMT기준시간중의 한국표준시를 반환한다.
	*
	* return GMT+09:00형태의 대한민국표준시
	*/
	public static Calendar getCalendar(){
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
		calendar.setTime(new Date());
		return calendar;
	}
	
	/**
	* GMT기준시간중의 한국표준시를 반환한다.
	*
	* return GMT+09:00형태의 대한민국표준시
	*/
	public static Calendar getCalendar(String dateString){
		Calendar calendar = new GregorianCalendar( TimeZone.getTimeZone("GMT+09:00"),Locale.KOREA);
		calendar.setTime (string2Date(dateString, "yyyyMMdd"));

		return calendar;
	}
	
	/**
	* GMT기준시간중의 한국표준시를 반환한다.
	*
	* return GMT+09:00형태의 대한민국표준시
	*/
	public static Calendar getCalendar(String dateString, String format){
		Calendar calendar = new GregorianCalendar( TimeZone.getTimeZone("GMT+09:00"),Locale.KOREA);
		calendar.setTime (string2Date(dateString, format));

		return calendar;
	}
	
	/**
	* 문자열 데이터를 사용자형태의 Date형태의 객체로 바꾸어준다
	* String -> Date (2000/09/25)
	*
	* @param s Date형태로 만들게 될 yyyy/mm/dd형태의 문자열
	* @return yyyy/mm/dd형태로 변경되어진 Date객체
	*/
	public static java.util.Date string2Date(String s, String format){
		java.util.Date d = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			d = sdf.parse(s, new ParsePosition(0));
		}
		catch (Exception e) {
			throw new RuntimeException("Date format not valid.");
		}
		return d;
	}
	
	/** 2012.11.26 
	* 오늘을 기준으로 어제의 날짜를 알아낸다
	* @author shin (copy해옴) 
	* @return yyyy/mm/dd형태로 변경되된 문자열값
	*/
	public static String getYesterday(){
		Calendar cal = getCalendar();
		cal.roll(Calendar.DATE, -1);
		Date d = cal.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		return sdf.format(d);
	}
	
	public static String getLastDayOfCurrentMonth(){
		Calendar calendar = Calendar.getInstance();
		
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+"";
	}
	
	public static String getLastDayOfCurrentMonth(int year, int month){
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)+"";
	}
	
	public static int getLastDayOfCurrentMonth(String dateString){
		Calendar calendar = Calendar.getInstance();
		
		int year = StringUtil.parseInt(dateString.substring(0,4),0);
		int month = StringUtil.parseInt(dateString.substring(5,7),0)-1;
		
		calendar.set(year, month, 1);
		
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	
	/***************************************************************
	* 해당날짜의 요일을 계산한다.
	* @param : String dateValue(Input Value) //20000225
	* @return : String mm(Output Value) //금 (2000/2/25)
	***************************************************************/
	public static String getDayOfWeekKr(String dateValue){

		if (dateValue == null || dateValue.equals("")){
			return "";
		}

		dateValue = StringUtil.deleteChar(dateValue);

		int yyyy=0,MM=1,dd=1,day_of_week; // default

		String days[]={"일","월","화","수","목","금","토"};

		try{
			yyyy=Integer.parseInt(dateValue.substring(0,4));
			MM=Integer.parseInt(dateValue.substring(4,6));
			dd=Integer.parseInt(dateValue.substring(6,8));
		}catch (Exception ex){
			// do nothing
		}

		Calendar cal=Calendar.getInstance();
		cal.set(yyyy,MM-1,dd);
		day_of_week=cal.get(Calendar.DAY_OF_WEEK); //1(일),2(월),3(화),4(수),5(목),6(금),7(토)

		return days[day_of_week-1];
	}
	
	/***************************************************************
	* 해당날짜의 요일을 계산한다.
	* @param : String dateValue(Input Value) //20000225
	* @return : String mm(Output Value) //금 (2000/2/25)
	***************************************************************/
	public static String getDayOfWeekIdx(String dateValue){

		if (dateValue == null || dateValue.equals("")){
			return "";
		}

		dateValue = StringUtil.deleteChar(dateValue);

		int yyyy=0,MM=1,dd=1,day_of_week; // default

		try{
			yyyy=Integer.parseInt(dateValue.substring(0,4));
			MM=Integer.parseInt(dateValue.substring(4,6));
			dd=Integer.parseInt(dateValue.substring(6,8));
		}catch (Exception ex){
			// do nothing
		}

		Calendar cal=Calendar.getInstance();
		cal.set(yyyy,MM-1,dd);
		day_of_week=cal.get(Calendar.DAY_OF_WEEK); //1(일),2(월),3(화),4(수),5(목),6(금),7(토)

		return day_of_week+"";
	} 

	
	/***************************************************************
	* 해당날짜의 주차을 계산한다.
	* @param : String dateValue(Input Value) //20000225
	* @return : String mm(Output Value) //금 (2000/2/25)
	***************************************************************/
	public static String getWeekOfMonth(String dateValue){

		if (dateValue == null || dateValue.equals("")){
			return "";
		}

		dateValue = StringUtil.deleteChar(dateValue);

		int yyyy=0,MM=1,dd=1,day_of_week; // default

		try{
			yyyy=Integer.parseInt(dateValue.substring(0,4));
			MM=Integer.parseInt(dateValue.substring(4,6));
			dd=Integer.parseInt(dateValue.substring(6,8));
		}catch (Exception ex){
			// do nothing
		}

		Calendar cal=Calendar.getInstance();
		cal.set(yyyy,MM-1,dd);
		day_of_week=cal.get(Calendar.WEEK_OF_MONTH);

		return day_of_week+"";
	}
	/**********************************************************
	 * (0 = Sunday, 1 = Monday, 2 =  Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday)
	 * 특정일(yyyyMMdd) 에서 주어진 일자만큼 더한 날짜를 계산한다.
	 * @param : String s_start(Input Value) //yyyy-mm-dd
	 * @param : String s_end(Input Value) //yyyy-mm-dd
	 * @return : String time(Output Value)
	 **********************************************************/
	public static String getRelativeDate(String date, String format, int rday)
			throws Exception {
		if (date == null) {
			return null;
		}

		if (date.length() < 8) {
			return ""; // 최소 8 자리
		}

		String time = "";

		try {
			TimeZone kst = TimeZone.getTimeZone("JST");
			TimeZone.setDefault(kst);
			Calendar cal = Calendar.getInstance(kst); // 현재

			int yyyy = Integer.parseInt(date.substring(0, 4));
			int mm = Integer.parseInt(date.substring(4, 6));
			int dd = Integer.parseInt(date.substring(6, 8));

			cal.set(yyyy, mm - 1, dd); // 카렌더를 주어진 date 로 세팅하고
			cal.add(cal.DATE, rday); // 그 날자에서 주어진 rday 만큼 더한다.

			time = new SimpleDateFormat(format).format(cal.getTime());
		} catch (Exception ex) {
		}
		return time;
	}
	
	/**
	 * 시작일로부터 종료일까지의 일수를 구함
	 * @param fromDate 시작일자
	 * @param toDate 종료일자
	 * @param both 양편넣기 여부
	 * @return 시작일자로 부터 종료일까지의 일수
	 */
    public static int getDiffDays(Date fromDate, Date toDate, boolean both){
		long diffDays = toDate.getTime() - fromDate.getTime();
		long days = diffDays / (24 * 60 * 60 * 1000);
		if (both){
			if (days >= 0) days += 1; else days -= 1;
		}
		return new Long(days).intValue();
    }
	
}
