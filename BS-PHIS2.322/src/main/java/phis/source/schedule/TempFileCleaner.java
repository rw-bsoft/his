/**
 * @(#)TempFileCleaner.java Created on 2010-8-12 下午04:42:34
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.source.schedule;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import phis.source.utils.EHRUtil;

/**
 * @description 清空临时目录。
 * 
 * @author <a href="mailto:zhengs@bsoft.com.cn">zhengshi</a>
 */
public class TempFileCleaner implements Job {

	private static final Log logger = LogFactory.getLog(TempFileCleaner.class);

	/**
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	public void execute(JobExecutionContext jxCtx) {
		logger.info("Work started...");
		String path = this.getClass().getClassLoader().getResource("")
				.getPath();
		File tmpDir = new File(path + "../../tmp/");
		File cvdDir = new File(path + "../../cvd/");
		if (tmpDir.exists()) {
			EHRUtil.removeDirectory(tmpDir);
		}
		if (cvdDir.exists()) {
			EHRUtil.removeDirectory(cvdDir);
		}
		logger.info("Mission completed...");
	}
}
