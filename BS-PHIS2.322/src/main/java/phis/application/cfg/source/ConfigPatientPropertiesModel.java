/**
 * @description 病人性质
 * 
 * @author zhangyq 2012.05.30
 */
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
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class ConfigPatientPropertiesModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigPatientPropertiesModel.class);

	public ConfigPatientPropertiesModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 自费比例保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doSaveOwnExpenseProportion(List<Map<String, Object>> body,
			int BRXZ) throws ModelDataOperationException {
		try {
			int body_size = body.size();
			Map<String, Object> parameters_BRXZ = new HashMap<String, Object>();
			parameters_BRXZ.put("BRXZ", Long.parseLong(BRXZ + ""));
			dao.doUpdate("DELETE FROM GY_ZFBL WHERE BRXZ=:BRXZ", parameters_BRXZ);
			for (int i = 0; i < body_size; i++) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("SFXM", body.get(i).get("SFXM"));
				parameters.put("BRXZ", body.get(i).get("BRXZ"));
				// 自付比例保存到数据库时缩小100倍,存储格式为 0.123
				parameters
						.put("ZFBL",
								(Double.parseDouble(body.get(i).get("ZFBL")
										+ "")) / 100);
				dao.doSave("create", BSPHISEntryNames.GY_ZFBL, parameters,
						false);
			}
		} catch (ValidateException e) {
			logger.error(
					"Reception to be validated: conceited proportion failed to save.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "前台验证：自负比例保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: conceited proportion failed to save.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：自负比例保存失败.");
		}
	}

	/**
	 * 自费比例查询
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected List<Map<String, Object>> doQueryOwnExpenseProportion(int BRXZ)
			throws ModelDataOperationException {
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("BRXZ", Long.parseLong(BRXZ + ""));
			return dao
					.doSqlQuery(
							"SELECT a.SFXM as SFXM,a.SFMC as SFXM_text,b.ZFBL*100 as ZFBL,b.BRXZ as BRXZ FROM GY_SFXM a,GY_ZFBL b WHERE a.SFXM = b.SFXM and (b.BRXZ=:BRXZ) UNION SELECT SFXM,SFMC AS SFXM_text,100 AS ZFBL,"
									+ BRXZ
									+ " AS BRXZ FROM GY_SFXM WHERE SFXM NOT IN (SELECT SFXM FROM GY_ZFBL WHERE BRXZ =:BRXZ )",
							parameters);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: conceited proportion failed to save.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：自负比例保存失败.");
		}
	}

	/**
	 * 病人性质保存。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected Map<String, Object> doSavePatientNature(String op,
			Map<String, Object> body) throws ModelDataOperationException {
		try {
			Map<String, Object> req = null;

			req = dao.doSave(op, BSPHISEntryNames.GY_BRXZ, body, false);
			if ("0".equals(body.get("GSXZ") + "")) {
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("GSXZ", Long.parseLong(req.get("BRXZ") + ""));
				dao.doUpdate("update GY_BRXZ set GSXZ=:GSXZ where BRXZ=:GSXZ",
						parameters);
			}
			if ("create".equals(op)) {
				List<Map<String, Object>> list = dao.doQuery(
						"SELECT SFXM as SFXM FROM GY_SFXM ", null);
				for (int i = 0; i < list.size(); i++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("SFXM",
							Long.parseLong(list.get(i).get("SFXM") + ""));
					map.put("BRXZ", Long.parseLong(req.get("BRXZ") + ""));
					map.put("ZFBL", 1);
					dao.doSave("create", BSPHISEntryNames.GY_ZFBL, map, false);
				}
			}
			return req;
		} catch (ValidateException e) {
			logger.error(
					"Reception to be validated: save the patient the nature of failure.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "前台验证：病人性质保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: save the patient the nature of failure.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：病人性质保存失败.");
		}
	}

	/**
	 * 病人性质删除。
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public Map<String, Object> doRemovePatientNature(int pkey,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("BRXZ", Long.parseLong(pkey + ""));
			Map<String, Object> brxzmap = new HashMap<String, Object>();
			brxzmap.put("BRXZ", pkey + "");
			Long l = dao.doCount("MS_BRDA", "BRXZ=:BRXZ", brxzmap);
			if (l > 0) {
				res.put(Service.RES_CODE, 605);
				res.put(Service.RES_MESSAGE, "该病人性质已使用,不能删除!");
			} else {
				dao.doUpdate("DELETE FROM GY_ZFBL WHERE BRXZ=:BRXZ", map);
				dao.doUpdate("DELETE FROM GY_YPJY  WHERE BRXZ=:BRXZ", map);
				dao.doUpdate("DELETE FROM GY_FYJY WHERE BRXZ=:BRXZ", map);
				dao.doRemove(Long.parseLong(pkey + ""),
						BSPHISEntryNames.GY_BRXZ);
			}
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background validation: remove the patient the nature of the failure of.",
					e);
			throw new ModelDataOperationException("后台验证：删除病人性质失败.", e);
		}
		return null;
	}

	/**
	 * 药品限制和费用限制保存
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> doSaveLimit(String tableName,
			Map<String, Object> body, String fName, String fValue)
			throws ModelDataOperationException {
		try {
			/*
			 * if ("GY_YPJY".equals(tableName)) { dao.removeByFieldValue(fName,
			 * fValue, BSPHISWEntryNames.GY_YPJY); } if
			 * ("GY_FYJY".equals(tableName)) { dao.removeByFieldValue(fName,
			 * fValue, BSPHISWEntryNames.GY_FYJY); }
			 */
			List<Map<String, Object>> data = (List<Map<String, Object>>) body
					.get("data");
			for (int i = 0; i < data.size(); i++) {
				if ("GY_YPJY".equals(tableName)) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("BRXZ",
							Long.parseLong(data.get(i).get("BRXZ") + ""));
					parameters.put("YPXH",
							Long.parseLong(data.get(i).get("YPXH") + ""));
					dao.doUpdate(
							"delete from GY_YPJY where BRXZ=:BRXZ and YPXH=:YPXH",
							parameters);
					// dao.removeByFieldValue(fName, fValue,
					// BSPHISWEntryNames.GY_YPJY);
					data.get(i)
							.put("ZFBL",
									Double.parseDouble(data.get(i).get("ZFBL")
											+ "") / 100);
					dao.doSave("create", BSPHISEntryNames.GY_YPJY, data.get(i),
							false);
				}
				if ("GY_FYJY".equals(tableName)) {
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("BRXZ",
							Long.parseLong(data.get(i).get("BRXZ") + ""));
					parameters.put("FYXH",
							Long.parseLong(data.get(i).get("FYXH") + ""));
					dao.doUpdate(
							"delete from GY_FYJY where BRXZ=:BRXZ and FYXH=:FYXH",
							parameters);
					data.get(i)
							.put("ZFBL",
									Double.parseDouble(data.get(i).get("ZFBL")
											+ "") / 100);
					data.get(i)
							.put("CXBL",
									Double.parseDouble(data.get(i).get("CXBL")
											+ "") / 100);
					dao.doSave("create", BSPHISEntryNames.GY_FYJY, data.get(i),
							false);
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
		return null;
	}

	/**
	 * 药品限制和费用限制list
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	protected List<Object> doListLimit(String tableName,
			Map<String, Object> body, List<Object> cnd,
			Map<String, Object> parameters) throws ModelDataOperationException {
		try {
			parameters.put("BRXZ", Long.parseLong(body.get("BRXZ") + ""));
			Map<String, Object> counparameters = new HashMap<String, Object>();
			counparameters.put("BRXZ", Long.parseLong(body.get("BRXZ") + ""));
			List<Object> rs = new ArrayList<Object>();
			int total = 0;
			List<Map<String, Object>> inofList = new ArrayList<Map<String, Object>>();
			if ("GY_YPJY".equals(tableName)) {
				String hql = "select a.BRXZ as BRXZ,a.YPXH as YPXH,b.YPMC as YPMC,b.YPGG as YPGG,b.YPDW as YPDW,a.ZFBL*100 as ZFBL,a.YPXE as YPXE from "
						+ " GY_YPJY a,"
						+ " YK_TYPK b where a.YPXH=b.YPXH and a.BRXZ=:BRXZ";
				String countHql = "select count(*) as NUM from  GY_YPJY a,"
						+ " YK_TYPK b where a.YPXH=b.YPXH and a.BRXZ=:BRXZ";
				if (cnd != null) {
					if ("like".equals(cnd.get(0) + "")) {
						hql += " and b.PYDM like '"
								+ ((List<Object>) cnd.get(2)).get(1) + "'";
						countHql += " and b.PYDM like '"
								+ ((List<Object>) cnd.get(2)).get(1) + "'";
					}
				}
				hql += " order by a.YPXH desc";
				List<Map<String, Object>> countList = dao.doQuery(countHql,
						counparameters);
				inofList = dao.doQuery(hql, parameters);
				if (countList != null) {
					total = Integer.parseInt(countList.get(0).get("NUM") + "");
				}
			}
			if ("GY_FYJY".equals(tableName)) {
				String hql = "select a.BRXZ as BRXZ,a.FYXH as FYXH,b.FYMC as FYMC,b.FYDW as FYDW,a.ZFBL*100 as ZFBL,a.FYXE as FYXE,a.CXBL*100 as CXBL from "
						+ " GY_FYJY a,"
						+ " GY_YLSF b where a.FYXH=b.FYXH and a.BRXZ=:BRXZ";
				String countHql = "select count(*) as NUM from  GY_FYJY a,"
						+ " GY_YLSF b where a.FYXH=b.FYXH and a.BRXZ=:BRXZ";
				if (cnd != null) {
					if ("like".equals(cnd.get(0) + "")) {
						hql += " and b.PYDM like '"
								+ ((List<Object>) cnd.get(2)).get(1) + "'";
						countHql += " and b.PYDM like '"
								+ ((List<Object>) cnd.get(2)).get(1) + "'";
					}
				}
				hql += " order by a.FYXH desc";
				inofList = dao.doQuery(hql, parameters);
				parameters.remove("first");
				parameters.remove("max");
				List<Map<String, Object>> countList = dao.doQuery(countHql,
						parameters);
				if (countList != null) {
					total = Integer.parseInt(countList.get(0).get("NUM") + "");
				}
			}
			rs.add(total);
			rs.add(inofList);
			return rs;
		} catch (PersistentDataOperationException e) {
			logger.error("Failed to load.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "加载失败.");
		}
	}

	/**
	 * 药品限制和费用限制删除
	 * 
	 * @param jsonReq
	 * @param jsonRes
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected Map<String, Object> doRemoveLimit(String tableName,
			Map<String, Object> body) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("BRXZ", Long.parseLong(body.get("BRXZ") + ""));
		if (body.get("FYXH") != null) {
			parameters.put("FYXH", Long.parseLong(body.get("FYXH") + ""));
		} else {
			parameters.put("YPXH", Long.parseLong(body.get("YPXH") + ""));
		}
		try {
			if ("GY_YPJY".equals(tableName)) {
				dao.doUpdate("delete from GY_YPJY where BRXZ=:BRXZ and YPXH=:YPXH", parameters);
			}
			if ("GY_FYJY".equals(tableName)) {
				dao.doUpdate("delete from GY_FYJY where BRXZ=:BRXZ and FYXH=:FYXH", parameters);
			}
		} catch (PersistentDataOperationException e) {
			logger.error("Delete failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "删除失败.");
		}
		return null;
	}
}
