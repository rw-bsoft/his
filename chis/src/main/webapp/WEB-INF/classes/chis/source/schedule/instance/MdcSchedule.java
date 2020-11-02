package chis.source.schedule.instance;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class MdcSchedule {

	private static Log logger = LogFactory.getLog(MdcSchedule.class);

	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;

	public static void executeMDC() {
		MdcSchedule mdc = new MdcSchedule();
		mdc.ss = util.getSession();
		logger.info("MDC Begin");
		Long b=System.currentTimeMillis();
		try{
			util.executeXmlKpi("mdc.xml");
//			util.executeXmlKpi("his.xml");
//			mdc.executeICD();
			Long e=System.currentTimeMillis();
			logger.info("MDC End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("mdc" , e);
		} finally {
			if (mdc.ss != null && mdc.ss.isOpen())
				mdc.ss.close();
		}

	}
	
	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
	public void executeICD(){
		try {
			HashMap manageUnitMap = new HashMap();
			ArrayList rsArry = new ArrayList();
			HashMap map = new HashMap();
			String hql = "select substr(b.checkupOrganization, 1, 9), char(b.totalCheckupDate,iso) , icdCode, count(icdCode) from PER_ICD a, PER_CheckupRegister b where a.checkupNo = b.checkupNo and b.totalCheckupDate is not null and checkupOrganization is not null group by substr(b.checkupOrganization, 1, 9),char(b.totalCheckupDate, iso), icdCode";
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
