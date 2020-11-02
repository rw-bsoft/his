package chis.source.schedule;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.web.context.WebApplicationContext;
import chis.source.BaseDAO;
import chis.source.gis.ServiceUtil;
import chis.source.util.BSCHISUtil;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;
import ctd.util.exp.ExpRunner;

public class StatScheduleUtil {
	private static Log logger = LogFactory.getLog(StatScheduleUtil.class);
	public WebApplicationContext wac = (WebApplicationContext)AppContextHolder.get();
	public Session ss = null;
	public SessionFactory sf = null;
	public String databaseName = "";

	// 获取Session
	@SuppressWarnings("deprecation")
	public Session getSession() {
		wac = (WebApplicationContext)AppContextHolder.get();
		sf = (SessionFactory) wac.getBean("mySessionFactory");
		ss = sf.openSession();
		try {
			databaseName = ss.connection().getMetaData()
					.getDatabaseProductName();
		} catch (Exception e) {
			logger.error("Cannot get database name.", e);
		}
		return ss;
	}

	// 从XML取SQL统计
	@SuppressWarnings("rawtypes")
	public void executeXmlKpi(String fileName) {
		String appHome = this.getClass().getClassLoader().getResource("/")
				.getPath();
		if (appHome != null) {
			try {
				appHome = URLDecoder.decode(appHome, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		String Path = "";
		if (databaseName.indexOf("Oracle") >= 0) {
			Path = appHome + "config/schedule/oracle/" + fileName;
		} else if (databaseName.indexOf("DB2") >= 0) {
			Path = appHome + "config/schedule/db2/" + fileName;
		}
		try {
			Document xmlDoc = ServiceUtil.read(Path);
			List sqlList = xmlDoc.getDocument().selectNodes("//sql");
			Element d = xmlDoc.getDocument().getRootElement();
			String rundate = d.attributeValue("rundate");
			String kpiCode = "";
			String date = "";
			for (int i = 0; i < sqlList.size(); i++) {
				Element kpiEl = (Element) sqlList.get(i);
				kpiCode = kpiEl.attributeValue("kpiCode");
				String kpiSql = kpiEl.attributeValue("kpiSql");
				String querySql = "select max(runDate) from STAT_ManageUnit where kpiCode='"
						+ kpiCode + "'";
				ScrollableResults mD = ss.createSQLQuery(querySql).scroll();
				Object maxDate = null;
				while (mD.next()) {
					maxDate = mD.get(0);
				}
				if (kpiEl.attributeValue("date") != null && rundate != null) {
					date = kpiEl.attributeValue("date");
					if (kpiEl.attributeValue("rundate") != null) {
						rundate = kpiEl.attributeValue("rundate");
					}

					if (maxDate != null) {
						String wDate = executeStatDate(date, maxDate.toString());
						String ss[] = kpiSql.split("where");
						if (ss.length == 2) {
							kpiSql = ss[0] + " where " + wDate + " and "
									+ ss[1];
						}
					}
				}
				ArrayList result = executeKpi(kpiSql.toString(),
						kpiCode.toString());
				if (result != null) {
					executeSQL(result);
				} else {
					continue;
				}
			}
		} catch (MalformedURLException e) {
			logger.info(e);
			e.printStackTrace();
		} catch (DocumentException e) {
			logger.info(e);
			e.printStackTrace();
		}
	}

	// 统计时间
	public String executeStatDate(String field, String maxDate) {
		String date = "";
		if (databaseName.indexOf("Oracle") >= 0) {
			if (maxDate != null && !maxDate.equals("")) {
				date = "to_char("
						+ field
						+ ",'yyyy-mm-dd')<=to_char(current_date,'yyyy-mm-dd') and to_char("
						+ field + ",'yyyy-mm-dd')>='" + maxDate + "'";
			} else {
				date = "to_char(" + field
						+ ",'yyyy-mm-dd')=to_char(current_date,'yyyy-mm-dd')";
			}
		} else if (databaseName.indexOf("DB2") >= 0) {
			if (maxDate != null && !maxDate.equals("")) {
				date = "char(" + field
						+ ",iso)<=char(current_date,iso) and char(" + field
						+ ",iso)>='" + maxDate + "'";
			} else {
				date = "char(" + field + ",iso)=char(current_date,iso)";
			}
		}
		return date;
	}

	// 执行SQL 装数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList executeKpi(String hql, String KPICode) {
		Long b = System.currentTimeMillis();
		ArrayList rsArry = new ArrayList();
		try {
			SQLQuery x = ss.createSQLQuery(hql);

			if (x.list().size() > 0) {
				ScrollableResults q = x.scroll();
				while (q.next()) {
					HashMap manageUnitMap = new HashMap();
					String manageUnit = (String) q.get(0);
					String date = (String) q.get(1);
					String kpi = String.valueOf(q.get(2));
					if ("null".equals(kpi) || date == null
							|| manageUnit == null) {
						logger.info("此查询结果中有空值 KPICode:" + KPICode + " hql:"
								+ hql);
						continue;
					}
					String doctor = "";
					if (q.get().length > 3) {
						doctor = String.valueOf(q.get(3));
					}
					manageUnitMap.put("manageUnit", manageUnit);
					manageUnitMap.put("date", date);
					manageUnitMap.put("kpi", kpi);
					manageUnitMap.put("KPICode", KPICode);
					if (!"".endsWith(doctor) && doctor != null) {
						manageUnitMap.put("doctor", doctor);
					}
					rsArry.add(manageUnitMap);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.info("select数据:" + KPICode + ":" + hql + " e:", e);
		}
		Long e = System.currentTimeMillis();
		logger.info(KPICode + ":" + (e - b) / 1000 + "秒");
		return rsArry;
	}

	// 插入,更新数据
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void executeSQL(ArrayList rsArry) {
		if (rsArry != null) {
			long now = System.currentTimeMillis();
			Date systemdate = new Date(now);
			int x = 0;
			int cr = 0;
			int gx = 0;
			long inTime = 0;
			long upTime = 0;
			long rTime = 0;
			String KPICode = "";
			String querySql = "";
			String debug = "";
			StringBuilder hqls = new StringBuilder();
			String key = "";
			for (int i = 0; i < rsArry.size(); i++) {
				try {
					long bb = System.currentTimeMillis();
					HashMap<String, Object> r = (HashMap<String, Object>) rsArry
							.get(i);
					String d = StringUtils.trimToEmpty((String) r.get("date"));
					if (d == "") {
						long dNull = System.currentTimeMillis();
						rTime += dNull - bb;
						x++;
						continue;
					}
					String manageUnit = StringUtils.trimToEmpty((String) r
							.get("manageUnit"));
					KPICode = StringUtils
							.trimToEmpty((String) r.get("KPICode"));
					String kpi = StringUtils.trimToEmpty((String) r.get("kpi"));
					String doctor = StringUtils.trimToEmpty((String) r
							.get("doctor"));
					String date[] = d.split("-");
					if (date[0].equals("0000") || date[1].equals("00")) {
						long dNull = System.currentTimeMillis();
						rTime += dNull - bb;
						x++;
						continue;
					}
					if (databaseName.indexOf("Oracle") >= 0) {
						querySql = "select StatID,KPI from STAT_ManageUnit where manageUnit='"
								+ manageUnit
								+ "' and to_char(statDate,'yyyy-mm-dd')='"
								+ date[0]
								+ "-"
								+ date[1]
								+ "-"
								+ date[2]
								+ "' and KPICode='" + KPICode + "'";
					} else if (databaseName.indexOf("DB2") >= 0) {
						querySql = "select StatID,KPI from STAT_ManageUnit where manageUnit='"
								+ manageUnit
								+ "' and char(statDate,iso)='"
								+ date[0]
								+ "-"
								+ date[1]
								+ "-"
								+ date[2]
								+ "' and KPICode='" + KPICode + "'";
					}
					if (!"".equals(doctor) && doctor != null) {
						querySql += " and doctor='" + doctor + "'";
					}

					String season = getSeason(Integer
							.parseInt((String) date[1]));
					SQLQuery ls = ss.createSQLQuery(querySql);
					ScrollableResults lr = ls.scroll();
					HashMap<String, String> rsMap = new HashMap<String, String>();
					while (lr.next()) {
						rsMap.put("StatID", (String) lr.get(0));
						rsMap.put("KPI", String.valueOf(lr.get(1)));
					}
					if (ls.list().size() == 0) {
						long inB = System.currentTimeMillis();

						// key = KeyManager.getKeyByName("STAT_ManageUnit");
						key = ExpRunner
								.run("['idGen','STAT_ManageUnit']", null)
								.toString();
						;
						debug = "插入数据 StatID:" + key + "manageUnit:"
								+ manageUnit + "year:" + date[0] + "season:"
								+ season + "month:" + date[1] + "KPICode:"
								+ r.get("KPICode") + "KPI:" + kpi;
						hqls = new StringBuilder(
								"insert into STAT_ManageUnit(StatID,manageUnit,year,season,month,KPICode,KPI,statDate,RunDate,doctor) values(:StatID,:manageUnit,:year, :season, :month,:KPICode,:KPI,:statDate,:RunDate,:doctor) ");
						Query query = ss.createSQLQuery(hqls.toString());
						query.setParameter("StatID", key);
						query.setParameter("manageUnit", manageUnit);
						query.setParameter("year", date[0]);
						query.setParameter("season", season);
						query.setParameter("month", date[1]);
						query.setParameter("KPICode", r.get("KPICode"));
						query.setParameter("KPI", kpi);
						query.setParameter(
								"statDate",
								BSCHISUtil.toDate(date[0] + "-" + date[1] + "-"
										+ date[2]));
						query.setParameter("RunDate", systemdate);
						query.setParameter("doctor", doctor);
						query.executeUpdate();
						cr++;
						long inE = System.currentTimeMillis();
						inTime += inE - inB;
					} else {
						long upB = System.currentTimeMillis();
						if (rsMap.containsValue(kpi)
								|| rsMap.get("StatID") == null) {
							x++;
							long dNull = System.currentTimeMillis();
							rTime += dNull - bb;
							continue;
						}
						debug = "更新数据 StatID:" + rsMap.get("StatID")
								+ "manageUnit:" + manageUnit + "year:"
								+ date[0] + "season:" + season + "month:"
								+ date[1] + "KPICode:" + r.get("KPICode")
								+ "KPI:" + kpi;
						hqls = new StringBuilder(
								"update STAT_ManageUnit set manageUnit=:manageUnit,year=:year,season=:season,month=:month,KPICode=:KPICode,KPI=:KPI,statDate=:statDate,RunDate=:RunDate,doctor=:doctor where StatID=:StatID");
						Query query = ss.createSQLQuery(hqls.toString());
						query.setParameter("StatID", rsMap.get("StatID"));
						query.setParameter("manageUnit", manageUnit);
						query.setParameter("year", date[0]);
						query.setParameter("season", season);
						query.setParameter("month", date[1]);
						query.setParameter("KPICode", r.get("KPICode"));
						query.setParameter("KPI", kpi);
						query.setDate(
								"statDate",
								BSCHISUtil.toDate(date[0] + "-" + date[1] + "-"
										+ date[2]));
						query.setDate("RunDate", systemdate);
						query.setParameter("doctor", doctor);
						query.executeUpdate();
						gx++;
						long upE = System.currentTimeMillis();
						upTime += upE - upB;
					}
				} catch (Exception e) {
					logger.info("Exception", e);
					logger.info("更新插入数据:" + KPICode + ":" + debug);
				}
			}
			long end = System.currentTimeMillis();
			logger.info(KPICode + " 跳出次数:" + x + " 插入:" + cr + " 更新:" + gx);
			logger.info("更新时间:" + upTime + "毫秒");
			logger.info("插入:" + inTime + "毫秒");
			logger.info("跳出:" + rTime + "毫秒");
			logger.info("总时间:" + (end - now) / 1000 + "秒");
		}
	}

	public String getSeason(Integer m) {
		String s = "";
		switch (m) {
		case 1:
		case 2:
		case 3:
			s = "1";
			break;
		case 4:
		case 5:
		case 6:
			s = "2";
			break;
		case 7:
		case 8:
		case 9:
			s = "3";
			break;
		case 10:
		case 11:
		case 12:
			s = "4";
		}
		return s;
	}

	/**
	 * 获取DAO
	 * @param session
	 * @return
	 */
	public static BaseDAO getDAO(Session session) {
		Context ctx = new Context();
		ctx.put(Context.WEB_SESSION, session);
		BaseDAO dao = new BaseDAO();
		return dao;
	}

}
