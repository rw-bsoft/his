/**
 * @(#)DrugManageModel.java Created on 2013-5-28 下午3:14:51
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.pub;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DrugManageModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public DrugManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:判断药品是否已在
	 * @param mdcUse
	 * @param YPXH
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-28 下午3:48:08
	 * @Modify:
	 */
	public boolean existDrug(String mdcUse, long YPXH)
			throws ModelDataOperationException {
		String hql = new StringBuffer("select count(*) as recordNum from ")
				.append(PUB_DrugDirectory)
				.append(" where mdcUse = :mdcUse and YPXH = :YPXH").toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("mdcUse", mdcUse);
		parameters.put("YPXH", YPXH);
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "判断药品是否已在失败！", e);
		}
		boolean exist = false;
		if (rsMap != null && rsMap.size() > 0) {
			long recordNum = (Long) rsMap.get("recordNum");
			if (recordNum > 0) {
				exist = true;
			}
		}
		return exist;
	}

	/**
	 * 
	 * @Description:保存药品
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-5-28 下午3:51:07
	 * @Modify:
	 */
	public Map<String, Object> saveDrug(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, PUB_DrugDirectory, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存药品数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存药品失败！", e);
		}
		return rsMap;
	}

	public void loadDicData(Map<String, Object> req, Map<String, Object> res)
			throws ModelDataOperationException {
		String sql = "select ICD10 as ICD10,JBMC as JBMC "
				+ "from GY_JBBM "
				+ "where upper(ICD10) like:ICD10 order by ICD10";
		String sql_count = "select count(*) as count "
				+ "from GY_JBBM "
				+ "where upper(ICD10) like:ICD10";
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("ICD10", req.get("query").toString().toUpperCase() + "%");
			List<Map<String, Object>> countRecords = dao.doSqlQuery(sql_count, parameters);
			if(countRecords!=null&& countRecords.size()>0){
				res.put("count", countRecords.get(0).get("COUNT"));
			}
			parameters.put("first", req.get("start"));
			parameters.put("max", req.get("limit"));
			List<Map<String, Object>> record = dao.doSqlQuery(sql, parameters);
			for (int i = 0; i < record.size(); i++) {
				record.get(i).put("numKey", i+1);
			}
			res.put("icd10", record);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询ICD10失败！", e);
		}

	}
}
