package phis.application.ivc.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ChangeDoctorOrDepartmentService extends AbstractActionService implements 
	DAOSupportable {
	
	/**
	 * 根据医生工号查找对应的挂号科室
	 */
	@SuppressWarnings("unchecked")
	public void doFindKsdmByYsdm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = ((Map<String, Object>) req.get("body"));
		ChangeDoctorOrDepartmentModel cdod = new ChangeDoctorOrDepartmentModel(dao);
		try {
			String str = cdod.doFindKsdm(body, ctx);
			String subStr = str.substring(1, str.length()-1);
			String[] subArr = subStr.split(",");
			res.put("ksdm", str);
			res.put("firstKs",subArr[0]);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
	}
	
	
	/**
	 * 根据挂号科室查找对应的医生工号
	 */
	public void doFindYsdmByKsdm(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ChangeDoctorOrDepartmentModel cdod = new ChangeDoctorOrDepartmentModel(dao);
		try {
			String str = cdod.doFindYsdm(body, ctx);
			String subStr = str.substring(1, str.length()-1);
			String[] subArr = subStr.split(",");
			res.put("ysdm", str);
			res.put("firstYs",subArr[0]);
		} catch (ModelDataOperationException e) {
			throw new ServiceException(e);
		}
		
	}
	
}
