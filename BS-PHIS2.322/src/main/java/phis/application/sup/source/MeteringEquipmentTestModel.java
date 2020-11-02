package phis.application.sup.source;

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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.validator.ValidateException;

/**
 * 量设备检定model
 * 
 * @author Administrator
 * 
 */
public class MeteringEquipmentTestModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(MeteringEquipmentTestModel.class);

	public MeteringEquipmentTestModel(BaseDAO dao) {
		this.dao = dao;
	}

	public int parseInt(Object o) {
		if (o == null || "".equals(o)) {
			return 0;
		}
		return Integer.parseInt(o + "");
	}

	public long parseLong(Object o) {
		if (o == null || "".equals(o)) {
			return 0L;
		}
		return Long.parseLong(o + "");
	}

	public double parseDouble(Object o) {
		if (o == null || "".equals(o)) {
			return new Double(0);
		}
		return Double.parseDouble(o + "");
	}

	/**
	 * 查询左边List
	 * 
	 * @author caijy
	 * @createDate 2014-3-5
	 * @description
	 * @updateInfo
	 * @return
	 * @throws ModelDataOperationException
	 */
	public List<Map<String, Object>> queryLeftList(List<Object> cnds,
			Context ctx) throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		hql.append("SELECT a.JLXH as JLXH,a.JGID as JGID,a.KFXH as KFXH,a.JLBH as JLBH,a.ZBXH as ZBXH,a.WZXH as WZXH,a.CJXH as CJXH,a.KSDM as KSDM,a.DWXH as DWXH,a.CCBH as CCBH,a.WZDW as WZDW,a.WZDJ as WZDJ,a.GRRQ as GRRQ,a.GRGH as GRGH,a.JLQJFL as JLQJFL,a.JLLB as JLLB,a.CLFW as CLFW,a.ZQDJ as ZQDJ,a.FDZ as FDZ,a.JDZQ as JDZQ,a.JDRQ as JDRQ,1 as JDJG,a.QJBZ as QJBZ,a.XCJD as XCJD,a.DQJDXH as DQJDXH,a.ZFBZ as ZFBZ,a.DDMC as DDMC,a.CCRQ as CCRQ,1 as JDJL, a.HGZH as HGZH,a.SJQD as SJQD, b.PYDM as PYDM,b.WZMC as WZMC, a.BGGH as BGGH  FROM WL_JLXX a LEFT JOIN WL_WZZD b ON B.WZXH = a.WZXH WHERE ( a.KFXH = :kfxh ) AND( a.ZFBZ = 0 )");
		StringBuffer hql_ksmc = new StringBuffer();
		hql_ksmc.append("select KSMC as KSMC from GY_KSDM where KSDM=:ksdm");// 查询科室名称
		List<Map<String, Object>> ret = null;
		try {
			if (cnds != null) {
				hql.append(" and ").append(ExpRunner.toString(cnds, ctx));
			}
			Map<String, Object> map_par = new HashMap<String, Object>();
			UserRoleToken user = UserRoleToken.getCurrent();
			int kfxh = 0;
			if (user.getProperty("treasuryId") != null
					&& user.getProperty("treasuryId") != "") {
				kfxh = parseInt(user.getProperty("treasuryId"));// 用户的机构ID
			}
			map_par.put("kfxh", kfxh);
			ret = dao.doSqlQuery(hql.toString(), map_par);
			for (Map<String, Object> map : ret) {
				if (map.get("KSDM") != null && parseLong(map.get("KSDM")) != 0) {
					Map<String, Object> map_par_ksmc = new HashMap<String, Object>();
					map_par_ksmc.put("ksdm", parseLong(map.get("KSDM")));
					Map<String, Object> map_ksmc = dao.doLoad(
							hql_ksmc.toString(), map_par_ksmc);
					map.put("KSMC", map_ksmc.get("KSMC") + "");
				}
			}
			SchemaUtil.setDictionaryMassageForList(ret,
					BSPHISEntryNames.WL_JLXX_DETAILLIST);
		} catch (ExpException e) {
			logger.error("查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		} catch (PersistentDataOperationException e) {
			logger.error("查询失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
		return ret;
	}

	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-6
	 * @description 保存检定信息
	 * @updateInfo
	 * @param body
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void saveMeteringEquipmentTest(Map<String, Object> body, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> map_jdxx = (Map<String, Object>) body.get("jdxx");
		List<Map<String, Object>> list_jlxx = (List<Map<String, Object>>) body
				.get("jlxx");
		try {
			for (Map<String, Object> map_jlxx : list_jlxx) {
				Map<String, Object> jdxx = new HashMap<String, Object>();
				jdxx.putAll(map_jdxx);
				jdxx.remove("JDXH");
				jdxx.put("JLXH", parseLong(map_jlxx.get("JLXH")));
				if (map_jlxx.get("JDJL") != null) {
					jdxx.put("JDJL", parseLong(map_jlxx.get("JDJL")));
				} else {
					jdxx.put("JDJL", 1);
				}
				if (map_jlxx.get("JDJG") != null) {
					jdxx.put("JDJG", parseLong(map_jlxx.get("JDJG")));
				} else {
					jdxx.put("JDJG", 1);
				}
				if (map_jlxx.get("HGZH") != null) {
					jdxx.put("HGZH", map_jlxx.get("HGZH"));
				}
				UserRoleToken user = UserRoleToken.getCurrent();
				int kfxh = 0;
				if (user.getProperty("treasuryId") != null
						&& user.getProperty("treasuryId") != "") {
					kfxh = parseInt(user.getProperty("treasuryId"));// 用户的机构ID
				}
				jdxx.put("KFXH", kfxh);
				dao.doSave("create", BSPHISEntryNames.WL_JDXX, jdxx, false);
			}
		} catch (ValidateException e) {
			logger.error("保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		} catch (PersistentDataOperationException e) {
			logger.error("保存失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "保存失败");
		}
	}

}
