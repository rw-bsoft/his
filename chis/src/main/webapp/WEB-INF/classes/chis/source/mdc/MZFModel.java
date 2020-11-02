/**
 * @(#)DiabetesModel.java Created on 2012-1-18 下午3:05:31
 * 
 * 版权：版权所有 bsoft 保留所有权力。
 */
package chis.source.mdc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;
import chis.source.util.SchemaUtil;
import ctd.util.exp.ExpException;
import ctd.validator.ValidateException;
/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class MZFModel extends MDCBaseModel {

	public MZFModel(BaseDAO dao) {
		super(dao);
	}

	public List<Map<String,Object>> getDiabetesInquire(String empiId) throws ModelDataOperationException{
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
//		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
//		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2); 
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd1, "inquireId",
					MDC_DiabetesInquire);
			SchemaUtil.setDictionaryMessageForList(list,
					MDC_DiabetesInquire);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病询问记录失败。");
		}
		return list;
		
	}
	
	public List<Map<String,Object>> getDiabetesRepeatVisit(String empiId,String visitId) throws ModelDataOperationException{
		List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "empiId", "s", empiId);
		List<Object> cnd2 = CNDHelper.createSimpleCnd("eq", "visitId", "s", visitId);
		List<Object> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2); 
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd, "recordId",
					MDC_DiabetesRepeatVisit);
//			Map param=new HashMap();
//			param.put("empiId", empiId);
//			param.put("visitId", visitId);
//			String hql=" select phrId as phrId,visitId as visitId,empiId as empiId,visitDate as visitDate," +
//					   " diagnosisDate as diagnosisDate,diagnosisUnit as diagnosisUnit,comfirmUnit as comfirmUnit,fbs as fbs,"+
//					   " pbs as pbs,createUnit as createUnit,createUser as createUser,createDate as createDate,"+
//					   " lastModifyUnit as lastModifyUnit,lastModifyUser as lastModifyUser,lastModifyDate as lastModifyDate "+
//					   " from MDC_DiabetesRepeatVisit where visitId=:visitId and empiId=:empiId ";
//			list = (List<Map<String, Object>>) dao.doLoad(hql, param);
			SchemaUtil.setDictionaryMessageForList(list,
					MDC_DiabetesRepeatVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病复诊记录失败。");
		}
		return list;
		
	}
	
	public Map<String,Object> saveDiabetesInquire(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String,Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesInquire, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病询问记录验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病询问记录信息失败！");
		}
		return m;
	}
	
	public Map<String,Object> saveDiabetesRepeatVisit(String op, Map<String, Object> record,
			boolean validate) throws ModelDataOperationException {
		Map<String,Object> m = null;
		try {
			m = dao.doSave(op, MDC_DiabetesRepeatVisit, record, validate);
		} catch (ValidateException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病复诊记录验证失败！");
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "保存糖尿病复诊记录信息失败！");
		}
		return m;
	}
	
	public Map<String,Object> getLastDiabetesVisitPlan(String phrId) throws ModelDataOperationException, ExpException{
		
		String exp = "['and',['eq',['$','recordId'],['s','"+phrId+"']],['eq',['$','businessType'],['s','2']]]";
		List<Object> cnd = (List<Object>) CNDHelper.toListCnd(exp);
		List<Map<String, Object>> list = null;
		try {
			list = (List<Map<String, Object>>) dao.doQuery(cnd, "endDate desc",
					PUB_VisitPlan);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取随访记录记录失败!");
		}
		if(list != null && list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	
}
