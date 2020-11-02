package phis.application.war.source;

import java.util.List;
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
 * description:会诊邀请Services
  *@author:zhangfs
 * create on 2013-5-22
 */
public class WardConsultationYqService extends AbstractActionService implements DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(WardConsultationYqService.class);



	/**
	 * 添加会诊邀请记录
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveorupdate(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardConsultationYqModel hbtbm = new WardConsultationYqModel(dao);
		try {
			//保存
			hbtbm.save(body, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}
	
	/**
	 * 查询是否有会诊者之前记录
	 */
	@SuppressWarnings("unchecked")
	public void doQueryBq(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardTransferDeptModel deptdao = new WardTransferDeptModel(dao);
		
		List<Map<String, Object>> list=deptdao.querySelectList(ctx, body);
		res.put("body", list);
		
	}
}
