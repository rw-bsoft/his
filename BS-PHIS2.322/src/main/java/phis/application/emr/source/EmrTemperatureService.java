package phis.application.emr.source;

import java.util.Map;

import phis.source.BaseDAO;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class EmrTemperatureService extends AbstractActionService implements DAOSupportable {
	
	public void doLoadDaynicSchema(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrTemperatureModel model = new EmrTemperatureModel(dao);
		try {
			model.doLoadDaynicSchema(res);
		} catch (Exception e) {
			throw new ServiceException("获取病历编号错误!", e);
		}

	}
	/**
	* @Title: doSaveSMTZ
	* @Description: TODO(保存生命体征)
	* @param @param req
	* @param @param res
	* @param @param dao
	* @param @param ctx
	* @param @throws ServiceException    设定文件
	* @return void    返回类型
	* @throws
	*/
	public void doSaveSMTZ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		EmrTemperatureModel model = new EmrTemperatureModel(dao);
		try {
			model.saveSMTZ(req,res,ctx);
		} catch (Exception e) {
			throw new ServiceException("保存生命体征出错!", e);
		}

	}
}
