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

public class HospitalToBedInfoModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalToBedInfoModel(BaseDAO dao) {
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
	public void doGetToBedInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String queryCnd = null;
		String queryCnds = null;
		if (req.containsKey("cnds")) {
			queryCnd = req.get("cnds") + "";
		}
		if (req.containsKey("cnd")) {
			queryCnds = req.get("cnd") + "";
		}
		// Long cwks = 0L;
		Long brbq = 0L;
		String brch = null;
		int brxb = -1;
		String qcnd[] = new String[3];
		if (queryCnd.indexOf("#") > 0) {
			// modify by yangl 原有参数传递方式不变，但是CWKS直接通过sql查询
			// cwks = Long.parseLong(queryCnd.substring(0,
			// queryCnd.indexOf("?")));
			qcnd = queryCnd.split("#");
			if (qcnd[1] != null && !qcnd[1].equals("undefined") && !qcnd[1].equals("null")) {
				brch = qcnd[1];
			}
			if (qcnd[2] != null && !qcnd[2].equals("undefined") && !qcnd[2].equals("null")) {
				brxb = Integer.parseInt(qcnd[2]);
			}
			if (qcnd[3] != null && !qcnd[3].equals("undefined") && !qcnd[3].equals("null")) {
				brbq = Long.parseLong(qcnd[3]);
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
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		StringBuffer sql = new StringBuffer(
				"SELECT cwsz.BRCH as BRCH,cwsz.FJHM as FJHM,cwsz.CWKS as CWKS,cwsz.KSDM as KSDM,cwsz.CWXB as CWXB,cwsz.CWFY as CWFY,brry.ZYH as ZYH,brry.ZYHM as ZYHM,brry.BRXM as BRXM,brry.BRXB as BRXB,brry.BRXZ as BRXZ,brry.BRKS as BRKS,brry.BRBQ as BRBQ FROM ZY_CWSZ cwsz left outer join ZY_BRRY brry on cwsz.ZYH = brry.ZYH where cwsz.JGID=:JGID and cwsz.CWKS=(select CWKS from ZY_CWSZ where BRCH=:BRCH and JGID=:JGID) and cwsz.BRCH<>:BRCH  and cwsz.KSDM=:KSDM ");
		if (brxb == 1) {
			sql.append("and cwsz.CWXB<>2 ");
		}
		if (brxb == 2) {
			sql.append("and cwsz.CWXB<>1 ");
		}
		if (!"null".equals(queryCnds) && queryCnds != null) {
			String[] que = queryCnds.split(",");
			String qur = null;
			if (que[2].substring(3, que[2].indexOf("]")).equals("BRCH")) {
				qur = "and cwsz." + que[2].substring(3, que[2].indexOf("]"))
						+ " " + que[0].substring(1) + " '"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
			}
			sql.append(" " + qur);
		}
		sql.append(" ORDER BY cwsz.BRCH");
		parameters.put("JGID", manaUnitId);
		// parameters.put("CWKS", cwks);
		parameters.put("BRCH", brch);
		parameters.put("KSDM", brbq);
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		parameterssize.put("JGID", manaUnitId);
		// parameterssize.put("CWKS", cwks);
		parameterssize.put("BRCH", brch);
		parameterssize.put("KSDM", brbq);
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.ZY_CWSZ + "_KZ");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
