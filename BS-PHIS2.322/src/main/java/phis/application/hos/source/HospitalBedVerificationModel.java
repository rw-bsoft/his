package phis.application.hos.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.application.ivc.source.TreatmentNumberModule;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.utils.BSPHISUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class HospitalBedVerificationModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(TreatmentNumberModule.class);

	public HospitalBedVerificationModel(BaseDAO dao) {
		this.dao = dao;
	}

	/*
	 * 床位分配前的验证
	 */
	public void doGetBedVerification(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		UserRoleToken user = UserRoleToken.getCurrent();
		String manaUnitId = user.getManageUnit().getId();
		int ll_brxb = 0;// 病人性别
		int ll_cwxb = 0;// 床位限别
		// 取病人及床位信息
		Map<String, Object> parametersbrryinfo = new HashMap<String, Object>();
		parametersbrryinfo.put("ZYH", Long.parseLong(body.get("ZYH") + ""));
		parametersbrryinfo.put("JGID", manaUnitId);
		try {
			Map<String, Object> brryinfomap = dao
					.doLoad("SELECT BRXB as BRXB,BRKS as BRKS,RYRQ as RYRQ FROM ZY_BRRY Where ZYH  = :ZYH  and JGID = :JGID",
							parametersbrryinfo);
			Map<String, Object> parameterscwszinfo = new HashMap<String, Object>();
			parameterscwszinfo.put("BRCH", body.get("BRCH"));
			parameterscwszinfo.put("JGID", manaUnitId);
			Map<String, Object> cwszinfomap = dao
					.doLoad("SELECT CWXB as CWXB,CWKS as CWKS,KSDM as KSDM,JCPB as JCPB FROM ZY_CWSZ Where BRCH = :BRCH and JGID = :JGID",
							parameterscwszinfo);
			ll_brxb = Integer.parseInt(brryinfomap.get("BRXB") + "");
			ll_cwxb = Integer.parseInt(cwszinfomap.get("CWXB") + "");
			if (ll_cwxb == 1 || ll_cwxb == 2) {
				if (ll_brxb != ll_cwxb) {
					res.put(Service.RES_CODE, 601);
				}
			} else {
				Map<String, Object> parametersrcjlcount = new HashMap<String, Object>();
				parametersrcjlcount.put("JGID", manaUnitId);
				parametersrcjlcount.put("ZYH",
						Long.parseLong(body.get("ZYH") + ""));
				Long l = dao.doCount("ZY_RCJL",
						"ZYH =:ZYH and JGID =:JGID and CZLX=1 and BQPB=0",
						parametersrcjlcount);
				if (l <= 0) {
					res.put(Service.RES_CODE, 602);
				} else {
					res.put(Service.RES_CODE, 603);
				}
			}
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 床位分配
	 * 
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveBedVerification(Map<String, Object> body,
			Map<String, Object> res, Context ctx)
			throws ModelDataOperationException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ZYH", body.get("ZYH"));
		parameters.put("BRCH", body.get("BRCH"));
		parameters.put("RYRQ", body.get("RYRQ"));
		BSPHISUtil.cwgl_cwfp(parameters, dao, ctx);
	}
}
