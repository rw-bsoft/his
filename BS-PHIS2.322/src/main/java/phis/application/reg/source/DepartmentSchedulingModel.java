package phis.application.reg.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.SchemaUtil;
import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.util.context.Context;
import ctd.validator.ValidateException;

public class DepartmentSchedulingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(DepartmentSchedulingModel.class);

	public DepartmentSchedulingModel(BaseDAO dao) {
		this.dao = dao;
	}

	//科室排班查询
	public void doGetModelDataOperation(Map<String, Object> req,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		String queryCnd = null;
		if (req.containsKey("cnd")) {
			queryCnd = (String) req.get("cnd");
		}
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		int ghrq = Integer.parseInt(queryCnd.substring(0, 1));
		int zblb = Integer.parseInt(queryCnd.substring(2));
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listsize = new ArrayList<Map<String, Object>>();

		Map<String, Object> parameters = new HashMap<String, Object>();
		Map<String, Object> parameterssize = new HashMap<String, Object>();

		String sql = "select c.MZMC as MZMC,a.KSDM as GHKS,a.KSMC as KSMC,1 as KSPB,a.JGID as JGID,b.GHXE as GHXE," +
				" b.YYXE as YYXE,b.YGRS as YGRS,b.YYRS as YYRS,b.JZXH as JZXH,b.TGBZ as TGBZ,b.YYKSSJ as YYKSSJ," +
				" b.YYJSSJ as YYJSSJ,b.YYJG as YYJG " +
				" from MS_GHKS a,MS_KSPB b,MS_MZLB c where a.KSDM=b.GHKS and b.GHRQ=:GHRQ and b.ZBLB=:ZBLB" +
				" and a.MZLB=c.MZLB and a.JGID=:JGID UNION SELECT b.MZMC as MZMC,a.KSDM as GHKS,a.KSMC as KSMC," +
				" 0 as KSPB,a.JGID as JGID,0 as GHXE,0 as YYXE,0 as YGRS,0 as YYRS,0 as JZXH,0 as TGBZ," +
				" '' as YYKSSJ,'' as YYJSSJ,'' as YYJG " +
				" FROM MS_GHKS a,MS_MZLB b WHERE a.MZLB=b.MZLB and a.JGID = :JGID and a.KSDM " +
				" not in (select GHKS FROM MS_KSPB WHERE JGID = :JGID and GHRQ=:GHRQ and ZBLB=:ZBLB)";
		parameters.put("JGID", manaUnitId);
		parameters.put("GHRQ", ghrq);
		parameters.put("ZBLB", zblb);
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		parameterssize.put("JGID", manaUnitId);
		parameterssize.put("GHRQ", ghrq);
		parameterssize.put("ZBLB", zblb);
		try {
			list = dao.doSqlQuery(sql, parameters);
			listsize = dao.doSqlQuery(sql, parameterssize);
			SchemaUtil.setDictionaryMassageForList(list, "phis.application.reg.schemas.MS_KSPB");
			res.put("totalCount", Long.parseLong(listsize.size() + ""));
			res.put("body", list);
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 科室排班保存 *@param req
	 * 
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDepartmentScheduling(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		try {
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> map_ = body.get(i);// 一条一条放到map_中
				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("GHKS", Long.parseLong(map_.get("GHKS") + ""));
				parameters.put("ZBLB", Integer.parseInt(map_.get("ZBLB") + ""));
				parameters.put("GHRQ", Integer.parseInt(map_.get("GHRQ") + ""));
				parameters.put("JGID", map_.get("JGID"));
				dao.doUpdate(
						"delete from MS_KSPB where GHKS=:GHKS and ZBLB=:ZBLB and GHRQ=:GHRQ and JGID=:JGID",
						parameters);
				if (!"0".equals(map_.get("KSPB"))) {
					dao.doSave("create", BSPHISEntryNames.MS_KSPB, map_, false);
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

}
