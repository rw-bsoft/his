/**
 * @(#)HealthCheckSchedule.java Created on 2012-5-28 上午10:31:02
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.healthCheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

/**
 * @description
 * 
 * @author <a href="mailto:yub@bsoft.com.cn">俞波</a>
 */
public class HealthCheckScheduleModule implements BSCHISEntryNames {
	BaseDAO dao;

	public HealthCheckScheduleModule(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	public List<Map<String, Object>> getHealthCheckRecord()
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.phrId as recordId,a.empiId as empiId ,a.manaDoctorId as doctorId ,a.manaUnitId as manaUnitId,(select max(b.createDate) from ")
				.append(HC_HealthCheck)
				.append(" b where a.phrId=b.phrId) as maxDate from ")
				.append(EHR_HealthRecord)
				.append(" a where a.status=:status").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("获取创建年度健康检查的数据失败！", e);
		}
		return rsList;
	}

}
