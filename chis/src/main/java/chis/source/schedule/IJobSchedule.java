package chis.source.schedule;

import org.quartz.JobExecutionException;


public interface IJobSchedule {
	/**
	 * 定时任务高度入口
	 * @throws JobExecutionException
	 */
	public void execute() throws JobExecutionException;
}
