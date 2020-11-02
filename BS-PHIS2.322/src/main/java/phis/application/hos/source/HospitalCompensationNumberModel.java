package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.util.context.Context;

public class HospitalCompensationNumberModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(HospitalCompensationNumberModel.class);

	public HospitalCompensationNumberModel(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 补号码
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetCompensationNumber(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnitId();// 用户的机构ID
		String zyhm = BSPHISUtil.get_public_fillleft(req.get("ZYHM") + "", "0",
				BSPHISUtil.getRydjNo(manaUnitId, "ZYHM", "", dao).length());
		res.put("ZYHM", zyhm);
	}
}
