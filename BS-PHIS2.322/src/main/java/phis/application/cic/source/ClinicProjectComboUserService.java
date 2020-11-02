package phis.application.cic.source;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.AbstractActionService;
import phis.source.service.DAOSupportable;
import phis.source.service.ServiceCode;
import ctd.account.UserRoleToken;
import ctd.service.core.ServiceException;
import ctd.util.context.Context;

public class ClinicProjectComboUserService extends AbstractActionService implements DAOSupportable {
	protected Logger logger = LoggerFactory.getLogger(ClinicProjectComboUserService.class);
	@SuppressWarnings("unchecked")
	public void doSaveComboUser(Map<String, Object> req, Map<String, Object> res, BaseDAO dao, Context ctx) throws ServiceException{
		List<Map<String, Object>> body = (List<Map<String, Object>>) req.get("body");
		UserRoleToken user = UserRoleToken.getCurrent();
		long KSDM = user.getProperty("biz_departmentId", Long.class);
		String YGDM = user.getUserId() + "";//暂时员工代码全部使用该方法替代
		int SSLB = 1; 
		String JGID = user.getManageUnit().getId();
		
		int CFLX = 1;
		if (req.get("CFLX") != null) {
			CFLX = Integer.parseInt(req.get("CFLX")+"");
		}
		try {
			String sql="KSDM=:KSDM and SSLB=:SSLB and JGID=:JGID and YGDM=:YGDM and CFLX=:CFLX";
			
			Map<String, Object> parameters = new HashMap<String, Object>();
		    parameters.put("KSDM",KSDM);
		    parameters.put("SSLB",SSLB);
		    parameters.put("JGID",JGID);
		    parameters.put("YGDM",YGDM);
		    parameters.put("CFLX",CFLX);
		    
		    Long l = dao.doCount("GY_CYZD", sql, parameters);
			if(l>0){
			  dao.doUpdate("delete from GY_CYZD  where YGDM='" + YGDM + "' and SSLB='" + SSLB
						+ "' and JGID=" + JGID + " and CFLX="+CFLX+"", null);
			}
			for (int i = 0; i < body.size(); i++) {
				Map<String, Object> qxkz = (Map<String, Object>) body.get(i);
				qxkz.put("JGID",JGID);
				qxkz.put("SSLB", SSLB);
				qxkz.put("KSDM", KSDM);
				qxkz.put("YGDM", YGDM);
				qxkz.put("CFLX", CFLX);
				if(body.get(i).get("JBXH") == null){
					qxkz.put("ZDXH", Integer.parseInt(body.get(i).get("ZDXH")+""));
				}else {
					qxkz.put("ZDXH", Integer.parseInt(body.get(i).get("JBXH")+""));
				}
				qxkz.put("ZDMC", body.get(i).get("ZDMC"));
				qxkz.put("PYDM", body.get(i).get("PYDM"));
				qxkz.put("ICD10", body.get(i).get("ICD10"));
				
			    dao.doSave("create", "phis.application.cic.schemas.GY_CYZD_CIC", qxkz, false);
			}
			Session ss = (Session) ctx.get(Context.DB_SESSION);
			ss.flush();
		} catch (PersistentDataOperationException e) {
			e.printStackTrace();
			throw new ServiceException("保存失败！", e);
		}
		req.put(RES_CODE, ServiceCode.CODE_OK);
		req.put(RES_MESSAGE, "Success");
	}
	
	public void doQueryProjectComboList(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicProjectComboUseModel smm = new ClinicProjectComboUseModel(dao);
		try {
			smm.doQueryProjectComboList(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询失败！", e);
		}
	}
	public void doQueryInCommonUseInfo(Map<String, Object> req,Map<String, Object> res, BaseDAO dao, Context ctx)
			throws ServiceException {
		ClinicProjectComboUseModel smm = new ClinicProjectComboUseModel(dao);
		try {
			smm.doQueryInCommonUseInfo(req, res, ctx);
		} catch (ModelDataOperationException e) {
			throw new ServiceException("查询失败！", e);
		}
	}

}