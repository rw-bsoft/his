package chis.source.mobile;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.PersistentDataOperationException;

public class HypertensionMobileModel implements BSCHISEntryNames {
	BaseDAO dao;

	public HypertensionMobileModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	/**
	 * 移动端上传糖尿病服药前,首先根据visitId删除服药,然后上传移动端录入的服药
	 * 
	 * @param visitId
	 * @throws PersistentDataOperationException
	 */
	public void removeMedicine(String visitId)
			throws PersistentDataOperationException {
		dao.doRemove("visitId", visitId, MDC_HypertensionMedicine);
	}
}
