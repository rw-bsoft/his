package phis.application.stm.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;

import phis.source.BSPHISEntryNames;
import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.ParameterUtil;
import phis.source.utils.SchemaUtil;
import phis.source.utils.T;
import ctd.account.UserRoleToken;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;
import ctd.util.exp.ExpressionProcessor;

public class SkintestQuery extends SimpleQuery {
	/**
	 * 查询病人就诊信息
	 */
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manageUnit = user.getManageUnit().getId();
		String PSXQ = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.PSXQ, ctx);
		int QYMZSF = 0;
		String QYMZSFtr = ParameterUtil.getParameter(manageUnit,
				BSPHISSystemArgument.QYMZSF, ctx);
		if (QYMZSFtr != "") {
			QYMZSF = Integer.parseInt(QYMZSFtr);
		}
		int wcbz = "cancelTestSkin".equals(req.get("openBy")) ? 1 : 0;
		String sj = "cancelTestSkin".equals(req.get("openBy")) ? "c.PSSJ"
				: "c.SQSJ";
		StringBuffer hql = new StringBuffer();
		try {
			Date now = new Date();
			// SimpleDateFormat matter = new SimpleDateFormat(
			// "yyyy-MM-dd HH:mm:ss");
			Date regBegin = DateUtils.addDays(now, -Integer.parseInt(PSXQ));
			String cnd = "['ge',['$','" + sj + "'],['todate',['s','"
					+ T.format(regBegin, T.DATETIME_FORMAT)
					+ "'],['s','yyyy-mm-dd hh24:mi:ss']]]";
			String filterSql = ExpressionProcessor.instance().toString(cnd);
			hql.append("select a.CFSB as CFSB,a.FPHM as FPHM,d.BRXM as BRXM,CFHM as CFHM from MS_CF01 a,MS_BRDA d where a.JGID=:JGID and a.BRID=d.BRID and (:QYMZSF = 0 or exists (select 'X' from MS_CF02 d  where a.CFSB = d.CFSB and (d.SFJG <> 1 or d.SFJG is null))) and  exists (select 'X' from MS_CF02 b, YS_MZ_PSJL c where b.YPXH=c.YPBH and a.CFSB = b.CFSB and a.CFSB = c.CFSB and b.PSPB > 0 and c.WCBZ = :WCBZ and "
					+ filterSql + ")");
		} catch (Exception e) {
			e.printStackTrace();
			res.put(RES_CODE, ServiceCode.CODE_REQUEST_PARSE_ERROR);
			res.put(RES_MESSAGE, "获取皮试记录错误");
			return;
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 1;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo");
		}
		int first = pageNo - 1;
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			BaseDAO dao = new BaseDAO();
			parameters.put("JGID", manageUnit);
			parameters.put("WCBZ", wcbz);
			parameters.put("QYMZSF", QYMZSF);
			int count = dao.doSqlQuery(hql.toString(), parameters).size();
			List<Map<String, Object>> list;
//			parameters.put("first", first * pageSize);
//			parameters.put("max", pageSize);
			list = dao.doSqlQuery(hql.toString(), parameters);
			res.put("pageSize", pageSize);
			res.put("pageNo", pageNo);
			res.put("totalCount", count);
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
