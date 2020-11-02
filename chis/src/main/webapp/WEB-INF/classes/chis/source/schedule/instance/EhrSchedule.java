package chis.source.schedule.instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class EhrSchedule {

	private static Log logger = LogFactory.getLog(EhrSchedule.class);

	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;

	public static void executeEHR() {
		EhrSchedule mhc = new EhrSchedule();
		mhc.ss = util.getSession();
		logger.info("EHR Begin");
		Long b=System.currentTimeMillis();
		try {
			util.executeXmlKpi("ehr.xml");
			Long e=System.currentTimeMillis();
			logger.info("EHR End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("EHR" + e);
		} finally {
			if (mhc.ss != null && mhc.ss.isOpen())
				mhc.ss.close();
		}
	}
}
