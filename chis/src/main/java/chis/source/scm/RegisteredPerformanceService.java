package chis.source.scm;

import chis.source.BaseDAO;
import chis.source.Constants;
import chis.source.PersistentDataOperationException;
import chis.source.service.AbstractActionService;
import chis.source.service.DAOSupportable;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisteredPerformanceService extends AbstractActionService implements
        DAOSupportable {
			    /**
			 * 保存挂号一般诊疗履约记录
			     * @throws ServiceException 
			 *
			 */
            @SuppressWarnings("unchecked")
            public void doSaveRegisteredPerformance(Map<String, Object> req,
                    Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
                Map<String, Object> body = (Map<String, Object>) req.get("body");
                Map<String, Object> saveData = (Map<String, Object>)body.get("data");
                try {     			
        			if(null != saveData.get("SBXH")){
                        Map<String, Object> parameterMap = new HashMap<String, Object>();
                        parameterMap.put("SPIID", "0000000000000037");
                        parameterMap.put("empiId", saveData.get("empiId"));
            			String Sql = "select a.favoreeName as serviceObj, a.operatorUnit as serviceOrg, "+
    								 "a.operatorUnit as serviceTeam, a.operator as servicer, "+
    								 "b.packageName as servicePack, b.taskName as serviceItems, "+
    								 "b.SCID, c.taskId as taskId, b.SPID as servicePackId, "+
    								 "b.taskCode as taskCode, b.taskCode as serviceItemSid, c.SCINID,c.SCIID "+
    								 "from SCM_SignContractRecord a "+
    								 "left join SCM_ServiceContractPlanTask b "+
    								 "on a.SCID=b.SCID "+
    								 "left join SCM_INCREASEITEMS c "+
    								 "on b.taskId=c.TASKID "+
    								 "where FAVOREEEMPIID=:empiId "+
    								 "and a.signFlag=1 "+
    								 "and b.SPIID=:SPIID";
            			List<Map<String, Object>> listMap = dao.doSqlQuery(Sql, parameterMap);   
            			Map<String, Object> map = listMap.get(0);
            			saveData.put("SPIID", "0000000000000037");
            			saveData.put("servicePackId", map.get("SERVICEPACKID"));
            			saveData.put("serviceItems", map.get("SERVICEITEMS"));
            			saveData.put("servicePack", map.get("SERVICEPACK"));
            			saveData.put("servicer", map.get("SERVICER"));
            			saveData.put("serviceTeam", map.get("SERVICETEAM"));
            			saveData.put("SCID", map.get("SCID"));
            			saveData.put("taskId", map.get("TASKID"));
            			saveData.put("taskCode", map.get("TASKCODE"));
            			saveData.put("serviceOrg", map.get("SERVICEORG"));
            			saveData.put("SCIID", map.get("SCIID"));
            			saveData.put("SCINID", map.get("SCINID"));
            			saveData.put("serviceItemsId", map.get("SERVICEITEMSID"));
            			saveData.put("serviceObj", map.get("SERVICEOBJ"));
                    	saveData.put("createTime", new Date());
                    	saveData.put("createUser", map.get("SERVICER"));
            			saveData.put("serviceDate", new Date());
            			saveData.put("serviceMode","0");
            			saveData.put("dataSource","1");		
            			if(saveData.get("serviceDesc") != null){
            				saveData.put("serviceDesc", "挂号: "+saveData.get("serviceDesc"));
            			}else{
            				saveData.put("serviceDesc", "挂号");
            			}
            			
        				saveData.put("SBXH",Long.parseLong(saveData.get("SBXH").toString()));
            			//新增“基本医疗-免一般诊疗服务个人自付部分”履约记录
            			dao.doSave("create", SCM_NewService, saveData, false); 
                        String hql = new StringBuffer("update ").append(" SCM_INCREASEITEMS ").append(" set SERVICETIMES = SERVICETIMES +1")
                                .append(" where SCID =:SCID and TASKCODE =:TASKCODE ").toString();
                        Map<String, Object> upMap = new HashMap<String, Object>();
                        upMap.put("SCID",Long.parseLong(saveData.get("SCID").toString()));
                        upMap.put("TASKCODE",saveData.get("taskCode").toString());
                        dao.doUpdate(hql,upMap); 
        			}	
                }catch(Exception e){
                    throw new ServiceException(Constants.CODE_DATABASE_ERROR,
                            "保存履约记录失败！", e);
                }
        }
}
