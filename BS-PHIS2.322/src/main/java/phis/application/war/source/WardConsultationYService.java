package phis.application.war.source;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * description:会诊意见
  *@author:zhangfs
 * create on 2013-5-29
 */
public class WardConsultationYService extends AbstractActionService implements DAOSupportable  {
	
	public WardConsultationYService(){
		
	}
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(WardConsultationYService.class);

	public WardConsultationYService(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:保存
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doSave(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
		WardConsultationYjModel model = new WardConsultationYjModel(dao);
		try {
			//保存
			Map<String, Object> map = model.save(req, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}
	/**
	 * description:会诊意见的Load
	 * add_by Liws
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQuery(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		
		WardConsultationYjModel model = new WardConsultationYjModel(dao);
		
		try {
			Map<String, Object> list = model.query(req, ctx);
			res.put("recode", list);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
