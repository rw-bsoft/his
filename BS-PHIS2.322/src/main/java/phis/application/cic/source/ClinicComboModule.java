/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.cic.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import phis.source.utils.CNDHelper;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

/**
 * @description 处方组套维护
 * 
 * @author zhangyq 2012.05.28
 */
public class ClinicComboModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(ClinicComboModule.class);

	public ClinicComboModule(BaseDAO dao) {
		this.dao = dao;
	}

	public void savePersonalComboExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String SFQY = body.get("SFQY") + "";
		String sql = "ZTBH=:ZTBH";
		try {
			parameters.put("ZTBH", Long.parseLong(body.get("ZTBH").toString()));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(SFQY)) {
					// 启用
					parameters.put("SFQY", 0);
					dao.doUpdate("update " + schemaList
							+ " set SFQY=:SFQY where ZTBH=:ZTBH", parameters);
					res.put(Service.RES_CODE, 601);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("SFQY", 1);
					dao.doUpdate("update " + schemaList
							+ " set SFQY=:SFQY WHERE ZTBH=:ZTBH", parameters);
					res.put(Service.RES_CODE, 602);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 603);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/**
	 * 处方组套明细保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSavePrescriptionDetails(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String schema = (String) req.get("schema");
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			for (int i = 0; i < body.size(); i++) {
				String op = (String) body.get(i).get("_opStatus");
				dao.doSave(op, schema, body.get(i), false);
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}
	}

	/**
	 * 处方组套明细删除
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doRemovePrescriptionDetails(Map<String, Object> body, int pkey,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String sql = "ZTBH=:ZTBH";
		String sql1 = "ZTBH=:ZTBH and SFQY=1";// 判断是否启用的sql
		try {
			dao.doRemove(pkey, "phis.application.cic.schemas.YS_MZ_ZT02_CF");
			parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			if (l == 0) {
				Long l1 = dao.doCount(schemaList, sql1, parameters);
				if (l1 > 0) {// 如果启用了 才取消启动
					dao.doUpdate(
							"update YS_MZ_ZT01 set SFQY=0 where ZTBH=:ZTBH",
							parameters);
					res.put(Service.RES_CODE, 604);
					res.put(Service.RES_MESSAGE, "该组套明细为空，自动取消启用");
				}
			}
		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		}

	}

	/*
	 * 病历模板启用功能
	 */
	public void saveMedicalTemplateExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String QYBZ = body.get("QYBZ") + "";
		String sql = "JLXH=:JLXH and ZSXX is not null and XBS is not null";
		try {
			parameters.put("JLXH", body.get("JLXH"));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(QYBZ)) {
					// 启用
					parameters.put("QYBZ", 0);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ where JLXH=:JLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("QYBZ", 1);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ WHERE JLXH=:JLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 607);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	/*
	 * 诊疗方案启用功能
	 */
	public void saveTherapeuticRegimenExecute(Map<String, Object> body,
			String schemaList, String schemaDetailsList,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		String QYBZ = body.get("QYBZ") + "";
		String sql = "ZLXH=:ZLXH and (BLMBBH is not null or CFZTBH is not null or XMZTBH is not null or JBXH is not null)";
		try {
			parameters.put("ZLXH", body.get("ZLXH"));
			Long l = dao.doCount(schemaDetailsList, sql, parameters);
			// 如果明细不为空
			if (l > 0) {
				// 没有启用
				if ("0".equalsIgnoreCase(QYBZ)) {
					// 启用
					parameters.put("QYBZ", 0);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ where ZLXH=:ZLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "取消启用成功");
				} else {
					// 取消启用
					parameters.put("QYBZ", 1);
					dao.doUpdate("update " + schemaList
							+ " set QYBZ=:QYBZ WHERE ZLXH=:ZLXH", parameters);
					res.put(Service.RES_CODE, 605);
					res.put(Service.RES_MESSAGE, "启用成功");
				}
			} else {
				res.put(Service.RES_CODE, 607);
				res.put(Service.RES_MESSAGE, "明细为空，不能启用");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "启用失败");
		}
	}

	// 删除组套时判断组套明细是否有值 如果有 提示不能删
	public void removePrescriptionDel(Map<String, Object> body,
			Map<String, Object> res, String op, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		String sql = "ZTBH=:ZTBH";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZTBH", Long.parseLong(body.get("ZTBH") + ""));
		try {
			Long l = dao.doCount("YS_MZ_ZT02", sql, parameters);
			if (l > 0) {
				res.put(Service.RES_CODE, 613);
				res.put(Service.RES_MESSAGE, "组套明细已存在，无法删除");
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "组套明细校验失败");
		}
	}

	// 如果是个人常用药 就自动插入3个组套类别的值,如果有了就return掉
	public void doSaveCommonlyUsedDrugs(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = user.getUserId() + "";// 暂时员工代码全部使用该方法替代
		body.put("JGID", jgid);
		String sql = "SSLB=:SSLB and JGID=:JGID ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SSLB", Integer.parseInt(body.get("SSLB") + ""));
		if (Integer.parseInt(body.get("SSLB") + "") == 4) { // sslb = 4 个人常用药
															// sslb = 5,6
															// 分别代表科室，全院
			body.put("YGDM", userId);
			sql += " and YGDM=:YGDM ";
			parameters.put("YGDM", userId);
		} else {
			if (body.get("KSDM") != null) {
				sql += " and KSDM=:KSDM ";
				parameters.put("KSDM", body.get("KSDM") + "");
			}
		}
		parameters.put("JGID", jgid);
		try {
			Long l = dao.doCount("YS_MZ_ZT01", sql, parameters);
			// 处方组套维护，因有西药、草药、中药三种，因此判断大于等于3时才return
			// 如果是住院主题还有项目。
			if (l > 4) {
				return;
			} else {
				for (int i = 0; i < 5; i++) {
					int ztlb = i+1;
					String hql = "SSLB=:SSLB and ZTLB=:ZTLB and JGID=:JGID and ZTMC=:ZTMC";
					Map<String, Object> param = new HashMap<String, Object>();
					param.put("SSLB", Integer.parseInt(body.get("SSLB") + ""));
					param.put("ZTLB", ztlb);
					param.put("JGID", jgid);
					param.put("ZTMC", "个人常用");
					if (Integer.parseInt(body.get("SSLB") + "") == 4) { // sslb = 4 个人常用药
						// sslb = 5,6
						// 分别代表科室，全院
						body.put("YGDM", userId);
						hql += " and YGDM=:YGDM ";
						param.put("YGDM", userId);
					} else {
						if (body.get("KSDM") != null) {
							hql += " and KSDM=:KSDM ";
							param.put("KSDM", body.get("KSDM") + "");
						}
					}
					Long n = dao.doCount("YS_MZ_ZT01", hql, param);
					if(n == 0){
						body.put("ZTLB", i + 1);
						dao.doSave("create",
								"phis.application.cic.schemas.YS_MZ_ZT01_CF", body,
								false);
					}
				}
			}
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	// 如果是个人常用药 就自动插入3个组套类别的值,如果有了就return掉
	public void doSavePersonalCommonly(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();// 用户的机构ID
		String userId = user.getUserId() + "";// 暂时员工代码全部使用该方法替代
		body.put("YGDM", userId);
		body.put("JGID", jgid);
		String sql = "SSLB=:SSLB and YGDM=:YGDM and JGID=:JGID and ZTLB=:ZTLB and KSDM is null ";
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("SSLB", 4);
		parameters.put("ZTLB", 4);
		parameters.put("YGDM", userId);
		parameters.put("JGID", jgid);
		try {
			Long l = dao.doCount("YS_MZ_ZT01", sql, parameters);
			if (l > 0)
				return;
			dao.doSave("create", "phis.application.cic.schemas.YS_MZ_ZT01_CF",
					body, false);
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	@SuppressWarnings("unchecked")
	public void saveClinicMedicalDetail(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> JKCF = (Map<String, Object>) body.get("JKCF");
		String JLXH = body.get("JLXH") + "";
		body.remove(JKCF);
		List<Map<String, Object>> updateList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> createList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> removeList = new ArrayList<Map<String, Object>>();
		try {
			Map<String, Object> reBody = dao.doSave("update", GY_BLMB_Y, body,
					true);
			List cnd = CNDHelper.createSimpleCnd("eq", "JLXH", "s", JLXH);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					PUB_PelpleHealthTeach_MB);
			for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
				String key = (String) it.next();
				Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
				boolean isUpdate = false;
				m.put("JLXH", JLXH);
				for (Map<String, Object> map : list) {
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						m.put("id", map.get("id"));
						isUpdate = true;
						break;
					}
				}
				if (isUpdate == true) {
					updateList.add(m);
				} else {
					createList.add(m);
				}
			}
			for (Map<String, Object> map : list) {
				boolean isRemove = true;
				for (Iterator it = JKCF.keySet().iterator(); it.hasNext();) {
					String key = (String) it.next();
					Map<String, Object> m = (Map<String, Object>) JKCF.get(key);
					if (m.get("diagnoseId").equals(map.get("diagnoseId"))) {
						isRemove = false;
						break;
					}
				}
				if (isRemove == true) {
					removeList.add(map);
				}
			}
			list.clear();
			for (Map<String, Object> map : createList) {
				Map<String, Object> result = dao.doSave("create",
						PUB_PelpleHealthTeach_MB, map, true);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : updateList) {
				Map<String, Object> result = dao.doSave("update",
						PUB_PelpleHealthTeach_MB, map, true);
				result.putAll(map);
				list.add(result);
			}
			for (Map<String, Object> map : removeList) {
				dao.doRemove(map.get("id"), PUB_PelpleHealthTeach_MB);
			}
			reBody.put("JKCFRecords", list);
			res.put("body", reBody);
		} catch (ValidateException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error("Save failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败.");
		}

	}

	public void loadClinicMedicalDetail(String pkey, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ModelDataOperationException {
		try {
			Map<String, Object> result = dao.doLoad(GY_BLMB_Y, pkey);
			List cnd = CNDHelper.createSimpleCnd("eq", "JLXH", "s", pkey);
			List<Map<String, Object>> list = dao.doList(cnd, "recordId",
					PUB_PelpleHealthTeach_MB);
			result.put("JKCFRecords", list);
			res.put("body", result);
		} catch (PersistentDataOperationException e) {
			logger.error("load failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载病历模版失败.");
		}

	}
}
