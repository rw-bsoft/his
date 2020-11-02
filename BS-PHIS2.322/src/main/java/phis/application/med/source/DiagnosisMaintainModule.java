package phis.application.med.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BSPHISEntryNames;
import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class DiagnosisMaintainModule {
	protected Logger logger = LoggerFactory.getLogger(DiagnosisMaintainModule.class);
	private BaseDAO dao;
	public DiagnosisMaintainModule(BaseDAO dao){
		this.dao = dao;
	}
	/**
	 * 医技诊断结果维护模块,保存操作
	 * @param req
	 * @param res
	 * @param ctx
	 * @throws ModelDataOperationException 
	 */
	public void doSaveZDJG(Map<String, Object> req,
			Map<String, Object> res, Context ctx) throws ModelDataOperationException{
		Object updataObj = req.get("updateList");
		Object removeObj = req.get("removeList");
		List<Map<String, Object>> updateList = null;
		List<Map<String, Object>> removeList = null;
		Map<String, Object> map = null , parameter = null;
		StringBuilder sqlBuilder = null;
		String zdid = "";
		long zddmCount = 1;
		try{
			//在删除对象不为空时，说明进行了删除动作
			if(removeObj != null){
				removeList = (List<Map<String, Object>>) removeObj;
				for(int i=0; i<removeList.size(); i++){
					map = removeList.get(i);
					sqlBuilder = new StringBuilder();
					parameter = new HashMap<String, Object>();
					zdid = String.valueOf(map.get("ZDID"));
					parameter.put("ZDID", zdid);
					zddmCount = dao.doCount("YJ_BG01", " ZDDM = :ZDID", parameter);
					if(zddmCount > 0){//zddmCount结果大于0时说明该诊断结果已经使用过，不允许删除
						res.put(Service.RES_MESSAGE, "诊断名称为\"" +map.get("ZDMC") + "\"已经在医技执行中使用,不能删除!");
						throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "诊断名称为\"" +map.get("ZDMC") + "\"已经在医技执行中使用,不能删除!");
					}else{//zddmCount小于等于0时，说明该诊断结果未使用过，允许删除
						parameter = new HashMap<String, Object>();
						sqlBuilder = new StringBuilder();
						parameter.put("ZDID", Long.parseLong(zdid));
						sqlBuilder.append("DELETE FROM YJ_ZDJG WHERE ZDID = :ZDID");
						dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					}
				}
			}
			//在更新对象不为空时，说明进行了更新或插入动作
			if(updataObj != null){
				updateList = (List<Map<String, Object>>) updataObj;
				for(int i=0; i<updateList.size(); i++){
					map = updateList.get(i);
					sqlBuilder = new StringBuilder();
					parameter = new HashMap<String, Object>();
					zdid = String.valueOf(map.get("ZDID"));
					parameter.put("ZDDM", String.valueOf(map.get("ZDDM")).toUpperCase());
					parameter.put("ZDMC", String.valueOf(map.get("ZDMC")));
					//当有诊断ID并且_optStatus != "create"时，为更新操作
					if(!"".equals(zdid) && !"null".equals(zdid) && !"create".equals(String.valueOf(map.get("_opStatus")))){
						sqlBuilder.append("UPDATE YJ_ZDJG SET ZDDM = :ZDDM , ZDMC = :ZDMC WHERE ZDID = :ZDID");
						parameter.put("ZDID", Long.parseLong(zdid));
						dao.doSqlUpdate(sqlBuilder.toString(), parameter);
					}else{//新增操作
						UserRoleToken user = UserRoleToken.getCurrent();
						String manaUnitId = user.getManageUnit().getId();// 用户的机构ID
						parameter.put("JGID", Long.parseLong(manaUnitId));
						parameter.put("KSDM", Long.parseLong(String.valueOf(req.get("medicalId"))));
						dao.doSave("create", BSPHISEntryNames.YJ_ZDJG, parameter, false);
					}
				}
			}
			
		}catch (ModelDataOperationException e) {
			throw e;
		} 
		catch (Exception e) {
			logger.error("医技诊断结果维护模块,保存操作失败", e);
			throw new ModelDataOperationException(
					ServiceCode.CODE_DATABASE_ERROR, "医技诊断结果维护模块,保存操作失败");
		}
	}
}
