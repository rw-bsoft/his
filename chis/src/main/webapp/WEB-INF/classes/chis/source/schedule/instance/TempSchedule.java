package chis.source.schedule.instance;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class TempSchedule {
	private static Log logger = LogFactory.getLog(CdhSchedule.class);

	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;

	public static void executeTemp() {
		TempSchedule temp = new TempSchedule();
		temp.ss = util.getSession();
		logger.info("temp Begin");
		Long b=System.currentTimeMillis();
		try {
			util.executeXmlKpi("temp.xml");
			//temp.executeICD();
			Long e=System.currentTimeMillis();
			logger.info("temp End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("temp" + e);
		} finally {
			if (temp.ss != null && temp.ss.isOpen())
				temp.ss.close();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void executeICD(){
		try {
			HashMap manageUnitMap = new HashMap();
			ArrayList rsArry = new ArrayList();
			HashMap map = new HashMap();
			String hql = "select substring(b.checkupOrganization, 1, 9), str(b.totalCheckupDate, 'yyyy-q-mm-dd') , icdCode, count(icdCode) from PER_ICD a, PER_CheckupRegister b where a.checkupNo = b.checkupNo and b.totalCheckupDate is not null and checkupOrganization is not null group by substring(b.checkupOrganization, 1, 9), str(b.totalCheckupDate, 'yyyy-q-mm-dd'), icdCode";
			ScrollableResults q = ss.createSQLQuery(hql).scroll();
			while (q.next()) {
				manageUnitMap.put("manageUnit", q.get(0));
				manageUnitMap.put("date", q.get(1));
				manageUnitMap.put("kpi", q.get(3));
				manageUnitMap.put("KPICode", "ICD_"+q.get(2));
				rsArry.add(manageUnitMap);
			}
			util.executeSQL(rsArry);
		} catch (Exception e) {
			logger.info("ICD" , e);
		}
		
	}
}
