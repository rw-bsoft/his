package chis.source.schedule.instance;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class CdhSchedule {
	private static Log logger = LogFactory.getLog(CdhSchedule.class);
	
	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;
	
	public static void executeCDH(){
		logger.info("CDH Begin");
		Long b=System.currentTimeMillis();
		CdhSchedule cdh=new CdhSchedule();
		cdh.ss=util.getSession();
		try{
		util.executeXmlKpi("cdh.xml");
//		cdh.getCDHDnxtgls();
//		cdh.getCDHBzxtgls();
		Long e=System.currentTimeMillis();
		logger.info("CDH End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("cdh" + e);
		} finally {
			if (cdh.ss != null && cdh.ss.isOpen())
				cdh.ss.close();
		}
	}
	
	// 当年系统管理数
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void getCDHDnxtgls() {
		try {
			HashMap manageUnitMap = new HashMap();
			HashMap manageUnitMap1 = new HashMap();
			HashMap manageUnitMap2 = new HashMap();
			ArrayList rsArry = new ArrayList();
			ArrayList rsArry1 = new ArrayList();
			ArrayList rsArry2 = new ArrayList();
			HashMap map = new HashMap();
			String hql1="select substr(manaUnitId,1,9),char(createDate,iso) ,count(empiId) from CDH_HealthCard where length(manaUnitId) >= 9 group by substr(manaUnitId,1,9),date(createDate)";
			String hql2 = "select substr(a.manaUnitId,1,9),char(a.createDate,iso),count(*) from CDH_HealthCard a,PUB_VisitPlan b where b.planType = '6' and a.empiId=b.empiId and b.planStatus != '1' and length(a.manaUnitId)=9 and  year(b.planDate)=year(current_date) and char(b.planDate)< char(current_date) group by substr(a.manaUnitId,1,9),char(a.createDate,iso)";
			ScrollableResults q1 = ss.createQuery(hql1).scroll();
			ScrollableResults q2 = ss.createQuery(hql2).scroll();
			while (q1.next()) {
				manageUnitMap1.put("manageUnit", q1.get(0));
				manageUnitMap1.put("date", q1.get(1));
				manageUnitMap1.put("kpi", q1.get(2));
				manageUnitMap.put("KPICode", "CDH_dnxtgls");
				rsArry1.add(manageUnitMap1);
			}
			while (q2.next()) {
				manageUnitMap2.put("manageUnit", q2.get(0));
				manageUnitMap2.put("date", q2.get(1));
				manageUnitMap2.put("kpi", q2.get(2));
				rsArry2.add(manageUnitMap2);
			}
			for(int i=0;i<rsArry1.size();i++){
				HashMap<String,Object> a1=(HashMap<String, Object>) rsArry1.get(i);
				String code=(String) a1.get("manageUnit");
				for(int j=0;j<rsArry2.size();j++)
				{
					HashMap<String,Object> a2=(HashMap<String, Object>) rsArry1.get(i);
					String code2=(String) a2.get("manageUnit");
					if(code==code2)
					{
						int kpi=((Integer)a1.get("kpi")).intValue()-((Integer)a2.get("kpi")).intValue();
						a1.put("kpi", kpi);
					}
				}
				a1.put("KPICode", "CDH_dnxtgls");
				rsArry.add(a1);
			}
			util.executeSQL(rsArry);
		} catch (Exception e) {
			logger.info("CDH_dnxtgls" + e);
		}
	}
	
	//标准系统管理数
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public void getCDHBzxtgls() {
		try {
			HashMap manageUnitMap = new HashMap();
			HashMap manageUnitMap1 = new HashMap();
			HashMap manageUnitMap2 = new HashMap();
			ArrayList rsArry = new ArrayList();
			ArrayList rsArry1 = new ArrayList();
			ArrayList rsArry2 = new ArrayList();
			HashMap map = new HashMap();
			String hql1="select substr(manaUnitId,1,9),char(createDate,iso) ,count(empiId) from CDH_HealthCard where length(manaUnitId) >= 9 group by substr(manaUnitId,1,9),to_char(createDate,'yyyy-q-mm')";
			String hql2 = "select substr(a.manaUnitId,1,9),char(a.createDate,iso),count(*) from CDH_HealthCard a,PUB_VisitPlan b where b.planType = '6' and a.empiId=b.empiId and b.planStatus != '1' and length(a.manaUnitId)=9  and b.planDate)< current_date group by substr(a.manaUnitId,1,9),char(a.createDate,iso)";
			ScrollableResults q1 = ss.createQuery(hql1).scroll();
			ScrollableResults q2 = ss.createQuery(hql2).scroll();
			while (q1.next()) {
				manageUnitMap1.put("manageUnit", q1.get(0));
				manageUnitMap1.put("date", q1.get(1));
				manageUnitMap1.put("kpi", q1.get(2));
				manageUnitMap.put("KPICode", "CDH_bzxtgls");
				rsArry1.add(manageUnitMap1);
			}
			while (q2.next()) {
				manageUnitMap2.put("manageUnit", q2.get(0));
				manageUnitMap2.put("date", q2.get(1));
				manageUnitMap2.put("kpi", q2.get(2));
				rsArry2.add(manageUnitMap2);
			}
			for(int i=0;i<rsArry1.size();i++){
				HashMap<String,Object> a1=(HashMap<String, Object>) rsArry1.get(i);
				String code=(String) a1.get("manageUnit");
				for(int j=0;j<rsArry2.size();j++)
				{
					HashMap<String,Object> a2=(HashMap<String, Object>) rsArry1.get(i);
					String code2=(String) a2.get("manageUnit");
					if(code==code2)
					{
						int kpi=((Integer)a1.get("kpi")).intValue()-((Integer)a2.get("kpi")).intValue();
						a1.put("kpi", kpi);
					}
					
				}
				a1.put("KPICode", "CDH_bzxtgls");
				rsArry.add(a1);
			}
			util.executeSQL(rsArry);
		} catch (Exception e) {
			logger.info("CDH_bzxtgls" + e);
			System.out.println("e" + e);
		}
	}
	
}