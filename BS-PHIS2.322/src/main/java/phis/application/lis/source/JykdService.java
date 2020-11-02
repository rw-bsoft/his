package phis.application.lis.source;

import java.util.Map;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import phis.application.mds.source.MedicineUtils;
import phis.application.pacs.source.JckdModel;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

public class JykdService extends AbstractActionService implements
DAOSupportable{
	/**
	 * 标本接收状态回写
	 * @author caijy
	 * @createDate 2017-3-12
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doBbjszthx(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JykdModel model = new JykdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.bbjszthx(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 门诊计费执行情况服务
	 * @author caijy
	 * @createDate 2017-3-12
	 * @description 
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	protected void doMzjfzxqk(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		JykdModel model = new JykdModel(dao);
		String xml=MedicineUtils.parseString(req.get("xml"));
		try {
			 String reXml=model.mzjfzxqk(xml,ctx);
			 res.put("data", reXml);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
}
