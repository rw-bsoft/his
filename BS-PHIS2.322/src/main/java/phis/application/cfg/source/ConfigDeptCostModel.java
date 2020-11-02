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
import phis.source.utils.SchemaUtil;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.util.exp.ExpException;
import ctd.util.exp.ExpRunner;
import ctd.util.exp.ExpressionProcessor;
import ctd.validator.ValidateException;

/**
 * @description 机构收费项目维护费用信息model
 * 
 * @author zhangyq 2012.05
 */
public class ConfigDeptCostModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ConfigDeptCostModel.class);

	public ConfigDeptCostModel(BaseDAO dao) {
		this.dao = dao;
	}

	// 费用限用保存
	public Map<String, Object> doSaveCost(String fyxh,
			List<Map<String, Object>> body) throws ModelDataOperationException {
		try {
			Map<String, Object> req = null;
			dao.removeByFieldValue("FYXH", Integer.parseInt(fyxh),
					BSPHISEntryNames.GY_FYJY);
			for (int i = 0; i < body.size(); i++) {
				req = dao.doSave("create", BSPHISEntryNames.GY_FYJY,
						body.get(i), false);// 费用限用保存GY_FYJY
			}
			return req;
		} catch (ValidateException e) {
			logger.error(
					"reception to be validated：Costs restricted the save fails.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "前台验证：费用限用保存失败.");
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification ：costs restricted the save fails.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：费用限用保存失败.");
		}
	}

	/**
	 * 机构项目调入查询
	 * 
	 * @param FYGB
	 * @param JGID
	 * @param ctx
	 * @return
	 */
	public List<Object> doCostCallList(String FYGB, List<Object> cnds,
			Map<String, Object> parameters, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			parameters.put("JGID", manaUnitId);
			parameters.put("FYGB", Integer.parseInt(FYGB));
			Map<String, Object> parameters1 = new HashMap<String, Object>();
			parameters1.put("JGID", manaUnitId);
			parameters1.put("FYGB", Integer.parseInt(FYGB));
			String hql = "select FYXH as FYXH,FYMC as FYMC,FYDW as FYDW,PYDM as PYDM,MZSY as MZSY,ZYSY as ZYSY,YJSY as YJSY,TJFY as TJFY,TXZL as TXZL,WBDM as WBDM,JXDM as JXDM,QTDM as QTDM,FYGB as FYGB,ZFPB as ZFPB,XMBM as XMBM,BZJG as BZJG,XMLX as XMLX,YJJK as YJJK,JCSQ as JCSQ,MZSQ as MZSQ,TSTS as TSTS,LISLX as LISLX,XMFL as XMFL from GY_YLSF a where a.FYGB=:FYGB and a.ZFPB = 0 and a.FYXH not in (select FYXH from "
					+ " GY_YLMX  where JGID=:JGID)";
			String countHql = "select count(*) as NUM from GY_YLSF a where a.FYGB=:FYGB and a.ZFPB = 0 and a.FYXH not in (select FYXH from "
					+" GY_YLMX  where JGID=:JGID)";
			if (cnds != null) {
				String where = " and " + ExpressionProcessor.instance().toString(cnds);
				hql += where;
				countHql += where;
			}
			hql += " order by a.FYXH desc";
			List<Object> rs = new ArrayList<Object>();
			List<Map<String, Object>> countList = dao.doQuery(countHql,
					parameters1);
			List<Map<String, Object>> inofList = dao.doQuery(hql, parameters);
			int total = 0;
			if (countList != null) {
				total = Integer.parseInt(countList.get(0).get("NUM") + "");
			}
			rs.add(total);
			rs.add(inofList);
			return rs;
		} catch (ExpException e) {
			logger.error(
					"Reception to be validated: Get the import cost information failed.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "前台验证：获取未导入费用信息失败.");
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: Get the import cost information failed.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：获取未导入费用信息失败.");
		}
	}

	// 机构费用调入
	public int doSaveCallin(List<Object> body, Context ctx)
			throws ModelDataOperationException {
		try {
			UserRoleToken user = UserRoleToken.getCurrent();
			String manaUnitId = user.getManageUnitId();// 用户的机构ID
			// Map<String, Object> parameters= new HashMap<String, Object>();
			// parameters.put("FYXH",body.toString().substring(1,body.toString().length()
			// - 1));
			int req = dao.doSqlUpdate(
					"insert into GY_YLMX (jgid,fyxh,fydj,zfpb,dzbl) select '"
							+ manaUnitId
							+ "',fyxh,BZJG,0,0 from GY_YLSF where fyxh in ("
							+ body.toString().substring(1,
									body.toString().length() - 1) + ")", null);// 机构费用保存GY_YLMX
			return req;
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: the institutional costs transferred to fail.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：机构费用调入失败.");
		}
	}

	// 机构费用全部调入
	// @SuppressWarnings("unchecked")
	public void doSaveCallinAll(String FYGB, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FYGB", Integer.parseInt(FYGB));
		parameters.put("JGID", manaUnitId);
		String sql = "insert into GY_YLMX (jgid,fyxh,fydj,zfpb,dzbl) select '"
				+ manaUnitId
				+ "',fyxh,nvl(BZJG,0),0,0 from GY_YLSF " +
				"where FYGB=:FYGB and ZFPB=0 and fyxh not in " +
				"(select a.fyxh from GY_YLSF a,GY_YLMX"
				+ " b where a.fyxh=b.fyxh and a.FYGB=:FYGB and b.jgid=:JGID)";
		try {
			dao.doSqlUpdate(sql, parameters);
		} catch (PersistentDataOperationException e) {
			logger.error(
					"Background verification: all the costs transferred to fail.",
					e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：费用全部调入失败.");
		}
	}

	// 项目明细from修改回填数据
	// @SuppressWarnings("unchecked")
	public Map<String, Object> doFromLoadItemDetails(String FYXH, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FYXH", Long.parseLong(FYXH));
		parameters.put("JGID", manaUnitId);
		Map<String, Object> rs = null;
		try {
			rs = dao.doLoad(
					"select a.JGID as JGID,a.ZFPB as ZFPB,a.FYXH as FYXH,a.FYKS as FYKS,a.FYDJ as FYDJ,a.DZBL as DZBL,a.JGBZ as JGBZ,b.FYMC as FYMC,b.WJBM as WJBM,b.FYGB as FYGB,b.FYDW as FYDW,b.PYDM as PYDM,b.MZSY as MZSY,b.ZYSY as ZYSY,b.YJSY as YJSY,b.TJFY as TJFY,b.TXZL as TXZL,b.WBDM as WBDM,b.JXDM as JXDM,b.XMLX as XMLX,b.QTDM as QTDM,b.BZJG as BZJG,b.XMBM as XMBM,b.BHDM as BHDM,a.ZDCR as ZDCR from "
							+ " GY_YLMX "
							+ " a,"
							+ " GY_YLSF "
							+ " b where a.FYXH=b.FYXH and a.JGID=:JGID and a.FYXH=:FYXH order by a.FYXH,a.JGID desc",
					parameters);
			rs = SchemaUtil.setDictionaryMassageForForm(rs,
					BSPHISEntryNames.GY_YLMX_DR);
		} catch (PersistentDataOperationException e) {
			logger.error("Background verification: Data backfill failure.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "后台验证：数据回填失败.");
		}
		return rs;
	}

	public int doLogoutProject(Map<String, Object> body)
			throws ModelDataOperationException {
		StringBuffer hql = new StringBuffer();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JGID", (String) body.get("JGID"));
		parameters.put("FYXH", Long.parseLong(body.get("FYXH") + ""));
		if ("1".equals(body.get("ZFPB") + "")) {
			parameters.put("ZFPB", 0);
		} else {
			parameters.put("ZFPB", 1);
		}
		int zfpb = (Integer) body.get("ZFPB");
		hql.append("update GY_YLMX ")
				.append(" set ZFPB=:ZFPB where FYXH=:FYXH and JGID=:JGID");
		try {
			dao.doUpdate(hql.toString(), parameters);
			return ServiceCode.CODE_OK;
		} catch (PersistentDataOperationException e) {
			if (zfpb == 0) {
				logger.error("取消注销失败", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医疗项目明细取消注销失败.");
			} else {
				logger.error("注销失败", e);
				throw new ModelDataOperationException(
						ServiceCode.CODE_DATABASE_ERROR, "医疗项目明细注销失败.");
			}
		}

	}
}
