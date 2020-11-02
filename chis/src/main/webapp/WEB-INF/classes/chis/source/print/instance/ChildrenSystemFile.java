/**
 * @(#)ChildrenRecordReportFile.java Created on 10:10:01 AM
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package chis.source.print.instance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import chis.source.print.base.BSCHISPrint;
import ctd.account.UserRoleToken;
import ctd.print.IHandler;
import ctd.print.PrintException;
import ctd.util.AppContextHolder;
import ctd.util.context.Context;

/**
 * @description
 * 
 * @author <a href="mailto:yaozh@bsoft.com.cn">yao zhenhua</a>
 */
public class ChildrenSystemFile extends BSCHISPrint implements IHandler {
	private static Log logger = LogFactory.getLog(ChildrenSystemFile.class);
	
	@SuppressWarnings("unused")
	private String empiId;

	@SuppressWarnings("unused")
	private HibernateTemplate ht;

	//private String phrId;
	
	public void getParameters (Map<String, Object> map,
			Map<String, Object> parameters,Context ctx){
		Session ss = null;
		UserRoleToken ur = UserRoleToken.getCurrent();
		parameters.put("user", ur.getUserName());
		parameters.put("date", new Date());
		int year = Integer.valueOf((String)map.get("year"));
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, Calendar.SEPTEMBER);
		c.set(Calendar.DAY_OF_MONTH, 30);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
		String manageUnit = (String) map.get("manageUnit");
		try {
			ss = AppContextHolder.getBean(AppContextHolder.DEFAULT_SESSION_FACTORY,SessionFactory.class)
					.openSession();

			SQLQuery q = null;
			String sql = "select count(*) from (select distinct phrId from";
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a,Cdh_Checkuplab b where b.haemoglobin <60 and a.checkupid = b.checkupid and a.manaUnitId like '"+manageUnit+"%')"+")");
			int haemoglobin1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("haemoglobin1", haemoglobin1);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a,Cdh_Checkuplab b where b.haemoglobin >=60 and b.haemoglobin < 90  and a.checkupid = b.checkupid and a.manaUnitId like '"+manageUnit+"%'))");
			int haemoglobin2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("haemoglobin2", haemoglobin2);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a,Cdh_Checkuplab b where b.haemoglobin >=90 and b.haemoglobin < 110 and a.checkupid = b.checkupid and a.manaUnitId like '"+manageUnit+"%'))");
			int haemoglobin3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("haemoglobin3", haemoglobin3);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a,Cdh_Checkuplab b where b.haemoglobin >=110 and a.checkupid = b.checkupid and a.manaUnitId like '"+manageUnit+"%'))");
			int haemoglobin4 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("haemoglobin4", haemoglobin4);
			
			parameters.put("haemoglobinCount", haemoglobin1+haemoglobin2+haemoglobin3+haemoglobin4);
			
			q = ss.createSQLQuery("select count(*) from Cdh_Checkup a where a.electronicDiagnostic like '%,4,%' or a.electronicDiagnostic like '4,%' or a.electronicDiagnostic like '%,4' or a.electronicDiagnostic = '4' and a.manaUnitId like '"+manageUnit+"%'");
			parameters.put("electronicDiagnostic4", Integer.valueOf(q.list().get(0).toString()));
			
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '6' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus6 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus6", nutritionStatus6);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '7' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus7 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus7", nutritionStatus7);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '8' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus8 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus8", nutritionStatus8);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '9' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus9 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus9", nutritionStatus9);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '2' and a.manaUnitId like '"+manageUnit+"%'))");
			parameters.put("nutritionStatus2", Integer.valueOf(q.list()
					.get(0).toString()));
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '3' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus3", nutritionStatus3);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '4' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus4 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus4", nutritionStatus4);
			q = ss.createSQLQuery(sql+" (select a.phrId from Cdh_Checkup a where a.nutritionStatus = '5' and a.manaUnitId like '"+manageUnit+"%'))");
			int nutritionStatus5 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("nutritionStatus5", nutritionStatus5);
			parameters.put("nutritionStatus6789", nutritionStatus6 + nutritionStatus7 + nutritionStatus8 + nutritionStatus9);
			parameters.put("nutritionStatus345", nutritionStatus3 + nutritionStatus4 + nutritionStatus5);
			

			q = ss.createSQLQuery(sql+" (select a.phrId from cdh_inquire a where a.illnessType like '%3%' and a.manaUnitId like '"+manageUnit+"%'))");
			parameters.put("illnessType3", Integer.valueOf(q.list()
					.get(0).toString()));
			q = ss.createSQLQuery(sql+" (select a.phrId from cdh_inquire a where a.illnessType like '%4%' and a.manaUnitId like '"+manageUnit+"%'))");
			parameters.put("illnessType4", Integer.valueOf(q.list()
					.get(0).toString()));
			
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo c ,ehr_healthrecord a where c.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-12) and c.birthday <= to_date('"+date+"','yyyy-MM-dd') and a.empiid = c.empiid and a.manaUnitId like '"+manageUnit+"%'");
			int countY0 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countY0", countY0);
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo c ,ehr_healthrecord a where c.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-24) and c.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-12) and a.empiid = c.empiid and a.manaUnitId like '"+manageUnit+"%' ");
			int countY1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countY1", countY1);
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo c ,ehr_healthrecord a where c.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-36) and c.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-24) and a.empiid = c.empiid and a.manaUnitId like '"+manageUnit+"%' ");
			int countY2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countY2", countY2);
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo c ,ehr_healthrecord a where c.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-48) and c.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-36) and a.empiid = c.empiid and a.manaUnitId like '"+manageUnit+"%' ");
			int countY3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countY3", countY3);
			
			
			q = ss.createSQLQuery("select count(*) "+
								  "from (select distinct a.phrId "+
								          "from cdh_checkup a ,(select phrId "+
								  "from ehr_healthrecord b, "+
								       "(select empiId "+
								          "from mpi_demographicinfo c "+
								         "where c.birthday > add_months(to_date('"+date+"', 'yyyy-MM-dd'), -12) "+
								           "and c.birthday <= to_date('"+date+"', 'yyyy-MM-dd')) d "+
								 "where b.empiId = d.empiId ) e "+
								 "where a.phrid = e.phrId  "+
								 "and a.manaunitid like '"+manageUnit+"%' "+
								 ")");
			int countS0 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countS0", countS0);
			q = ss.createSQLQuery("select count(*) "+
								  "from (select distinct a.phrId "+
								         "from cdh_checkup a, "+
								                "(select phrId "+
								                "from ehr_healthrecord b, "+
								                        "(select empiId "+
								                        "from mpi_demographicinfo c "+
								                        "where 	c.birthday > add_months(to_date('"+date+"', 'yyyy-MM-dd'),-24) "+
								                        "and 	c.birthday <= add_months(to_date('"+date+"', 'yyyy-MM-dd'),-12) "+
								                        ") d "+
								                "where b.empiId = d.empiId "+
								                ") e "+
								         "where a.phrid = e.phrId "+
								         "and a.manaunitid like '"+manageUnit+"%')"); 
			int countS1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countS1", countS1);
			q = ss.createSQLQuery("select count(*) "+
					   		 	  "from (select distinct a.phrId "+
					   		 	  		"from cdh_checkup a, "+
						   		 	  		"(select phrId "+
						                  	"from ehr_healthrecord b, "+
						                        	"(select empiId "+
						                        	"from mpi_demographicinfo c "+
						                        	"where c.birthday > add_months(to_date('"+date+"', 'yyyy-MM-dd'),-36) "+
						                            "and c.birthday <= add_months(to_date('"+date+"', 'yyyy-MM-dd'),-24) "+
						                            ") d "+
				                            "where b.empiId = d.empiId "+
				                            ") e "+
		                            	"where a.phrid = e.phrId "+
		                            	"and a.manaunitid like '"+manageUnit+"%')"); 
			int countS2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countS2", countS2);
			q = ss.createSQLQuery("select count(*) "+
				   				  "from (select distinct a.phrId "+
						   				  "from cdh_checkup a, "+
						   				  		"(select phrId "+
						   				  		"from ehr_healthrecord b, "+
							                        "(select empiId "+
							                           "from mpi_demographicinfo c "+
							                          "where c.birthday > add_months(to_date('"+date+"', 'yyyy-MM-dd'),-48) "+
							                            "and c.birthday <= add_months(to_date('"+date+"', 'yyyy-MM-dd'),-36) "+
							                         ") d "+
							                	"where b.empiId = d.empiId "+
							                	") e "+
								          "where a.phrid = e.phrId "+
								          "and a.manaunitid like '"+manageUnit+"%')"); 
			int countS3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countS3", countS3);
			
			parameters.put("p0", countY0 == 0?0: (double)countS0/countY0);
			parameters.put("p1", countY1 == 0?0: (double)countS1/countY1);
			parameters.put("p2", countY2 == 0?0: (double)countS2/countY2);
			parameters.put("p3", countY3 == 0?0: (double)countS3/countY3);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
									"(select empiId from " +
										"(select empiId, count(*) as cnt " +
										"from pub_visitplan b " +
										"where b.extend1 in (1, 3, 6, 9) " +
										"and b.visitid is not null " +
										"and b.plantype = '6' group by empiId) " +
									"where cnt = 4) c," +
									"ehr_healthrecord d " +
								"where a.empiid = d.empiid " +
								"and a.empiId = c.empiid " +
								"and a.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-12) " +
								"and a.birthday <= to_date('"+date+"','yyyy-MM-dd') "+
								"and d.manaunitid like '"+manageUnit+"%'");
			int countC0 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countC0", countC0);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
					"(select empiId from " +
						"(select empiId, count(*) as cnt " +
						"from pub_visitplan b " +
						"where b.extend1 in (12, 18) " +
						"and b.visitid is not null " +
						"and b.plantype = '6' group by empiId) " +
					"where cnt = 4) c," +
					"ehr_healthrecord d " +
				"where a.empiid = d.empiid " +
				"and a.empiId = c.empiid " +
				"and a.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-24) " +
				"and a.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-12) "+
				"and d.manaunitid like '"+manageUnit+"%'");
			int countC1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countC1", countC1);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
					"(select empiId from " +
						"(select empiId, count(*) as cnt " +
						"from pub_visitplan b " +
						"where b.extend1 in (24, 30) " +
						"and b.visitid is not null " +
						"and b.plantype = '6' group by empiId) " +
					"where cnt = 4) c," +
					"ehr_healthrecord d " +
				"where a.empiid = d.empiid " +
				"and a.empiId = c.empiid " +
				"and a.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-36) " +
				"and a.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-24) "+
				"and d.manaunitid like '"+manageUnit+"%'");
			int countC2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countC2", countC2);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
					"(select empiId from " +
						"(select empiId, count(*) as cnt " +
						"from pub_visitplan b " +
						"where b.extend1 in (36) " +
						"and b.visitid is not null " +
						"and b.plantype = '6' group by empiId) " +
					"where cnt = 4) c," +
					"ehr_healthrecord d " +
				"where a.empiid = d.empiid " +
				"and a.empiId = c.empiid " +
				"and a.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-48) " +
				"and a.birthday <= add_months(to_date('"+date+"','yyyy-MM-dd'),-36) "+
				"and d.manaunitid like '"+manageUnit+"%'");
			int countC3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countC3", countC3);
			
			parameters.put("cp0", countY0 == 0?0: (double)countC0/countY0);
			parameters.put("cp1", countY1 == 0?0: (double)countC1/countY1);
			parameters.put("cp2", countY2 == 0?0: (double)countC2/countY2);
			parameters.put("cp3", countY3 == 0?0: (double)countC3/countY3);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
					"(select empiId from " +
						"(select empiId, count(*) as cnt " +
						"from pub_visitplan b " +
						"where b.extend1 in (1, 3, 6, 9) " +
						"and b.visitid is not null " +
						"and b.plantype = '6' group by empiId) " +
					"where cnt = 4) c," +
					"ehr_healthrecord d " +
				"where a.empiid = d.empiid " +
				"and a.empiId = c.empiid " +
				"and a.birthday > add_months(current_date,-12) " +
				"and a.birthday <= current_date "+
				"and d.manaunitid like '"+manageUnit+"%'");
			int countN0 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countN0", countN0);

			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
				"(select empiId from " +
					"(select empiId, count(*) as cnt " +
					"from pub_visitplan b " +
					"where b.extend1 in (1, 3, 6, 9,12,18) " +
					"and b.visitid is not null " +
					"and b.plantype = '6' group by empiId) " +
				"where cnt = 6) c," +
				"ehr_healthrecord d " +
			"where a.empiid = d.empiid " +
			"and a.empiId = c.empiid " +
			"and a.birthday > add_months(current_date,-24) " +
			"and a.birthday <= add_months(current_date,-12) "+
			"and d.manaunitid like '"+manageUnit+"%'");
			int countN1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countN1", countN1);

			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
				"(select empiId from " +
					"(select empiId, count(*) as cnt " +
					"from pub_visitplan b " +
					"where b.extend1 in (1, 3, 6, 9,12,18,24, 30) " +
					"and b.visitid is not null " +
					"and b.plantype = '6' group by empiId) " +
				"where cnt = 8) c," +
				"ehr_healthrecord d " +
			"where a.empiid = d.empiid " +
			"and a.empiId = c.empiid " +
			"and a.birthday > add_months(current_date,-36) " +
			"and a.birthday <= add_months(current_date,-24) "+
			"and d.manaunitid like '"+manageUnit+"%'");
			int countN2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countN2", countN2);

			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a, " +
				"(select empiId from " +
					"(select empiId, count(*) as cnt " +
					"from pub_visitplan b " +
					"where b.extend1 in (1, 3, 6, 9,12,18,24, 30,36) " +
					"and b.visitid is not null " +
					"and b.plantype = '6' group by empiId) " +
				"where cnt = 9) c," +
				"ehr_healthrecord d " +
			"where a.empiid = d.empiid " +
			"and a.empiId = c.empiid " +
			"and a.birthday > add_months(current_date,-48) " +
			"and a.birthday <= add_months(current_date,-36) "+
			"and d.manaunitid like '"+manageUnit+"%'");
			int countN3 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("countN3", countN3);
			
			parameters.put("cn0", countY0 == 0?0: (double)countN0/countY0);
			parameters.put("cn1", countY1 == 0?0: (double)countN1/countY1);
			parameters.put("cn2", countY2 == 0?0: (double)countN2/countY2);
			parameters.put("cn3", countY3 == 0?0: (double)countN3/countY3);
			
			q = ss.createSQLQuery("select count(*) from " +
					"(select distinct phrId from cdh_inquire a where a.phrId in " +
						"(select phrId from ehr_healthrecord b where b.empiid in " +
							"(select empiId from mpi_demographicinfo c where c.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'),-6) and c.birthday <= to_date('"+date+"','yyyy-MM-dd'))" +
						")" +
					")");
			int feedWayCount = Integer.valueOf(q.list()
					.get(0).toString());
			parameters.put("feedWayCount", feedWayCount);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo p where empiId in " +
									"(select empiId from ehr_healthrecord where phrId in " +
										"(select distinct o.phrId from cdh_inquire o, pub_visitplan q where q.plantype = '5' and extend1 <= 6 and o.inquireid = q.visitid and o.feedway in (1,2,3)  and o.manaUnitId like '"+manageUnit+"%')) " +
									" and p.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'), -6) and p.birthday <= to_date('"+date+"','yyyy-MM-dd')");
			int feedWay1 = Integer.valueOf(q.list()
					.get(0).toString());
			parameters.put("feedWay1", feedWay1);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo p where empiId in " +
					"(select empiId from ehr_healthrecord where phrId in " +
						"(select distinct o.phrId from cdh_inquire o, pub_visitplan q where q.plantype = '5' and extend1 <= 6 and o.inquireid = q.visitid and o.feedway in (1,3) and o.manaUnitId like '"+manageUnit+"%')) " +
					"and p.birthday > add_months(to_date('"+date+"','yyyy-MM-dd'), -6) and p.birthday <= to_date('"+date+"','yyyy-MM-dd') ");
			int feedWay2 = Integer.valueOf(q.list()
					.get(0).toString());
			parameters.put("feedWay2", feedWay2);
			parameters.put("feedWayP1", feedWayCount == 0?0: (double)feedWay1/feedWayCount);
			parameters.put("feedWayP2", feedWayCount == 0?0: (double)feedWay2/feedWayCount);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a , cdh_deadregister b where a.empiid = b.empiid and a.birthday > add_months(b.deathdate,-12) and a.birthday <= b.deathdate and b.manaUnitId like '"+manageUnit+"%'");
			int deadCount1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("deadCount1", deadCount1);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo a , cdh_deadregister b where a.empiid = b.empiid and a.birthday > b.deathdate-28 and a.birthday <= b.deathdate and b.manaUnitId like '"+manageUnit+"%'");
			int deadCount2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("deadCount2", deadCount2);
			
			parameters.put("deadCount", deadCount1+deadCount2);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								         "where a.appraisehy in ('hSD0', 'hSD0-hSD1', 'hSD1', 'hSD1-hSD2', "+
								                "'hSD2', 'hSD2-hSD3', 'hSD3', '>hSD3')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int heightM1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("heightM1", heightM1);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								         "where a.appraisehy in ('hSD0-hSD1neg', 'hSD1neg', 'hSD1neg-hSD2neg', 'hSD2neg', "+
								                "'hSD2neg-hSD3neg', 'hSD3neg', '<hSD3neg')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int heightM2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("heightM2", heightM2);
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								          "where a.appraisehy in ('hSD2neg-hSD3neg', 'hSD3neg', '<hSD3neg')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int heightX2sd = Integer.valueOf(q.list().get(0).toString());
			parameters.put("heightX2sd", heightX2sd);
			
			
			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								         "where a.appraisehy in ('wSD0', 'wSD0-wSD1', 'wSD1', 'wSD1-wSD2', "+
								                "'wSD2', 'wSD2-wSD3', 'wSD3', '>wSD3')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int weightM1 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("weightM1", weightM1);

			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								         "where a.appraisehy in ('wSD0-wSD1neg', 'wSD1neg', 'wSD1neg-wSD2neg', 'wSD2neg', "+
								                "'wSD2neg-wSD3neg', 'wSD3neg', '<wSD3neg')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int weightM2 = Integer.valueOf(q.list().get(0).toString());
			parameters.put("weightM2", weightM2);

			q = ss.createSQLQuery("select count(*) from mpi_demographicinfo m, (select empiId,t.manaunitid " + 
								  "from ehr_healthrecord t, "+
								     "(select distinct phrId "+
								          "from cdh_checkup a "+
								          "where a.appraisehy in ('wSD2neg-wSD3neg', 'wSD3neg', '<wSD3neg')) p "+
								 "where t.phrid = p.phrId) n "+
								 "where m.empiid = n.empiId  "+
								 "and m.birthday > add_months(to_date('"+date+"','yyyy_mm-dd'),-36) "+
								 "and m.birthday <= to_date('"+date+"','yyyy_mm-dd') "+
								 "and n.manaUnitId like '"+manageUnit+"%'");
			int weightX2sd = Integer.valueOf(q.list().get(0).toString());
			parameters.put("weightX2sd", weightX2sd);
			sqlDate2String(parameters);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("ChildrenSystemFile has error"+e.getMessage());
		} finally {
			ss.close();
		}

	}

	public void getFields(Map<String, Object> request,
			List<Map<String, Object>> records, Context ctx)
			throws PrintException {
		
	}


}
