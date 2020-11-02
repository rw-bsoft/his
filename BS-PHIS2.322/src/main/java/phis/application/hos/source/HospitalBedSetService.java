package phis.application.hos.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class HospitalBedSetService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(HospitalBedInfoService.class);

	@SuppressWarnings("unchecked")
	public void doSaveBed(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws  ServiceException {
		String op = (String) req.get("op");
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		int code = 200;
		String msg = "床位保存成功";
		try {
			HospitalBedSetModel cpm = new HospitalBedSetModel(dao);
			res.put("body", cpm.doSaveBed(op,body));
			String num = (String) (((Map<String,Object>)res.get("body")).get("NUM"));
			if("1".equals(num) && "create".equals(op)){
				code = 400;
				msg = "该床位号码已存在，数据保存失败！";
			}
			if("-1".equals(num)){
				code = 400;
				msg = "该床位号码已存在，请修改！";
			}
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		res.put(RES_CODE, code);
		res.put(RES_MESSAGE, msg);
	}
}
