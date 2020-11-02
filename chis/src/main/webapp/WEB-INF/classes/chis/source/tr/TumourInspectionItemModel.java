/**
 * @(#)TumourInspectionItemModel.java Created on 2014-11-5 下午2:17:28
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.tr;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ctd.validator.ValidateException;

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
public class TumourInspectionItemModel implements BSCHISEntryNames {
	private BaseDAO dao;

	public TumourInspectionItemModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存肿瘤检查项目
	 * @param op
	 * @param record
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-5 下午2:27:33
	 * @Modify:
	 */
	public Map<String, Object> saveTumourInspectionItem(String op,
			String entryName, Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, entryName, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤检查项目时数据验证失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存肿瘤检查项目失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:判断项目在某项目类别中是否已经存在
	 * @param itemType
	 * @param definiteItemName
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2014-11-5 下午2:35:50
	 * @Modify:
	 */
	public boolean existTumourInspectionItem(String itemType,
			String definiteItemName,String itemId) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer("select count(*) as recNum from ")
				.append(MDC_TumourInspectionItem)
				.append(" where itemType=:itemType")
				.append(" and definiteItemName=:definiteItemName");
		Map<String, Object> pMap = new HashMap<String, Object>();
		pMap.put("itemType", itemType);
		pMap.put("definiteItemName", definiteItemName);
		if(StringUtils.isNotEmpty(itemId)){
			hql.append(" and itemId !=:itemId");
			pMap.put("itemId", itemId);
		}
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(hql.toString(), pMap);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询项目是否存在失败！", e);
		}
		boolean existTii  = false;
		if(rsMap != null && rsMap.size() > 0){
			long recNum = (Long) rsMap.get("recNum");
			if(recNum > 0){
				existTii = true;
			}
		}
		return existTii;
	}
}
