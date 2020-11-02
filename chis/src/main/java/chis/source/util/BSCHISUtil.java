/**
 * @(#)EHRUtil.java Created on Oct 7, 2009 4:50:32 PM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.conf.SystemCofigManageModel;

import com.alibaba.fastjson.JSONException;
import ctd.account.UserRoleToken;
import ctd.app.Application;
import ctd.controller.exception.ControllerException;
import ctd.net.rpc.invoker.InvokerHeaders;
import ctd.resource.ResourceCenter;
import ctd.schema.DisplayModes;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.schema.SchemaRelation;
import ctd.security.Permission;
import ctd.security.support.condition.FilterCondition;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

/**
 * @description
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class BSCHISUtil {

	private static final Pattern integerPattern = Pattern
			.compile("(0|[1-9]\\d*)");
	private static final Pattern numberPattern = Pattern
			.compile("(0|[1-9]\\d*)$|^(0|[1-9]\\d*)\\.(\\d+)");

	/**
	 * 获取config下的配置文件路径
	 * 
	 * @param subPath
	 *            子路径,不需要包含config本身.
	 * @return
	 * @throws IOException
	 */
	public static String getConfigPath(String subPath) throws IOException {
		StringBuffer path = new StringBuffer(Constants.DOMIN);
		path.append(File.separatorChar);
		path.append("config");
		path.append(File.separatorChar);
		path.append(subPath);
		return ResourceCenter.load(path.toString()).getURI().getPath();
	}

	/**
	 * 获取配置文件路径
	 * 
	 * @param subPath
	 *            子路径
	 * @return
	 * @throws IOException
	 */
	public static String getPath(String subPath) throws IOException {
		StringBuffer path = new StringBuffer(Constants.DOMIN);
		path.append(File.separatorChar);
		path.append(subPath);
		return ResourceCenter.load(path.toString()).getURI().getPath();
	}

	/**
	 * 比较两个日期的年月日，忽略时分秒。
	 * 
	 * @param d1
	 * @param d2
	 * @return 如果d1晚于d2返回大于零的值，如果d1等于d2返回0，否则返回一个负值。
	 */
	public static int dateCompare(Date d1, Date d2) {
		if (d1 == null || d2 == null) {
			return 0;
		}
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
		String pattern = "\\d{4}[-,/,\\\\]{1}(0[1-9]{1}|1[0-2]{1})[-,/,\\\\]{1}(([0-2]{1}[1-9]{1})|10|20|30|31)";
		if (false == Pattern.matches(pattern, date)) {
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
		String ptn = "[0-1]{1}([0-9]{1}|2[03]{1})[:,-,/]{1}[0-5]{1}[0-9]{1}[:,-,/]{1}[0-5]{1}[0-9]{1}";
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

		return days / 7;
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

		return (days + 1) / 7;
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
		List<SchemaItem> schemaItems = sc.getItems();
		for (SchemaItem item : schemaItems) {
			String key = item.getId();
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
	 * 将string转为double;
	 * 
	 * @param value
	 * @return
	 */
	public static double parseToDouble(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0.0;
		}
		return Double.parseDouble(value);
	}

	/**
	 * 将string转为int;
	 * 
	 * @param value
	 * @return
	 */
	public static int parseToInt(String value) {
		if (value == null || "".equals(value) || "null".equals(value)) {
			return 0;
		}
		return Integer.parseInt(value);
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

	public static Date getFirstDayOfMonth(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
	}

	public static Date getLastDayOfMonth(Date d) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return calendar.getTime();
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
		int days = BSCHISUtil.getPeriod(datum, date);
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
		StringBuffer sbIds = new StringBuffer("'");
		for (int i = 0; i < list.size(); i++) {
			sbIds.append(list.get(i).trim()).append("','");
		}
		String ids = sbIds.toString().substring(0, sbIds.length() - 2);
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void sort(List list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size() - i - 1; j++) {
				String c1 = String.valueOf(list.get(j));// 把object 类型转换成字符型
				String c2 = String.valueOf(list.get(j + 1));
				int v1 = Integer.valueOf(c1);// 再转换成整形
				int v2 = Integer.valueOf(c2);
				if (v1 > v2) {
					int temp = v2;// 设置中间变量，调换两数位置
					list.set(j + 1, v1);
					list.set(j, temp);
				}
			}
		}
	}

	public static HashMap<String, Object> getRpcHeader() {
		HashMap<String, Object> header = new HashMap<String, Object>();
		UserRoleToken r = UserRoleToken.getCurrent();
		header.put(InvokerHeaders.USER_ID, r.getUserId());
		header.put(InvokerHeaders.USER_ROLE_TOKEN_ID, r.getId());
		return header;
	}

	public static boolean isBlank(Object o) {
		if (o == null) {
			return true;
		} else {
			if (o instanceof String) {
				if (o.equals("")) {
					return true;
				}
			}
		}
		return false;
	}

	public static double transitionDouble(Object vObject) {
		double douRS = 0.0;
		if (vObject instanceof String) {
			douRS = Double.valueOf((String) vObject);
		} else if (vObject instanceof Integer) {
			douRS = ((Integer) vObject).doubleValue();
		} else if (vObject instanceof Long) {
			douRS = ((Long) vObject).doubleValue();
		} else {
			douRS = (Double) vObject;
		}
		return douRS;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Date date1 = toDate("2012-05-25");
		Date date2 = toDate("2012-05-28");
		System.out.println(getPeriod(date1, date2));
	}

	/**
	 * 更据年度和是否体检组装个档和专档的查询sql
	 * 
	 * @param schemaId
	 * @param queryCnd
	 * @param queryCndsType
	 * @param sortInfo
	 * @param pageSize
	 * @param pageNo
	 * @param year
	 * @param checkType
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static String getSQLBySchemaAddTable(String schemaId, List queryCnd,
			String queryCndsType, String sortInfo, String year, String checkType) {
		Schema sc = null;
		String sql = "select ";
		String sqlCopy = "select ";
		List cnds = new ArrayList();
		String queryCondition = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		boolean flag = true;
		if(schemaId.equals("chis.application.mobileApp.schemas.EHR_HealthRecordApp")){
			 flag = false;
		schemaId="chis.application.hr.schemas.EHR_HealthRecord";
		}
		if(schemaId.equals("chis.application.mobileApp.schemas.MDC_HypertensionRecordApp")){
			 flag = false;
		schemaId="chis.application.hy.schemas.MDC_HypertensionRecord";
		}
		String tableNames = schemaId.substring(schemaId.lastIndexOf(".") + 1,
				schemaId.length()) + " a,";
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if (!(p.getMode().isAccessible())) {
				continue;
			}
			if (it.getDisplayMode() == DisplayModes.NO_LIST_DATA) {
				continue;
			}
			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				sql = sql + refAlias + "." + fid + " as " + fid + ",";
				sqlCopy = sqlCopy + "t1." + fid + " as " + fid + ",";
				if (loadedRelation.containsKey(refAlias)) {
					continue;
				}
				SchemaRelation sr = sc.getRelationByAlias((String) it
						.getProperty("refAlias"));
				tableNames = tableNames
						+ sr.getFullEntryName().substring(
								sr.getFullEntryName().lastIndexOf(".") + 1,
								sr.getFullEntryName().length()) + " ,";
				List<?> cd = sr.getJoinCondition();
				if (cd != null) {
					cnds.add(cd);
				}
				loadedRelation.put(refAlias, true);
			} else {
				sql = sql + "a." + fid + " as " + fid + ",";
				sqlCopy = sqlCopy + "t1." + fid + " as " + fid + ",";
			}
		}
		tableNames = tableNames.substring(0, tableNames.length() - 1) + " ";
		if (StringUtils.isEmpty(queryCndsType)) {
			queryCndsType = "query";
		}
		FilterCondition c = (FilterCondition) sc.lookupCondition(queryCndsType);
		if(flag){
		if (c != null) {
			List<Object> roleCnd = (List<Object>) c.getDefine();
			if (roleCnd != null && !roleCnd.isEmpty()) {
				cnds.add(roleCnd);
			}
		}
		}
		if (queryCnd != null && !queryCnd.isEmpty()) {
			cnds.add(queryCnd);
		}
		List<Object> whereCnd = null;
		int cndCount = cnds.size();
		if (cndCount == 0) {
			whereCnd = (List<Object>) queryCnd;
		} else if (cndCount == 1) {
			whereCnd = (List<Object>) cnds.get(0);
		} else {
			whereCnd = new ArrayList<Object>();
			whereCnd.add("and");
			for (Object cd : cnds) {
				whereCnd.add((List<Object>) cd);
			}
		}
		if (StringUtils.isEmpty(sortInfo)) {
			sortInfo = sc.getSortInfo();
		}
		try {
			if ("1".equals(checkType)) {
				sql = sql.substring(0, sql.length() - 1) + " ";
				sql = sqlCopy
						+ " decode(t2.empiid,null,'"
						+ year
						+ "年未检',  '"
						+ year
						+ "年已检') as checkType "
						+ " from ("
						+ sql
						+ " from "
						+ tableNames
						+ " where "
						+ ExpressionProcessor.instance().toString(whereCnd)
						+ " ) t1 left join (select distinct t3.empiid from HC_HealthCheck t3 where to_char(t3.CHECKDATE,'yyyy')='"
						+ year + "') t2 on  t1.empiId = t2.EMPIID ";
				if (sortInfo != null && sortInfo.indexOf(".") > -1) {
					sortInfo = sortInfo.replaceAll("a\\.", "t1.");
				}
			} else if ("2".equals(checkType)) {
				sql += " '" + year + "年未检' as checkType  from  " + tableNames
						+ " where "
						+ ExpressionProcessor.instance().toString(whereCnd);
				;
				queryCondition = " a.empiId not in (select t.empiId from HC_HealthCheck"
						+ " t where a.empiId=t.empiId and to_char(t.checkDate,'yyyy')="
						+ year + ") ";
			} else if ("3".equals(checkType)) {
				sql += " '" + year + "年已检' as checkType  from  " + tableNames
						+ " where "
						+ ExpressionProcessor.instance().toString(whereCnd);
				;
				queryCondition = " a.empiId  in (select t.empiId from HC_HealthCheck"
						+ " t where a.empiId=t.empiId and to_char(t.checkDate,'yyyy')="
						+ year + ") ";
			}
		} catch (ExpException e) {
			e.printStackTrace();
		}
		if (queryCondition != null) {
			sql = sql + " and " + queryCondition;
		}
		if (sortInfo != null) {
			sql = sql + " order by " + sortInfo;
		}
		return sql;
	}

	/**
	 * 更据年度和是否体检组装个档和专档的总数查询sql
	 * 
	 * @param schemaId
	 * @param queryCnd
	 * @param queryCndsType
	 * @param sortInfo
	 * @param pageSize
	 * @param pageNo
	 * @param year
	 * @param checkType
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static String getCountSQLBySchemaAddTable(String schemaId,
			List queryCnd, String queryCndsType, String sortInfo, String year,
			String checkType) {
		Schema sc = null;
		String sql = "select count(*) as totalCount ";
		String tableNames = schemaId + " a,";	
		boolean flag = true;
		if(tableNames.equals("chis.application.mobileApp.schemas.EHR_HealthRecordApp a,")){
			flag = false;
			tableNames="EHR_HealthRecord a,";
		}
		if(tableNames.equals("chis.application.mobileApp.schemas.MDC_HypertensionRecordApp a,")){
			flag = false;
			tableNames="MDC_HypertensionRecord a,";
		}
		List cnds = new ArrayList();
		String queryCondition = null;
		try {
			sc = SchemaController.instance().get(schemaId);
		} catch (ControllerException e) {
			e.printStackTrace();
		}
		HashMap<String, Boolean> loadedRelation = new HashMap<String, Boolean>();
		List<SchemaItem> items = sc.getItems();
		for (SchemaItem it : items) {
			if (it.isVirtual()) {
				continue;
			}
			String fid = it.getId();
			Permission p = it.lookupPremission();
			if (!(p.getMode().isAccessible())) {
				continue;
			}
			if (it.getDisplayMode() == DisplayModes.NO_LIST_DATA) {
				continue;
			}
			if (it.hasProperty("refAlias")) {
				fid = (String) it.getProperty("refItemId");
				String refAlias = (String) it.getProperty("refAlias");
				if (loadedRelation.containsKey(refAlias)) {
					continue;
				}
				SchemaRelation sr = sc.getRelationByAlias((String) it
						.getProperty("refAlias"));
				tableNames = tableNames + sr.getFullEntryName() + " ,";
				List<?> cd = sr.getJoinCondition();
				if (cd != null) {
					cnds.add(cd);
				}
				loadedRelation.put(refAlias, true);
			}
		}
		if ("1".equals(checkType)) {

		} else if ("2".equals(checkType)) {
			queryCondition = " a.empiId not in (select t.empiId from chis.application.hc.schemas.HC_HealthCheck"
					+ " t where a.empiId=t.empiId and to_char(t.checkDate,'yyyy')="
					+ year + ") ";
		} else if ("3".equals(checkType)) {
			queryCondition = " a.empiId in (select t.empiId from chis.application.hc.schemas.HC_HealthCheck"
					+ " t where a.empiId=t.empiId and to_char(t.checkDate,'yyyy')="
					+ year + ") ";
		}
		tableNames = tableNames.substring(0, tableNames.length() - 1) + " ";
		if (StringUtils.isEmpty(queryCndsType)) {
			queryCndsType = "query";
		}
		FilterCondition c = (FilterCondition) sc.lookupCondition(queryCndsType);
		if(flag){
		if (c != null) {
			List<Object> roleCnd = (List<Object>) c.getDefine();
			if (roleCnd != null && !roleCnd.isEmpty()) {
				cnds.add(roleCnd);
			}
		}
		}
		if (queryCnd != null && !queryCnd.isEmpty()) {
			cnds.add(queryCnd);
		}
		List<Object> whereCnd = null;
		int cndCount = cnds.size();
		if (cndCount == 0) {
			whereCnd = (List<Object>) queryCnd;
		} else if (cndCount == 1) {
			whereCnd = (List<Object>) cnds.get(0);
		} else {
			whereCnd = new ArrayList<Object>();
			whereCnd.add("and");
			for (Object cd : cnds) {
				whereCnd.add((List<Object>) cd);
			}
		}
		try {
			sql = sql + " from  " + tableNames + " where "
					+ ExpressionProcessor.instance().toString(whereCnd);
		} catch (ExpException e) {
			e.printStackTrace();
		}
		if (queryCondition != null) {
			sql = sql + " and " + queryCondition;
		}

		return sql;
	}

	/**
	 * 获取计划周期天数，在不同月份会有1-2天的误差
	 * 
	 * @param planDate
	 *            计算开始日期
	 * 
	 * @param cycle
	 * @param frequency
	 * @return
	 */
	public static int getAllCycleDays(Date planDate, int cycle, int frequency) {
		if (planDate == null) {
			planDate = new Date();
		}
		Calendar cDay1 = Calendar.getInstance();
		cDay1.setTime(planDate);
		cDay1.add(frequency, cycle);
		Date endDate = cDay1.getTime();
		int days = getPeriod(endDate, planDate);
		return days;
	}
	
	/**
	 * 获取计划周期天数，在不同月份会有1-2天的误差
	 * 
	 * @param planDate
	 *            计算开始日期
	 * 
	 * @param cycle
	 * @param frequency
	 * @return
	 */
	public static int getAllCycleDaysMonth(Date planDate, int cycle, int frequency) {
		if (planDate == null) {
			planDate = new Date();
		}
		Date beginDate = BSCHISUtil.getFirstDayOfMonth(planDate);
		Calendar cDay1 = Calendar.getInstance();
		cDay1.setTime(planDate);
		cDay1.add(frequency, cycle);
		Date endDate = cDay1.getTime();
		endDate = BSCHISUtil.getFirstDayOfMonth(endDate);
		int days = getPeriod(endDate, beginDate);
		return days;
	}

	public static Date getSectionCutOffDate(String endMonthName,
			boolean nextYear) {
		Date cutDate = new Date();
		try {
			Application app;
			try {
				app = ApplicationUtil.getApplication(Constants.UTIL_APP_ID);
			} catch (ControllerException e) {
				throw new ModelDataOperationException(e);
			}
			String endMonth = (String) app.getProperty(endMonthName);
			String date = getEndDateForYear(endMonth);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			cutDate = sdf.parse(date);
			if (nextYear) {
				Calendar cd = Calendar.getInstance();
				cd.setTime(cutDate);
				cd.add(Calendar.YEAR, 1);
				cutDate = cd.getTime();
			}
		} catch (ModelDataOperationException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return cutDate;
	}

	/**
	 * 获取配置的年度结束日期
	 * 
	 * @param endMonth
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("deprecation")
	public static String getEndDateForYear(String endMonth)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (endMonth == null || "".equals(endMonth) || "12".equals(endMonth)) {
			source = sdf.format(new Date()) + "-12-31 23:59:59";
			return source;
		}
		int month = 11;
		if (endMonth.startsWith("0")) {
			month = Integer.parseInt(endMonth.substring(1)) - 1;
		} else {
			month = Integer.parseInt(endMonth) - 1;
		}
		Calendar cDay1 = Calendar.getInstance();
		Date date = new Date();
		date.setMonth(month);
		cDay1.setTime(date);
		int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		source = sdf.format(new Date()) + "-" + endMonth + "-" + lastDay
				+ " 23:59:59";
		return source;
	}

	/**
	 * 获取配置的年度开始日期
	 * 
	 * @param startMonth
	 * @return
	 * @throws ParseException
	 */
	public static String getStartDateForYear(String startMonth)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String source = "";
		if (startMonth == null || "".equals(startMonth)
				|| "01".equals(startMonth)) {
			source = sdf.format(new Date()) + "-01-01 00:00:00";
			return source;
		}
		source = (Integer.parseInt(sdf.format(new Date())) - 1) + "-"
				+ startMonth + "-01 00:00:00";
		return source;
	}
	//获取汉子拼音首字母
    public static String getPYSZM(String chinese){         
        String pinyinStr = "";  
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat(); 
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);  
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        for (int i = 0; i < newChar.length; i++) {  
            if (newChar[i] > 128) {  
                try {  
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0].charAt(0);  
                } catch (BadHanyuPinyinOutputFormatCombination e) {  
                    e.printStackTrace();  
                }  
            }else{  
                pinyinStr += newChar[i];  
            }  
        }  
        return pinyinStr;  
    }
}
