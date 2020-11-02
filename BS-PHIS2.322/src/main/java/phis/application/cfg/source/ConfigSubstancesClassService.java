package phis.application.cfg.source;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;



import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;
import ctd.validator.ValidateException;
public class ConfigSubstancesClassService extends AbstractActionService implements
		DAOSupportable {
	protected Logger logger = LoggerFactory
			.getLogger(ConfigBooksCategoryService.class);
   
	/**
    * 分类字典树的查询
    * @param req
    * @param res
    * @param dao
    * @param ctx
    * @throws ModelDataOperationException
    */
	public void doQueryNodInfo(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ModelDataOperationException {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		cbcm.queryNodInfo(req,res,ctx);
	}
	/**
	 * 类别序号
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doQueryLBXH(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ModelDataOperationException {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		cbcm.doQueryLBXH(req,res);
	}
	
	/**
	 * 保存分类字典树
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doSaveSubstancesClassTree(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)
	throws ModelDataOperationException {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		try {
			cbcm.doSaveSubstancesClassTree(req,res,ctx);
		} catch (ValidateException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除树节点
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ModelDataOperationException
	 */
	public void doDeleteNode(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx) throws ModelDataOperationException
			  {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		cbcm.doDeleteNode(req,res,ctx);
	}
	
	/**物质分类--已分类物质查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doAlreadyClassQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		try {
			cbcm.doAlreadyClassQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取已分类物资信息列表失败！", e);
		}
	}
	
	/**物质分类--未分类物质查询
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 */
	public void doAlreadyNoClassQuery(Map<String, Object> req,
			Map<String, Object> res, BaseDAO dao, Context ctx)throws ServiceException {
		ConfigSubstancesClassModule cbcm = new ConfigSubstancesClassModule(dao);
		try {
			cbcm.doAlreadyNoClassQuery(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("获取未分类物资信息列表失败！", e);
		}
	}
	/**
	 * 已分类物质   保存
	 * @param req
	 * @param res
	 * @param dao
	 * @param ctx
	 * @throws ServiceException
	 * @throws ParseException
	 * @throws PersistentDataOperationException
	 */
	@SuppressWarnings("unchecked")
	public void doSaveWZFL(Map<String, Object> req, Map<String, Object> res,BaseDAO dao, Context ctx) 
	            throws ServiceException, ParseException,PersistentDataOperationException {
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		String JGID = user.getManageUnit().getId();
		try {
			//List<Map<String, Object>> inofList = null;
			if (body.get(0).get("ZDXH") != null || body.get(0).get("ZDXH") !="") {
				Long ZDXH = Long.parseLong(body.get(0).get("ZDXH")+"");
				int LBXH = Integer.parseInt(body.get(0).get("LBXH")+"");
				
				Map<String,Object> removeMap = new HashMap<String, Object>();
				removeMap.put("ZDXH", ZDXH);
				removeMap.put("LBXH", LBXH);
				dao.doUpdate("delete from  WL_WZFL where ZDXH=" + ZDXH + " and LBXH=" + LBXH+ "", null);
			//	dao.doSave("delete", "WL_WZFL",removeMap, false);
				
			}
			for (int i = 0; i < body.size(); i++) {
				if (body.get(i).get("WZXH") != null) {
					Map<String, Object> saveMap = body.get(i);
					saveMap.put("JGID", JGID);
					dao.doSave("create", BSPHISEntryNames.WL_WZFL,saveMap, false);
				}
			}
			
		/*	
			
			if (body.get(0).containsKey("FIRST")) {
				dao.doRemove("LBXH",LBXH,"WL_WZFL");
			}else {
				if (body.size()>0 && body.get(0).containsKey("LBXH")) {
					int LBXH = Integer.parseInt(body.get(0).get("LBXH")+"");
					Map<String, Object> parameters = new HashMap<String, Object>();
				    parameters.put("LBXH",LBXH);
					// 返回list的查询语句
					StringBuffer sql_list = new StringBuffer("SELECT DISTINCT t.FLXH as FLXH,t.LBXH as LBXH,t.ZDXH as ZDXH,t.WZXH as WZXH  FROM ");
					sql_list.append(" WL_WZFL t ");
					sql_list.append(" WHERE (t.LBXH =:LBXH )  " );
					inofList = dao.doSqlQuery(sql_list.toString(), parameters);
				}
				for (int i = 0; i < body.size(); i++) {
					Map<String, Object> reqMap = new HashMap<String, Object>();
					
					if (body.get(i).get("FLXH") == null || body.get(i).get("FLXH")=="") {
						Map<String, Object> parameters = new HashMap<String, Object>();
						// 返回list的查询语句
						StringBuffer sql_Max = new StringBuffer("SELECT MAX(t.FLXH) as FLXH  FROM ");
						sql_Max.append(" WL_WZFL t ");
						inofList = dao.doSqlQuery(sql_Max.toString(), parameters);
						if (inofList.get(0).get("FLXH") != null) {
							long FLXH = Long.parseLong(inofList.get(0).get("FLXH")+"")+(i+1);
							reqMap.put("FLXH", FLXH);
						}else {
							reqMap.put("FLXH", (i+1));
						}
						
						reqMap.put("LBXH", Integer.parseInt(body.get(i).get("LBXH")+""));
						reqMap.put("ZDXH", Long.parseLong(body.get(i).get("ZDXH")+""));
						reqMap.put("WZXH", Long.parseLong(body.get(i).get("WZXH")+""));
						reqMap.put("JGID", JGID);
						dao.doSave("create", "WL_WZFL",reqMap, false);
					}else {
						//reqMap.put("FLXH", Long.parseLong(body.get(i).get("FLXH")+""));
						reqMap.put("LBXH", Integer.parseInt(body.get(i).get("LBXH")+""));
						reqMap.put("ZDXH", Long.parseLong(body.get(i).get("ZDXH")+""));
						reqMap.put("WZXH", Long.parseLong(body.get(i).get("WZXH")+""));
						reqMap.put("JGID", JGID);
					
					    int num = 0;
					    long FLXH = Long.parseLong(body.get(i).get("FLXH")+"");
						for (int j = 0; j < inofList.size(); j++) {
							long FLXH2 = Long.parseLong(inofList.get(j).get("FLXH")+"");
							if (FLXH == FLXH2) {
								num = num+1;
							}
						}
						if (num == 0) {
							dao.doSave("create", "WL_WZFL",reqMap, false);
						}
				   }
				}
				for (int i = 0; i < inofList.size(); i++) {
					int num = 0;
					long FLXH = 0 ;
					for (int j = 0; j < body.size(); j++) {
						if (inofList.get(i).get("FLXH") != null && body.get(j).get("FLXH") != null && body.get(j).get("FLXH") != "") {
							long FLXH1 = Long.parseLong(inofList.get(i).get("FLXH")+"");
							long FLXH2 = Long.parseLong(body.get(j).get("FLXH")+"");
							if (FLXH1 == FLXH2) {
								num = num+1;
							}else {
								FLXH = FLXH1;
							}
						}
					}
					if (num  ==  0 && (FLXH != 0)) {
						dao.doRemove(FLXH,"WL_WZFL");
					}
				}
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("确认失败！", e);
		}
  }
}