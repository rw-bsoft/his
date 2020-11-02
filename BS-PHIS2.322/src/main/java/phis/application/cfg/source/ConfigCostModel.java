package phis.application.cfg.source;

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
import phis.source.service.ServiceCode;


import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 收费项目维护费用信息model
 * 
 * @author zhangyq 2012.05
 */
public class ConfigCostModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ConfigCostModel.class);

	public ConfigCostModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 费用限制查询
	 * 
	 * @param ypxh
	 * @param ctx
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> doCostConstraintsList(Long fyxh, Context ctx)
			throws ModelDataOperationException {
		String sql = "select a.BRXZ as BRXZ,a.XZMC as XZMC,nvl(b.ZFBL*100,0) as ZFBL,nvl(b.FYXE,0) as FYXE,nvl(b.CXBL*100,0) as CXBL from "
				+ " GY_BRXZ "
				+ " a left outer join "
				+ " GY_FYJY "
				+ " b on a.BRXZ=b.BRXZ and b.FYxh=:FYXH where a.BRXZ not in (select SJXZ from "
				+ " GY_BRXZ ) order by a.BRXZ desc";
		List<Map<String, Object>> rs = null;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FYXH", fyxh);
			rs = dao.doSqlQuery(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: costs Constraints load failure.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：费用限制加载失败.");
		}
		return rs;
	}

	// 保存费用基本信息
	@SuppressWarnings("unchecked")
	public Map<String, Object> doSaveCostDetail(String op,
			Map<String, Object> res, Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> req = doSaveCostInformation(op, res, body);// 保存费用基础信息YK_TYPK
			if (req == null)
				return res;
			Long fyxh = Long.parseLong((op.equals("create") ? req.get("FYXH")
					: body.get("FYXH")) + "");// 获取主键
			ArrayList<Map<String, Object>> bmInfos = (ArrayList<Map<String, Object>>) body
					.get("cfgaliastab");// 从body里取出别名信息
			ArrayList<Map<String, Object>> limitInfos = (ArrayList<Map<String, Object>>) body
					.get("cfglimittab");// 从body里取出费用性质信息
			doSaveCostAlias(bmInfos, fyxh);// 保存别名信息YP_YPBM
			doSaveCostConstraints(limitInfos, fyxh);// 保存用药限制信息
			return req;
		} catch (ValidateException e) {
			logger.error(
					"Reception to be validated: the costs Information failed to save.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "前台验证：费用信息保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: the costs Information failed to save.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：费用信息保存失败.");
		}
	}

	// 保存费用信息 ,并判断不能重复
	public Map<String, Object> doSaveCostInformation(String op,
			Map<String, Object> res, Map<String, Object> body)
			throws ValidateException, PersistentDataOperationException {
		Map<String, Object> req = null;
		if ("create".equals(op)) {
			String sql = "FYMC=:FYMC and FYGB=:FYGB";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FYMC", body.get("FYMC"));
			parameters.put("FYGB", body.get("FYGB"));
			Long l = dao.doCount("GY_YLSF", sql, parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "费用属性已存在");
			} else {
				req = dao.doSave(op, BSPHISEntryNames.GY_YLSF, body, false);// 保存费用基础信息YK_TYPK
			}
		} else {
			String sql = "FYMC=:FYMC and FYGB=:FYGB and FYXH<>:FYXH";
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("FYMC", body.get("FYMC"));
			parameters.put("FYGB", body.get("FYGB"));
			parameters.put("FYXH", Long.parseLong(body.get("FYXH") + ""));
			Long l = dao.doCount("GY_YLSF", sql, parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "费用属性已存在");
			} else {
				req = dao.doSave(op, BSPHISEntryNames.GY_YLSF, body, false);// 保存费用基础信息YK_TYPK
			}

		}
		return req;
	}

	// 保存费用别名
	public void doSaveCostAlias(ArrayList<Map<String, Object>> bmInfos,
			Long fyxh) throws ValidateException,
			PersistentDataOperationException {
		if (bmInfos != null && bmInfos.size() > 0) {
			dao.removeByFieldValue("FYXH", fyxh, BSPHISEntryNames.GY_FYBM);
			for (Map<String, Object> alias : bmInfos) {
				alias.put("FYXH", fyxh);
				dao.doSave("create", BSPHISEntryNames.GY_FYBM, alias, false);
			}
		}
	}

	// 费用限制保存
	public void doSaveCostConstraints(
			ArrayList<Map<String, Object>> limitInfos, Long fyxh)
			throws ValidateException, PersistentDataOperationException {
		dao.removeByFieldValue("FYXH", fyxh, BSPHISEntryNames.GY_FYJY);
		if (limitInfos != null && limitInfos.size() > 0) {
			for (Map<String, Object> limit : limitInfos) {
				limit.put("FYXH", fyxh);
				if (!("0".equals(limit.get("ZFBL") + "")
						&& "0".equals(limit.get("CXBL") + "") && "0"
						.equals(limit.get("FYXE") + ""))) {
					limit.put("ZFBL",
							Double.parseDouble(limit.get("ZFBL") + "") / 100);
					limit.put("CXBL",
							Double.parseDouble(limit.get("CXBL") + "") / 100);
					dao.doSave("create", BSPHISEntryNames.GY_FYJY, limit, false);
				}
			}
		}
	}
}
