package chis.source.schedule.instance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import chis.source.schedule.StatScheduleUtil;

public class GzlSchedule {
	private static Log logger = LogFactory.getLog(GzlSchedule.class);

	private static StatScheduleUtil util = new StatScheduleUtil();

	public Session ss = null;

	public static void executeGzl() {
		MhcSchedule mhc = new MhcSchedule();
		mhc.ss = util.getSession();
		logger.info("GzlSchedule Begin");
		Long b=System.currentTimeMillis();
		try {
			util.executeXmlKpi("gzl.xml");
			Long e=System.currentTimeMillis();
			logger.info("GzlSchedule End:" + (e-b)/1000+"秒");
		} catch (Exception e) {
			logger.info("GzlSchedule:" + e);
		} finally {
			if (mhc.ss != null && mhc.ss.isOpen())
				mhc.ss.close();
		}
	}
}
