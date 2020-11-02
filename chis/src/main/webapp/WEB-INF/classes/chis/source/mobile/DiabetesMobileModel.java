package chis.source.mobile;

import java.util.List;
import java.util.Map;
import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;
import chis.source.util.CNDHelper;

public class DiabetesMobileModel implements BSCHISEntryNames {
	BaseDAO dao;

	public DiabetesMobileModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 查询糖尿病服药情况(移动端服药页签是否能够点击)
	 * 
	 * @param reqBody
	 * @return
	 * @throws ModelDataOperationException
	 */
	public Map<String, Object> enabledInputMedicine(Map<String, Object> reqBody)
			throws ModelDataOperationException {
		String visitId = (String) reqBody.get("visitId");
		String phrId = (String) reqBody.get("phrId");
		List<?> cnd1 = CNDHelper.createSimpleCnd("eq", "a.visitId", "s",
				visitId);
		List<?> cnd2 = CNDHelper.createSimpleCnd("eq", "a.phrId", "s", phrId);
		List<?> cnd = CNDHelper.createArrayCnd("and", cnd1, cnd2);
		try {
			return dao.doLoad(cnd, MDC_DiabetesVisit);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException(
					Constants.CODE_DATABASE_ERROR,
					"查询糖尿病随访服药情况(移动端服药页签是否能够点击)失败！");
		}
	}

	/**
	 * 移动端上传糖尿病服药前,首先根据visitId删除服药,然后上传移动端录入的服药
	 * 
	 * @param visitId
	 * @throws PersistentDataOperationException
	 */
	public void removeMedicine(String visitId)
			throws PersistentDataOperationException {
		dao.doRemove("visitId", visitId, MDC_DiabetesMedicine);
	}
}
