package phis.application.war.source;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.utils.BSPHISUtil;

import ctd.service.dao.SimpleLoad;
import ctd.util.context.Context;

public class WardPatientLoad extends SimpleLoad {
	/**
	 * 查询病人就诊信息
	 */
	@SuppressWarnings("unchecked")
	public void execute(Map<String, Object> req, Map<String, Object> res,
			Context ctx) {
		try {
			super.execute(req, res, ctx);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("ZYH", req.get("pkey"));
			params.put("JSLX", 0);
			Map<String, Object> jkhj = BSPHISUtil.gf_zy_gxmk_getjkhj(params,
					new BaseDAO(), ctx);
			Map<String, Object> zfhj = BSPHISUtil.gf_zy_gxmk_getzfhj(params,
					new BaseDAO(), ctx);
			Map<String, Object> body = (Map<String, Object>) res.get("body");
			Map<String, Object> ret = BSPHISUtil.getPersonAge(
					(Date) body.get("CSNY"), (Date) body.get("RYRQ"));
			body.put("AGE", ret.get("age"));
			body.put("YJK", jkhj.get("JKHJ"));
			body.put("ZFJE", zfhj.get("ZFHJ"));
			body.put("SYJE", Double.parseDouble(jkhj.get("JKHJ").toString())
					- Double.parseDouble(zfhj.get("ZFHJ").toString()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
