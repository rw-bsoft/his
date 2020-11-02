/**
 * @(#)DiabetesModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.DiabetesRiskStatus;
import chis.source.dic.PlanStatus;
import chis.source.dic.WorkType;
import chis.source.service.ServiceCode;
import chis.source.util.BSCHISUtil;
import chis.source.util.UserUtil;
import ctd.service.core.ServiceException;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesRiskModel extends DiabetesModel {

	public DiabetesRiskModel(BaseDAO dao) {
		super(dao);
	}

	public void createDiabetesRiskVisitPlan(Map<String,Object> body) throws ValidateException, PersistentDataOperationException{
		Map<String,Object> plan = new HashMap<String,Object>();
		plan.put("recordId", body.get("phrId"));
		plan.put("empiId", body.get("empiId"));
		plan.put("beginDate", body.get("beginDate"));
		plan.put("endDate", body.get("endDate"));
		plan.put("planStatus", PlanStatus.NEED_VISIT);
		plan.put("planDate", body.get("planDate"));
		plan.put("sn", body.get("sn"));
		plan.put("businessType", BusinessType.DiabetesRisk);
		dao.doInsert(PUB_VisitPlan, plan, false);
	}
	
	public void writeOffDiabetesRiskVisitPlan(Map<String,Object> body ) throws ServiceException{
		try {
			String[] businessTypes = { BusinessType.DiabetesRisk };
			this.logOutVisitPlan("empiId", (String) body.get("empiId"), businessTypes);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	public void logOutVisitPlan(String whereField, String whereValue,
			String... businessType) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer(" update ").append("PUB_VisitPlan")
				.append(" set planStatus = :planStatus,")
				.append(" lastModifyUser=:lastModifyUser,")
				.append(" lastModifyDate=:lastModifyDate")
				.append(" where 1=1 ").append(" and ")
				.append(whereField).append(" = :whereValue");
		Map<String, Object> map = new HashMap<String, Object>();
		if (businessType != null && businessType.length > 0) {
			if (businessType.length == 1) {
				hql.append("  and  businessType=(");
			} else {
				hql.append(" and businessType in (");
			}
			StringBuffer args = new StringBuffer();
			for (int i = 0; i < businessType.length; i++) {
				args.append(", :businessType").append(i);
				map.put("businessType" + i, businessType[i]);
			}
			args.append(" )");
			hql.append(args.substring(1));
		}
		map.put("lastModifyUser", UserUtil.get(UserUtil.USER_ID));
		map.put("lastModifyDate", new Date());
		map.put("planStatus", PlanStatus.WRITEOFF);
		map.put("whereValue", whereValue);
		try {
			dao.doUpdate(hql.toString(), map);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销计划信息失败!", e);
		}
	}
	
	public void removeDiabetesRecordWorkList(String empiId) throws PersistentDataOperationException{
		StringBuffer hql = new StringBuffer(" delete from ").append("PUB_WorkList")
				.append(" where  empiId=:empiId and workType=:workType");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("workType", WorkType.MDC_DIABETESRECORD);
		dao.doUpdate(hql.toString(), parameters);
	}
	
	public List<Map<String,Object>> getDiabetesRiskByEmpiId(String empiId) throws ModelDataOperationException {
		String hql = new StringBuffer(" from ").append("MDC_DiabetesRisk")
				.append(" where empiId =:empiId and (status =:status1 or status=:status2 )")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("status1", "0");
		parameters.put("status2", "1");
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病高危核实记录失败");
		}
		return rsList;
	}
	
	public void updateFirstAssessmentDate(Map<String,Object> m) throws  PersistentDataOperationException, ModelDataOperationException{
		String hql = new StringBuffer(" update ").append(MDC_DiabetesRisk)
				.append(" set firstAssessmentDate =:firstAssessmentDate where empiId =:empiId and status=:status")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", m.get("empiId"));
		parameters.put("firstAssessmentDate", BSCHISUtil.toDate((String) m.get("estimateDate")));
		parameters.put("status", DiabetesRiskStatus.CONFIRM);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病高危核实记录失败");
		}
	}
}
