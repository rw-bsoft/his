package phis.application.ivc.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 
 * @description
 * 
 * @author <a href="mailto:gaof@bsoft.com.cn">gaof</a>
 */
public class ReportSettingModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public ReportSettingModel(BaseDAO dao) {
		this.dao = dao;
	}

	public void doBBMCQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();// 用户的机构ID

		Map<String, Object> parameters = new HashMap<String, Object>();

		StringBuffer sql = new StringBuffer();
		sql.append(
				" select  max(jgid) as jgid,bbbh,bbmc from"
						+ " (select * from gy_bbmc where jgid=")
				.append(uid)
				.append(" union "
						+ "select * from gy_bbmc where jgid=0) group by bbbh,bbmc order by bbbh");
		try {
			List<Map<String, Object>> inofList = dao.doSqlQuery(sql.toString(),
					parameters);

			res.put("body", inofList);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException("BBMC查询失败！", e);
		}
	}

	public void doXMGBQuery(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();// 用户的机构ID
		Integer BBBH = 0;
		if (req.get("cnd") != null) {
			BBBH = (Integer) req.get("cnd");
		}

		Map<String, Object> parameters = new HashMap<String, Object>();

		StringBuffer sql = new StringBuffer();
		sql.append(
				" SELECT "
						+ "GY_XMGB.GBXH,"
						+ "GY_SFXM.SFXM,GY_SFXM.SFMC,GY_XMGB.XMMC,GY_XMGB.SFXM,"
						+ " GY_XMGB.GBXM,GY_XMGB.SXH ,GY_XMGB.BBBH FROM GY_SFXM,GY_XMGB"
						+ " WHERE GY_SFXM.SFXM = GY_XMGB.SFXM and GY_XMGB.JGID = ")
				.append(JGID)
				.append(" and GY_XMGB.BBBH = ")
				.append(BBBH)
				.append(" UNION SELECT "
						+ "null as GBXH,"
						+ "GY_SFXM.SFXM,GY_SFXM.SFMC,GY_SFXM.SFMC AS XMMC,"
						+ " GY_SFXM.SFXM,GY_SFXM.SFXM AS GBXM,GY_SFXM.SFXM AS SXH ,")
				.append(BBBH)
				.append(" as BBBH"
						+ " FROM GY_SFXM WHERE GY_SFXM.SFXM NOT IN (SELECT GY_XMGB.SFXM "
						+ "FROM GY_XMGB WHERE GY_XMGB.JGID = ").append(JGID)
				.append(" and GY_XMGB.BBBH = ").append(BBBH).append(" )");
		try {
			List<Map<String, Object>> inofList = dao.doSqlQuery(sql.toString(),
					parameters);

			res.put("body", inofList);

		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException("XMGB查询失败！", e);
		}
	}

	@SuppressWarnings("unchecked")
	public void doXMGBSave(Map<String, Object> req, Map<String, Object> res,
			Context ctx) throws ServiceException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String uid = user.getManageUnitId();// 用户的机构ID

		List<Map<String, Object>> data = (List<Map<String, Object>>) req
				.get("body");
		try {
			for (Map<String, Object> xmgb : data) {
				// 更新数据库
				Integer gbxh = (Integer) xmgb.get("GBXH");
				if (gbxh == null || gbxh == 0) {
					// insert
					xmgb.put("JGID", uid);
					dao.doSave("create", BSPHISEntryNames.GY_XMGB, xmgb, false);
				} else {
					// update
					xmgb.put("JGID", uid);
					dao.doSave("update", BSPHISEntryNames.GY_XMGB, xmgb, false);
				}
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("保存失败！", e);
		}

	}
}
