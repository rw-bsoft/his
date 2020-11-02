package chis.source.dea;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.service.ServiceCode;
import chis.source.util.CNDHelper;

import ctd.validator.ValidateException;

public class DeathReportCardModel  implements BSCHISEntryNames {
	BaseDAO dao = null;

	public DeathReportCardModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 删除孕产妇死亡报告卡数据
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public void delDeathReport(String empiId) throws   ModelDataOperationException {
		try {
			 Map<String, Object> para = new HashMap<String, Object>();
			 para.put("empiId", empiId);
			 para.put("status", "1");
			 dao.doUpdate("update DEA_DeathReportCard set status= :status where empiId = :empiId", para);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除孕产妇死亡报告卡数据。", e);
		}
	}
	
	/**
	 * 删除孕产妇死亡报告卡
	 * 
	 * @param phrId
	 * @throws ModelDataOperationException
	 */
	public void deleteDeathReportCard(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuilder("delete from ").append(DEA_DeathReportCard)
				.append(" where empiId = :empiId ").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除孕产妇死亡报告卡", e);
		}
	}
	
	/**
	 * 保存孕产妇死亡报告卡数据
	 * 
	 * @param data
	 * @param op
	 * @param res
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> saveDeathReport(Map<String, Object> data,
			String op) throws ValidateException, ModelDataOperationException {
		try {
			return dao.doSave(op, DEA_DeathReportCard, data, true);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存孕产妇死亡报告卡数据。", e);
		}
	}
	
	/**
	 * 加载孕产妇死亡报告卡数据
	 * 
	 * @param empiId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadDeathReportCard(String empiId)
			throws ValidateException, ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
			List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "status", "s", "0");
		    List<?> cnd3= CNDHelper.createArrayCnd("and", cnd, cnd2);
			
		    List<Map<String, Object>> ls = (List<Map<String, Object>>) dao.doQuery(cnd3, "cardId",DEA_DeathReportCard);
		   if(ls.size()>0){
			   return ls.get(ls.size()-1);
		   }
		   return null ;
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载孕产妇死亡报告卡数据。", e);
		}
	}
	
	
	/**
	 * 根据孕妇档案编号加载孕产妇死亡报告卡数据
	 * 
	 * @param empiId
	 * @return
	 * @throws ValidateException
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> loadDeathByPregnantId(String pregnantId)
			throws ValidateException, ModelDataOperationException {
		try {
			List<?> cnd = CNDHelper
					.createSimpleCnd("eq", "pregnantId", "s", pregnantId);
			return dao.doLoad(cnd, DEA_DeathReportCard);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载孕产妇死亡报告卡数据。", e);
		}
	}
}
