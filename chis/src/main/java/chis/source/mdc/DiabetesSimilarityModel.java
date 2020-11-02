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
import chis.source.dic.DiagnosisType;
import chis.source.util.BSCHISUtil;
import chis.source.util.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.validator.ValidateException;

/**
 * @description
 * 
 * @author <a href="mailto:chenxr@bsoft.com.cn">ChenXianRui</a>
 */
public class DiabetesSimilarityModel extends DiabetesModel {

	public DiabetesSimilarityModel(BaseDAO dao) {
		super(dao);
	}

	public List<Map<String, Object>> getDiabetesSimilarityByEmpiId(String empiId)
			throws ModelDataOperationException {
		String hql = new StringBuffer(" from ")
				.append(MDC_DiabetesSimilarity)
				.append(" where empiId =:empiId and ( diagnosisType =:diagnosisType1 or diagnosisType=:diagnosisType2 )")
				.toString();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("diagnosisType1", DiagnosisType.QZ);
		parameters.put("diagnosisType2", DiagnosisType.YS);
		parameters.put("empiId", empiId);
		List<Map<String, Object>> rsList = null;
		try {
			rsList = dao.doQuery(hql, parameters);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR, "获取糖尿病疑似记录失败");
		}
		return rsList;
	}

	/**
	 * 插入糖尿病疑似记录
	 * 
	 * @param empiId
	 * @param phrId
	 * @param fbs空腹血糖
	 *            (mmol/L)
	 * @param pbs餐后血糖
	 *            (mmol/L)
	 * @param dataSource数据来源
	 */
	public void insertDiabetesSimilarity(String sourceId, String empiId,
			String phrId, Double fbs, Double pbs,String dataSource, Map<String, Object> data) {
		if (fbs == null && pbs == null) {
			return;
		}
		if (pbs != null && pbs >= 11.1) {
		} else if (fbs != null && fbs >= 7.0) {
		} else {
			return;
		}
		if (sourceId == null) {
			return;
		}
		sourceId=dataSource+sourceId;
		List<Object> cnd = CNDHelper.createSimpleCnd("eq", "a.empiId", "s",
				empiId);
		try {
			List<Map<String, Object>> list1 = dao.doQuery(cnd, "a.empiId",
					MDC_DiabetesRecord);
			String currentDate = BSCHISUtil.toString(new Date(), null);
			List<Object> cnd1 = CNDHelper.createSimpleCnd("eq", "a.sourceId",
					"s", sourceId);
			List<Object> cnd2 = CNDHelper.createSimpleCnd("eq",
					"to_char(a.registerDate,'yyyy-mm-dd')", "s", currentDate);
			List<Object> cnd3 = CNDHelper.createArrayCnd("or", cnd1, cnd2);
			cnd = CNDHelper.createArrayCnd("and", cnd, cnd3);
			List<Map<String, Object>> list2 = dao.doQuery(cnd,
					"a.inputDate desc", MDC_DiabetesSimilarity);
			if (list1 != null && list1.size() > 0) {
				return;
			}
			if (list2 != null && list2.size() > 0) {
				return;
			}
			UserRoleToken urt = UserRoleToken.getCurrent();
			Map<String, Object> r = new HashMap<String, Object>();
			r.put("empiId", empiId);
			r.put("phrId", phrId);
			r.put("fbs", fbs);
			r.put("pbs", pbs);
			r.put("sourceId", sourceId);
			r.put("diagnosisType", DiagnosisType.YS);
			r.put("registerDate", new Date());
			r.put("registerUser", urt.getUserId());
			r.put("registerUnit", urt.getManageUnitId());
			r.put("inputDate", new Date());
			r.put("inputUser", urt.getUserId());
			r.put("inputUnit", urt.getManageUnitId());
			if (data.get("manaUnitId") != null) {
				r.put("manaUnitId", data.get("manaUnitId"));
			} else {
				r.put("manaUnitId", urt.getManageUnitId());
			}
			if (data.get("height") != null) {
				r.put("height", data.get("height"));
			}
			if (data.get("weight") != null) {
				r.put("weight", data.get("weight"));
			}
			if (data.get("bmi") != null) {
				r.put("bmi", data.get("bmi"));
			} else if (data.get("height") != null && data.get("weight") != null) {
				double h = Double.valueOf(data.get("height") + "");
				double w = Double.valueOf(data.get("weight") + "");
				double bmi = w / (h * h / 10000);
				bmi = ((int) (bmi * 100)) / 100.0;
				r.put("bmi", bmi);
			}
			dao.doSave("create", MDC_DiabetesSimilarity, r, false);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}

}
