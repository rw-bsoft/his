package chis.source.mobilempi;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.pub.PublicModel;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.schema.SchemaItem;
import ctd.util.S;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;

public class VisitPlanModels extends PublicModel {

	public VisitPlanModels(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}

	public Map<String, Object> listVistPlan(Map<String, Object> req)
			throws ModelDataOperationException {
		long start = System.currentTimeMillis();
		int pageSize = Constants.DEFAULT_PAGESIZE;
		if (req.containsKey("pageSize")) {
			pageSize = 10;
		}
		int pageNo = Constants.DEFAULT_PAGENO;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		String endDate = "";
		// if (req.containsKey("endDate")) {
		// endDate = (String) req.get("endDate");
		// }
		// String startDate = "";
		// if (req.containsKey("startDate")) {
		// startDate = (String) req.get("startDate");
		// }
		StringBuffer sfBuffer = new StringBuffer();
		String where = "";
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> par = new HashMap<String, Object>();
		Map<String, Object> parMap = new HashMap<String, Object>();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // 设置当前日期
		c.add(Calendar.DATE, 14);
		Date date = c.getTime();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String strDate = format.format(date);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date()); // 设置当前日期
		c2.add(Calendar.DATE, -14);
		Date date2 = c2.getTime();
		SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
		String strDate2 = format.format(date2);
//		parameters.put("max", pageSize);
//		parameters.put("first", pageNo);
		String cnd = null;
		String sqlCnd = "";
		if (req.containsKey("cnd")) {
			String cnd2 = (String) req.get("cnd");
			sqlCnd = " and (a.personName like '%" + cnd2
					+ "%' or a.idcard like '%" + cnd2
					+ "%' or c.regioncode_text like '%" + cnd2 + "%')";
		}
		String role = UserRoleToken.getCurrent().getRoleId();
		String manaDoctorId = UserRoleToken.getCurrent().getUserId();
		String sql = "";
		if (role.equals("chis.100")) {
			sql = "select a.empiId as empiId,a.personName as personName,"
					+ " a.idCard as idCard,a.birthday as birthday,a.mobileNumber as mobileNumber"
					+ ",c.regionCode_text as regionCode_text,c.regionCode as regionCode,case when length(c.regionCode)>19 then  substr(c.regionCode,5,18)  when length(c.regionCode)=19 then  substr(c.regionCode,2,18) else  c.regionCode  end  as regionCode1,"
					+ "b.planId as planId,c.phrId as recordId,b.beginDate as beginDate,b.endDate as endDate"
					+ ",b.visitId as visitId,b.planDate as planDate,b.businessType as businessType,b.planStatus as planStatus"
					+ " from mpi_demographicinfo a , pub_visitplan b ,EHR_HealthRecord c where  a.empiid =b.empiid and a.empiid = "
					+ "c.empiid and b.planStatus=:planStatus  and to_date('"
					+ strDate
					+ "','yyyy-MM-dd')>=b.plandate "
					+ "and to_date('"
					+ strDate2
					+ "','yyyy-MM-dd')<=b.plandate and b.businessType in (1,2) "
					+ sqlCnd + " and c.familyDoctorId='" + manaDoctorId
					+ "' order by a.regionCode";
		} else {
			sql = "select a.empiId as empiId,a.personName as personName,"
					+ " a.idCard as idCard,a.birthday as birthday,a.mobileNumber as mobileNumber"
					+ ",c.regionCode_text as regionCode_text,c.regionCode as regionCode,case when length(c.regionCode)>19 then  substr(c.regionCode,5,18)  when length(c.regionCode)=19 then  substr(c.regionCode,2,18) else  c.regionCode  end  as regionCode1,"
					+ "b.planId as planId,c.phrId as recordId,b.beginDate as beginDate,b.endDate as endDate"
					+ ",b.visitId as visitId,b.planDate as planDate,b.businessType as businessType,b.planStatus as planStatus"
					+ " from mpi_demographicinfo a , pub_visitplan b ,EHR_HealthRecord c where  a.empiid =b.empiid and a.empiid = "
					+ "c.empiid  and b.planStatus=:planStatus and to_date('"
					+ strDate
					+ "','yyyy-MM-dd')>=b.plandate "
					+ "and to_date('"
					+ strDate2
					+ "','yyyy-MM-dd')<=b.plandate and b.businessType in (1,2) "
					+ sqlCnd + " and c.manaDoctorId='" + manaDoctorId
					+ "' order by c.regionCode";
		}
		List<Map<String, Object>> rs = null;
		// parameters.put("manaDoctorId",
		// UserRoleToken.getCurrent().getUserId());
		parameters.put("planStatus", "0");
		try {
			rs = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Map<String, Object>> planList = null;
		String empiIdRs = "";
		for (int i = 0; i < rs.size(); i++) {
			if (empiIdRs.equals((String) rs.get(i).get("EMPIID"))) {
				planList.add(rs.get(i));
				parMap.put(empiIdRs, planList);
			} else {
				planList = new ArrayList<Map<String, Object>>();
				empiIdRs = (String) rs.get(i).get("EMPIID");
				Map<String, Object> mapSub = rs.get(i);
				planList.add(mapSub);
				parMap.put(empiIdRs, planList);
			}
		}
		Map<String, Object> resBody = new HashMap<String, Object>();
		List<Map<String, Object>> subList = new ArrayList<Map<String, Object>>();
		for (String key1 : parMap.keySet()) {
			String empiId = key1;
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			Map<String, Object> mapNew = new LinkedHashMap<String, Object>();
			Map<String, Object> mapHy = new LinkedHashMap<String, Object>();
			Map<String, Object> mapDi = new LinkedHashMap<String, Object>();
			List<Map<String, Object>> rsMap = (List<Map<String, Object>>) parMap
					.get(empiId);
			map = rsMap.get(0);
			mapNew.put("empiId", (String) map.get("EMPIID"));
			mapNew.put("personName", (String) map.get("PERSONNAME"));
			mapNew.put("idCard", (String) map.get("IDCARD"));
			mapNew.put("birthday", (Date) map.get("BIRTHDAY"));
			mapNew.put("mobileNumber", (String) map.get("MOBILENUMBER"));
			mapNew.put("regionCode", (String) map.get("REGIONCODE"));
			mapNew.put("regionCode1", (String) map.get("REGIONCODE1"));
			mapNew.put("regionCode_text", (String) map.get("REGIONCODE_TEXT"));
			mapNew.put("planDate", (Date) map.get("PLANDATE"));
			Date birthday = (Date) mapNew.get("birthday");
			int age = VisitPlanModels.calculateAge(birthday, null);
			mapNew.put("age", age);
			if (age > 150) {
				continue;
			}
			mapNew.put("plan", parMap.get(empiId));
			subList.add(mapNew);
		}
		List<Map<String, Object>> planList1 = new ArrayList<Map<String, Object>>();
		int start1= pageSize*pageNo;
		int start2= (pageSize*pageNo)+pageSize+1;
		if(start2>subList.size()){
			start2=subList.size();
		}else{
			start2=start2;
		}
		for (int i =start1; i <start2;  i++) {
			if(i<=subList.size()){
			   planList1.add(subList.get(i));
		  }
		}
		Collections.sort(planList1, new Comparator<Map<String, Object>>() {
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				long a1 = Long.parseLong(o1.get("regionCode1") + "");
				long a2 = Long.parseLong(o2.get("regionCode1") + "");
				if (a1 > a2) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		resBody.put("data", planList1);
//		resBody.put("limit", pageSize);
//		resBody.put("start", pageNo);
		return resBody;
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
}
