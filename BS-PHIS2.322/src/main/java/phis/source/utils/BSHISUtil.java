/**
 * @(#)EHRUtil.java Created on Oct 7, 2009 4:50:32 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.utils;

import java.io.File;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import phis.source.Constants;

import com.alibaba.fastjson.JSONException;

import ctd.schema.Schema;
import ctd.schema.SchemaItem;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class BSHISUtil {

	private static final Pattern integerPattern = Pattern
			.compile("(0|[1-9]\\d*)");
	private static final Pattern numberPattern = Pattern
			.compile("(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d+)");

	public static String getDate() {
		Date date = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd");
		return matter.format(date);
	}

	/**
	 * 获取当前指定格式的日期
	 * 
	 * @param format
	 * @return
	 */
	public static String getDate(String format) {
		Date date = new Date();
		SimpleDateFormat matter = new SimpleDateFormat(format);
		return matter.format(date);
	}

	public static String getDateTime() {
		Date date = new Date();
		SimpleDateFormat matter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return matter.format(date);
	}

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
	 * 日期转换为字符串，使用“yyyy-MM-dd”的格式转换。
	 * 
	 * @param date
	 * @return
	 */
	public static String toString(Date date) {
		return toString(date, null);
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
		String pattern = "/^((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$/";
		// 年月日正则表达式有错误
		// String pattern =
		// "\\d{4}[-,/,\\\\]{1}(0[1-9]{1}|1[0-2]{1})[-,/,\\\\]{1}(([0-2]{1}[1-9]{1})|10|20|30|31)";
		if (Pattern.matches(pattern, date)) {
			return null;
		}
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7)) - 1;
		int day = Integer.parseInt(date.substring(8, 10));
		Calendar c = Calendar.getInstance();
		c.set(year, month, day, 0, 0, 0);
		if (str.length() < 19) {
			return c.getTime();
		}
		String time = str.substring(11, 19);
		/* 将ptn中的[0-1]改称[0-2]，原因：20点之后会转化成0点 */
		String ptn = "[0-2]{1}([0-9]{1}|2[03]{1})[:,-,/]{1}[0-5]{1}[0-9]{1}[:,-,/]{1}[0-5]{1}[0-9]{1}";
		if (Pattern.matches(ptn, time)) {
			int hour = Integer.parseInt(time.substring(0, 2));
			int minute = Integer.parseInt(time.substring(3, 5));
			int second = Integer.parseInt(time.substring(6, 8));
			c.set(year, month, day, hour, minute, second);
		}
		return c.getTime();
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
		int days = getPeriod(begin, datum);

		return days % 7 > 0 ? days / 7 + 1 : days / 7;
	}

	/**
	 * 基准日期到开始日期满几周。
	 * 
	 * @param begin
	 * @param datum
	 * @return
	 */
	public static int getFullWeeks(Date begin, Date datum) {
		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(begin);
		Calendar now = Calendar.getInstance();
		if (datum != null) {
			now.setTime(datum);
		}
		if (dateCompare(beginDate.getTime(), now.getTime()) > 0) {
			return -1;
		}
		int days = getPeriod(begin, datum);

		return days / 7;
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
		if (birthday == null)
			return 0;
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
	 * 将一个MAP对象的数据添加到一个Map对象，并将字典代码转换成相应的文本值。
	 * 
	 * @param data
	 *            待添加的数据。
	 * @param target
	 *            目标数据，数据将被添加到这里。
	 * @param sc
	 * @throws JSONException
	 */
	public static void addMapToJson(Map<String, Object> data,
			Map<String, Object> target, Schema sc) {
		for (String key : data.keySet()) {
			SchemaItem item = sc.getItem(key);
			Object v = data.get(key);
			if (v == null) {
				continue;
			}
			if (item != null && item.isCodedValue()) {
				String vText = item.toDisplayValue(v);
				Map<String, Object> o = new HashMap<String, Object>();
				o.put("key", v);
				o.put("text", vText);
				target.put(key, o);
			} else {
				target.put(key, v);
			}
		}
	}

	/**
	 * 断一个字符串是不是整数。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		return integerPattern.matcher(str).matches();
	}

	/**
	 * 判断一个字符串是不是一个数字。
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		return numberPattern.matcher(str).matches();
	}

	/**
	 * 删除一个文件或目录。
	 * 
	 * @param dir
	 * @return 是否成功
	 */
	public static boolean removeDirectory(File dir) {
		if (dir.isFile()) {
			return dir.delete();
		}
		File files[] = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile()) {
				if (!files[i].delete()) {
					return false;
				}
				continue;
			}
			if (!removeDirectory(files[i])) {
				return false;
			}
		}
		return dir.delete();
	}

	/**
	 * 获取date在基准日期后第n周的头一天，比如date在datum的第10周，返回datum后第10周的头一天。
	 * 
	 * @param datum
	 *            基准日期
	 * @param date
	 *            目标日期
	 * @return
	 */
	public static String getBeginDateOfWeek(String datum, Date date) {
		Date datumDate = toDate(datum);
		return toString(getBeginDateOfWeek(datumDate, date));
	}

	/**
	 * 获取date在基准日期后第n周的头一天，比如date在datum的第10周，返回datum后第10周的头一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static Date getBeginDateOfWeek(Date datum, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(datum);
		int days = getPeriod(datum, date);
		int x = days % 7;
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, -x);
		return c.getTime();
	}

	/**
	 * 获取date在基准日期后第n周的最后一天，比如date在datum的第10周，返回datum后第10周的最后一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static String getEndDateOfWeek(String datum, Date date) {
		Date datumDate = toDate(datum);
		return toString(getEndDateOfWeek(datumDate, date));
	}

	/**
	 * 获取date在基准日期后第n周的最后一天，比如date在datum的第10周，返回datum后 第10周的最后一天。
	 * 
	 * @param datum
	 * @param date
	 * @return
	 */
	public static Date getEndDateOfWeek(Date datum, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(datum);
		int days = BSHISUtil.getPeriod(datum, date);
		int x = 7 - days % 7;
		c.setTime(date);
		c.add(Calendar.DAY_OF_YEAR, x - 1);
		return c.getTime();
	}

	/**
	 * IDLOADER多余多份档案并存的业务，挑出一条最正常的档案，传到前台去。最正常档案排序。 1、正常 2、未注销，已结案 3、注销（非作废）
	 * 4、注销（作废）
	 * 
	 * @param ids
	 * @return
	 */
	public static Map<String, Object> getTopRecord(List<Map<String, Object>> ids) {
		Map<String, Object> topRec = null;
		if (ids.size() == 0)
			return ids.get(0);
		for (int i = 0; i < ids.size(); i++) {
			Map<String, Object> idRec = ids.get(i);
			String status = (String) idRec.get("status");
			String closeFlag = (String) idRec.get("closeFlag");
			String cancellationReason = (String) idRec
					.get("cancellationReason");
			if (status != null && status.equals("0")) {
				if (closeFlag == null || closeFlag.equals("")
						|| closeFlag.equals("0")) {
					return idRec;
				} else {
					topRec = idRec;
				}
			} else if (status != null && status.equals("1")) {// 注销（非作废）
				if (!"6".equals(cancellationReason))
					return idRec;
			}
		}
		if (topRec == null)
			return ids.get(0);
		return topRec;
	}

	/**
	 * 将List<String>转换为以逗号(,)分隔的字符串返回
	 * 
	 * @param list
	 * @return
	 */
	public static String listToString(List<String> list) {
		StringBuffer sbIds = new StringBuffer("");
		for (int i = 0; i < list.size(); i++) {
			sbIds.append(list.get(i).trim()).append(",");
		}
		String ids = sbIds.toString().substring(0, sbIds.length() - 1);
		return ids;
	}

	public static String join(List<String> list, String separator) {
		StringBuffer v = new StringBuffer("");
		for (int i = 0; i < list.size(); i++) {
			v.append(list.get(i).trim()).append(separator);
		}
		return v.toString().length() > 0 ? v.toString().substring(0,
				v.length() - 1) : v.toString();
	}

	public static void main(String[] args) {
		System.out.println(BSHISUtil.getDate());
	}

	/**
	 * 得到几天前的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateBefore(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
		return now.getTime();
	}

	/**
	 * 得到几天后的时间
	 * 
	 * @param d
	 * @param day
	 * @return
	 */
	public static Date getDateAfter(Date d, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(d);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	/**
	 * 获取当前日期是星期几<br>
	 * 
	 * @param dt
	 * @return 将日期转化成数字，星期日表示1，星期六表示2.。。
	 */
	public static int getWeekOfDate(Date dt) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dt);
		int w = cal.get(Calendar.DAY_OF_WEEK);
		return w;
	}

	/**
	 * 计算两个日期的差，参数null表示当前日期。
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getDifferDays(Date date1, Date date2) {
		if (date1 != null && date2 != null) {
			return (int) ((date1.getTime() - date2.getTime()) / (24 * 60 * 60 * 1000));
		} else {
			return 0;
		}
	}

	/**
	 * 得到本月第一天的日期
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		cDay.set(Calendar.DAY_OF_MONTH, 1);
		return cDay.getTime();
	}

	/**
	 * 得到本月最后一天的日期
	 **/
	public static Date getLastDayOfMonth(Date date) {
		Calendar cDay = Calendar.getInstance();
		cDay.setTime(date);
		cDay.set(Calendar.DAY_OF_MONTH,
				cDay.getActualMaximum(Calendar.DAY_OF_MONTH));
		return cDay.getTime();
	}

	/**
	 * 获得2个日期之间的天数之差
	 * 
	 * @param begin
	 * @param datum
	 * @return
	 */
	public static int getWeeksForTem(Date begin, Date datum) {
		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(begin);
		Calendar now = Calendar.getInstance();
		if (datum != null) {
			now.setTime(datum);
		}
		if (dateCompare(beginDate.getTime(), now.getTime()) > 0) {
			return -1;
		}
		int days = getPeriod(begin, datum) + 1;

		return (int) (Math.ceil(days / 7f));
	}     
	
	/** 
     * double 相加 
     * add by zhaojian 2018-09-20
     * 解决double类型两个数相加结果不准确问题
     * @param d1 
     * @param d2 
     * @return 
     */ 
    public static double doublesum(double d1,double d2){ 
        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
        return bd1.add(bd2).doubleValue(); 
    } 
    /** 
     * double 相减 
     * add by zhaojian 2018-09-20
     * 解决double类型两个数相减结果不准确问题
     * @param d1 
     * @param d2 
     * @return 
     */ 
    public static double doublesub(double d1,double d2){ 
        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
        return bd1.subtract(bd2).doubleValue(); 
    } 
}
