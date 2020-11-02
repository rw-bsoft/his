/**
 * @(#)ChineseMedicineManageModel.java Created on 2014-6-24 上午9:12:22
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
import chis.source.service.ServiceCode;
import chis.source.util.SchemaUtil;


import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:yaosq@bsoft.com.cn">姚士强</a>
 */
public class ChineseMedicineManageModel implements BSCHISEntryNames {

	private BaseDAO dao;

	public ChineseMedicineManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	public Map<String, Object> saveChineseMedicineManage(String schema,
			String op, Map<String, Object> body)
			throws ModelDataOperationException {
		Map<String, Object> result = null;
		try {
			result = dao.doSave(op, schema, body, true);
			result=SchemaUtil.setDictionaryMessageForForm(result, MDC_ChineseMedicineManage);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存老年人中医药保健失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存老年人中医药保健失败！", e);
		}
		return result;
	}

	public void logoutChineseMedicineManage(String schema, String pkey)
			throws ModelDataOperationException {
		String hql = "update MDC_ChineseMedicineManage set status='1' where id=:pkey";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("pkey", pkey);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销老年人中医药保健失败！", e);
		}

	}
	
	public void logoutChineseMedicineManagetoday(String empiId, String today)
			throws ModelDataOperationException {
		String hql = "update MDC_ChineseMedicineManage set status='1' where empiId=:empiId and to_char(createDate,'yyyy-mm-dd')=:today ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		parameters.put("today", today);
		try {
			dao.doUpdate(hql, parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "注销老年人中医药保健失败！", e);
		}

	}
	
	public void checkHasOldRecord(String empiId, Map<String, Object> res) throws ModelDataOperationException {
		String hql = "from "+MDC_OldPeopleRecord+" where empiId=:empiId";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("empiId", empiId);
		try {
			List<Map<String, Object>> list = dao.doQuery(hql, parameters);
			if(list!=null&&list.size()>0){
				res.put("hasOldRecord", true);
			}else{
				res.put("hasOldRecord", false);
			}
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询是否存在老年人档案失败！", e);
		}
		
	}

}
