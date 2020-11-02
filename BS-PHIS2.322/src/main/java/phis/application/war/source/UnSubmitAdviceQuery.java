package phis.application.war.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;

import ctd.service.core.ServiceException;
import ctd.service.dao.SimpleQuery;
import ctd.util.context.Context;

public class UnSubmitAdviceQuery extends SimpleQuery {
	/**
	 * 查询病人就诊信息
	 * @throws ServiceException 
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		super.execute(req, res, ctx);
		Long zyh = Long.parseLong(req.get("ZYH").toString());
		BaseDAO dao = new BaseDAO();
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ZYH", zyh);
			List<Map<String, Object>> list = dao
					.doQuery(
							"SELECT b.SBXH as JLXH,c.FYMC||'/'||c.FYDW as YZMC,b.YLSL as YCSL,a.KDRQ as KSSJ,b.YLDJ as YPDJ,a.YSDM as YSGH,d.PERSONNAME as YSGH_text FROM YJ_ZY01 a,YJ_ZY02 b,GY_YLSF c,SYS_Personnel d WHERE a.YSDM=d.PERSONID and b.YLXH = c.FYXH and ( a.YJXH = b.YJXH ) AND ( a.ZYH = :ZYH ) AND ( a.ZXPB = 0 OR a.ZXPB IS NULL)",
							params);
			if (list != null && list.size() > 0) {
				((List<Map<String, Object>>) res.get("body")).addAll(list);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}
}
