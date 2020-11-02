package phis.application.ivc.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;

import ctd.util.context.Context;

public class ClinicPerscriptionModel {
	private BaseDAO dao;

	public ClinicPerscriptionModel(BaseDAO dao) {
		this.dao = dao;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> perscriptionCopyCheck(
			Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> res = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> cf01_List = (List<Map<String, Object>>) body
				.get("cf01List");
		;
		List<Map<String, Object>> cf02_List = (List<Map<String, Object>>) body
				.get("cf02List");
		;
		String pharmacyId = String.valueOf(cf01_List.get(0).get("YFSB"));
		// pharmacyId = "1";//需去掉
		try {
			for (Map<String, Object> med : cf02_List) {
				Object ypxh = med.get("YPXH");
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("YFSB", Long.parseLong(pharmacyId.toString()));
				params.put("YPXH", Long.parseLong(ypxh.toString()));
				params.put("YPCD", Long.parseLong(med.get("YPCD").toString()));
				StringBuffer hql = new StringBuffer(
						"select a.YPXH as YPXH,a.YPMC as YPMC,b.YFGG as YFGG,a.YPDW as YPDW,a.PSPB as PSPB,a.JLDW as JLDW,a.YPJL as YPJL,a.GYFF as GYFF,a.YFBZ as YFBZ,d.LSJG as LSJG,d.YPCD as YPCD,f.CDMC as YPCD_text,a.TYPE as TYPE,a.TSYP as TSYP,b.YFDW as YFDW,d.LSJG as YPDJ,a.ZBLB as FYGB,a.KSBZ as KSBZ,a.KSSDJ as KSSDJ,a.YQSYFS as YQSYFS");
				hql.append(" from YK_TYPK a,YF_YPXX b,YF_KCMX d,YK_CDDZ f ");
				hql.append(" where  ( a.YPXH = d.YPXH ) AND ( b.YPXH = a.YPXH ) AND ( b.YFSB = d.YFSB ) AND ( a.ZFPB = 0 ) AND ( b.YFZF = 0 ) AND ( d.JYBZ = 0 ) AND (d.YPCD=f.YPCD) AND  b.YFSB=:YFSB   AND a.YPXH = :YPXH and d.YPCD=:YPCD ORDER BY d.SBXH");
				List<Map<String, Object>> meds = dao.doQuery(hql.toString(),
						params);
				if (meds.size() > 0) {// 取第一条记录
					Map<String, Object> zt_med = meds.get(0);
					// zt_med.putAll(med);//需要重新定义，明确哪些是用原处方信息
					zt_med.put("YCJL", med.get("YCJL"));
					zt_med.put("YYTS", med.get("YYTS"));
					zt_med.put("YPSL", med.get("YPSL"));
					zt_med.put("YPYF", med.get("YPYF"));
					zt_med.put("YPYF_text", med.get("YPYF_text"));
					zt_med.put("GYTJ", med.get("GYTJ"));
					zt_med.put("GYTJ_text", med.get("GYTJ_text"));
					zt_med.put("BZMC", med.get("BZMC"));
					zt_med.put("MRCS", med.get("MRCS"));
					zt_med.put("YPZH", med.get("YPZH"));
					zt_med.put("ZFYP", med.get("ZFYP"));
					/*********add by lizhi 药品剂量、处方帖数*********/
					zt_med.put("YPJL", med.get("YPJL"));
					zt_med.put("CFTS", med.get("CFTS"));
					// 获取组套中药品自负比例和库存数量
					Map<String, Object> data = new HashMap<String, Object>();
					data.put("TYPE", zt_med.get("TYPE"));
					data.put("FYXH", zt_med.get("YPXH"));
					data.put("BRXZ", body.get("BRXZ"));
					data.put("FYGB", zt_med.get("FYGB"));
					Map<String, Object> zfbl = BSPHISUtil.getPayProportion(
							data, ctx, dao);
					zt_med.put("ZFBL", zfbl.get("ZFBL"));
					zt_med.put("YPYF", zt_med.get("SYPC"));
					zt_med.put("SYPC", med.get("YPYF"));
					zt_med.put("SYPC_text", med.get("YPYF_text"));
					res.add(zt_med);
				} else {
					med.put("errorMsg", "暂无库存!");
					res.add(med);
				}
			}
		} catch (Exception e) {
			throw new ModelDataOperationException("数据异常，请联系管理员!", e);
		}
		return res;
	}
}
