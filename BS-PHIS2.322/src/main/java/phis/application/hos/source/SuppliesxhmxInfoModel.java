package phis.application.hos.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.util.context.Context;

public class SuppliesxhmxInfoModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public SuppliesxhmxInfoModel(BaseDAO dao) {
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
	public void doGetSuppliesxhmxInfo(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();
		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();
		String ksdm = (String) user.getProperty("wardId");
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
				"select distinct a.BRID as BRID,a.KSDM as KSDM,c.OFFICENAME as OFFICENAME,to_char(a.XHRQ,'yyyy-mm-dd hh24:mi:ss') as XHRQ,b.BRXM as BRXM FROM WL_XHMX a,MS_BRDA b,SYS_Office c where a.BRID =b.BRID and a.KSDM=c.ID and a.ZTBZ=0 and a.KSDM=:KSDM");
		parameters.put("KSDM", ksdm);
		parameterssize.put("KSDM", ksdm);
		try {
			list = dao.doSqlQuery(sql.toString(), parameters);
			listsize = dao.doSqlQuery(sql.toString(), parameterssize);
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
