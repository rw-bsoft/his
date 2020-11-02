/**
 * @(#)EHRUtil.java Created on Oct 7, 2009 4:50:32 PM
 * 
 * ��Ȩ����Ȩ���� bsoft.com.cn ��������Ȩ����
 */
package phis.source.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import phis.source.Constants;
import ctd.schema.Schema;
import ctd.schema.SchemaItem;
import ctd.service.core.Service;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class EHRUtil {

	private static final Log logger = LogFactory.getLog(EHRUtil.class);

	/**
	 * 比较两个日期的年月日，忽略时分秒。
	 * 
	 * @param d1
	 * @param d2
	 * @return 如果d1晚于d2返回大于零的值，如果d1等于d2返回0，否则返回一个负值。
	 */
	public static int dateCompare(Date d1, Date d2) {
		Calendar c = Calendar.getInstance();
		c.setTime(d1);
		Calendar c2 = Calendar.getInstance();
		c2.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
		Date date0 = c2.getTime();

		c.setTime(d2);
		c2.set(Calendar.YEAR, c.get(Calendar.YEAR));
		c2.set(Calendar.MONTH, c.get(Calendar.MONTH));
		c2.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR));
		Date date1 = c2.getTime();

		return date0.compareTo(date1);
	}

	/**
	 * 日期转换为字符串，如果pattern为null，使用“yyyy-MM-dd”的格式转换。
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String toString(Date date, String pattern) {
		DateFormat sdf = pattern == null ? new SimpleDateFormat(
				Constants.DEFAULT_SHORT_DATE_FORMAT) : new SimpleDateFormat(
				pattern);
		return sdf.format(date);
	}

	/**
	 * 将日期转换成Date对象，支持的格式为yyyy-MM-dd HH:mm:ss，日期分隔符为（-,/,\）中的任意一个，
	 * 时间分隔符为（:,-,/）中的任意一个。 如果传入的日期格式不正确将返回null。
	 * 
	 * @param date
	 * @return 如果传入的日期格式正确将返回一个Date对象，否则返回null。
	 */
	public static Date toDate(String str) {
		if (str == null || str.length() < 10) {
			return null;
		}
		String date = str.substring(0, 10);
		String pattern = "\\d{4}[-,/,\\\\]{1}(0[1-9]{1}|1[0-2]{1})[-,/,\\\\]{1}(([0-2]{1}[1-9]{1})|10|20|30|31)";
		if (false == Pattern.matches(pattern, date)) {
			return null;
		}
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));
		Calendar c = Calendar.getInstance();
		c.set(year, month, day,0,0,0);
		if (str.length() < 19) {
			return c.getTime();
		}
		String time = str.substring(11, 19);
		String ptn = "[0-1]{1}([0-9]{1}|2[03]{1})[:,-,/]{1}[0-5]{1}[0-9]{1}[:,-,/]{1}[0-5]{1}[0-9]{1}";
		if (Pattern.matches(ptn, time)) {
			int hour = Integer.parseInt(time.substring(0, 2));
			int minute = Integer.parseInt(time.substring(3, 5));
			int second = Integer.parseInt(time.substring(6, 8));
			c.set(year, month, day, hour, minute, second);
		}
		return c.getTime();
	}

	public static void setError(HashMap<String,Object> res, int code, String msg){
		res.put(Service.RES_CODE, code);
		res.put(Service.RES_MESSAGE, msg);
		logger.error(msg);
	}

	/**
	 * 
	 * 
	 * @param begin
	 * @param datum
	 * @return 基准日期是开始日期后的第几周
	 */
	public static int getWeeks(Date begin, Date datum) {
		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(begin);
		Calendar now = Calendar.getInstance();
		if (datum != null) {
			now.setTime(datum);
		}
		if (dateCompare(beginDate.getTime(), now.getTime()) > 0) {
			return -1;
		}
		if (now.get(Calendar.YEAR) == beginDate.get(Calendar.YEAR)) {
			int days = now.get(Calendar.DAY_OF_YEAR)
					- beginDate.get(Calendar.DAY_OF_YEAR) + 1;
			return days % 7 > 0 ? days / 7 + 1 : days / 7;
		}
		int years = now.get(Calendar.YEAR) - beginDate.get(Calendar.YEAR);
		int days = beginDate.getActualMaximum(Calendar.DAY_OF_YEAR)
				- beginDate.get(Calendar.DAY_OF_YEAR) + 1;
		for (int i = 0; i < years - 1; i++) {
			beginDate.add(Calendar.YEAR, 1);
			days += beginDate.getActualMaximum(Calendar.DAY_OF_YEAR);
		}
		days += now.get(Calendar.DAY_OF_YEAR);

		return days % 7 > 0 ? days / 7 + 1 : days / 7;
	}

	/**
	 * 判断两个日期之间是否有间隔一个自然月，间隔一个自然月的定义是：月分相减为1，日期相减大于等于零。
	 * 如以下的日期：2010-02-11，2010-03-12，这两个日期是间隔一个自然月，2010-02-11，2010-03-10，
	 * 这两个日期的间隔不足一个自然月。
	 * 
	 * @param d0
	 *            较早的一个日期。
	 * @param d1
	 *            较晚的一个日期。
	 * @return
	 */
	public static boolean overMonth(Date date0, Date date1) {
		Calendar c0 = Calendar.getInstance();
		c0.setTime(date0);
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		int y0 = c0.get(Calendar.YEAR);
		int y1 = c1.get(Calendar.YEAR);
		int m0 = c0.get(Calendar.MONTH);
		int m1 = c1.get(Calendar.MONTH);
		int d0 = c0.get(Calendar.DAY_OF_MONTH);
		int d1 = c1.get(Calendar.DAY_OF_MONTH);
		if (y0 == y1 && m0 == m1) {
			return false;
		}
		if (m1 == 1 && d0 > c1.getActualMaximum(Calendar.MONTH)
				&& d1 == c1.getActualMaximum(Calendar.MONTH)) {
			return true;
		}
		if (d1 < d0) {
			return false;
		}
		return true;
	}

	/**
	 * 计算年龄（周岁）。
	 * 
	 * @param birthday
	 *            出生日期。
	 * @param calculateDate
	 *            计算日。
	 * @return
	 */
	public static int calculateAge(Date birthday, Date calculateDate) {
		Calendar c = Calendar.getInstance();
		if (calculateDate != null) {
			c.setTime(calculateDate);
		}
		Calendar birth = Calendar.getInstance();
		birth.setTime(birthday);
		int age = c.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
		c.set(Calendar.YEAR, birth.get(Calendar.YEAR));
		if (dateCompare(c.getTime(), birth.getTime()) < 0) {
			return age - 1;
		}
		return age;
	}

	/**
	 * 计算两个日期之间的天数，参数null表示当前日期。
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getPeriod(Date date1, Date date2) {
		if (date1 == null && date2 == null) {
			return 0;
		}
		if (date1 != null && date2 != null && date1.compareTo(date2) == 0) {
			return 0;
		}
		Calendar begin = Calendar.getInstance();
		if (date1 != null) {
			begin.setTime(date1);
		}
		Calendar end = Calendar.getInstance();
		if (date2 != null) {
			end.setTime(date2);
		}
		if (begin.after(end)) {
			Calendar temp = end;
			end = begin;
			begin = temp;
		}
		if (end.get(Calendar.YEAR) == begin.get(Calendar.YEAR)) {
			return end.get(Calendar.DAY_OF_YEAR)
					- begin.get(Calendar.DAY_OF_YEAR);
		}
		int years = end.get(Calendar.YEAR) - begin.get(Calendar.YEAR);
		int days = begin.getActualMaximum(Calendar.DAY_OF_YEAR)
				- begin.get(Calendar.DAY_OF_YEAR);
		for (int i = 0; i < years - 1; i++) {
			begin.add(Calendar.YEAR, 1);
			days += begin.getActualMaximum(Calendar.DAY_OF_YEAR);
		}
		days += end.get(Calendar.DAY_OF_YEAR);
		return days;
	}

	/**
	 * 计算两个日期间的月数。
	 * 
	 * @param date1
	 *            较早的一个日期
	 * @param date2
	 *            较晚的一个日期
	 * @return 如果前面的日期小于后面的日期将返回-1。
	 */
	public static int getMonths(Date date1, Date date2) {
		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(date1);
		Calendar now = Calendar.getInstance();
		now.setTime(date2);
		if (beginDate.after(now)) {
			return -1;
		}
		int mon = now.get(Calendar.MONTH) - beginDate.get(Calendar.MONTH);
		if (now.get(Calendar.DAY_OF_MONTH) < beginDate
				.get(Calendar.DAY_OF_MONTH)) {
			if (!(now.getActualMaximum(Calendar.DAY_OF_MONTH) == now
					.get(Calendar.DAY_OF_MONTH))) {
				mon -= 1;
			}
		}
		if (now.get(Calendar.YEAR) == beginDate.get(Calendar.YEAR)) {
			return mon;
		}
		return 12 * (now.get(Calendar.YEAR) - beginDate.get(Calendar.YEAR))
				+ mon;
	}

	/**
	 * 将一个MAP对象转换为一个JSON对象，并根据Schema将字典代码转成文本。
	 * 
	 * @param map
	 * @param sc
	 * @return
	 * @throws JSONException
	 */
	public static HashMap<String,Object> mapToJson(Map<String, Object> map, Schema sc){
		HashMap<String,Object> json = new HashMap<String,Object>();
		addMapToJson(map, json, sc);
		return json;
	}

	/**
	 * 将一个MAP对象的数据添加到一个JSON对象，并将字典代码转换成相应的文本值。
	 * 
	 * @param map
	 * @param json
	 * @param sc
	 * @throws JSONException
	 */
	public static void addMapToJson(Map<String, Object> map, HashMap<String,Object> json,
			Schema sc) {
//		Map<String, SchemaItem> schemaItems = sc.getAllItems();
//		for (String key : map.keySet()) {
//			SchemaItem item = schemaItems.get(key);
//			Object v = map.get(key);
//			if (v == null) {
//				continue;
//			}
//			if (item != null && item.isCodedValue()) {
//				String vText = item.getDisplayValue(v);
//				HashMap<String,Object> o = new HashMap<String,Object>();
//				o.put("key", v);
//				o.put("text", vText);
//				json.put(key, o);
//			} else {
//				json.put(key, v);
//			}
//		}
	}

	/**
	 * 断一个字符串是不是整数。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		return Pattern.matches("(0|[1-9]\\d*)", str);
	}

	/**
	 * 判断一个字符串是不是一个数字。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return Pattern.matches("(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d+)", str);
	}

	/**
	 * 删除一个文件或目录。
	 * 
	 * @param dir
	 */
	public static void removeDirectory(File dir) {
		if (dir.isFile()) {
			dir.delete();
			return;
		}
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				files[i].delete();
				continue;
			}
			removeDirectory(files[i]);
		}
		dir.delete();
	}

	/**
	 * 获取date在基准日期后第n周的头一天，比如date在datum的第10周，返回datum后 第10周的头一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static String getBeginDateOfThisWeek(String datum, Date date) {
		Date lmp = EHRUtil.toDate(datum);
		return EHRUtil.toString(getBeginDateOfThisWeek(lmp, date), null);
	}

	/**
	 * 获取date在基准日期后第n周的头一天，比如date在datum的第10周，返回datum后 第10周的头一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static Date getBeginDateOfThisWeek(Date datum, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(datum);
		int days = EHRUtil.getPeriod(datum, date);
		int x = days % 7;
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, -x );
		return c.getTime();
	}

	public static void main(String [] args){
		Calendar c = Calendar.getInstance();
		c.set(2011, 7, 18);
		c.add(Calendar.DAY_OF_YEAR, 3);
		Date d1 = c.getTime();
		c.add(Calendar.WEEK_OF_YEAR, 4);
//		c.add(Calendar.DAY_OF_YEAR, -1);
		Date d2 = c.getTime();
		
		System.out.println("404---------->" + getWeeks(d1, d2));
		
	}
	
	
	/**
	 * 获取date在基准日期后第n周的最后一天，比如date在datum的第10周，返回datum后 第10周的最后一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static String getEndDateOfThisWeek(String datum, Date date) {
		Date lmp = EHRUtil.toDate(datum);
		return EHRUtil.toString(getEndDateOfThisWeek(lmp, date), null);
	}
	
	/**
	 * 获取date在基准日期后第n周的最后一天，比如date在datum的第10周，返回datum后 第10周的最后一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static Date getEndDateOfThisWeek(Date datum, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(datum);
		int days = EHRUtil.getPeriod(datum, date);
		int x = 7 - days % 7;
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, x - 1);
		return c.getTime();
	}
	
	/**
	 * IDLOADER多余多份档案并存的业务，挑出一条最正常的档案，传到前台去。最正常档案排序。
	 * 1、正常
	 * 2、未注销，已结案
	 * 3、注销（非作废）
	 * 4、注销（作废）
	 * @param ids
	 * @return
	 */
	public static Map<String, Object> getTopRecord(List<Map<String, Object>> ids){
		Map<String, Object> topRec =null;
		if(ids.size()==0)
			return ids.get(0);
		for(int i =0 ;i<ids.size();i++){
			Map<String, Object> idRec = ids.get(i);
			String status =(String) idRec.get("status");
			String closeFlag =(String) idRec.get("closeFlag");
			String cancellationReason = (String) idRec.get("cancellationReason");
			if(status!=null && status.equals("0")){
				if(closeFlag==null || closeFlag.equals("") || closeFlag.equals("0")){
					return idRec ;
				}else{
					topRec = idRec ;
				}
			}else if(status!=null && status.equals("1")){//注销（非作废）
				if(!"6".equals(cancellationReason))
					return idRec;
			}
		}
		if(topRec==null)
			return ids.get(0);
		return topRec ;
	}
	
	public static String join(List<String> list,String separator){
		String v = "";
		for(int i=0;i<list.size();i++){
			v = list.get(i)+separator;
		}
		return v.length()>0?v.substring(0, v.length()-1):v;
	}
}
