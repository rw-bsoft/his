package phis.application.war.source;

import java.util.Date;
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

import ctd.account.UserRoleToken;
import ctd.util.context.Context;

/**
 * description:会诊意见DAO
 * 
 * @author:zhangfs create on 2013-5-29
 */
public class WardConsultationYjModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(WardConsultationYjModel.class);

	private static final String YS_ZY_HZYJ = "YS_ZY_HZYJ";

	public WardConsultationYjModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:保存会诊记录表单 add_by zhangfs
	 * 
	 * @param
	 * @return
	 */
	public Map<String, Object> save(Map<String, Object> req, Context ctx) throws ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String jgid = user.getManageUnitId();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("SQXH", body.get("SQXH"));// 申请序号
		map.put("SSYS", body.get("SSYS"));// 所属医生
		map.put("KSDM", body.get("DBKS"));// 代表科室
		map.put("SXYS", user.getUserId());// 签名医生
		map.put("SXSJ", new Date());// 签名时间
		map.put("HZYJ", body.get("HZYJ"));// 会诊意见
		map.put("JGID", jgid);// 机构ID
		Map<String, Object> parameters = new HashMap<String, Object>();
		int SQXH = Integer.parseInt(body.get("SQXH").toString());
		Map<String, Object> parameters2 = new HashMap<String, Object>();
		parameters2.put("HZYJ", body.get("HZYJ"));
		try {
			Map<String, Object> o = dao.doLoad("select JLXH as JLXH,SQXH as SQXH from YS_ZY_HZYJ where SQXH=" + SQXH,
					parameters);
			if (o != null) {
				int JLXH = Integer.parseInt(o.get("JLXH").toString());
				int le = dao.doUpdate("update YS_ZY_HZYJ set HZYJ =:HZYJ where JLXH =" + JLXH, parameters2);
			} else {
				Map<String, Object> map2 = dao.doInsert(BSPHISEntryNames.YS_ZY_HZYJ, map, false);
				return map2;
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public Map<String, Object> query(Map<String, Object> req, Context ctx) throws ModelDataOperationException {

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int SQXH = Integer.parseInt(body.get("SQXH").toString());

		Map<String, Object> parameters = new HashMap<String, Object>();

		try {
			Map<String, Object> o = dao.doLoad(
					"select KSDM as KSDM,SSYS as SSYS, JLXH as JLXH,HZYJ as HZYJ from YS_ZY_HZYJ where SQXH=" + SQXH,
					parameters);

			if (o != null) {
				return o;
			}
			SchemaUtil.setDictionaryMassageForForm(o, BSPHISEntryNames.YS_ZY_HZYJ);
		} catch (PersistentDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
