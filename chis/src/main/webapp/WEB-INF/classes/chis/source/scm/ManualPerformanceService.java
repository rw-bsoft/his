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
import java.util.Map;

public class ManualPerformanceService extends AbstractActionService implements
        DAOSupportable {
    /**
 * 保存手动履约记录
     * @throws ServiceException 
 *
 */
            @SuppressWarnings("unchecked")
            public void doSaveManualPerformance(Map<String, Object> req,
                    Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
                Map<String, Object> body = (Map<String, Object>) req.get("body");
                Map<String, Object> saveData = (Map<String, Object>)body.get("data");
        		String op = (String) saveData.get("op");
                try {
            		if("update".equals(op)){
            			Map<String, Object> tagerData = new HashMap<String,Object>();
            			tagerData.put("serviceId", saveData.get("serviceId"));
            			tagerData.put("serviceOrg", saveData.get("serviceOrg"));
            			tagerData.put("serviceTeam", saveData.get("serviceTeam"));
            			tagerData.put("servicer", saveData.get("servicer"));
            			tagerData.put("serviceMode", saveData.get("serviceMode"));
            			tagerData.put("serviceDate", saveData.get("serviceDate"));
                        dao.doSave("update", SCM_NewService, tagerData, false);
            		}
            		else{
            			saveData.put("createTime", new Date());
            			saveData.put("dataSource","0");
            			saveData.put("serviceDesc", "手动履约: "+saveData.get("serviceDesc"));
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
