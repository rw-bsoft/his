package phis.source.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @CopyRight：版权所有bsoft,保留所有权力。
 * @Date:QuartzJob.java created on Apr 9, 2010 11:18:56 AM
 * @Author:<a href="mailto:yangwf@bsoft.com.cn">weifeng yang</a>
 * @Description:统计图 定时任务：将统计结果插入数据库
 * @Modify:
 */
public class StatSchedule {

	private static final Log logger = LogFactory.getLog(StatSchedule.class);

	public void execute() {
		logger.info("StatSchedule Begin");
		Long b = System.currentTimeMillis();
		// CdhSchedule.executeCDH();
		// MdcSchedule.executeMDC();
		// GzlSchedule.executeGzl();
		// EhrSchedule.executeEHR();
		// TempSchedule.executeTemp();
		Long e = System.currentTimeMillis();
		logger.info("StatSchedule End:" + (e - b) / 1000 + "秒");
	}
}
