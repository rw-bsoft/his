/**
 * @(#)AdvancedSearchService.java Created on 2009-8-10 下午04:08:08
 * 
 * 版权：版权所有 bsoft.com.cn 保留所有权力。
 */
package phis.application.reg.source;

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

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

/**
 * @description 医生排班
 * 
 * @author shiwy 2012.08.28
 */
public class OfficeDataModule implements BSPHISEntryNames {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(OfficeDataModule.class);

	public OfficeDataModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 根据星期几和上午下午去科室排班查询科室
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */

	public void doGetOfficeData(Map<String, Object> body,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnit().getId();
		try {
			parameters.put("GHRQ", Integer.parseInt(body.get("day")+""));
			parameters.put("ZBLB", Integer.parseInt(body.get("dayInt")+""));
			parameters.put("JGID", jgid);
			String hql="select a.KSDM as value,a.KSMC as text from MS_GHKS a,MS_KSPB b where a.KSDM = b.GHKS and a.JGID = b.JGID and b.GHRQ =:GHRQ and b.ZBLB =:ZBLB and b.JGID =:JGID";
			List<Map<String, Object>> list=dao.doQuery(hql, parameters);
			res.put("offices", list);
		} catch (PersistentDataOperationException e) {
			logger.error("remove failed.", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "获取科室失败.");
		}

	}
}
