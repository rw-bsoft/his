/**
 * @description 收费项目
 * 
 * @author shiwy 2012.06.29
 */
package phis.application.fsb.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class JcOutPharmacySetService extends AbstractActionService implements
		DAOSupportable {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(JcOutPharmacySetService.class);

	@SuppressWarnings("unchecked")
	public void doSaveCommit(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req
				.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();
		int GNFL = 4;
		try {
			Map<String, Object> parameters = new HashMap<String, Object>();
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> qxkz = (Map<String, Object>) body.get(i);
				if (body.get(i).get("YFSB") == null
						|| body.get(i).get("YFSB") == "") {
					throw new RuntimeException("药房不能为空！");
				}
				if (body.get(i).get("ZXPB") == null
						|| body.get(i).get("ZXPB") == "") {
					throw new RuntimeException("注销不能为空！");
				}
				if (body.get(i).get("TYPE") == null
						|| body.get(i).get("TYPE") == "") {
					throw new RuntimeException("医嘱类型不能为空！");
				}
				if (body.get(i).get("DMSB") == null
						|| body.get(i).get("DMSB") == "") {
					throw new RuntimeException("药品类型不能为空！");
				}
				long YFSB = Long.parseLong(body.get(i).get("YFSB") + "");
				int TYPE = Integer.parseInt(body.get(i).get("TYPE") + "");
				String DMSB = body.get(i).get("DMSB") + "";
				int ZXPB = Integer.parseInt(body.get(i).get("ZXPB") + "");
				parameters.put("TYPE", TYPE);
				parameters.put("DMSB", DMSB);
				parameters.put("JGID", JGID);
				parameters.put("GNFL", GNFL);

				if (body.get(i).get("JLXH") == null
						|| body.get(i).get("JLXH") == "") {
					qxkz.put("GNFL", 4);
					qxkz.put("JGID", JGID);
					qxkz.put("ZXPB", ZXPB);
					qxkz.put("YFSB", YFSB);
					qxkz.put("TYPE", TYPE);
					qxkz.put("DMSB", DMSB);
					dao.doSave("create", BSPHISEntryNames.JC_FYYF, qxkz, false);
				} else {
					long JLXH = Long.parseLong(body.get(i).get("JLXH") + "");
					dao.doUpdate("update JC_FYYF set GNFL=" + GNFL + " ,TYPE="
							+ TYPE + " , YFSB=" + YFSB + " , DMSB='" + DMSB
							+ "'" + " WHERE JLXH=" + JLXH + "", null);

				}

			}

		} catch (PersistentDataOperationException e) {
			throw new ServiceException("保存失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "Success");
	}

	@SuppressWarnings("unchecked")
	public void doUpdateStage(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {

		Map<String, Object> body = (Map<String, Object>) req.get("body");
		long JLXH = Long.parseLong(body.get("JLXH") + "");
		int ZXPB = Integer.parseInt(body.get("ZXPB") + "");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnitId();

		try {
			dao.doUpdate("update JC_FYYF set ZXPB=" + ZXPB + " where JLXH="
					+ JLXH + " and JGID='" + JGID + "'", null);

		} catch (PersistentDataOperationException e) {
			throw new ServiceException("注销失败！", e);
		}
		res.put(RES_CODE, ServiceCode.CODE_OK);
		res.put(RES_MESSAGE, "Success");

	}

}
