/**
 * @(#)HyperYearFixGroupScheduleModel.java Created on 2012-5-25 下午6:15:50
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.hypertension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.HyperRecordStatus;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HyperYearFixGroupScheduleModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public HyperYearFixGroupScheduleModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 将最后一条随访已访或者最后一条随访已超期的高血压档案
	public List<Map<String, Object>> getHypertensionLastVisitRecord()
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.phrId as recordId,a.empiId as empiId,")
				.append("a.manaUnitId as manaUnitId,a.manaDoctorId as doctorId,")
				.append("b.planDate as maxPlanDate, b.planStatus as maxPlanStatus from ")
				.append(MDC_HypertensionRecord)
				.append(" a, ")
				.append(PUB_VisitPlan)
				.append(" b where a.empiId=b.empiId and a.status<>:status and ")
				.append("b.businessType=:businessType and b.planDate >= all(select planDate from ")
				.append(PUB_VisitPlan)
				.append(" c where a.empiId=c.empiId and c.businessType=:businessType)")
				.toString();

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", HyperRecordStatus.WRITE_OFF);
		parameters.put("businessType", BusinessType.GXY);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取生成评估任务列表的数据失败！", e);
		}
		return rsList;
	}
}
