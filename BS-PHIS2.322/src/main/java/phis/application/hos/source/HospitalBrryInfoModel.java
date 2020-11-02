package phis.application.hos.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.util.context.Context;

public class HospitalBrryInfoModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalBrryInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 病人信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetBrryInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		Long brks = 0L;
		int cwxb = -1;
		String[] qcnd = new String[2];
		if (queryCnd.indexOf("#") > 0) {
			qcnd = queryCnd.split("#");
		}
		if (qcnd.length > 0) {
			if (qcnd[0] != null) {
				brks = Long.parseLong(qcnd[0]);
			}
			if (qcnd[1] != null) {
				cwxb = Integer.parseInt(qcnd[1]);
			}
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		UserRoleToken user =UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();

		StringBuffer sql = new StringBuffer(
				"SELECT brry.ZYH as ZYH,brry.ZYHM as ZYHM,brry.BRXM as BRXM,brry.BRXB as BRXB,brry.BRXZ as BRXZ,brry.BRKS as BRKS,brry.RYRQ as RYRQ,brry.CYPB as CYPB FROM ZY_BRRY brry WHERE brry.CYPB <= 1 and brry.BRCH is null and brry.JGID =:JGID AND brry.BRKS =:BRKS");
		StringBuffer brrycount = new StringBuffer(
				"CYPB <= 1 and BRCH is null and JGID =:JGID AND BRKS =:BRKS");
		if (cwxb != 3) {
			sql.append(" and brry.BRXB=:BRXB");
			brrycount.append(" and BRXB=:BRXB");
			parameters.put("BRXB", cwxb);
			parameterssize.put("BRXB", cwxb);
		}
		parameters.put("JGID", manaUnitId);
		parameters.put("BRKS", brks);
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);

		parameterssize.put("JGID", manaUnitId);
		parameterssize.put("BRKS", brks);
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);

			Long num = dao.doCount("ZY_BRRY", brrycount.toString(),
					parameterssize);
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> parametersrcjlcount = new HashMap<String, Object>();
				parametersrcjlcount.put("ZYH",
						Long.parseLong(list.get(i).get("ZYH") + ""));
				parametersrcjlcount.put("JGID", manaUnitId);
				if (Integer.parseInt(list.get(i).get("CYPB") + "") == 0) {
					Long l = dao.doCount("ZY_RCJL",
							"ZYH = :ZYH and JGID =:JGID and CZLX=-1",
							parametersrcjlcount);
					if (l > 0) {
						list.get(i).put("CYPB", 2);
					}
				}
			}
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.ZY_BRRY + "_XX");
			res.put("totalCount", num);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
