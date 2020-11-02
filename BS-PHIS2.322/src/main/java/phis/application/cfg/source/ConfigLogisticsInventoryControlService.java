package phis.application.cfg.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ConfigLogisticsInventoryControlService extends AbstractActionService
implements DAOSupportable{
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-29
	 * @description ������Ϣ��ѯ
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryLogisticsInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		int pageSize = 25;
		if (req.containsKey("pageSize")) {
			pageSize = (Integer) req.get("pageSize");
		}
		int pageNo = 0;
		if (req.containsKey("pageNo")) {
			pageNo = (Integer) req.get("pageNo") - 1;
		}
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("first", pageNo * pageSize);
		parameters.put("max", pageSize);
		List<?> cnd=null;
		if(req.containsKey("cnd")){
			cnd=(List<?>)req.get("cnd");
		}
		try {
			List<Object> ret = mmd.queryLogisticsInformation(cnd,parameters,ctx);
			res.put("totalCount", ret.get(0));
			res.put("body", ret.get(1));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}

	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-29
	 * @description his������Ʒ���ձ���
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveLogisticsInformation(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		try {
			 mmd.saveLogisticsInformation(body,ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-30
	 * @description ��ȡ��Ʒ�Ʒѱ�־
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryWPJFBZ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		try {
			int ret = mmd.queryWPJFBZ(ctx);
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
	 * @createDate 2013-5-30
	 * @description ��֤�����Ƿ�����Ʒ�Ʒѱ�־ ���ж��Ƿ���ڶ����ⷿ
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doVerificationWPJFBZ(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret = mmd.verificationWPJFBZ(body,ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	 /**
	  * 
	  * @author caijy
	  * @createDate 2013-7-19
	  * @description ��Ʊ����ǰ�ж��������Ƿ����
	  * @updateInfo
	  * @param req
	  * @param res
	  * @param dao
	  * @param ctx
	  * @throws ServiceException
	  */
	@SuppressWarnings("unchecked")
	public void doVerificationMzfpzf(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			Map<String,Object> ret = mmd.verificationMzfpzf(body,ctx);
			res.put(RES_CODE, ret.get("code"));
			res.put(RES_MESSAGE, ret.get("msg"));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 
	 * @author caijy
	 * @createDate 2013-5-31
	 * @description ���ﴦ��������Ŀ����������
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void doQueryAddData(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		try {
			List<Map<String,Object>> ret = mmd.queryAddData(body,ctx);
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
	 * @createDate 2013-6-17
	 * @description ��ѯ������ϸ��¼,���������Ա�ҩ,������Ҫ��̨ȥ��ѯ
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryCfmx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String,Object>> ret = mmd.queryCfmx(cnd,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 
	 * @author zhaojian
	 * @createDate 2019-7-24
	 * @description 门诊医生站处置录入列表信息查询
	 * @updateInfo
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryYjmx(Map<String, Object> req, Map<String, Object> res,
			BaseDAO dao, Context ctx) throws ServiceException {
		ConfigLogisticsInventoryControlModel mmd = new ConfigLogisticsInventoryControlModel(dao);
		List<?> cnd = (List<?>) req.get("cnd");
		try {
			List<Map<String,Object>> ret = mmd.queryYjmx(cnd,ctx);
			res.put("body", ret);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
