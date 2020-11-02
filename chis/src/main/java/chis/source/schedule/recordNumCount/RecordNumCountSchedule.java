/**
 * @(#)recordNumCountSchedule.java Created on 2012-8-2 上午11:32:56
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.recordNumCount;

import java.util.Date;

import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.pub.StatModel;
import chis.source.schedule.AbstractJobSchedule;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class RecordNumCountSchedule extends AbstractJobSchedule {

	public void doJob(BaseDAO dao) throws ModelDataOperationException {
		Date d = new Date();
		StatModel statModel = new StatModel(dao, d);
		statModel.caculate();
	}

}
