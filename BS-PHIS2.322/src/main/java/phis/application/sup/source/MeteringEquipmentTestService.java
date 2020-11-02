package phis.application.sup.source;


import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;


import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * 量设备检定service
 * @author Administrator
 *
 */
public class MeteringEquipmentTestService extends AbstractActionService implements
		DAOSupportable {
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-6
	 * @description 查询检入信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryLeftList(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MeteringEquipmentTestModel model = new MeteringEquipmentTestModel(dao);
		List<Object> cnds = req.get("cnd")==null?null:(List<Object>)req.get("cnd");
		try {
			List<Map<String,Object>> ret=model.queryLeftList(cnds,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2014-3-6
	 * @description 保存检定信息
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveMeteringEquipmentTest(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		MeteringEquipmentTestModel model = new MeteringEquipmentTestModel(dao);
		Map<String,Object> body=(Map<String,Object>)req.get("body");
		try {
			model.saveMeteringEquipmentTest(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
