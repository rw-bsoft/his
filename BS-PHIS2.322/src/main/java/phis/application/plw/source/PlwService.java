package phis.application.plw.source;

import ctd.service.core.ServiceException;
import ctd.util.AppContextHolder;
import ctd.util.annotation.RpcService;
import ctd.util.context.Context;
import ctd.util.context.ContextUtils;
import ctd.validator.ValidateException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Auther: Fengld
 * @Date: 2019/3/4 15:49
 * @Description: 家医签约后台服务类
 */
public class PlwService extends AbstractActionService implements DAOSupportable {
    protected Logger logger = LoggerFactory.getLogger(PlwService.class);

    /**
     * 查询孕妇减免是否有效
     * @param req
     * @param res
     * @param dao
     * @param ctx
     */
    public void doQueryIsPLW(Map<String, Object> req, Map<String, Object> res,
                BaseDAO dao, Context ctx){
        PlwModel model = new PlwModel(dao);      
        List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
        List<String> resList = new ArrayList<String>();
        try {
            ret = (List<Map<String, Object>>) model.queryIsPLW(req);
            if(ret != null){
                for(int i=0;i<ret.size();i++){ 
                	String YCFJM = "0";
                	if(ret.get(i).get("YCFJM") != null){
                		YCFJM = ret.get(i).get("YCFJM").toString();
                	}
                	if( YCFJM.equals("1")){
                		resList.add(ret.get(i).get("YJXH").toString());
                	}           	
                }  
            }   
            res.put("body", resList);
        } catch (Exception e) {
            logger.error("PLW-查询孕产妇减免是否有效失败"+ret);
            e.printStackTrace();
        }
    }
    
    public void doQueryLastRecord(Map<String, Object> req, Map<String, Object> res,
            BaseDAO dao, Context ctx){
        PlwModel model = new PlwModel(dao);      
        List<Map<String,Object>> ret = new ArrayList<Map<String,Object>>();
        List<String> resList = new ArrayList<String>();
        try {
        	model.queryLastRecord(req,res,dao,ctx);              
        } catch (Exception e) {
            logger.error("PLW-查询孕产妇减免查询上一条记录失败"+ret);
            e.printStackTrace();
        } 	
    } 
}
