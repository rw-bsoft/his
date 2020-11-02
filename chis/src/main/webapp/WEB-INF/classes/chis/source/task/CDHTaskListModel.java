/**
 * @(#)AbstractTaskListModel.java Created on 2012-3-19 上午11:23:05
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.task;
 
import java.util.Date;

import chis.source.BaseDAO;
import chis.source.util.BSCHISUtil;
import ctd.schema.Schema;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class CDHTaskListModel extends AbstractTaskListModel  {

	public CDHTaskListModel(BaseDAO dao) {
		super(dao);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param sc
	 * @return
	 */
	protected String buildQueryFields(Schema sc) {
		StringBuffer fields = new StringBuffer(super.buildQueryFields(sc));
		String currentDate = BSCHISUtil.toString(new Date(), null);
		fields.append(",(a.endDate-date('").append(currentDate)
				.append("')) as remainDays");
		fields.append(",a.endDate as lastDate");
		fields.append(",a.beginDate as earliestDate");
		return fields.toString();
	}

}
