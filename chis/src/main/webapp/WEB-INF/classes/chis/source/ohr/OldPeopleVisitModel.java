/**
 * @(#)OldPeopleVisitModel.java Created on 2012-3-22 上午11:22:00
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.ohr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.dic.BusinessType;
import chis.source.dic.VisitEffect;
import chis.source.util.CNDHelper;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:tianj@bsoft.com.cn">田军</a>
 */
public class OldPeopleVisitModel implements BSCHISEntryNames {

	BaseDAO dao = null;

	public OldPeopleVisitModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 保存老年人随访记录
	 * 
	 * @param op
	 * @param body
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	protected Map<String, Object> saveOldPeopleVisit(String op,
			Map<String, Object> body) throws ValidateException,
			ModelDataOperationException {
		try {
			return dao.doSave(op, BSCHISEntryNames.MDC_OldPeopleVisit, body,
					false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("老年人随访记录保存失败", e);
		}
	}

	/**
	 * 查询被终止管理的老年人随访数据
	 * 
	 * @param phrId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getEndedOldPeopleVisit(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> record = null;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "visitEffect", "s",
					VisitEffect.END);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "phrId", "s", phrId);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			List<Map<String, Object>> rs = dao.doQuery(cnd, "visitDate desc",
					MDC_OldPeopleVisit);
			if (rs != null && rs.size() > 0) {
				record = rs.get(0);
			}
			
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询被终止管理的老年人随访数据失败！", e);
		}
		return record;
	}

	/**
	 * 查询随访计划类型为老年人的最后一笔数据
	 * 
	 * @param phrId
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> getLastVisitPlan(String phrId)
			throws ModelDataOperationException {
		Map<String, Object> record = null;
		try {
			List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "recordId", "s",
					phrId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "businessType", "s",
					BusinessType.LNR);
			List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);

			List<Map<String, Object>> rs = dao.doQuery(cnd, "endDate desc",
					PUB_VisitPlan);
			if (rs != null && rs.size() > 0) {
				record = rs.get(0);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("查询类型为老年人随访计划数据失败！", e);
		}
		return record;
	}

	/**
	 * 删除被终止的老年人随访计划
	 * 
	 * @param phrId
	 * @param visitId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void deleteOverOldPeopleVisit(String phrId, String visitId)
			throws ModelDataOperationException {
		try {
			String hql = new StringBuffer("delete ").append(MDC_OldPeopleVisit)
					.append(" where visitId = :visitId")
					.append(" and phrId = :phrId ").toString();

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("visitId", visitId);
			parameters.put("phrId", phrId);

			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException("删除被终止管理的老年人随访数据失败！", e);
		}
	}
}
