package phis.application.cic.source;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import phis.source.BaseDAO;
import phis.source.ModelDataOperationException;
import phis.source.PersistentDataOperationException;
import phis.source.service.ServiceCode;
import ctd.service.core.Service;
import ctd.util.context.Context;

public class ClinicComboNameModel {
	protected BaseDAO dao;
	protected Logger logger = LoggerFactory
			.getLogger(ClinicComboNameModel.class);
	public ClinicComboNameModel(BaseDAO dao) {
		this.dao = dao;
	}
	public void ClinicComboNameVerification(Map<String, Object> body,
			Map<String, Object> res,String op,BaseDAO dao, Context ctx) throws ModelDataOperationException{
		       if("create".equals(op)){
			    		  String sql="ZTMC=:ZTMC and JGID=:JGID and SSLB=:SSLB and ZTLB=:ZTLB";
						   Map<String, Object> parameters = new HashMap<String, Object>();
						   if(!body.get("YGDM").equals("")){
							   sql += " and YGDM=:YGDM ";
							   parameters.put("YGDM", body.get("YGDM"));
						   }else {
							   sql += " and KSDM=:KSDM ";
							   parameters.put("KSDM", body.get("KSDM"));
						   }
						   parameters.put("ZTMC", body.get("ZTMC"));  
						   parameters.put("JGID", body.get("JGID"));
						   parameters.put("SSLB", Integer.parseInt(body.get("SSLB").toString()));
						   parameters.put("ZTLB", Integer.parseInt(body.get("ZTLB").toString()));
						   try {
							Long l = dao.doCount("YS_MZ_ZT01", sql, parameters);
							if(l>0){
								res.put(Service.RES_CODE, 613);
								res.put(Service.RES_MESSAGE, "组套名称已经存在");
							}
						} catch (PersistentDataOperationException e) {
							logger.error("Save failed.", e);
							throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "组套名称校验失败");
						}
		        }else{
				    		  String sql="ZTMC=:ZTMC and JGID=:JGID and SSLB=:SSLB and ZTLB=:ZTLB and ZTBH<>:ZTBH";
							   Map<String, Object> parameters = new HashMap<String, Object>();
							   if(!body.get("YGDM").equals("")){
								   sql += " and YGDM=:YGDM ";
								   parameters.put("YGDM", body.get("YGDM"));
							   }else {
								   sql += " and KSDM=:KSDM ";
								   parameters.put("KSDM", body.get("KSDM").toString());
							   }
							   parameters.put("ZTMC", body.get("ZTMC"));
							   parameters.put("JGID", body.get("JGID"));
							   parameters.put("SSLB",Integer.parseInt(body.get("SSLB").toString()));
							   parameters.put("ZTLB", Integer.parseInt(body.get("ZTLB").toString()));
							   parameters.put("ZTBH", Long.parseLong(body.get("ZTBH")+""));
							   try {
								Long l = dao.doCount("YS_MZ_ZT01", sql, parameters);
								if(l>0){
									res.put(Service.RES_CODE, 613);
									res.put(Service.RES_MESSAGE, "项目组套名称已经存在");
								}
							} catch (PersistentDataOperationException e) {
								logger.error("Save failed.", e);
								throw new ModelDataOperationException(ServiceCode.CODE_DATABASE_ERROR, "组套名称校验失败");
							}
			   }
	}
}
