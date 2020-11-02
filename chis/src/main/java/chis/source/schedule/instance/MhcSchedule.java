package chis.source.schedule.instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class MhcSchedule {

	private static Log logger = LogFactory.getLog(MhcSchedule.class);

	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;

	public static void executeMHC() {
		MhcSchedule mhc = new MhcSchedule();
		mhc.ss = util.getSession();
		logger.info("MHC Begin");
		Long b=System.currentTimeMillis();
		try {
			util.executeXmlKpi("mhc.xml");
			Long e=System.currentTimeMillis();
			logger.info("MHC End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("mhc" + e);
		} finally {
			if (mhc.ss != null && mhc.ss.isOpen())
				mhc.ss.close();
		}
	}
}
