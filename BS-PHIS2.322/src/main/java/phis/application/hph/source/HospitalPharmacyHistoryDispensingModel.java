package phis.application.hph.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
/**
 * 发药查询Model
 * @author caijy
 *
 */
public class HospitalPharmacyHistoryDispensingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalPharmacyHistoryDispensingModel.class);

	public HospitalPharmacyHistoryDispensingModel(BaseDAO dao) {
		this.dao = dao;
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-2-28
	 * @description 发药查询上面病区下拉框数据查询
	 * @updateInfo
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<List<Object>> queryWard(Context ctx)
			throws ModelDataOperationException {
		List<List<Object>> list_ret = new ArrayList<List<Object>>();
		UserRoleToken user = UserRoleToken.getCurrent();
		long yfsb = MedicineUtils.parseLong(user.getProperty("pharmacyId"));
		String jgid = user.getManageUnit().getRef();
		StringBuffer hql_lyks = new StringBuffer();
		hql_lyks.append("select LYKS as LYKS from YF_YFLB where YFSB=:yfsb");
		Map<String, Object> map_par = new HashMap<String, Object>();
		map_par.put("yfsb", yfsb);
		try {
			Map<String, Object> map_lyks = dao.doLoad(hql_lyks.toString(),
					map_par);
			if (map_lyks == null) {
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "领药科室查询失败");
			}
			if (map_lyks.get("LYKS") == null
					|| (map_lyks.get("LYKS") + "").length() == 0) {
				return list_ret;
			}
			List<Object> lyks = new ArrayList<Object>();
			lyks.add(0);
			String[] lykss = (map_lyks.get("LYKS") + "").split(",");
			for (int i = 0; i < lykss.length; i++) {
				lyks.add(MedicineUtils.parseInt(lykss[i]));
			}
			StringBuffer hql_bq = new StringBuffer();
			hql_bq.append("select ID as KSDM,officeName as KSMC from SYS_Office where officeCode not in (select parentId from SYS_Office where parentId is not null) and hospitalArea=1 and organizCode=:jgid and ID in (:lyks)");
			map_par.clear();
			map_par.put("jgid", jgid);
			map_par.put("lyks", lyks);
			List<Map<String, Object>> list_bqs = dao.doSqlQuery(
					hql_bq.toString(), map_par);
			for (Map<String, Object> map_bq : list_bqs) {
				List<Object> list_bq = new ArrayList<Object>();
				list_bq.add(MedicineUtils.parseInt(map_bq.get("KSDM")));
				list_bq.add(map_bq.get("KSMC"));
				list_ret.add(list_bq);
			}
		} catch (PersistentDataOperationException e) {
			MedicineUtils.throwsException(logger, "领药科室查询失败", e);
		}
		return list_ret;
	}
}
