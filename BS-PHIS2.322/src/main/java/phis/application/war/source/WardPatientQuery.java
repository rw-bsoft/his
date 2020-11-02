package phis.application.war.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.CNDHelper;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.BSPHISUtil;

import ctd.account.UserRoleToken;
import ctd.dictionary.DictionaryController;
import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import phis.source.utils.T;
import ctd.util.context.Context;

public class WardPatientQuery extends SimpleQuery {
	/**
	 * 查询病人列表
	 */
	protected Logger logger = LoggerFactory.getLogger(WardPatientQuery.class);

	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,Context ctx) {
		try {
			if (req.get("CY72") != null && req.get("CY72").toString().equals("1")) {
				StringBuffer cnds = new StringBuffer();
				Date regBegin = DateUtils.addDays(new Date(), -720);
				cnds = cnds.append("['ge',['$','a.CYRQ'],['todate',['s','" + T.format(regBegin, T.DATETIME_FORMAT)
						+ "'],['s','yyyy-mm-dd hh24:mi:ss']]]");
				if (req.get("cnd") != null) {
					req.put("cnd",CNDHelper.createArrayCnd("and",(List<?>) req.get("cnd"),CNDHelper.toListCnd(cnds.toString())));
				} else {
					req.put("cnd", CNDHelper.toListCnd(cnds.toString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
			res.put(RES_MESSAGE, "获取72小时内出院病人信息错误");
			return;
		}
		try {
			super.execute(req, res, ctx);
		} catch (ServiceException e1) {
			e1.printStackTrace();
			logger.error("ward patient query error:" + e1.getMessage());
		}
		List<Map<String, Object>> body = (List<Map<String, Object>>) res.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();// 用户的机构ID
		BaseDAO dao = new BaseDAO();
		for (Map<String, Object> record : body) {
			Map<String, Object> ret = BSPHISUtil.getPersonAge((Date) record.get("CSNY"), (Date) record.get("RYRQ"));
			record.put("AGE", ret.get("age"));
			int Ll_cypb = Integer.parseInt(record.get("CYPB") + "");
			long Ll_zyh = Long.parseLong(record.get("ZYH") + "");
			if (Ll_cypb == 1) {
				continue;
			}
			if (Ll_cypb == 8) {
				record.put("CYPB", "8");
				record.put("CYPB_text",DictionaryController.instance().getDic("phis.dictionary.patientStatus").getText("8"));
				continue;
			}
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("JGID", jgid);
			params.put("ZYH", Ll_zyh);
			long ll_count;
			try {
				ll_count = dao.doCount("ZY_RCJL","JGID = :JGID and ZYH=:ZYH and CZLX=-1", params);
				if (ll_count > 0) {
					record.put("CYPB", 102);
					record.put("CYPB_text",DictionaryController.instance().getDic("phis.dictionary.patientStatus").getText("102"));
				}
			} catch (PersistentDataOperationException e) {
				logger.error("ward patient query error:" + e.getMessage());
			}

		}
	}

}
