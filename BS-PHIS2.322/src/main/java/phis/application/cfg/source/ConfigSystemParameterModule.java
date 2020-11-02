package phis.application.cfg.source;

import java.util.HashMap;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;

import ctd.account.UserRoleToken;

import ctd.service.core.Service;

public class ConfigSystemParameterModule {
	protected BaseDAO dao;

	public ConfigSystemParameterModule() {
	}

	public ConfigSystemParameterModule(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * 查询住院发票是不是不止维护一个发票
	 * 
	 * @param req
	 * @param res
	 * @throws ModelDataOperationException
	 */
	public void doQueryAblePjGyQy(Map<String, Object> req,
			Map<String, Object> res) throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		UserRoleToken user = UserRoleToken.getCurrent();
		parameters.put("JGID", user.getManageUnit().getId());
		String csmc = req.get("CSMC") + "";
		String masg = "票据号码已经维护了多个号码段,该参数不能设置为支持公有";
		if ("ZYFPSFZCGY".equals(csmc)) {
			parameters.put("PJLX", 1);
			masg = "住院发票号码已经维护了多个号码段,该参数不能设置为支持公有";
		} else if ("JKSJSFZCGY".equals(csmc)) {
			parameters.put("PJLX", 2);
			masg = "住院缴款收据已经维护了多个号码段,该参数不能设置为支持公有";
		}
		try {
			Long l = dao.doCount("ZY_YGPJ",
					"JGID=:JGID and PJLX=:PJLX and SYBZ=0", parameters);
			if (l > 1) {
				res.put(Service.RES_CODE, 600);
				res.put(Service.RES_MESSAGE, masg);
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "查询失败");
		}
	}
}
