/**
 * @(#)PsyAnnualAssessmentModel.java Created on 2012-5-25 下午7:41:20
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.schedule.psychosis;

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
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class PsyAnnualAssessmentModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public PsyAnnualAssessmentModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 获取创建年度评估的数据失败！
	 * 
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> getPsyAnnualAssessmentRecord()
			throws ModelDataOperationException {
		String hql = new StringBuffer(
				"select a.phrId as recordId,a.empiId as empiId ,a.manaDoctorId as doctorId ,a.manaUnitId as manaUnitId,(select max(b.createDate) from ")
				.append(PSY_AnnualAssessment)
				.append(" b where a.phrId=b.phrId) as maxDate from ")
				.append(PSY_PsychosisRecord)
				.append(" a where a.status=:status").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status", Constants.CODE_STATUS_NORMAL);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取创建年度评估的数据失败！", e);
		}
		return rsList;
	}
}
