package phis.application.med.source;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phis.source.BSPHISSystemArgument;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.utils.ParameterUtil;
import ctd.account.UserRoleToken;
import ctd.account.user.User;
import ctd.service.core.Service;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
/**
 * 病区项目提交Service服务
 * @author gejj
 *
 */
public class WardProjectService extends AbstractActionService implements
		DAOSupportable {
	
	/**
	 * 病区提交模块，按项目提交左边列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryProjectLeft(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try{
			WardProjectModule module = new WardProjectModule(dao);
			res.put("body", module.queryProjectLeft(req, res));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		
	}
	/**
	 * 病区提交模块,按病人提交左边列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryPatientLeft(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		try{
			WardProjectModule module = new WardProjectModule(dao);
			res.put("body", module.queryPatientLeft(req, res));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 按病人提交，右边列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryPatientRight(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		try{
			WardProjectModule module = new WardProjectModule(dao);
			res.put("body", module.queryPatientRight(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 按项目提交，右边列表
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doQueryProjectRight(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		try{
			WardProjectModule module = new WardProjectModule(dao);
			res.put("body", module.queryProjectRight(req, res, ctx));
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	/**
	 * 根据机构编号获取系统配置的复核标志值
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException 
	 */
	public void doGetFHBZ(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		try{
			UserRoleToken user = UserRoleToken.getCurrent();
			String jgid = user.getManageUnit().getId();// 用户的机构ID
			String al_fhbz = ParameterUtil.getParameter(jgid, BSPHISSystemArgument.FHYZHJF, ctx);
			//复核标志为1表示需要复核后才能在病区项目提交中显示，这时需要查询未复核数
			if("1".equals(al_fhbz)){
				WardProjectModule module = new WardProjectModule(dao);
				long count = module.getUnReviewCount(req, res, ctx);
				res.put("body", count);
			}
			res.put(Service.RES_CODE, 200);
			res.put(Service.RES_MESSAGE, al_fhbz);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
	
	/**
	 * 保存按钮
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	public void doSave(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException{
		List<Map<String, String>> updateList = (List<Map<String, String>>)req.get("body");
		WardProjectModule module = new WardProjectModule(dao);
		try{
			module.dosave(updateList, ctx);
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
		res.put(Service.RES_CODE, 200);
	}
	
	/**
	 * 确认按钮
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 */
	@SuppressWarnings("unchecked")
	public void doSaveDetermine(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		List<Map<String, String>> leftList = (List<Map<String, String>>)req.get("leftJson");
		List<Map<String, Object>> rightList = (List<Map<String, Object>>)req.get("rightJson");
		//存放同组的信息
		Map<String, List<Map<String, Object>>> rightGroup = new HashMap<String, List<Map<String,Object>>>();
		try{
			WardProjectModule module = new WardProjectModule(dao);
			if(rightList != null){
				List<Map<String, Object>> list = null;
				//获取所有作废的药品
				List<Map<String, Object>> invalidList = module.queryUnInvalidProject(ctx);
				//遍历右边列表
				for(Map<String, Object> map : rightList){
					//校验执行科室不能为空
					if(map.get("ZXKS") == null || "".equals(map.get("ZXKS")) || "null".equals(map.get("ZXKS")) || "0".equals(map.get("ZXKS"))
							|| map.get("ZXKS_text") == null || "".equals(map.get("ZXKS_text")) || "null".equals(map.get("ZXKS_text"))){
						res.put(RES_CODE, "201");
						res.put(RES_MESSAGE, "执行科室不能为空!");
						return;
					}
					//校验项目是否为作废项
					if(!module.checkInvalidProject(invalidList, map, res)){
						//该项目为作废项，不进行提交
						return ;
					}
					//判断是否已经在rightGroup中存在相同的组
					if(rightGroup.get(String.valueOf(map.get("YZZH"))) != null){
						map.put("YJZX", "0");
						rightGroup.get(String.valueOf(map.get("YZZH"))).add(map);
					}else{
						list = new ArrayList<Map<String,Object>>();
						map.put("YJZX", "1");//对于同一组的第一条，它的YJZX应为1
						list.add(map);
						rightGroup.put(String.valueOf(map.get("YZZH")), list);
					}
					String je1 = (map.get("JE")+"").substring(0, (map.get("JE")+"").indexOf("x"));
					String je2 = (map.get("JE")+"").substring((map.get("JE")+"").indexOf("x")+1,(map.get("JE")+"").length());
					double JE = Double.parseDouble(je1)*Double.parseDouble(je2);
					map.put("JE", JE);
					
				}
				//校验同组的执行科室是否相同
				if(!module.checkGroupIsEqualZXKS(rightGroup, res)){
					//不相同直接返回，不进行提交
					return;
				}
			}
			String commitType = String.valueOf(req.get("commitType"));
			if("projectCommitTab".equals(commitType)){//按项目提交
				module.doSaveDetermine(leftList, rightGroup, res, String.valueOf(req.get("jgid")), ctx);
			}else{//按病人提交
				module.doSaveDetermine(leftList, rightGroup, res, String.valueOf(req.get("jgid")), ctx);
			}
		} catch (ModelDataOperationException e) {
			res.put(RES_CODE, e.getCode());
			res.put(RES_MESSAGE, e.getMessage());
			throw new ServiceException(e);
		}
	}
}
