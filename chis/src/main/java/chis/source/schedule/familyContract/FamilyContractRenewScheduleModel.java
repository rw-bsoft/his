package chis.source.schedule.familyContract;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chis.source.BSCHISEntryNames;
import chis.source.BaseDAO;
import chis.source.ModelDataOperationException;
import chis.source.PersistentDataOperationException;

public class FamilyContractRenewScheduleModel implements BSCHISEntryNames {
	BaseDAO dao;

	public FamilyContractRenewScheduleModel(BaseDAO dao) {
		super();
		this.dao = dao;
	}

	public void renewFamilyContractRecord() throws ModelDataOperationException {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 1);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date end = cal.getTime();
		StringBuffer hql = new StringBuffer("update ").append(
				EHR_FamilyContractBase).append(
				" set FC_End=:end where FC_End < :now and FC_Sign_Flag='1'");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("end", end);
		parameters.put("now", date);
		try {
			dao.doUpdate(hql.toString(), parameters);
		} catch (PersistentDataOperationException e) {
			throw new ModelDataOperationException("到期家庭签约记录续约失败。", e);
		}
	}
}
