package phis.application.war.source;

import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;

import ctd.service.core.ServiceException;
import ctd.util.context.Context;

/**
 * description:提醒查询
  *@author:zhangfs
 * create on 2013-5-30
 */
public class RemainService extends AbstractActionService implements DAOSupportable {

	public RemainService() {

	}

	protected BaseDAO dao;
	protected Logger logger = LoggerFactory.getLogger(RemainService.class);

	public RemainService(BaseDAO dao) {
		this.dao = dao;
	}

	/**
	 * description:转入列表
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doQueryInList(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		RemainModel model = new RemainModel(dao);
		try {
			//保存
			model.queryIn(req, res, ctx);

		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	/**
	 * description:科室转出而对方未确认
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doQueryOutList(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {

		RemainModel model = new RemainModel(dao);
		try {
			//保存
			model.queryOut(req, res, ctx);

		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	/**
	 * description:确认
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doSave(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		RemainModel hbtbm = new RemainModel(dao);
		try {
			//保存
			hbtbm.save(body, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	/**
	 * description:分配床位列表
	 * add_by zhangfs
	 * @param 
	 * @return
	 */
	public void doQueryFp(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		RemainModel hbtbm = new RemainModel(dao);
		try {
			//保存
			hbtbm.queryCwInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage());
		}

	}

	/**
	 * 判断新床位是否已有病人
	 */
	public void doQueryInfo(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");

		RemainModel deptdao = new RemainModel(dao);
		String fpcw = body.get("fpcw").toString();
		//int type = Integer.parseInt(body.get("type").toString());

		long count = 0l;

		count = deptdao.queryIsExistPatient(ctx, fpcw);

		res.put("count", count);
	}
	
	@SuppressWarnings("unchecked")
	public void doCheckBedSexLimit(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) 
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		RemainModel remainModel = new RemainModel(dao);
		res.put("illegal", remainModel.checkBedSexLimit(body, ctx));
	}

	/**
	 * 根据床位号码查询床位信息
	 */
	public void doQueryInfoByFpcw(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		RemainModel deptdao = new RemainModel(dao);

		String oldksString = null, newksString = null;
		String sex1 = null, sex2 = null;

		int type = Integer.parseInt(body.get("type").toString());
		List<Map<String, Object>> maps = deptdao.queryCwInfoByFpcw(ctx, body.get("oldcwhm").toString(), res);
		List<Map<String, Object>> mapss = deptdao.queryCwInfoByFpcw(ctx, body.get("newcwhm").toString(), res);
		switch (type) {
		case 1:

			if (maps.size() > 0) {
				oldksString = maps.get(0).get("CWKS").toString();
			}
			if (mapss.size() > 0) {
				newksString = mapss.get(0).get("CWKS").toString();
			}
			if (StringUtils.equals(oldksString, newksString)) {
				res.put("result", 1);
			} else {
				res.put("result", 0);
			}
			break;

		case 2:
			if (maps.size() > 0) {
				sex1 = maps.get(0).get("CWXB").toString();
			}
			if (mapss.size() > 0) {
				sex2 = mapss.get(0).get("CWXB").toString();
			}
			if (StringUtils.equals(sex1, sex2)) {
				res.put("result", 1);
			} else {
				res.put("result", 0);
			}

			break;
		default:
			break;
		}

	}

	/**
	 * 新增查询确认按钮
	 */
	public void doSaveConfirm(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException, ModelDataOperationException {
		RemainModel model = new RemainModel(dao);
		Map<String, Object> body = (Map<String, Object>) req.get("body");
		model.savecwgl_zccl(body, dao, ctx, res);

	}
}
