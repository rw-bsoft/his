package phis.application.war.source;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.utils.BSHISUtil;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * description:会诊申请
  *@author:zhangfs
 * create on 2013-5-22
 */
public class WardConsultationSqService extends AbstractActionService implements DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(WardConsultationSqService.class);

	/**
	 * 获取会诊病人信息
	 * 
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doGetHzList(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		WardConsultationSqModel hbtbm = new WardConsultationSqModel(dao);
		try {
			hbtbm.getList(req, res, ctx);
		} catch (Exception e) {
			throw new ServiceException("获取会诊病人信息！", e);
		}
	}
	/**
	 * 添加会诊申请记录
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
		WardConsultationSqModel hbtbm = new WardConsultationSqModel(dao);
		WardConsultationYqModel yqdao = new WardConsultationYqModel(dao);
		try {
			//取出入院时间，取出会诊时间
			Date hzsj=BSHISUtil.toDate(body.get("hzsj").toString());
			Date ryrq=BSHISUtil.toDate(body.get("ryrq").toString());
			int flag=BSHISUtil.dateCompare(hzsj, ryrq);
			if(flag<0){
				res.put("overtime", -1);
			}else{
				//保存
				Map<String, Object> map = hbtbm.save(body, ctx);

				//保存邀请记录前先查询邀请对象对应的记录是否存在
				
					Map<String, Object> map2 = new HashMap<String, Object>();
					map2.put("SQXH", map.get("SQXH").toString());
					map2.put("YQDX", body.get("yqdx").toString());
					yqdao.save(map2, ctx);
			
				//保存会诊遗嘱记录
				body.put("SQXH", map.get("SQXH").toString());
				body.put("YQDX", body.get("yqdx").toString());
				hbtbm.saveHzYz(body, ctx);
			}
			
			
		} catch (ModelDataOperationException e) {
			throw new ServiceException("会诊申请单保存失败!", e);
		}
	}

	/**
	 * 会诊者级联关联
	*/
//	@SuppressWarnings("unchecked")
//	public void doQueryBh(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
//			throws ServiceException {
//
//		Map<String, Object> body = (Map<String, Object>) req.get("body");
//		WardConsultationSqModel dao2 = new WardConsultationSqModel(dao);
//
//		List<Map<String, Object>> list;
//		try {
//			list = dao2.queryYGBH(body.get("yqdx").toString());
//			res.put("body", list);
//		} catch (PersistentDataOperationException e) {
//			logger.error(e.getMessage());
//		}
//
//	}

	public void doQueryDetail(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		//Map<String, Object> body = (Map<String, Object>) req.get("body");
		WardConsultationSqModel dao2 = new WardConsultationSqModel(dao);

		try {
			dao2.doLoadInfo(req, res, ctx);
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}
	
	/**
	 * description:修改会诊记录信息
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doUpdateSq(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		WardConsultationSqModel model = new WardConsultationSqModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		try {
			model.update(body, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}
		
	}
	
	/**
	 * 提交
	 */
	public void doSub(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		WardConsultationSqModel model = new WardConsultationSqModel(dao);

		model.save(req);
		
		
	}
	
	/**
	 * 退回
	 */
	public void doBack(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		WardConsultationSqModel model = new WardConsultationSqModel(dao);

		model.back(req);
		
		
	}
	
	
	/**
	 * 结束会诊
	 */
	public void doStop(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		WardConsultationSqModel model = new WardConsultationSqModel(dao);

		model.stop(req);
		
		
	}
	
	/**
	 * 删除
	 */
	public void doRemove(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ServiceException {
		WardConsultationSqModel model = new WardConsultationSqModel(dao);

		model.remove(req);
		
		
	}
}
