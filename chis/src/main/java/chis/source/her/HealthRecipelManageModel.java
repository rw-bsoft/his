/**
 * @(#)HealthRecipelManageModel.java Created on 2013-6-9 下午4:10:41
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.her;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.InputStreamUtils;
import chis.source.util.SchemaUtil;

import ctd.controller.exception.ControllerException;
import ctd.schema.Schema;
import ctd.schema.SchemaController;
import ctd.security.support.condition.FilterCondition;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class HealthRecipelManageModel implements BSCHISEntryNames {
	private static Logger logger = LoggerFactory
			.getLogger(HealthRecipelManageModel.class);

	private BaseDAO dao;

	public HealthRecipelManageModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 
	 * @Description:保存健康处方
	 * @param op
	 * @param recodeMap
	 * @param validate
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-6-9 下午4:15:21
	 * @Modify:
	 */
	public Map<String, Object> saveHealthRecipel(String op,
			Map<String, Object> record, boolean validate)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doSave(op, HER_RecipelRecord, record, validate);
		} catch (ValidateException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康处方时数据验证失败", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存健康处方失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:获取健康处方
	 * @param pkey
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-6-9 下午5:42:58
	 * @Modify:
	 */
	public Map<String, Object> getHealthRecipel(String pkey)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doLoad(HER_RecipelRecord, pkey);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取健康处方失败！", e);
		}
		return rsMap;
	}

	/**
	 * 
	 * @Description:查询健康处方
	 * @param cnd
	 * @param orderBy
	 * @param pageNo
	 * @param pageSize
	 * @param queryCndsType
	 * @return
	 * @throws ModelDataOperationException
	 * @author ChenXianRui 2013-6-13 上午11:00:17
	 * @Modify:
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryHealthRecipel(List<?> cnd, String orderBy,
			String entryName, int pageNo, int pageSize, String queryCndsType)
			throws ModelDataOperationException {
		Map<String, Object> rsMap = null;
		try {
			rsMap = dao.doList(cnd, orderBy, entryName, pageNo, pageSize,
					queryCndsType);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询健康处方失败！", e);
		}

		List<Map<String, Object>> bodyList = (List<Map<String, Object>>) rsMap
				.get("body");
		List<Map<String, Object>> rsList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < bodyList.size(); i++) {
			Map<String, Object> rMap = bodyList.get(i);
			if (rMap.get("recipelContent") != null) {
				byte[] rcByte = (byte[]) rMap.get("recipelContent");
				try {
					String recipelContent = InputStreamUtils
							.byteTOString(rcByte);
					rMap.put("recipelContent", recipelContent);
				} catch (Exception e) {
					throw new ModelDataOperationException(
							Constants.CODE_DATABASE_ERROR, "健康处方转换成字符串失败！", e);
				}
			}
			rsList.add(rMap);
		}
		rsMap.put("body", rsList);
		return rsMap;
	}

	public Map<String, Object> listHealthRecipel(List queryCnd,
			String sortInfo, String entryName, int pageNo, int pageSize,
			String queryCndsType) throws ModelDataOperationException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> body = new HashMap<String, Object>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sqlCount = "select count(*) as totalCount from HER_HealthRecipeRecord a,"
				+ " MPI_DemographicInfo b, EHR_HealthRecord c"
				+ " where b.empiId=a.empiId and a.empiId=c.empiId(+)";
		String sql = "select a.id as id, a.empiId as empiId, a.recordId as recordId,"
				+ " c.phrId as phrId, b.personName as personName, b.sexCode as sexCode, "
				+ " a.wayId as wayId, a.recipeName as recipeName,  "
				+ "b.birthday as birthday, b.idCard as idCard, b.mobileNumber as mobileNumber,"
				+ " c.regionCode as regionCode, c.manaUnitId as manaUnitId, "
				+ "c.manaDoctorId as manaDoctorId, a.examineUnit as examineUnit,"
				+ " a.diagnoseName as diagnoseName, a.ICD10 as ICD10, a.guideDate as guideDate, "
				+ "a.guideUser as guideUser, a.healthTeach as healthTeach, "
				+ "a.guideWay as guideWay, a.inputUnit as inputUnit, a.inputDate as inputDate, "
				+ "a.inputUser as inputUser, a.lastModifyUser as lastModifyUser, "
				+ "a.lastModifyUnit as lastModifyUnit, a.lastModifyDate as lastModifyDate "
				+ "from HER_HealthRecipeRecord a, MPI_DemographicInfo b, EHR_HealthRecord c"
				+ " where b.empiId=a.empiId and a.empiId=c.empiId(+)";
		try {
			if (queryCnd != null) {
				sqlCount += " and "
						+ ExpressionProcessor.instance().toString(queryCnd);
				sql += " and "
						+ ExpressionProcessor.instance().toString(queryCnd);
			}
			Schema sc = SchemaController.instance().get(HER_HealthRecipeRecord);
			FilterCondition c = (FilterCondition)sc.lookupCondition("query");
			logger.warn("c================"+c);
			if(c != null){
				List<Object> roleCnd = (List<Object>)c.getDefine();
				logger.warn("roleCnd================"+roleCnd.toArray().toString());
				if(roleCnd != null && roleCnd.size()>0){
					sqlCount += " and "
							+ ExpressionProcessor.instance().toString(roleCnd);
					sql += " and "
							+ ExpressionProcessor.instance().toString(roleCnd);
				}
			}
			if (sortInfo != null) {
				sql += " order by " + sortInfo;
			}
			logger.warn(sql);
			list = dao.doSqlQuery(sqlCount, parameters);
			int totalCount = Integer.parseInt(list.get(0).get("TOTALCOUNT")
					+ "");
			parameters.put("first", (pageNo - 1) * pageSize);
			parameters.put("max", pageSize);
			list = dao.doSqlQuery(sql, parameters);
			list = SchemaUtil.setDictionaryMessageForListFromSQL(list,
					HER_HealthRecipeRecord);
			body.put("totalCount", totalCount);
			body.put("body", list);
		} catch (ExpException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询健康处方记录失败！", e);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询健康处方记录失败！", e);
		} catch (ControllerException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "查询健康处方记录失败！", e);
		}
		return body;
	}
}
