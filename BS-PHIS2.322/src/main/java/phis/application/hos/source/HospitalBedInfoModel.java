package phis.application.hos.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.SchemaUtil;
import ctd.account.UserRoleToken;
import ctd.util.context.Context;

public class HospitalBedInfoModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalBedInfoModel.class);

	public HospitalBedInfoModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 床位信息查询
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetBedInfo(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = req.get("cnd") + "";
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		StringBuffer sql = new StringBuffer(
				"select cw.JGID as JGID,cw.BRCH as BRCH,cw.FJHM as FJHM,cw.CWKS as CWKS,cw.KSDM as KSDM,cw.CWXB as CWXB,cw.CWFY as CWFY,cw.JCPB as JCPB,br.ZYH as ZYH,br.ZYHM as ZYHM,br.BRXM as BRXM,br.BRXB as BRXB,br.BRXZ as BRXZ,br.BRKS as BRKS,br.BRBQ as BRBQ,br.RYRQ as RYRQ,br.CYPB as CYPB FROM ZY_CWSZ cw left join ZY_BRRY br on cw.ZYH = br.ZYH where cw.JGID =:JGID");
		parameters.put("JGID", manaUnitId);
		parameterssize.put("JGID", manaUnitId);
		if (!"null".equals(queryCnd) && queryCnd != null) {
			String[] que = queryCnd.split(",");
			String qur = null;
			if (que[2].substring(3, que[2].indexOf("]")).equals("BRCH")) {
				qur = "and cw." + que[2].substring(3, que[2].indexOf("]"))
						+ " " + que[0].substring(1) + " '"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
			} else if (que[2].substring(3, que[2].indexOf("]")).equals("ZYHM")) {
				qur = "and br." + que[2].substring(3, que[2].indexOf("]"))
						+ " " + que[0].substring(1) + " '%"
						+ que[4].substring(0, que[4].indexOf("]")).trim() + "'";
			} else {
				qur = "and cw." + que[2].substring(3, que[2].indexOf("]"))
						+ "=" + que[4].substring(0, que[4].indexOf("]")).trim();
			}
			sql.append(" " + qur);
		}
		sql.append(" ORDER BY cw.BRCH");
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			SchemaUtil.setDictionaryMassageForList(list,
					BSPHISEntryNames.ZY_CWSZ + "_CWGL");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
